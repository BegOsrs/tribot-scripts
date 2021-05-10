package scripts.data;

import java.io.Serializable;
import java.util.List;
import static org.tribot.api.General.random;
import org.tribot.api2007.Skills.SKILLS;

public class ItemCombinerTask implements Serializable {

	private static final long serialVersionUID = 1L;

	public final SKILLS skill;
	public final List<ItemCombinerItem> items;
	public final int masterId;
	public final int childId;
	public final int componentId;
	public final int amount;
	public final int stopLevel;
	public final String stopLevelString;
	public final int inventoryTimeout;
	public final boolean isAlchingProduct;
	public final boolean isRestocking;
	private int amountLeft;

	public ItemCombinerTask() {
		this(null, null, -1, -1, -1, -1, -1, random(59053, 65302), false, false);
	}

	public ItemCombinerTask(SKILLS skill, List<ItemCombinerItem> items,
							int masterId, int childId, int componentId,
							int amount, int stopLevel, int inventoryTimeout,
							boolean isAlchingProduct, boolean isRestocking) {
		this.skill = skill;
		this.items = items;
		this.masterId = masterId;
		this.childId = childId;
		this.componentId = componentId;
		this.amount = amount;
		this.amountLeft = amount;
		this.stopLevel = stopLevel;
		this.stopLevelString = stopLevel > 0 ? "Lvl " + stopLevel : "";
		this.inventoryTimeout = inventoryTimeout;
		this.isAlchingProduct = isAlchingProduct;
		this.isRestocking = isRestocking;
	}

	public int getAmountLeft() {
		return amountLeft;
	}

	public void setAmountLeft(int amount) {
		this.amountLeft = amount;
	}

	public void decAmountLeft(int count) {
		this.amountLeft -= count;
	}

	public ItemCombinerItem getProduct() {
		return items.get(items.size() - 1);
	}

	public int getNumSupplies() {
		return items.size() - 1;
	}

	@Override
	public String toString() {
		String skillName = skill == null ? "No skill - " : skill.toString() + " -";
		String amountLeft = amount > 1000000000 ? "Infinite" : String.valueOf(amount);
		String stopLevel = this.stopLevel <= 1 ? "" : ", lvl " + this.stopLevel;
		return String.format("%s %s, (%d %s + %d %s -> %d %s), [%d, %d, %d]%s", skillName, amountLeft,
			items.get(0).getInvAmount(), items.get(0).getName(), items.get(1).getInvAmount(), items.get(1).getName(),
			getProduct().getInvAmount(), getProduct().getName(), masterId, childId, componentId, stopLevel);
	}

}
