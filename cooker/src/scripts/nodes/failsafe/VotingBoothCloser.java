package scripts.nodes.failsafe;

import scripts.api.beg.constants.GameInterface;
import scripts.framework.logic.task.Task;

public class VotingBoothCloser extends Task {

	@Override
	public String toString() {
		return "Closing voting booth";
	}

	@Override
	public boolean validate() {
		return GameInterface.VOTING_BOOTH.isSubstantiated();
	}

	@Override
	public boolean execute() {
		return !GameInterface.VOTING_BOOTH_CLOSE.click() ? failure("clicking close voting booth interface") : success();
	}

}
