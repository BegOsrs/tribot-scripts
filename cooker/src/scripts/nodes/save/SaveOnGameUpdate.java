package scripts.nodes.save;

import java.time.LocalDateTime;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.Player;
import static scripts.data.VariablesCooker.vars;
import scripts.framework.logic.task.Task;
import scripts.api.beg.utils.FileUtil;

public class SaveOnGameUpdate extends Task {

	@Override
	public String toString() {
		return "Saved progress";
	}

	@Override
	public boolean validate() {
		return Login.getLoginState() == STATE.LOGINSCREEN
			&& Login.getLoginResponse().contains("RuneScape has been updated");
	}

	@Override
	public boolean execute() {
		LocalDateTime d = LocalDateTime.now();
		String filename = String.format("%s_%d-%d-%d_(%dh-%dm-%ds)", Player.getRSPlayer().getName(),
			d.getDayOfMonth(), d.getMonthValue(), d.getYear(), d.getHour(), d.getMinute(), d.getSecond());
		if (!FileUtil.saveData("Cooking", filename, vars.tasks)) {
			return failure("saving save file");
		}
		return success();
	}

}
