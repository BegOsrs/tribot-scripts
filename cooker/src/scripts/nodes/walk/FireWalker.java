package scripts.nodes.walk;

import static org.tribot.api.General.random;
import org.tribot.api.Timing;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSTile;
import scripts.api.game.inventory.Inventory;
import static scripts.data.VariablesCooker.vars;
import scripts.api.beg.constants.GameItem;;
import scripts.api.beg.constants.GameObject;
import scripts.framework.logic.task.Task;
import scripts.api.beg.anticipate.Anticipate;

public class FireWalker extends Task {

	@Override
	public String toString() {
		return "Moving to fire area";
	}

	@Override
	public boolean validate() {
		return Inventory.contains(vars.firstSupply)
			&& Inventory.contains(vars.logs)
			&& Inventory.contains(GameItem.TINDERBOX)
			&& !vars.fireArea.contains(Player.getPosition());
	}

	@Override
	public boolean execute() {
		RSTile randomTile = vars.fireArea.getRandomTile();
		if (Objects.isAt(randomTile, GameObject.FIRE.name)) {
			return failure("a fire is already at position " + randomTile);
		}
		if (!Walking.walkTo(randomTile)) {
			return failure("walking to fire tile");
		}
		Anticipate.mouseToItem(GameItem.TINDERBOX.name);
		if (!Timing.waitCondition(() -> vars.fireArea.contains(Player.getPosition()), random(9834, 12434))) {
			return failure("walking to fire area");
		}
		return true;
	}

}
