package scripts.nodes.firemaking;

import java.util.Optional;
import org.tribot.api.Clicking;
import static org.tribot.api.General.random;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSItem;
import scripts.api.game.actions.Action;
import scripts.api.game.inventory.Inventory;
import static scripts.data.VariablesCooker.vars;
import scripts.api.beg.constants.GameAnimation;
import scripts.api.beg.constants.GameItem;;
import scripts.api.beg.constants.GameObject;
import scripts.framework.logic.task.Task;
import scripts.api.beg.utils.Timing;
import scripts.api.beg.antiban.AntiBan;

public class FireMaker extends Task {

	@Override
	public String toString() {
		return "Making fire";
	}

	@Override
	public boolean validate() {
		return Login.getLoginState() == STATE.INGAME
			&& vars.fireArea.contains(Player.getPosition())
			&& Inventory.contains(GameItem.TINDERBOX)
			&& Inventory.contains(vars.logs)
			&& Inventory.contains(vars.firstSupply);
	}

	@Override
	public boolean execute() {
		Optional<RSItem> tinderbox = Inventory.getOne(GameItem.TINDERBOX);
		if (tinderbox.isEmpty()) {
			return failure("tinderbox not found on inventory when attempting to make fire");
		}
		Optional<RSItem> logsItem = Inventory.getOne(vars.logs);
		if (logsItem.isEmpty()) {
			return failure("logs not found on inventory when attempting to make fire");
		}
		vars.fireTile = Player.getPosition();
		if (!Action.useItemOnItem(tinderbox.get(), logsItem.get())) {
			return failure("using tinderbox on logs");
		}
		if (!Timing.waitAnimationActive(GameAnimation.MAKING_FIRE, random(1905, 2985))) {
			return failure("firemaking animation did not start in time");
		}
		anticipateToFire();
		if (!Timing.waitCondition(() -> Objects.isAt(vars.fireTile, GameObject.FIRE.name), random(19502, 24301))) {
			return failure("failed to make fire in time");
		}
		return success();
	}

	private void anticipateToFire() {
		if (AntiBan.getAntiBan().getShouldHover()) {
			Optional<RSItem> supply = Inventory.getOne(vars.firstSupply);
			if (supply.isPresent() && Clicking.click(supply.get())) {
				Clicking.hover(vars.fireTile);
			}
		}
		AntiBan.getAntiBan().resetShouldHover();
	}

}
