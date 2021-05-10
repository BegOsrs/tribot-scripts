package scripts.nodes.cook;

import java.util.Optional;
import org.tribot.api.Clicking;
import org.tribot.api.General;
import static org.tribot.api.General.random;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Doors;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSObject;
import scripts.api.game.actions.Action;
import scripts.api.game.camera.Camera;
import scripts.api.game.interfaces.Interfaces;
import scripts.api.game.interfaces.MakeAmount;
import scripts.api.game.inventory.Inventory;
import scripts.api.game.objects.Objects;
import static scripts.data.VariablesCooker.vars;
import scripts.api.beg.constants.GameAnimation;
import scripts.api.beg.constants.GameInterface;
import scripts.api.beg.constants.GameObject;
import scripts.api.beg.constants.skills.Cooking;
import scripts.framework.logic.task.Task;
import scripts.api.beg.utils.Strings;
import scripts.api.beg.utils.Timing;
import scripts.api.beg.antiban.AntiBan;
import scripts.api.beg.anticipate.Anticipate;

public class RangeCooker extends Task {

	public RangeCooker() {
		if (vars.product.equalsIgnoreCase(Cooking.Food.KARAMBWAN.getProduct())) {
			vars.cookingInterface = GameInterface.COOKING_KARAMBWAN;
		}
		else {
			vars.cookingInterface = GameInterface.COOKING_ON_RANGE;
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
		if (!Inventory.contains(vars.firstSupply)) {
			return false;
		}
		if (!Objects.existsAny(10, GameObject.COOKING_RANGE, GameObject.RANGE, GameObject.STOVE, GameObject.CLAY_OVEN)) {
			return false;
		}
		return (vars.rangeSettings.rangeArea.contains(Player.getPosition())
			|| (vars.rangeSettings.walkToArea.contains(Player.getPosition()) &&
			!Doors.isDoorAt(vars.rangeSettings.doorTile, false)));
	}

	@Override
	public boolean execute() {
		if (!GameInterface.COOKING_ON_RANGE.isSubstantiated() && !this.useItemOnRange()) {
			return failure("using item on range");
		}
		Optional<RSInterface> cookingInterface = GameInterface.COOKING_ON_RANGE.find();
		if (cookingInterface.isEmpty()) {
			return failure("cooking interface not found");
		}
		if (!Interfaces.isInterfaceSubstantiated(cookingInterface.get())) {
			return failure("cooking interface is not visible");
		}
		if (!Interfaces.selectAmount(GameInterface.COOKING_ON_RANGE.master, MakeAmount.ALL)) {
			return failure("selecting \"All\" amount.");
		}
		if (vars.useKeybinds && !Strings.isEmpty(vars.keybind)) {
			Keyboard.typeString(vars.keybind);
		}
		else if (!Clicking.click(cookingInterface.get())) {
			return failure("clicking on cooking interface.");
		}
		long startTime = Timing.currentTimeMillis();
		if (!Timing.waitCondition(() -> {
			AntiBan.getAntiBan().timedActions();
			return Player.getAnimation() < 0
				&& !Timing.waitCondition(() ->
				GameAnimation.COOKING_ON_FIRE.isActive() || GameAnimation.COOKING_ON_RANGE.isActive(), General.random(2015, 2255));
		}, random(68040, 73050))) {
			return failure("reached range cooking timeout");
		}
		AntiBan.getAntiBan().generateTrackers((int) (Timing.currentTimeMillis() - startTime), true);
		AntiBan.getAntiBan().sleepReactionTime();
		return success();
	}

	private boolean useItemOnRange() {
		Optional<RSObject> range = Objects.findNearest(10, GameObject.COOKING_RANGE, GameObject.RANGE, GameObject.STOVE, GameObject.CLAY_OVEN);
		if (range.isEmpty()) {
			return false;
		}
		if (!Camera.makeVisible(range.get().getPosition())) {
			return failure("turning camera to range");
		}
		Optional<RSItem> firstItem = Inventory.getOne(vars.firstSupply);
		if (firstItem.isEmpty()) {
			return failure("cannot find first supply on inventory");
		}
		if (!Action.useItemOnObject(firstItem.get(), range.get())) {
			return failure("using " + vars.firstSupply + " on range");
		}
		if (!vars.useKeybinds) {
			Anticipate.mouseToInterface(GameInterface.CHAT_BOX_MESSAGES);
		}
		if (!Timing.waitSubstantiatedInterface(GameInterface.COOKING_ON_RANGE, General.random(6745, 7012))) {
			return failure("cooking interface didn't show up in time");
		}
		return true;
	}

}
