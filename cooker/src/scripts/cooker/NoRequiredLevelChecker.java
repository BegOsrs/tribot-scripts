package scripts.cooker;

import static scripts.data.VariablesCooker.vars;
import scripts.api.beg.exceptions.NoRequiredLevelException;
import scripts.framework.logic.task.Task;

public class NoRequiredLevelChecker extends Task {

	@Override
	public String toString() {
		return "Checking required level";
	}

	@Override
	public boolean validate() {
		return vars.noRequiredLevel;
	}

	@Override
	public boolean execute() {
		vars.noRequiredLevel = false;
		throw new NoRequiredLevelException();
	}

}
