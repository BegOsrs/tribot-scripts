package scripts.nodes.bank;

import static org.tribot.api.General.random;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.Objects;
import scripts.api.game.bank.Banking;
import scripts.api.game.inventory.Inventory;
import static scripts.data.VariablesCooker.vars;
import scripts.api.beg.constants.GameItem;;
import scripts.api.laniax.entityselector.Entities;
import scripts.api.laniax.entityselector.prefabs.ItemEntity;
import scripts.api.beg.exceptions.OutOfSuppliesException;
import scripts.framework.logic.task.Task;
import scripts.framework.script.TaskScript;

public class FireBanker extends Task {

	@Override
	public String toString() {
		return "Banking";
	}

	@Override
	public boolean validate() {
		if (Login.getLoginState() != STATE.INGAME) {
			return false;
		}
		if (Banking.isBankScreenOpen()) {
			return true;
		}
		if (!Entities.find(ItemEntity::new).nameEquals(vars.firstSupply).exists()) {
			return true;
		}
		if (!Entities.find(ItemEntity::new).nameEquals(GameItem.TINDERBOX.name).exists()) {
			return true;
		}
		return !Entities.find(ItemEntity::new).nameEquals(vars.logs).exists() &&
			!Objects.isAt(vars.fireTile, "Fire");
	}

	@Override
	public boolean execute() {
		if (!Banking.openBank()) {
			return failure("opening bank");
		}
		// Prevent script from depositing unnecessary supplies
		final int supplyAmount = Inventory.getCount(vars.firstSupply);
		final int tinderboxAmount = Inventory.getCount(GameItem.TINDERBOX);
		final int logsAmount = Inventory.getCount(vars.logs);
		if (supplyAmount > vars.painter.getAmountToCook() || supplyAmount > 26 || tinderboxAmount > 1 || logsAmount > 1) {
			Banking.depositAll();
		}
		else {
			Banking.depositAllExcept(GameItem.TINDERBOX.name, vars.logs, vars.firstSupply);
		}
		if (!this.withdrawSupplies()) {
			// Force task ending if script can't find required supplies
			if (++vars.failsafe > TaskScript.CHECK_SUPPLIES_RETRIES) {
				throw new OutOfSuppliesException();
			}
			return failure("withdrawing items");
		}
		vars.failsafe = 0;
		return success();
	}

	private boolean withdrawSupplies() {
		if (!Inventory.contains(GameItem.TINDERBOX) && !Banking.withdraw(1, GameItem.TINDERBOX)) {
			return false;
		}
		if (!Inventory.contains(vars.logs) && !Banking.withdraw(1, vars.logs)) {
			return false;
		}
		int suppliesCount = Inventory.getCount(vars.firstSupply);
		if (suppliesCount == 26) {
			return true;
		}
		// TODO doesnt make sense to get amount to cook from paint
		int amountToCook = vars.painter.getAmountToCook();
		int amountToWithdraw = amountToCook > 25 ? random(0, -1) : amountToCook - suppliesCount;
		return Banking.withdrawAndWait(amountToWithdraw, vars.firstSupply);
	}

}
