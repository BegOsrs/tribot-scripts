package scripts.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.tribot.api.General;
import scripts.ItemCombinerGUI;
import scripts.ItemCombinerPainter;
import scripts.api.beg.async.ACamera;

public class ItemCombinerVariables {

	public static final ItemCombinerVariables vars = new ItemCombinerVariables();
	public String arguments;
	public ItemCombinerGUI gui;
	public ItemCombinerPainter painter;
	public ACamera aCamera;
	// Option settings
	public int mouseSpeed;
	public boolean saveOnUpdate;
	public boolean antibanReaction;
	public boolean antibanDebug;
	public boolean useEscBtn;
	public boolean bankWalking;
	public boolean geWalking;
	public boolean antibanMouseOffscreen;
	public boolean changeTabsWithKeyboard;
	// task settings
	public List<ItemCombinerTask> tasks;
	public ItemCombinerTask task;
	// auxiliar variables
	public int noSuppliesCounter;
	public int noCoinsCounter;
	public Map<String, ItemCombinerRestockItem> restockItems;
	public ItemCombinerRestockItem currentRestockItem;

	private ItemCombinerVariables() {
		arguments = "";
		gui = null;
		painter = null;
		aCamera = null;

		mouseSpeed = General.random(90, 145);
		saveOnUpdate = true;
		antibanReaction = false;
		antibanDebug = true;
		useEscBtn = true;
		bankWalking = true;
		geWalking = false;
		antibanMouseOffscreen = false;
		changeTabsWithKeyboard = false;

		tasks = new LinkedList<>();
		task = new ItemCombinerTask();

		noSuppliesCounter = 0;
		noCoinsCounter = 0;
		restockItems = new HashMap<>(5);
		currentRestockItem = null;
	}

}