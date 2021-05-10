package scripts.nodes.failsafe;

import java.util.Optional;
import org.tribot.api.Clicking;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;
import scripts.api.beg.constants.GameInterface;
import scripts.framework.logic.task.Task;
import scripts.framework.logic.task.TaskPriority;

public class FixBank extends Task {

	public FixBank() {
		super(TaskPriority.HIGH);
	}

	public String toString() {
		return "Fixing missclick";
	}

	@Override
	public boolean validate() {
		return Interfaces.isInterfaceValid(GameInterface.BANK_COLLECT.master);
	}

	@Override
	public boolean execute() {
		Optional<RSInterface> osrsInterface = GameInterface.BANK_COLLECT_CLOSE.find();
		if (osrsInterface.isEmpty()) {
			return failure("collect interface was not found");
		}
		boolean click = Clicking.click(osrsInterface.get());
		if (!click) {
			return failure("clicking interface");
		}
		return success();
	}

}
