package scripts.nodes.cook;


import java.util.Optional;
import org.tribot.api.Clicking;
import org.tribot.api.General;
import static org.tribot.api.General.random;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.api.game.actions.Action;
import scripts.api.game.interfaces.Interfaces;
import scripts.api.game.interfaces.MakeAmount;
import scripts.api.game.inventory.Inventory;
import static scripts.data.VariablesCooker.vars;
import scripts.api.beg.constants.GameAnimation;
import scripts.api.beg.constants.GameInterface;
import scripts.api.beg.constants.GameObject;
import scripts.api.beg.constants.skills.Cooking;
import scripts.api.laniax.entityselector.Entities;
import scripts.api.laniax.entityselector.prefabs.ObjectEntity;
import scripts.framework.logic.task.Task;
import scripts.api.beg.utils.Strings;
import scripts.api.beg.utils.Timing;
import scripts.api.beg.antiban.AntiBan;
import scripts.api.beg.anticipate.Anticipate;


public class RoguesDenCooker extends Task {

	public RoguesDenCooker() {
		vars.fireTile = GameObject.ROGUES_DEN_FIRE.position;
		if (vars.product.equalsIgnoreCase(Cooking.Food.KARAMBWAN.getProduct())) {
			vars.cookingInterface = GameInterface.COOKING_KARAMBWAN;
		}
		else {
			vars.cookingInterface = GameInterface.COOKING_ON_FIRE;
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
		if (!Inventory.contains(vars.firstSupply)) {
			return false;
		}
		return Entities.find(ObjectEntity::new).nameEquals(GameObject.ROGUES_DEN_FIRE.name).tileEquals(vars.fireTile).exists();
	}

	@Override
	public boolean execute() {
		if (!useItemOnFire()) {
			return failure("using item on fire");
		}
		if (vars.fireTile.equals(Player.getPosition())) {
			Walking.clickTileMS(new RSTile(3043, 4972, 1), "Walk here");
		}
		Optional<RSInterface> cookingInterface = GameInterface.COOKING_ON_FIRE.find();
		if (cookingInterface.isEmpty()) {
			return failure("cooking interface not found");
		}
		if (!Interfaces.isInterfaceSubstantiated(cookingInterface.get())) {
			return failure("cooking interface is not visible");
		}
		if (!Interfaces.selectAmount(GameInterface.COOKING_ON_FIRE.master, MakeAmount.ALL)) {
			return failure("selecting \"All\" amount.");
		}
		if (vars.useKeybinds && !Strings.isEmpty(vars.keybind)) {
			Keyboard.typeString(vars.keybind);
		}
		else if (!Clicking.click(cookingInterface.get())) {
			return failure("clicking on cooking interface");
		}
		long startTime = Timing.currentTimeMillis();
		if (!Timing.waitCondition(() -> {
			AntiBan.getAntiBan().timedActions();
			return Player.getAnimation() < 0 &&
				!Timing.waitCondition(GameAnimation.COOKING_ON_FIRE::isActive, random(1984, 2158));
		}, random(69804, 72093))) {
			return failure("reached fire cooking timeout");
		}
		AntiBan.getAntiBan().generateTrackers((int) (Timing.currentTimeMillis() - startTime), true);
		AntiBan.getAntiBan().sleepReactionTime();
		return success();
	}

	private boolean useItemOnFire() {
		if (GameInterface.COOKING_ON_FIRE.isSubstantiated()) {
			return true;
		}
		Optional<RSItem> firstItem = Inventory.getOne(vars.firstSupply);
		if (firstItem.isEmpty()) {
			return false;
		}
		RSObject fireObject = Entities.find(ObjectEntity::new).tileEquals(vars.fireTile).getFirstResult();
		if (!Action.useItemOnObject(firstItem.get(), fireObject)) {
			return false;
		}
		if (!vars.useKeybinds) {
			Anticipate.mouseToInterface(GameInterface.CHAT_BOX_MESSAGES);
		}
		return Timing.waitSubstantiatedInterface(GameInterface.COOKING_ON_FIRE, General.random(4812, 5491));
	}

}
