package scripts.nodes.bank;

import static org.tribot.api.General.random;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import scripts.api.game.bank.Banking;
import scripts.api.game.inventory.Inventory;
import static scripts.data.VariablesCooker.vars;
import scripts.api.beg.exceptions.OutOfSuppliesException;
import scripts.framework.logic.task.Task;
import scripts.framework.script.TaskScript;
import scripts.api.beg.utils.Timing;

public class RoguesDenBanker extends Task {

	@Override
	public String toString() {
		return "Banking";
	}

	@Override
	public boolean validate() {
		return Login.getLoginState() == STATE.INGAME && (org.tribot.api2007.Banking.isBankScreenOpen() || Inventory.getCount(vars.firstSupply) < 1);
	}

	@Override
	public boolean execute() {
		if (!Banking.openBankBanker()) {
			return failure("opening bank");
		}
		if (!Banking.depositAllAndWait()) {
			return failure("depositing all");
		}
		if (!this.withdrawSupplies()) {
			if (++vars.failsafe > TaskScript.CHECK_SUPPLIES_RETRIES) {
				throw new OutOfSuppliesException();
			}
			return failure("withdrawing supplies");
		}
		Banking.close();
		if (!Timing.waitCondition(() -> !validate(), random(3023, 3450))) {
			return failure("timeout reached after attempting to close bank.");
		}
		vars.failsafe = 0;
		return success();
	}

	private boolean withdrawSupplies() {
		int amountToCook = vars.painter.getAmountToCook();
		return Banking.withdrawAndWait(amountToCook > 27 ? 0 : amountToCook, vars.firstSupply);
	}

}