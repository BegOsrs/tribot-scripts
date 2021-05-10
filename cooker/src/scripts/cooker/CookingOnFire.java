package scripts.cooker;

import java.util.stream.Stream;
import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSTile;
import scripts.api.game.bank.Banking;
import scripts.data.FireSettings;
import static scripts.data.VariablesCooker.vars;
import scripts.nodes.bank.FireBanker;
import scripts.nodes.cook.FireCooker;
import scripts.nodes.firemaking.FireMaker;
import scripts.nodes.walk.FireWalker;

public class CookingOnFire extends Cooking {

	public CookingOnFire() {
		this.setLocation();
		super.addNodes(new FireBanker(), new FireWalker(), new FireMaker(), new FireCooker());
	}

	private void setLocation() {  //TODO optimize code
		// Check if player is at fire area
		RSTile playerPos = Player.getPosition();
		for (FireSettings settings : FireSettings.values()) {
			if (settings.getFireArea().contains(playerPos)) {
				vars.fireArea = settings.getFireArea();
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
		for (FireSettings settings : FireSettings.values()) {
			if (settings.getBank().area.contains(playerPos)) {
				vars.fireArea = settings.getFireArea();
				return;
			}

		}
		Stream.of(FireSettings.values()).forEach(value -> System.out.println(value.name()));

		throw new RuntimeException("Only the places above are supported.");
	}

}
