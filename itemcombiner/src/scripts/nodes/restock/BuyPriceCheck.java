package scripts.nodes.restock;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api2007.GrandExchange.COLLECT_METHOD;
import org.tribot.api2007.GrandExchange.WINDOW_STATE;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSGEOffer;
import org.tribot.api2007.types.RSGEOffer.STATUS;
import org.tribot.api2007.types.RSGEOffer.TYPE;
import org.tribot.api2007.types.RSItem;
import scripts.api.game.grandexchange.GrandExchange;
import static scripts.data.ItemCombinerVariables.vars;
import scripts.api.laniax.entityselector.Entities;
import scripts.api.laniax.entityselector.prefabs.ItemEntity;
import scripts.api.beg.exceptions.OutOfSuppliesException;
import scripts.api.beg.logging.Logger;
import scripts.framework.logic.task.Task;
import scripts.framework.logic.task.TaskPriority;
import scripts.api.beg.utils.Timing;

public class BuyPriceCheck extends Task {

	public BuyPriceCheck() {
		super(TaskPriority.HIGH);
	}

	@Override
	public String toString() {
		return "Restocker: checking price (buying item)";
	}

	@Override
	public boolean validate() {
		if (vars.currentRestockItem == null) {
			return false;
		}
		if (vars.currentRestockItem.getPrice() > 0) {
			return false;
		}
		final String name = vars.currentRestockItem.getItem().getName();
		return Inventory.getCount(name) == 0 || GrandExchange.containsOffer(name, 1, 1, TYPE.BUY);
	}

	@Override
	public boolean execute() {
		final String name = vars.currentRestockItem.getItem().getName();
		final int maxPrice = vars.currentRestockItem.getItem().getMaxRestockPrice();

		// Make sure the inventory has enough coins for the set amount
		RSItem coins = Entities.find(ItemEntity::new).nameEquals("Coins").getFirstResult();
		if (coins == null) {
			Logger.getLogger().info("Failed to buy 1 " + name + ". Reason: coins missing on inventory.");
			vars.currentRestockItem = null;
			return false;
		}
		if (coins.getStack() / maxPrice < 1) {
			System.out.println("Failed to buy 1 " + name + ". Reason: not enough coins on inventory to buy 1 item.");
			if (++vars.noCoinsCounter >= 3) {
				throw new OutOfSuppliesException("Not enough coins to buy supplies.");
			}
			vars.currentRestockItem = null;
			return false;
		}

		if (!GrandExchange.isOpen() && !GrandExchange.open()) {
			return false;
		}

		RSGEOffer buyOffer = GrandExchange.findOffer(name, maxPrice, 1, TYPE.BUY);
		if (buyOffer == null) {
			System.out.println("Buying 1 overpriced " + name + " at maximum price (" + maxPrice + " gp each).");
			if (!GrandExchange.offer(name, maxPrice, 1, false)) {
				System.out.println("Failed to set an offer to buy 1 " + name + " at " + maxPrice + " gp each.");
				return false;
			}
			if (!Timing.waitGrandExchangeOfferStatus(name, maxPrice, 1, TYPE.BUY, STATUS.COMPLETED, General.random(60000, 180000))) {
				System.out.println("Failed to buy 1 " + name + " at " + maxPrice + " gp each in valid time.");
				if (!GrandExchange.abortOffer(name, maxPrice, 1, TYPE.BUY)) {
					System.out.println("Failed to about offer.");
					return false;
				}
				System.out.println("Setting price at maximum price (" + maxPrice + " gp each).");
				vars.currentRestockItem.setPrice(maxPrice);
			}
			buyOffer = GrandExchange.findOffer(name, maxPrice, 1, TYPE.BUY);
			if (buyOffer == null) {
				System.out.println("Failed to find buy offer with 1 " + name + " at " + maxPrice + " gp each.");
				return false;
			}
		}
		if (GrandExchange.getWindowState() == WINDOW_STATE.SELECTION_WINDOW && !Clicking.click(buyOffer)) {
			System.out.println("Failed to click on buy offer with 1 " + name + " at " + maxPrice + " gp each.");
			return false;
		}
		if (!Timing.waitCondition(() -> GrandExchange.getWindowState() == WINDOW_STATE.OFFER_WINDOW, General.random(3045, 5063))) {
			System.out.println("Failed to detect Offer Window in valid time.");
			return false;
		}
		if (!GrandExchange.collectItems(COLLECT_METHOD.ITEMS, GrandExchange.getCollectItems())) {
			System.out.println("Failed to collect items to inventory.");
			return false;
		}
		if (vars.currentRestockItem.getPrice() > 0
			&& !Timing.waitCondition(() -> Inventory.getCount(name) > 0, General.random(2304, 2950))) {
			System.out.println("Failed to detect collected item on inventory in valid time.");
			return false;
		}
		if (!Timing.waitCondition(() -> GrandExchange.getWindowState() == WINDOW_STATE.SELECTION_WINDOW, General.random(3045, 5063))) {
			System.out.println("Failed to detect Selection Window in valid time.");
			return false;
		}

		return true;
	}

}
