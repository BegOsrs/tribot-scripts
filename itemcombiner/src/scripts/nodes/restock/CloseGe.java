package scripts.nodes.restock;

import scripts.api.game.grandexchange.GrandExchange;
import static scripts.data.ItemCombinerVariables.vars;
import scripts.framework.logic.task.Task;
import scripts.framework.logic.task.TaskPriority;

public class CloseGe extends Task {

	public CloseGe() {
		super(TaskPriority.MEDIUM);
	}

	@Override
	public String toString() {
		return "Closing Grand Exchange";
	}

	@Override
	public boolean validate() {
		return vars.currentRestockItem == null && GrandExchange.isOpen();
	}

	@Override
	public boolean execute() {
		return GrandExchange.close();
	}

}
