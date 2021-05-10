package scripts.nodes.bank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.tribot.api.General;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.Player;
import scripts.api.game.bank.Banking;
import scripts.api.game.inventory.Inventory;
import scripts.api.game.magic.Magic;
import scripts.data.ItemCombinerItem;
import static scripts.data.ItemCombinerVariables.vars;
import scripts.api.beg.constants.Place;
import scripts.api.laniax.entityselector.Entities;
import scripts.api.laniax.entityselector.prefabs.ItemEntity;
import scripts.api.beg.exceptions.OutOfSuppliesException;
import scripts.api.beg.logging.Logger;
import scripts.framework.logic.task.Task;
import scripts.api.beg.utils.Timing;
import scripts.api.beg.anticipate.Anticipate;

public class AlchBank extends Task {

	@Override
	public String toString() {
		return "Banking";
	}

	@Override
	public boolean validate() {
		if (Login.getLoginState() != STATE.INGAME) {
			return false;
		}
		if (org.tribot.api2007.Banking.isBankScreenOpen()) {
			return true;
		}
		for (int i = 0; i < vars.task.getNumSupplies(); i++) {
			ItemCombinerItem item = vars.task.items.get(i);
			int amount = Inventory.getCount(item.getName());
			if (amount != item.getInvAmount()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean execute() {
		Inventory.unselectItem();
		Magic.unselectSpell();
		if (org.tribot.api2007.Banking.isBankScreenOpen() || (ChooseOption.isOpen() ? ChooseOption.select("Bank") : Banking.openBank())) {
			List<String> exceptItems = new LinkedList<>();
			List<Integer> amounts = new ArrayList<>(vars.task.items.size());
			for (int i = 0; i < vars.task.getNumSupplies(); i++) {
				ItemCombinerItem item = vars.task.items.get(i);
				int currentAmount = Inventory.getCount(item.getName());
				if (currentAmount > 0 && currentAmount <= item.getInvAmount()) {
					exceptItems.add(item.getName());
					amounts.add(i < 2 ? (item.getInvAmount() - currentAmount) : 0); // only withdraw secondary items when running out
				}
				else {
					amounts.add(item.getInvAmount());
				}
			}
			if (exceptItems.size() > 0) {
				org.tribot.api2007.Banking.depositAllExcept(exceptItems.toArray(new String[0]));
			}
			else {
				if (!Banking.depositAllAndWait()) {
					return failure("depositing all items");
				}
			}
			if (withdrawItems(Arrays.stream(amounts.toArray(new Integer[0])).mapToInt(Integer::intValue).toArray())) {
				Banking.close();
				vars.noSuppliesCounter = 0;
			}
			else if (++vars.noSuppliesCounter >= 50) { // array of no supplies instead
				if (vars.task.isRestocking) {
					Banking.withdraw(0, "Coins");
					//vars.restockItem = vars.task.items.getInstance(0); // TODO
				}
				else {
					Banking.depositAllAndWait();
					Banking.close();
					vars.noSuppliesCounter = 0;
					throw new OutOfSuppliesException();
				}
			}
		}

		return success();
	}

	private boolean isAtGrandExchange() {
		return Place.Bank.GRAND_EXCHANGE.area.contains(Player.getPosition());
	}

	private boolean withdrawItems(int... amounts) {
		ItemCombinerItem previousItem = null;
		ItemCombinerItem currentItem = null;
		for (int i = 0; i < vars.task.getNumSupplies(); i++) {
			int amount = amounts[i];
			if (amount > 0) {
				previousItem = currentItem;
				currentItem = vars.task.items.get(i);
				if (!org.tribot.api2007.Banking.withdraw(amount, currentItem.getName())) {
					if (previousItem != null) {
						ItemCombinerItem prevItem = previousItem;
						Timing.waitCondition(() -> Inventory.getCount(prevItem.getName()) == prevItem.getInvAmount(), General.random(2323, 3211));
					}
					Logger.getLogger().info("Failed to withdraw " + amount + " " + currentItem.getName());
					return false;
				}
			}
		}
		if (currentItem == null) {
			return true;
		}
		if (previousItem != null) {
			Anticipate.mouseToEntity(Entities.find(ItemEntity::new).nameEquals(previousItem.getName()).getFirstResult());
		}
		ItemCombinerItem currItem = currentItem;
		return Timing.waitCondition(() -> Inventory.getCount(currItem.getName()) == currItem.getInvAmount(), General.random(2012, 3201));
	}


}
