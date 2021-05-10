package scripts.nodes.restock;

import java.util.Optional;
import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api2007.Banking;
import org.tribot.api2007.GrandExchange.COLLECT_METHOD;
import org.tribot.api2007.GrandExchange.WINDOW_STATE;
import org.tribot.api2007.types.RSGEOffer;
import org.tribot.api2007.types.RSGEOffer.STATUS;
import org.tribot.api2007.types.RSGEOffer.TYPE;
import org.tribot.api2007.types.RSItem;
import scripts.api.beg.utils.Timing;
import scripts.api.game.grandexchange.GrandExchange;
import scripts.api.game.grandexchange.GrandExchange.CollectMethod;
import scripts.api.game.interfaces.Interfaces;
import scripts.api.game.inventory.Inventory;
import scripts.data.ItemCombinerItem;
import static scripts.data.ItemCombinerVariables.vars;
import scripts.api.beg.constants.GameItem;;
import scripts.api.laniax.entityselector.Entities;
import scripts.api.laniax.entityselector.prefabs.ItemEntity;
import scripts.api.beg.exceptions.OutOfSuppliesException;
import scripts.api.beg.logging.Logger;
import scripts.framework.logic.task.Task;
import scripts.framework.logic.task.TaskPriority;
import scripts.framework.script.TaskScript;

public class Restock extends Task {

	public Restock() {
		super(TaskPriority.HIGH);
	}

	@Override
	public String toString() {
		ItemCombinerItem item = vars.currentRestockItem.getItem();
		return "Restocking " + item.getRestockAmount() + " " + item.getName() + " for " + item.getMaxRestockPrice() + " gp each";
	}

	@Override
	public boolean validate() {
		return vars.currentRestockItem != null
			&& vars.currentRestockItem.getPrice() > 0
			&& !Banking.isBankScreenOpen();
	}

	@Override
	public boolean execute() {
		if (!GrandExchange.isOpen() && !GrandExchange.open()) {
			return false;
		}

		String name = vars.currentRestockItem.getItem().getName();
		int quantity = vars.currentRestockItem.getItem().getRestockAmount();
		int maxPrice = vars.currentRestockItem.getItem().getMaxRestockPrice();
		int price = vars.currentRestockItem.getPrice();

		RSGEOffer offer = GrandExchange.findOffer(name, price, quantity, TYPE.BUY);
		if (offer == null) {
			// Make sure the inventory has enough coins for the set amount
			Optional<RSItem> coins = Inventory.getOne(GameItem.COINS);
			if (coins.isEmpty()) {
				if (++vars.noCoinsCounter >= TaskScript.CHECK_SUPPLIES_RETRIES) {
					throw new OutOfSuppliesException();
				}
				return failure("Failed to buy " + quantity + " " + name + ". Reason: coins missing on inventory.");
			}
			quantity = Math.min(quantity, coins.get().getStack() / price);
			if (quantity < 1) {
				Logger.getLogger().info("Failed to buy " + quantity + " " + name + ". Reason: not enough coins on inventory to buy 1 item.");
				if (++vars.noCoinsCounter >= TaskScript.CHECK_SUPPLIES_RETRIES) {
					throw new OutOfSuppliesException();
				}
				return false;
			}
			if (org.tribot.api2007.GrandExchange.getWindowState() != WINDOW_STATE.SELECTION_WINDOW && !org.tribot.api2007.GrandExchange.goToSelectionWindow(true)) {
				Logger.getLogger().info("Failed to go to selection window.");
				return false;
			}
			if (!org.tribot.api2007.GrandExchange.offer(name, price, quantity, false)) {
				Logger.getLogger().info("Failed to buy " + quantity + " " + name + " at " + price + " gp each.");
				return false;
			}
			vars.restockItems.put(vars.currentRestockItem.getItem().getName(), vars.currentRestockItem);
		}
		else {
			Logger.getLogger().info("An offer for " + name + " at price " + price + " gp each is already set.");
			Logger.getLogger().info("Checking if any item has bought...");
			if (org.tribot.api2007.GrandExchange.getWindowState() != WINDOW_STATE.SELECTION_WINDOW && !org.tribot.api2007.GrandExchange.goToSelectionWindow(true)) {
				Logger.getLogger().info("Failed to go to selection window.");
				return false;
			}
			if (!Clicking.click(offer)) {
				Logger.getLogger().info("Failed to click on offer.");
				return false;
			}
			if (!Timing.waitCondition(() -> org.tribot.api2007.GrandExchange.getWindowState() == WINDOW_STATE.OFFER_WINDOW, General.random(3045, 5063))) {
				Logger.getLogger().info("Failed to detect Offer Window in valid time.");
				return false;
			}
			RSItem[] collectItems = org.tribot.api2007.GrandExchange.getCollectItems();
			if (collectItems.length > 0 && offer.getStatus() != STATUS.CANCELLED) {
				int transferredAmount = collectItems[0].getStack();
				Logger.getLogger().info("And " + transferredAmount + " " + name + " were bought...");
				Logger.getLogger().info("So we can collect the " + transferredAmount + " " + name + " from G.E.");
				if (!GrandExchange.collectItems(COLLECT_METHOD.BANK, collectItems)) {
					Logger.getLogger().info("Failed to collect " + transferredAmount + " " + name + " from bought offer.");
					return false;
				}
			}
			else {
				if (offer.getStatus() != STATUS.CANCELLED) {
					Logger.getLogger().info("None of the " + name + " were bought...");
					if (vars.currentRestockItem.isExpired()) {
						Logger.getLogger().info("So we cancel the offer.");
						if (!GrandExchange.abortOffer(offer)) {
							Logger.getLogger().info("Failed to abort offer.");
							return false;
						}
					}
					else {
						Logger.getLogger().info("Its not time to cancel offer yet.");
						General.sleep(1604, 4059);
						Logger.getLogger().info("Closing G.E.");
						if (!GrandExchange.close()) {
							Logger.getLogger().info("Failed to close G.E.");
							return false;
						}
						long sleepTime = vars.currentRestockItem.getTimeout() - Timing.currentTimeMillis();
						Logger.getLogger().info("Going to wait " + (sleepTime / 1000) + " seconds before rechecking offer.");
						General.sleep(sleepTime);
						return false;
					}
				}
				Logger.getLogger().info("Collecting aborted offer.");
				if (org.tribot.api2007.GrandExchange.getWindowState() == WINDOW_STATE.SELECTION_WINDOW) {
					if (!Timing.waitCondition(() -> Interfaces.isInterfaceSubstantiated(GrandExchange.getCollectInterface()), General.random(1605, 2058))) {
						Logger.getLogger().info("Failed to detect collect interface in time.");
						return false;
					}
				}
				else if (org.tribot.api2007.GrandExchange.getWindowState() == WINDOW_STATE.OFFER_WINDOW) {
					if (!Timing.waitCondition(() -> org.tribot.api2007.GrandExchange.getCollectItems().length > 0, General.random(1605, 2058))) {
						Logger.getLogger().info("Failed to detect collect items in time.");
						return false;
					}
				}
				else {
					if (!org.tribot.api2007.GrandExchange.goToSelectionWindow(true)) {
						Logger.getLogger().info("Failed to go to selection window.");
						return false;
					}
				}
				if (!GrandExchange.collectAll(CollectMethod.INVENTORY)) {
					Logger.getLogger().info("Failed to collect aborted offer to inventory.");
					return false;
				}
				int oldPrice = price;
				price = Math.min((int) (price * General.randomDouble(1.3, 1.4)), maxPrice);
				vars.currentRestockItem.setPrice(price);
				Logger.getLogger().info("Set another offer at increased price (old price was " + oldPrice + " gp each, new price is " + price + " gp each).");
				RSItem coins = Entities.find(ItemEntity::new).nameEquals("Coins").getFirstResult();
				if (coins == null) {
					Logger.getLogger().info("Failed to buy " + quantity + " " + name + ". Reason: coins missing on inventory.");
					vars.currentRestockItem = null;
					return false;
				}
				quantity = Math.min(quantity, coins.getStack() / price);
				if (quantity < 1) {
					Logger.getLogger().info("Failed to buy " + quantity + " " + name + ". Reason: not enough coins on inventory to buy 1 item.");
					vars.currentRestockItem = null;
					return false;
				}
				if (!org.tribot.api2007.GrandExchange.offer(name, price, quantity, false)) {
					Logger.getLogger().info("Failed to buy " + quantity + " " + name + " at " + price + " gp each.");
					return false;
				}
				if (!Timing.waitGrandExchangeOfferStatus(name, price, quantity, TYPE.BUY, STATUS.IN_PROGRESS, General.random(180593, 340382))) { // 2 to 5.5 min
					Logger.getLogger().info("Failed to buy " + quantity + " " + name + " at price " + price + " gp each."); //TODO
					if (price == maxPrice) {
						throw new OutOfSuppliesException("Unable to buy " + quantity + " " + name + " at maximum price (" + price + " gp each).");
					}
					return false;
				}
				if (!Timing.waitCondition(() -> Interfaces.isInterfaceSubstantiated(GrandExchange.getCollectInterface()), General.random(1605, 2058))) {
					Logger.getLogger().info("Collect interface did not show up in valid time.");
					return false;
				}
				offer = GrandExchange.findOffer(name, price, quantity, TYPE.BUY);
				if (!GrandExchange.collectAll(CollectMethod.BANK)) {
					Logger.getLogger().info("Failed to collect items to bank.");
					return false;
				}
			}
			General.sleep(2302, 3044);
			if (!GrandExchange.containsOffer(name, price, quantity, TYPE.BUY)) {
				vars.restockItems.remove(vars.currentRestockItem.getItem().getName());
			}
			vars.currentRestockItem = null;
		}
		if (!GrandExchange.close()) {
			Logger.getLogger().info("Failed to close G.E.");
			return false;
		}
		return true;
	}

}
