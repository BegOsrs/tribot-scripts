package scripts.nodes.alch;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import static org.tribot.api.General.random;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.Magic;

import static scripts.data.ItemCombinerVariables.vars;
import scripts.api.laniax.entityselector.Entities;
import scripts.api.laniax.entityselector.prefabs.ItemEntity;
import scripts.api.beg.exceptions.OutOfSuppliesException;
import scripts.framework.logic.task.Task;
import scripts.api.beg.utils.Timing;
import scripts.api.beg.anticipate.Anticipate;

public class AlchItems extends Task {

	@Override
	public String toString() {
		return "Alching " + vars.task.getProduct().getName();
	}

	@Override
	public boolean validate() {
		if (Login.getLoginState() != STATE.INGAME) {
			return false;
		}
		if (Inventory.getCount(vars.task.getProduct().getName()) < 1) {
			return false;
		}
		if (Inventory.getCount("Nature rune") < 1) {
			return false;
		}
		if (!Equipment.isEquipped("Staff of fire", "Fire battlestaff", "Mystic fire staff")) {
			throw new OutOfSuppliesException("Staff of fire is not equipped!");
		}

		return true;
	}

	@Override
	public boolean execute() {
		scripts.api.game.magic.Magic.unselectSpell();
		if (!TABS.MAGIC.open()) {
			return failure("opening magic tab");
		}
		if (!Magic.selectSpell("High Level Alchemy")) {
			return failure("selecting high alchemy spell");
		}
		ItemEntity items = Entities.find(ItemEntity::new).nameEquals(vars.task.getProduct().getName());
		if (!Timing.waitCondition(() -> GameTab.getOpen() == TABS.INVENTORY, random(3105, 3702))) {
			return failure("opening inventory tab");
		}
		if (!Clicking.click(items.getFirstResult())) {
			return failure("clicking first item");
		}
		if (items.getResults().length <= 1) {
			if (!Timing.waitCondition(() -> GameTab.getOpen() == TABS.MAGIC, random(3105, 3702))) {
				return failure("waiting for magic tab to open");
			}
			if (!TABS.INVENTORY.open()) {
				return failure("opening inventory tab");
			}
			Anticipate.mouseToBank();
			if (!Timing.waitCondition(() -> Inventory.getCount(vars.task.getProduct().getName()) < 1, random(3235, 3607))) {
				return failure("waiting for alchemy to happen");
			}
		}
		General.sleep(1624, 2452);
		return success();
	}

}
