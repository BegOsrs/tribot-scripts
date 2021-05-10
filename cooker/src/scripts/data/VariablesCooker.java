package scripts.data;

import java.util.List;
import org.tribot.api.General;
import org.tribot.api2007.types.RSTile;
import scripts.api.game.areas.RSMultipleArea;
import scripts.BegCookerPaint;
import scripts.gui.GUICooker;
import scripts.task.Task;
import scripts.api.beg.constants.GameInterface;

public class VariablesCooker {

	public static final VariablesCooker vars = new VariablesCooker();
	public List<Task> tasks;
	public GUICooker gui;
	public BegCookerPaint painter;
	public int mouseSpeed = General.random(90, 140);
	public boolean saveOnSystemUpdate = true;
	public boolean antibanReaction = false;
	public boolean antibanDebug = true;
	public boolean toogleRoofs = true;
	public boolean useBankEscBtn = true;
	public boolean useKeybinds = true;
	public String firstSupply;
	public int firstSupplyID = -1;
	public String secondSupply;
	public String logs;
	public String product;
	public String keybind;
	public RSTile fireTile;
	public RangeSettings rangeSettings;
	public RSMultipleArea fireArea;
	public int failsafe = 0;
	public String arguments;
	public boolean printDebug = true;
	public GameInterface cookingInterface;
	public boolean noRequiredLevel = false;

	private VariablesCooker() {
	}
}
