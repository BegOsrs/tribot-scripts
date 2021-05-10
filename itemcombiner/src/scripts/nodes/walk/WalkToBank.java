package scripts.nodes.walk;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.WebWalking;
import scripts.api.game.bank.Banking;
import static scripts.data.ItemCombinerVariables.vars;
import scripts.api.beg.constants.Place;
import scripts.framework.logic.task.Task;

public class WalkToBank extends Task {

	@Override
	public String toString() {
		return "Walking to the bank";
	}

	@Override
	public boolean validate() {
		return false;
	}

	@Override
	public boolean execute() {
		if (vars.geWalking && !isAtGrandExchange() && !walkToGrandExchange()) {
			return failure("walking to ge");
		}
		if (!vars.geWalking && vars.bankWalking && !Banking.isInBank() && !WebWalking.walkToBank()) {
			return failure("walking to bank");
		}
		return success();
	}

	private boolean isAtGrandExchange() {
		return Place.Bank.GRAND_EXCHANGE.area.contains(Player.getPosition());
	}

	private boolean walkToGrandExchange() {
		return WebWalking.walkTo(Place.Bank.GRAND_EXCHANGE.area.getRandomTile(), this::isAtGrandExchange,
			General.random(104, 203));
	}
}
