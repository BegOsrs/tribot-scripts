package scripts.nodes.bank;

import java.awt.event.KeyEvent;
import static java.lang.Math.min;
import java.util.function.Predicate;
import static org.tribot.api.General.random;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.ext.Doors;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import scripts.api.game.areas.RSMultipleArea;
import scripts.api.game.bank.Banking;
import scripts.api.game.camera.Camera;
import scripts.api.game.inventory.Inventory;
import static scripts.data.VariablesCooker.vars;
import scripts.api.beg.constants.Setting;
import scripts.api.beg.exceptions.OutOfSuppliesException;
import scripts.framework.logic.task.Task;
import scripts.framework.script.TaskScript;
import scripts.api.beg.utils.Timing;
import scripts.api.beg.anticipate.Anticipate;

public class RangeBanker extends Task {

	private final Predicate<RSPlayer> adjacentPlayer;

	public RangeBanker() {
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
		return "Banking";
	}

	@Override
	public boolean validate() {
		return Login.getLoginState() == STATE.INGAME
			&& !Inventory.contains(vars.firstSupply);
	}

	@Override
	public boolean execute() {
		if (!Banking.isInBank()) {
			if (!handleDoor()) {
				return failure("handling door");
			}
			if (!WebWalking.walkTo(vars.rangeSettings.bank.area.getRandomTile())) {
				return failure("walking to bank");
			}
		}
		if (!Banking.openBank()) {
			return failure("opening bank.");
		}
		if (!Banking.depositAllAndWait()) {
			return failure("depositing all items");
		}
		if (!this.withdrawSupplies()) {
			if (++vars.failsafe > TaskScript.CHECK_SUPPLIES_RETRIES) {
				Banking.depositAllAndWait();
				Banking.close();
				throw new OutOfSuppliesException();
			}
			return failure("withdrawing supplies");
		}
		if (vars.useBankEscBtn && Setting.CLOSE_INTERFACE_ESCAPE_BUTTON.isOn()) {
			Keyboard.pressKeys(KeyEvent.VK_ESCAPE);
		}
		else {
			Banking.close();
		}
		if (!Timing.waitCondition(() -> !validate(), random(3023, 3450))) {
			return failure("timeout reached after closing bank");
		}
		vars.failsafe = 0;
		return success();
	}

	private boolean withdrawSupplies() {
		int amountToCook = vars.painter.getAmountToCook();
		return Banking.withdrawAndWait(amountToCook > 25 ? 0 : amountToCook, vars.firstSupply);
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
		RSMultipleArea walkingArea = vars.rangeSettings.walkToArea;
		RSTile randomTile = walkingArea.getRandomTile(0, min(random(4, 6), walkingArea.getAllTiles().length - 1));
		Anticipate.mouseToEntity(randomTile);
		if (!Walking.clickTileMS(randomTile, "Walk here")) {
			return false;
		}
		return Timing.waitCondition(() -> walkingArea.contains(Player.getPosition()), random(1236, 1463));
	}

}
