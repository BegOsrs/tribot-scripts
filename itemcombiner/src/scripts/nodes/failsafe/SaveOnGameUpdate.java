package scripts.nodes.failsafe;

import java.time.LocalDateTime;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.Player;
import static scripts.data.ItemCombinerVariables.vars;
import scripts.framework.logic.task.Task;
import scripts.api.beg.utils.FileUtil;

public class SaveOnGameUpdate extends Task {

	@Override
	public String toString() {
		return "Saved progress";
	}

	@Override
	public boolean validate() {
		return Login.getLoginState() == STATE.LOGINSCREEN && Login.getLoginResponse().contains("RuneScape has been updated");
	}

	@Override
	public boolean execute() {
		LocalDateTime d = LocalDateTime.now();
		String player_name = Player.getRSPlayer().getName();
		String filename = player_name + " " + d.getDayOfMonth() + "-"
			+ d.getMonthValue() + "-" + d.getYear() + " " + "("
			+ d.getHour() + "h" + d.getMinute() + "m" + d.getSecond() + "s)";
		FileUtil.saveData("MakeItem", filename, vars.tasks);
		return success();
	}

}
