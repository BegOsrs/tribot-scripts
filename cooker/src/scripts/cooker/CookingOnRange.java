package scripts.cooker;

import org.tribot.api.General;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Player;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSTile;
import scripts.data.RangeSettings;
import static scripts.data.VariablesCooker.vars;
import scripts.nodes.bank.RangeBanker;
import scripts.nodes.cook.RangeCooker;
import scripts.nodes.walk.RangeWalker;
import scripts.api.beg.logging.Logger;

public class CookingOnRange extends Cooking {

	// Constructor
	public CookingOnRange() {
		this.setRangeSettings();
		super.addNodes(new RangeBanker(), new RangeWalker(), new RangeCooker());
	}

	// Get the range settings
	private void setRangeSettings() {         // TODO optimize code
		// Check if player is close to range
		RSTile playerPos = Player.getPosition();
		for (RangeSettings settings : RangeSettings.values()) {
			if (settings.rangeArea.contains(playerPos) || settings.walkToArea.contains(playerPos)) {
				vars.rangeSettings = settings;
				return;
			}
		}

		// Check if player is at bank, if not walk to it and then check which bank it is
		while (!Banking.isBankScreenOpen()) {
			if (!Banking.isInBank() && !WebWalking.walkToBank()) {
				continue;
			}
			Banking.openBank();
			General.sleep(200);
		}
		playerPos = Player.getPosition();
		for (RangeSettings settings : RangeSettings.values()) {
			if (settings.bank.area.contains(playerPos)) {
				vars.rangeSettings = settings;
				return;
			}
		}

		// Character is not at one of the supported banks. Script has to end.
		Logger.getLogger().info("Start the script at one of the following banks:");
		for (RangeSettings range : RangeSettings.values()) {
			Logger.getLogger().info(range.name());
			General.sleep(250);
		}
		throw new RuntimeException("Not a supported bank.");
	}

}
