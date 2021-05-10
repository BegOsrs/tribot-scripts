package scripts.nodes.walk;

import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSTile;
import static scripts.data.VariablesCooker.vars;
import scripts.framework.logic.task.Task;

public class RoguesDenWalker extends Task {

	private final RSTile safeTile;

	public RoguesDenWalker() {
		safeTile = new RSTile(3043, 4972, 1);
	}

	@Override
	public String toString() {
		return "Moving out of fire";
	}

	@Override
	public boolean validate() {
		return vars.fireTile.equals(Player.getPosition());
	}

	@Override
	public boolean execute() {
		return !Walking.clickTileMS(safeTile, "Walk here") ? failure("walking out of fire") : success();
	}

}
