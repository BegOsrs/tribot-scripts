package scripts.nodes.bank;

import org.tribot.api.General;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.Player;
import org.tribot.api2007.WebWalking;
import scripts.api.game.bank.Banking;
import scripts.api.game.inventory.Inventory;
import scripts.api.game.magic.Magic;
import scripts.data.ItemCombinerItem;
import scripts.data.ItemCombinerRestockItem;
import scripts.api.beg.constants.Place;
import scripts.api.laniax.entityselector.Entities;
import scripts.api.laniax.entityselector.prefabs.ItemEntity;
import scripts.api.beg.exceptions.OutOfSuppliesException;
import scripts.api.beg.logging.Logger;
import scripts.framework.logic.task.Task;
import scripts.api.beg.utils.Timing;
import scripts.api.beg.anticipate.Anticipate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static scripts.data.ItemCombinerVariables.vars;

public class Bank extends Task {

	private ItemCombinerItem currentWithdraw;
	private ItemCombinerItem previousWithdraw;

	public Bank() {
		this.currentWithdraw = null;
		this.previousWithdraw = null;
	}

	@Override
	public String toString() {
		return "Banking";
	}

	@Override
	public boolean validate() {
		if (Login.getLoginState() != STATE.INGAME) {
			return false;
		}
		// Always bank when screen is open (to close it)
		if (Banking.isBankScreenOpen()) {
			return true;
		}
		if (!isAtGrandExchange() || !Banking.isInBank()) {
			return false;
		}
		// Bank when it needs to withdraw secondary supplies (nature and/or fire runes)
		for (int i = 2; i < vars.task.getNumSupplies(); i++) {
			ItemCombinerItem item = vars.task.items.get(i);
			if (!Inventory.contains(item.getName())) {
				return true;
			}
		}
		// If not, then dont bank when natures+fires+product are on inventory
		if (Inventory.contains(vars.task.getProduct().getName())) {
			return false;
		}
		// Check if it needs to bank to withdraw/deposit primary supplies
		for (int i = 0; i < 2; i++) {
			ItemCombinerItem item = vars.task.items.get(i);
			if (Inventory.getCount(item.getName()) != item.getInvAmount()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean execute() {
		Inventory.unselectItem();
		Magic.unselectSpell();
		if (!Banking.isBankScreenOpen()
			&& !(ChooseOption.isOpen() ? ChooseOption.select("Bank") : Banking.openBank())) {
			Logger.getLogger().info("Failed to open bank screen.");
			return false;
		}
		// Calculate the necessary items and amounts to withdraw
		List<String> exceptItems = new LinkedList<>();
		List<Integer> amounts = new ArrayList<>(vars.task.items.size());
		for (int i = 0; i < vars.task.getNumSupplies(); i++) {
			ItemCombinerItem item = vars.task.items.get(i);
			int currentAmount = Inventory.getCount(item.getName());
			if (currentAmount > 0 && currentAmount <= item.getInvAmount()) {
				exceptItems.add(item.getName());
				// only withdraw secondary items when running out
				amounts.add(i < 2 ? (item.getInvAmount() - currentAmount) : 0);
			}
			else {
				amounts.add(item.getInvAmount());
			}
		}
		// Deposit all unnecessary items
		if (exceptItems.size() > 0) {
			Banking.depositAllExcept(exceptItems.toArray(new String[0]));
		}
		else {
			if (!Banking.depositAllAndWait()) {
				Logger.getLogger().info("Failed to deposit all.");
				return false;
			}
		}
		// Attempt to withdraw all items
		if (!withdrawItems(Arrays.stream(amounts.toArray(Integer[]::new)).mapToInt(Integer::intValue).toArray())) {
			// If we detect no supplies 10 times in a row
			if (vars.noSuppliesCounter >= 10) {
				// and we should restock then start restocker
				if (vars.task.isRestocking) {
					startRestocker();
				}
				// otherwise end task by lack of supplies
				else {
					throw new OutOfSuppliesException();
				}
			}
			return false;
		}
		Banking.close();
		vars.noSuppliesCounter = 0;
		return true;
	}

	private boolean walkToGrandExchange() {
		return WebWalking.walkTo(Place.Bank.GRAND_EXCHANGE.area.getRandomTile(), this::isAtGrandExchange, General.random(104, 203));
	}

	private boolean isAtGrandExchange() {
		return Place.Bank.GRAND_EXCHANGE.area.contains(Player.getPosition());
	}

	private boolean withdrawItems(int... amounts) {
		currentWithdraw = null;
		previousWithdraw = null;
		for (int i = 0; i < vars.task.getNumSupplies(); i++) {
			final int amount = amounts[i];
			if (amount <= 0) {
				continue;
			}
			if (currentWithdraw != null) {
				previousWithdraw = currentWithdraw;
			}
			currentWithdraw = vars.task.items.get(i);
			Logger.getLogger().info("Withdrawing: " + currentWithdraw);
			final int count = Banking.getCount(currentWithdraw.getName());
			if (vars.task.isRestocking && count <= currentWithdraw.getRestockAmount() && !vars.restockItems.containsKey(currentWithdraw.getName())) {
				return startRestocker();
			}
			if (!Banking.withdraw(amount, currentWithdraw.getName())) {
				Logger.getLogger().info("Failed to withdraw " + amount + " " + currentWithdraw.getName() + ".");
				if (previousWithdraw != null) {
					Logger.getLogger().info("Timing for previous withdraw (" + previousWithdraw.getName() + ") to go into inventory.");
					final ItemCombinerItem prevWithdraw = previousWithdraw;
					if (!Timing.waitCondition(() -> Inventory.getCount(prevWithdraw.getName()) == prevWithdraw.getInvAmount(), General.random(2323, 3211))) {
						Logger.getLogger().info("Previous withdraw (" + previousWithdraw.getName() + ") was not detected on inventory in valid time.");
					}
				}
				if (count < 1) {
					vars.noSuppliesCounter++;
					General.sleep(1102, 1205);
				}
				return false;
			}
		}
		if (currentWithdraw == null) { // all items had amount <= 0
			return true;
		}
		Anticipate.mouseToEntity(Entities.find(ItemEntity::new).nameEquals(vars.task.items.get(0).getName()).getFirstResult());
		Logger.getLogger().info("Timing for current withdraw (" + currentWithdraw.getName() + ") to go into inventory.");
		final ItemCombinerItem curWithdraw = currentWithdraw;
		if (!Timing.waitCondition(() -> Inventory.getCount(curWithdraw.getName()) == curWithdraw.getInvAmount(), General.random(2012, 3201))) {
			Logger.getLogger().info("Current withdraw (" + currentWithdraw.getName() + ") was not detected on inventory in valid time.");
			return false;
		}
		Logger.getLogger().info("All items successfully withdrew from bank.");
		return true;
	}

	private boolean startRestocker() {
		Logger.getLogger().info("Starting restocker: " + currentWithdraw);
		if (previousWithdraw != null) {
			Logger.getLogger().info("Timing for previous withdraw to go into inventory.");
			final ItemCombinerItem prevWithdraw = previousWithdraw;
			if (!Timing.waitCondition(() -> Inventory.getCount(prevWithdraw.getName()) == prevWithdraw.getInvAmount(), General.random(3012, 5201))) {
				Logger.getLogger().info("Previous withdraw (" + previousWithdraw.getName() + ") was not detected on inventory in valid time.");
				return false;
			}
		}
		if (Inventory.isFull()) {
			Logger.getLogger().info("Inventory is full so we have to deposit-all.");
			if (!Banking.depositAllAndWait()) {
				System.out.println("Failed to Deposit All.");
				return false;
			}
		}
		if (!Banking.withdrawAndWait(0, "Coins") && Inventory.getCount("Coins") < 1) {
			System.out.println("Failed to withdraw all coins and no coins on inventory were detected.");
			if (++vars.noCoinsCounter >= 10) {
				throw new OutOfSuppliesException("Not enough coins to buy supplies.");
			}
			return false;
		}
		if (!Banking.close()) {
			System.out.println("Failed to close bank.");
			return false;
		}
		System.out.println("Looking for previously unfinished restock...");
		vars.currentRestockItem = vars.restockItems.get(currentWithdraw.getName());
		if (vars.currentRestockItem == null) {
			System.out.println("No unfinished restock was detected. Generating a new one.");
			vars.currentRestockItem = new ItemCombinerRestockItem(currentWithdraw);
		}
		System.out.println("Current restock: " + vars.currentRestockItem);
		return true;
	}

}
