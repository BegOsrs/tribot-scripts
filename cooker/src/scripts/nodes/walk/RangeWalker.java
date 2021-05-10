package scripts.nodes.walk;

import org.tribot.api2007.*;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.ext.Doors;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import scripts.api.game.areas.RSMultipleArea;
import scripts.api.game.camera.Camera;
import scripts.api.game.inventory.Inventory;
import scripts.framework.logic.task.Task;
import scripts.api.beg.utils.Timing;
import scripts.api.beg.anticipate.Anticipate;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import static java.lang.Math.min;
import static org.tribot.api.General.random;
import static scripts.data.VariablesCooker.vars;

public class RangeWalker extends Task {

	private final BooleanSupplier atWalkingArea;
	private final Predicate<RSPlayer> adjacentPlayer;

	public RangeWalker() {
		this.atWalkingArea = () -> vars.rangeSettings.walkToArea.contains(Player.getPosition());
		this.adjacentPlayer = player -> {
			RSPlayer myself = Player.getRSPlayer();
			if (player == null || myself == null || player.equals(myself)) {
				return false;
			}
			RSTile myPos = myself.getPosition();
			RSTile otherPos = player.getPosition();
			RSTile doorTile = vars.rangeSettings.doorTile;
			return myPos != null && otherPos != null && (otherPos.equals(doorTile) || otherPos.equals(myPos));
		};
	}

	@Override
	public String toString() {
		return "Walking to Range";
	}

	@Override
	public boolean validate() {
		return Login.getLoginState() == STATE.INGAME
			&& Inventory.contains(vars.firstSupply)
			&& !atRangeArea();
	}

	@Override
	public boolean execute() {
		RSMultipleArea walkToArea = vars.rangeSettings.walkToArea;
		RSTile destination = walkToArea.getRandomTile();
		if (!walkToArea.contains(Player.getPosition())) {
			if (!Walking.clickTileMM(destination, 1)
				|| !Timing.waitCondition(() -> walkToArea.contains(Player.getPosition()), random(3478, 7853))) {
				if (!WebWalking.walkTo(destination, atWalkingArea, random(100, 200))) {
					return failure("walking to " + destination);
				}
			}
		}
		if (!handleDoor()) {
			return failure("failed to handle door.");
		}
		return success();
	}

	private boolean atRangeArea() {
		return vars.rangeSettings.rangeArea.contains(Player.getPosition());
	}

	private boolean handleDoor() {
		RSTile doorTile = vars.rangeSettings.doorTile;
		if (!Doors.isDoorAt(doorTile, false)) {
			return true;
		}
		if (!Camera.makeVisible(doorTile)) {
			return false;
		}
		if (!Doors.handleDoorAt(doorTile, true)) {
			return false;
		}
		if (!Players.exists(adjacentPlayer, 1)) {
			return true;
		}
		// Quickly pass through door because theres a player on adjacent tiles (prevent door closing)
		RSMultipleArea rangeArea = vars.rangeSettings.rangeArea;
		RSTile randomTile = rangeArea.getRandomTile(0, min(random(8, 12), rangeArea.getAllTiles().length - 1));
		Anticipate.mouseToEntity(randomTile);
		if (!Walking.clickTileMS(randomTile, "Walk here")) {
			return false;
		}
		return Timing.waitCondition(this::atRangeArea, random(1250, 1500));
	}

}