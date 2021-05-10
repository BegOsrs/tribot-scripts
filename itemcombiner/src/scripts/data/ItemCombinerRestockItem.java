package scripts.data;

import org.tribot.api.Timing;

public class ItemCombinerRestockItem {

	private final ItemCombinerItem item;
	private final long timeout;
	private int price;

	public ItemCombinerRestockItem(ItemCombinerItem item) {
		this.item = item;
		this.price = 0;
		//this.timeout = Timing.currentTimeMillis() + vars.offerTimeout;
		this.timeout = Timing.currentTimeMillis() + 120000; //TODO
	}

	public ItemCombinerItem getItem() {
		return item;
	}

	public int getPrice() {
		return this.price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public long getTimeout() {
		return timeout;
	}

	public boolean isExpired() {
		return Timing.currentTimeMillis() >= timeout;
	}

	@Override
	public String toString() {
		return "Item - " + item + "  |  Price - " + price;
	}

}
