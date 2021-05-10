package scripts.nodes.failsafe;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;
import scripts.api.laniax.entityselector.Entities;
import scripts.api.laniax.entityselector.prefabs.InterfaceEntity;
import scripts.api.beg.exceptions.NoRequiredLevelException;
import scripts.framework.logic.task.Task;

public class FixLeveling extends Task {

	@Override
	public String toString() {
		return "Checking required level";
	}

	@Override
	public boolean validate() {
		if (!Interfaces.isInterfaceValid(229)) {
			return false;
		}
		RSInterface rsInterface = Entities.find(InterfaceEntity::new).inMasterAndChild(229, 0).getFirstResult();
		if (rsInterface == null) {
			return false;
		}
		String text = rsInterface.getText();
		if (text == null) {
			return false;
		}
		return text.matches("You need.*level.*");
	}

	@Override
	public boolean execute() {
		throw new NoRequiredLevelException();
	}


}
