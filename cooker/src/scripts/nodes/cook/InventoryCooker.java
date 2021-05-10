package scripts.nodes.cook;

import java.util.Objects;
import java.util.Optional;
import org.tribot.api.Clicking;
import static org.tribot.api.General.random;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import scripts.api.game.interfaces.Interfaces;
import scripts.api.game.interfaces.MakeAmount;
import scripts.api.game.inventory.Inventory;
import scripts.api.game.mouse.Mouse;
import static scripts.data.VariablesCooker.vars;
import scripts.api.beg.constants.GameInterface;
import scripts.api.beg.constants.GameItem;;
import scripts.framework.logic.task.Task;
import scripts.api.beg.utils.Strings;
import scripts.api.beg.utils.Timing;
import scripts.api.beg.antiban.AntiBan;
import scripts.api.beg.anticipate.Anticipate;

public class InventoryCooker extends Task {

	public InventoryCooker() {
		if (vars.product.equalsIgnoreCase(GameItem.BREAD_DOUGH.name)) {
			vars.cookingInterface = GameInterface.COOKING_BREAD_DOUGH;
		}
		else if (vars.product.equalsIgnoreCase(GameItem.PIZZA_BASE.name)) {
			vars.cookingInterface = GameInterface.COOKING_PIZZA_BASE;
		}
		else if (vars.product.equalsIgnoreCase(GameItem.PASTRY_DOUGH.name)) {
			vars.cookingInterface = GameInterface.COOKING_PASTRY_DOUGH;
		}
		else if (vars.product.equalsIgnoreCase(GameItem.PITTA_DOUGH.name)) {
			vars.cookingInterface = GameInterface.COOKING_PITTA_DOUGH;
		}
		else {
			vars.cookingInterface = GameInterface.COOKING_ON_INVENTORY;
		}
	}

	@Override
	public String toString() {
		return "Cooking";
	}

	@Override
	public boolean validate() {
		if (Login.getLoginState() != STATE.INGAME) {
			return false;
		}
		if (Banking.isBankScreenOpen()) {
			return false;
		}
		// TODO doesnt make sense to get amount from paint
		int amountToCook = vars.painter.getAmountToCook();
		int expectedAmount = Objects.equals(vars.firstSupply, GameItem.POT_OF_FLOUR.name) ? Math.min(amountToCook, 9) : Math.min(amountToCook, 14);
		int firstItemCount = -1;
		if (!Strings.isEmpty(vars.firstSupply)) {
			firstItemCount = Inventory.getCount(vars.firstSupply);
		}
		else if (vars.firstSupplyID > -1) {
			firstItemCount = Inventory.getCount(vars.firstSupplyID);
		}
		if (firstItemCount <= 0 || firstItemCount > expectedAmount) {
			return false;
		}
		int secondItemCount = Inventory.getCount(vars.secondSupply);
		return secondItemCount > 0 && secondItemCount <= expectedAmount;
	}

	@Override
	public boolean execute() {
		long startTime = Timing.currentTimeMillis();
		if (!useFirstSupplyOnSecond()) {
			return failure("using items on each other");
		}
		Optional<RSInterface> cookingInterface = vars.cookingInterface.find();
		if (cookingInterface.isEmpty()) {
			return failure("cooking interface not found");
		}
		if (!Interfaces.isInterfaceSubstantiated(cookingInterface.get())) {
			return failure("cooking interface is not visible");
		}
		if (!Interfaces.selectAmount(vars.cookingInterface.master, MakeAmount.ALL)) {
			return failure("selecting \"All\" amount");
		}
		if (vars.useKeybinds && !Strings.isEmpty(vars.keybind)) {
			Keyboard.typeString(vars.keybind);
		}
		else if (!Clicking.click(cookingInterface.get())) {
			return failure("clicking on cooking interface");
		}
		AntiBan.getAntiBan().generateTrackers((int) (Timing.currentTimeMillis() - startTime), false);
		AntiBan.getAntiBan().sleepReactionTime();
		Anticipate.mouseToBank();
		startTime = Timing.currentTimeMillis();
		if (!Timing.waitConditionWhileAntiban(() -> !validate(), random(23045, 24896))) {
			return failure("reached make-item cooking timeout.");
		}
		AntiBan.getAntiBan().generateTrackers((int) (Timing.currentTimeMillis() - startTime), true);
		AntiBan.getAntiBan().sleepReactionTime();
		return success();
	}

	private boolean useFirstSupplyOnSecond() {
		if (vars.cookingInterface.isSubstantiated()) {
			return true;
		}
		RSItem[] firstItems = null;
		if (!Strings.isEmpty(vars.firstSupply)) {
			firstItems = Inventory.find(vars.firstSupply);
		}
		else if (vars.firstSupplyID > -1) {
			firstItems = Inventory.find(vars.firstSupplyID);
		}
		if (firstItems == null || firstItems.length < 1) {
			return false;
		}
		RSItem firstItem = firstItems[firstItems.length - 1];
		if (!Mouse.clickItem("Use", firstItem)) {
			return false;
		}
		Optional<RSItem> secondItem = Inventory.getOne(vars.secondSupply);
		if (secondItem.isEmpty() || !Clicking.click(secondItem.get())) {
			return false;
		}
		if (!vars.useKeybinds) {
			Anticipate.mouseToInterface(GameInterface.CHAT_BOX_MESSAGES);
		}
		return Timing.waitSubstantiatedInterface(vars.cookingInterface, random(1986, 3873));
	}

}
