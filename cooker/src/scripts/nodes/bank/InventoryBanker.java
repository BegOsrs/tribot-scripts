package scripts.nodes.bank;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.tribot.api.General;
import static org.tribot.api.General.random;
import org.tribot.api.Timing;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.WebWalking;
import scripts.api.game.bank.Banking;
import scripts.api.game.inventory.Inventory;
import static scripts.data.VariablesCooker.vars;
import scripts.api.beg.constants.GameItem;;
import scripts.api.beg.constants.skills.Cooking;
import scripts.api.laniax.entityselector.Entities;
import scripts.api.laniax.entityselector.prefabs.ItemEntity;
import scripts.api.beg.exceptions.OutOfSuppliesException;
import scripts.framework.logic.task.Task;
import scripts.framework.script.TaskScript;
import scripts.api.beg.utils.Strings;
import scripts.api.beg.antiban.AntiBan;

public class InventoryBanker extends Task {

	@Override
	public String toString() {
		return "Banking";
	}

	@Override
	public boolean validate() {
		return Login.getLoginState() == STATE.INGAME
			&& (Banking.isBankScreenOpen() || bankingForFirstItem() || bankingForSecondItem() || missingKnife(false));
	}

	@Override
	public boolean execute() {
		if (!Banking.isInBank() && !WebWalking.walkToBank()) {
			return failure("walking to a bank");
		}
		if (!Banking.isBankScreenOpen() && !(ChooseOption.isOpen() ? ChooseOption.select("Bank") : Banking.openBank())) {
			return failure("opening a bank");
		}
		int amountFirst = getAmountToWithdraw();
		int amountSecond = amountFirst;
		int amountTotal = amountFirst + amountSecond;
		int numFirst = 0;
		if (!Strings.isEmpty(vars.firstSupply)) {
			numFirst = Inventory.getCount(vars.firstSupply);
		}
		else if (vars.firstSupplyID > -1) {
			numFirst = Inventory.getCount(vars.firstSupplyID);
		}
		int numSecond = Inventory.getCount(vars.secondSupply);
		int numKnife = Inventory.getCount(GameItem.KNIFE.name);

		List<String> exceptList = new LinkedList<String>();
		if (vars.product.contains("Chopped") && numKnife == 1) {
			exceptList.add(GameItem.KNIFE.name);
			amountTotal++;
		}
		if (numFirst > 0 && numFirst <= amountFirst) {
			exceptList.add(vars.firstSupply);
			amountFirst = amountFirst - numFirst;
		}
		if (numSecond > 0 && numSecond <= amountSecond) {
			exceptList.add(vars.secondSupply);
			amountSecond = amountSecond - numSecond;
		}
		int exceptSize = exceptList.size();
		if (exceptSize > 0) {
			Banking.depositAllExcept(exceptList.toArray(new String[exceptSize]));
		}
		else if (!Banking.depositAllAndWait()) {
			return failure("depositing all items into bank");
		}
		final int total = amountTotal;
		final int first = amountFirst;
		final int second = amountSecond;
		if (!Timing.waitCondition(() -> Inventory.getCount() == total - first - second, random(2122, 2421))) {
			return failure("depositing items");
		}
		if (!withdrawSupplies(amountFirst, amountSecond)) {
			vars.failsafe++;
			return failure("withdrawing items");
		}
		Banking.close();
		if (!Timing.waitCondition(() -> !validate(), random(1968, 2394))) {
			return failure("timeout reached after attempting to close bank.");
		}
		vars.failsafe = 0;
		return success();
	}

	private boolean bankingForFirstItem() {
		int count = -1;
		if (!Strings.isEmpty(vars.firstSupply)) { //TODO use something like isUsingId() insteaad of checking
			//if string is empty
			count = Entities.find(ItemEntity::new).nameEquals(vars.firstSupply).getCount();
		}
		else if (vars.firstSupplyID > -1) {
			count = Entities.find(ItemEntity::new).idEquals(vars.firstSupplyID).getCount();
		}
		return count == 0 || count > getAmountToWithdraw();
	}

	private boolean bankingForSecondItem() {
		int count = -1;
		if (!Strings.isEmpty(vars.secondSupply)) {
			count = Entities.find(ItemEntity::new).nameEquals(vars.secondSupply).getCount();
		}
		return count == 0 || count > getAmountToWithdraw();
	}

	private boolean missingFirstItem() {
		if (!Strings.isEmpty(vars.firstSupply)) {
			return !Entities.find(ItemEntity::new).nameEquals(vars.firstSupply).exists()
				&& Banking.find(vars.firstSupply).length < 1;
		}
		else if (vars.firstSupplyID > -1) {
			return !Entities.find(ItemEntity::new).idEquals(vars.firstSupplyID).exists()
				&& Banking.find(vars.firstSupplyID).length < 1;
		}
		return true;
	}

	private boolean missingSecondItem() {
		return !Entities.find(ItemEntity::new).nameEquals(vars.secondSupply).exists()
			&& Banking.find(vars.secondSupply).length < 1;
	}

	private boolean missingKnife(boolean includeBank) {
		if (!vars.product.equalsIgnoreCase(Cooking.Food.CHOPPED_TUNA.getProduct())) {
			return false;
		}
		return !Entities.find(ItemEntity::new).nameEquals(GameItem.KNIFE.name).exists()
			&& (!includeBank || Banking.find(GameItem.KNIFE.name).length < 1);
	}

	private int getAmountToWithdraw() {
		int amountToCook = vars.painter.getAmountToCook();
		return (Objects.equals(vars.firstSupply, "Pot of flour")) ?
			Math.min(amountToCook, 9) :
			Math.min(amountToCook, 14);
	}

	private boolean withdrawSupplies(int firstAmount, int secondAmount) {
		if (vars.failsafe > 0) {
			checkSupplies();
		}
		if (!withdrawKnife()) {
			return false;
		}
		if (!withdrawFirstItem(firstAmount)) {
			return false;
		}
		AntiBan.getAntiBan().waitEntityInteractionDelay();
		return withdrawSecondItem(secondAmount);
	}

	private boolean withdrawKnife() {
		return !missingKnife(false) || Banking.withdraw(1, GameItem.KNIFE.name);
	}

	private boolean withdrawFirstItem(int amount) {
		String firstItemName = vars.firstSupply;
		int firstItemId = vars.firstSupplyID;
		return amount < 1 ||
			!Strings.isEmpty(firstItemName) ?
			Banking.withdraw(amount, firstItemName) :
			Banking.withdraw(amount, firstItemId);
	}

	private boolean withdrawSecondItem(int amount) {
		return amount < 1 || Banking.withdrawAndWait(amount, vars.secondSupply);
	}

	private void checkSupplies() {
		while (missingFirstItem() || missingSecondItem() || missingKnife(true)) {
			if (++vars.failsafe > TaskScript.CHECK_SUPPLIES_RETRIES) {
				Banking.depositAllAndWait();
				Banking.close();
				vars.failsafe = 0;
				throw new OutOfSuppliesException();
			}
			General.sleep(50);
		}
	}

}
