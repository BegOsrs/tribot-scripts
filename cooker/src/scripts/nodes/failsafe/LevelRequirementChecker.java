package scripts.nodes.failsafe;

import scripts.api.beg.constants.GameInterface;
import scripts.api.beg.exceptions.NoRequiredLevelException;
import scripts.framework.logic.task.Task;

public class LevelRequirementChecker extends Task {

	@Override
	public String toString() {
		return "Checking required level";
	}

	@Override
	public boolean validate() {
		return GameInterface.LEVEL_REQUIRED.isSubstantiated()
			&& GameInterface.LEVEL_REQUIRED.textMatches("You need.*level.*");
	}

	@Override
	public boolean execute() {
		throw new NoRequiredLevelException();
	}


}
