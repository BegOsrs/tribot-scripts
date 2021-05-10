package scripts.nodes.restock;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api2007.GrandExchange.COLLECT_METHOD;
import org.tribot.api2007.GrandExchange.WINDOW_STATE;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSGEOffer;
import org.tribot.api2007.types.RSGEOffer.STATUS;
import org.tribot.api2007.types.RSGEOffer.TYPE;
import scripts.api.game.grandexchange.GrandExchange;
import static scripts.data.ItemCombinerVariables.vars;
import scripts.api.beg.logging.Logger;
import scripts.framework.logic.task.Task;
import scripts.framework.logic.task.TaskPriority;
import scripts.api.beg.utils.Timing;

public class SellPriceCheck extends Task {

	public SellPriceCheck() {
		super(TaskPriority.HIGH);
	}

	@Override
	public String toString() {
		return "Restocker: checking price (selling item)";
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
		return Inventory.getCount(name) > 0 || GrandExchange.containsOffer(name, 1, 1, TYPE.SELL);
	}

	@Override
	public boolean execute() {
		if (!GrandExchange.isOpen() && !GrandExchange.open()) {
			return false;
		}
		String name = vars.currentRestockItem.getItem().getName();
		int maxPrice = vars.currentRestockItem.getItem().getMaxRestockPrice();
		RSGEOffer sellOffer = GrandExchange.findOffer(name, 1, 1, TYPE.SELL);
		if (sellOffer == null) {
			Logger.getLogger().info("Selling 1 underpriced " + name + " at 1 gp each.");
			if (!org.tribot.api2007.GrandExchange.offer(name, 1, 1, true)) {
				Logger.getLogger().info("Failed to set an offer to sell 1 " + name + " at 1 gp each.");
				return false;
			}
			if (!Timing.waitGrandExchangeOfferStatus(name, 1, 1, TYPE.SELL, STATUS.COMPLETED, General.random(10000, 20000))) {
				Logger.getLogger().info("Failed to sell 1 " + name + " at 1 gp each in valid time.");
				return false;
			}
			sellOffer = GrandExchange.findOffer(name, 1, 1, TYPE.SELL);
			if (sellOffer == null) {
				Logger.getLogger().info("Failed to find sell offer with 1 " + name + " at 1 gp each.");
				return false;
			}
		}
		if (org.tribot.api2007.GrandExchange.getWindowState() == WINDOW_STATE.SELECTION_WINDOW && !Clicking.click(sellOffer)) {
			Logger.getLogger().info("Failed to click on sell offer with 1 " + name + " at 1 gp each.");
			return false;
		}
		if (!Timing.waitCondition(() -> org.tribot.api2007.GrandExchange.getWindowState() == WINDOW_STATE.OFFER_WINDOW, General.random(3045, 5063))) {
			Logger.getLogger().info("Failed to detect Offer Window in valid time.");
			return false;
		}
		if (!org.tribot.api2007.GrandExchange.collectItems(COLLECT_METHOD.BANK, org.tribot.api2007.GrandExchange.getCollectItems())) {
			Logger.getLogger().info("Failed to collect 1 " + name + " and gps to inventory.");
			return false;
		}
		if (!Timing.waitCondition(() -> org.tribot.api2007.GrandExchange.getWindowState() == WINDOW_STATE.SELECTION_WINDOW, General.random(3045, 5063))) {
			Logger.getLogger().info("Failed to detect Selection Window in valid time.");
			return false;
		}
		int sellPrice = sellOffer.getTransferredGP();
		Logger.getLogger().info("Sold 1 " + name + " at " + sellPrice + " gp each.");
		int price = Math.min(sellPrice + General.random(1, 3), maxPrice);
		Logger.getLogger().info("Buying price for " + name + " is " + price + " gp each.");
		vars.currentRestockItem.setPrice(price);
		return true;
	}

}