package scripts.nodes.cook;

import java.util.Optional;
import org.tribot.api.Clicking;
import static org.tribot.api.General.random;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSObject;
import scripts.api.game.actions.Action;
import scripts.api.game.interfaces.Interfaces;
import static scripts.data.VariablesCooker.vars;

import scripts.api.game.interfaces.MakeAmount;
import scripts.api.beg.constants.GameAnimation;
import scripts.api.beg.constants.GameInterface;
import scripts.api.beg.constants.GameObject;
import scripts.api.beg.constants.skills.Cooking;
import scripts.api.laniax.entityselector.Entities;
import scripts.api.laniax.entityselector.prefabs.ItemEntity;
import scripts.api.laniax.entityselector.prefabs.ObjectEntity;
import scripts.framework.logic.task.Task;
import scripts.api.beg.utils.Strings;
import scripts.api.beg.antiban.AntiBan;
import scripts.api.beg.anticipate.Anticipate;

public class FireCooker extends Task {

	public FireCooker() {
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
		if (!Entities.find(ItemEntity::new).nameEquals(vars.firstSupply).exists()) {
			return false;
		}
		return Entities.find(ObjectEntity::new).setDistance(1).nameEquals("Fire").tileEquals(vars.fireTile).exists();
	}

	@Override
	public boolean execute() {
		if (!useItemOnFire()) {
			return failure("failed to use item on fire");
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
				!Timing.waitCondition(() -> Player.getAnimation() == GameAnimation.COOKING_ON_FIRE.id, random(1952, 2235));
		}, random(61983, 66056))) {
			return failure("cooking inventory timeout.");
		}
		AntiBan.getAntiBan().generateTrackers((int) (Timing.currentTimeMillis() - startTime), true);
		AntiBan.getAntiBan().sleepReactionTime();
		return success();
	}

	private boolean useItemOnFire() {
		if (GameInterface.COOKING_ON_FIRE.isSubstantiated()) {
			return true;
		}
		RSItem firstItem = Entities.find(ItemEntity::new).nameEquals(vars.firstSupply).getFirstResult();
		RSObject fireObject = Entities.find(ObjectEntity::new).nameEquals(GameObject.FIRE.name).setDistance(1)
			.tileEquals(vars.fireTile).getFirstResult();
		if (!Action.useItemOnObject(firstItem, fireObject)) {
			return false;
		}
		if (!vars.useKeybinds) {
			Anticipate.mouseToEntity(Interfaces.get(GameInterface.CHAT_BOX_MESSAGES.master, GameInterface.CHAT_BOX_MESSAGES.child));
		}
		return Timing.waitCondition(GameInterface.COOKING_ON_FIRE::isSubstantiated, random(3574, 3908));
	}

}
