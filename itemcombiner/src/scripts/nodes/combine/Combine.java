package scripts.nodes.combine;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api2007.Banking;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import scripts.api.game.interfaces.Interfaces;
import scripts.api.game.interfaces.MakeAmount;
import scripts.api.game.inventory.Inventory;
import scripts.data.ItemCombinerItem;
import scripts.api.beg.constants.GameInterface;
import scripts.api.laniax.entityselector.Entities;
import scripts.api.laniax.entityselector.prefabs.InterfaceEntity;
import scripts.api.laniax.entityselector.prefabs.ItemEntity;
import scripts.api.beg.logging.Logger;
import scripts.framework.logic.task.Task;
import scripts.api.beg.utils.Timing;
import scripts.api.beg.antiban.AntiBan;

import java.util.Optional;

import static scripts.data.ItemCombinerVariables.vars;

public class Combine extends Task {

	@Override
	public String toString() {
		return "Combining items";
	}

	@Override
	public boolean validate() {
		if (Login.getLoginState() != STATE.INGAME) {
			return false;
		}
		if (Banking.isBankScreenOpen()) {
			return false;
		}
		for (int i = 0; i < vars.task.getNumSupplies(); i++) {
			ItemCombinerItem item = vars.task.items.get(i);
			int amount = Inventory.getCount(item.getName());
			if (amount < 1 || amount > item.getInvAmount()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean execute() {
		printDebug();
		long startTime;
		if (hasInterface()) {
			if (!Interfaces.isInterfaceValid(vars.task.masterId)) {
				if (!combineItems()) {
					return failure("combining items");
				}
				if (!Timing.waitCondition(() -> Interfaces.isInterfaceSubstantiated(vars.task.masterId), General.random(2132, 2567))) {
					return failure("waiting for interface to show");
				}
			}
			if (hasAmountInterface() && !selectAmount()) {
				return failure("selecting amount");
			}
			if (!clickActionInterface()) {
				return failure("clicking interface to start the action");
			}
			startTime = Timing.currentTimeMillis();
			if (!Timing.waitCondition(() -> {
				this.idleAntiban();
				return vars.task.getAmountLeft() <= 0 || Interfaces.isInterfaceValid(GameInterface.LEVEL_UP.master) || !this.validate();
			}, vars.task.inventoryTimeout)) {
				return failure("waiting for items to be combined");
			}
		}
		else {
			if (!combineItems()) {
				return failure("combining items");
			}
			final int productCount = Inventory.getCount(vars.task.getProduct().getName());
			startTime = Timing.currentTimeMillis();
			if (!Timing.waitCondition(() -> Inventory.getCount(vars.task.getProduct().getName()) > productCount, General.random(3056, 4010))) {
				return failure("waiting for items to be combined");
			}
		}
		AntiBan.getAntiBan().generateTrackers((int) (Timing.currentTimeMillis() - startTime), true);
		AntiBan.getAntiBan().sleepReactionTime();
		return success();
	}

	private void idleAntiban() {
		if (!ChooseOption.isOpen()) {
			AntiBan.getAntiBan().moveCamera();
			AntiBan.getAntiBan().checkXp();
			AntiBan.getAntiBan().pickUpMouse();
			if (vars.antibanMouseOffscreen) {
				AntiBan.getAntiBan().leaveGame();
			}
			AntiBan.getAntiBan().examineEntity();
			AntiBan.getAntiBan().rightClick();
			AntiBan.getAntiBan().mouseMovement();
			AntiBan.getAntiBan().checkTabs();
		}
	}

	private boolean hasInterface() {
		return vars.task.masterId != -1 && vars.task.childId != -1;
	}

	private boolean hasAmountInterface() {
		return vars.task.masterId == 270;
	}

	private boolean hasAmountOption() {
		return vars.task.masterId != 270;
	}

	private boolean selectAmount() {
		return Interfaces.selectAmount(vars.task.masterId, MakeAmount.ALL);
	}

	private boolean clickActionInterface() {
		final int master = vars.task.masterId;
		final int child = vars.task.childId;
		final int component = vars.task.componentId;
		RSInterface combineInterface = Entities.find(InterfaceEntity::new).inMasterAndChild(master, child).getFirstResult();
		if (combineInterface == null) {
			return false;
		}
		if (component != -1) {
			combineInterface = combineInterface.getChild(component);
		}
		String option = hasAmountOption() ? "Make all" : "";
		return Clicking.click(option, combineInterface);
	}

	private boolean combineItems() {
		String firstItemName = vars.task.items.get(0).getName();
		String secondItemName = vars.task.items.get(1).getName();
		Optional<RSItem> first = Inventory.getOne(firstItemName);
		if (first.isEmpty()) {
			return false;
		}
		if (!Inventory.select(first.get())) {
			return false;
		}
		Optional<RSItem> second;
		if (!firstItemName.equalsIgnoreCase(secondItemName)) {
			second = Inventory.getOne(secondItemName);
		}
		else {
			second = Optional.ofNullable(Entities.find(ItemEntity::new).custom(item -> {
				if (item == null) {
					return false;
				}
				if (item.getArea().equals(first.get().getArea())) {
					return false;
				}
				RSItemDefinition def = item.getDefinition();
				return def != null && def.getName().equalsIgnoreCase(secondItemName);
			}).getFirstResult());
		}
		return second.isPresent() && Clicking.click(second.get());
	}

	private void printDebug() {
		Logger.getLogger().info("Combine items: "
			+ "Skill: " + vars.task.skill
			+ "; First item: " + vars.task.items.get(0).getName()
			+ "; Second item: " + vars.task.items.get(1).getName()
			+ "; Product: " + vars.task.getProduct().getName()
			+ "; First item amount: " + vars.task.items.get(0).getInvAmount()
			+ "; Second item amount: " + vars.task.items.get(1).getInvAmount()
			+ "; Product amount: " + vars.task.getProduct().getInvAmount()
			+ "; Master interface id: " + vars.task.masterId
			+ "; Child interface id: " + vars.task.childId
			+ "; Component interface id: " + vars.task.componentId
			+ "; Amount: " + vars.task.amount
			+ "; Amount left: " + vars.task.getAmountLeft()
			+ "; Stop level: " + vars.task.stopLevel);
	}

}
