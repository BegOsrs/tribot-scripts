package scripts;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Arguments;
import org.tribot.script.interfaces.Breaking;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.EventBlockingOverride;
import org.tribot.script.interfaces.Painting;
import org.tribot.script.interfaces.Pausing;
import org.tribot.script.interfaces.Starting;
import scripts.data.ItemCombinerItem;
import scripts.data.ItemCombinerTask;
import scripts.api.beg.utils.Strings;
import scripts.nodes.alch.AlchItems;
import scripts.nodes.bank.AlchBank;
import scripts.nodes.bank.Bank;
import scripts.nodes.combine.Combine;
import scripts.nodes.failsafe.FixBank;
import scripts.nodes.failsafe.FixLeveling;
import scripts.nodes.failsafe.SaveOnGameUpdate;
import scripts.nodes.restock.BuyPriceCheck;
import scripts.nodes.restock.CloseGe;
import scripts.nodes.restock.Restock;
import scripts.nodes.restock.SellPriceCheck;
import scripts.nodes.walk.WalkToBank;
import scripts.api.beg.exceptions.NoRequiredLevelException;
import scripts.api.beg.exceptions.OutOfSuppliesException;
import scripts.api.beg.logging.Logger;
import scripts.framework.logic.task.Task;
import scripts.framework.logic.task.TaskSet;
import scripts.framework.listeners.inventory.InventoryListener;
import scripts.framework.listeners.inventory.InventoryObserver;
import scripts.api.beg.antiban.AntiBan;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import static scripts.data.ItemCombinerVariables.vars;

@ScriptManifest(
	authors = "Beg",
	category = "Tools",
	name = "Beg Item Combiner",
	version = 1.1,
	description = "BEG Item Combiner;"
		+ "task-based system;"
		+ "Ability to combine two items together;"
		+ "Useful to a variety of bank-standing skills;"
		+ "Simple-to-use gui;"
		+ "Informative Painting;"
		+ "Ability to save/load progress;")
public class ItemCombinerMain extends Script implements InventoryListener, Painting, EventBlockingOverride, Breaking,
	Pausing, Ending, Starting, Arguments {

	@Override
	public void onStart() {
		Logger.getLogger().info("Script Started: Beg Item Combiner");
		vars.painter = new ItemCombinerPainter(Color.WHITE);
		InventoryObserver inventoryObserver = new InventoryObserver(null, () -> !Banking.isBankScreenOpen() && !isPaused() && !isOnBreak());
		inventoryObserver.addListener(this);
		inventoryObserver.start();
	}

	@Override
	public void run() {
		setUserInput();
		setUserOptions();
		performTasks();
	}

	private void setUserInput() {
		try {
			vars.tasks = this.parseArguments();
		}
		catch (Exception e) {
			e.printStackTrace();
			vars.gui = new ItemCombinerGUI();
			while (!vars.gui.isInputOver()) {
				sleep(500);
			}
			vars.mouseSpeed = vars.gui.getMouseSpeed();
			vars.saveOnUpdate = vars.gui.isSaveOnUpdateEnable();
			vars.antibanReaction = vars.gui.isReactionEnable();
			vars.antibanDebug = vars.gui.isPrintDebugEnable();
			vars.useEscBtn = vars.gui.useEscButton();
			vars.bankWalking = vars.gui.isBankWalkerEnable();
			vars.geWalking = vars.gui.isGeWalkerEnable();
			vars.antibanMouseOffscreen = vars.gui.isMouseOffscreenEnable();
			if (vars.tasks.isEmpty()) {
				vars.arguments = vars.gui.getScriptArguments();
				try {
					vars.tasks = this.parseArguments();
				}
				catch (Exception ex) {
					Logger.getLogger().error("Malformed script argument.");
				}
			}
		}
	}

	private void setUserOptions() {
		Logger.getLogger().info("Options:");
		Logger.getLogger().info("  - Mouse speed: " + vars.mouseSpeed);
		Mouse.setSpeed(vars.mouseSpeed);
		Logger.getLogger().info("  - Save on system update: " + vars.saveOnUpdate);
		Logger.getLogger().info("  - Print antiban debug: " + vars.antibanDebug);
		AntiBan.getAntiBan().setPrintDebug(vars.antibanDebug);
		Logger.getLogger().info("  - Enable antiban reaction: " + vars.antibanReaction);
		AntiBan.getAntiBan().setEnableReactionSleep(vars.antibanReaction);
		Logger.getLogger().info("  - Use esc button: " + vars.useEscBtn);
	}

	private void performTasks() {
		while (this.hasTasksToDo()) {
			Interfaces.closeAll();
			this.resetVars();
			this.doTask();
			General.sleep(250, 500);
		}
		Logger.getLogger().info("No more tasks to be done.");
	}

	private boolean hasTasksToDo() {
		Logger.getLogger().info("Checking for tasks...");
		return !vars.tasks.isEmpty();
	}

	private void doTask() {
		ItemCombinerTask task = vars.task = vars.tasks.get(0);
		Logger.getLogger().info("task: " + task.toString());
		Logger.getLogger().info("Task options: \n"
			+ "- alching product: " + task.isAlchingProduct + ";\n"
			+ "- restocking from G.E: " + task.isRestocking);
		ItemCombinerPainter painter = vars.painter;
		painter.setCurrentTask();
		TaskSet nodes = new TaskSet();
		nodes.add(new Combine());
		nodes.add(new FixBank());
		nodes.add(new WalkToBank());
		if (task.isAlchingProduct) {
			nodes.add(new Bank());
			nodes.add(new AlchItems());
		}
		else {
			nodes.add(new AlchBank());
		}
		if (task.isRestocking) {
			nodes.addAll(new BuyPriceCheck(), new SellPriceCheck(), new Restock(), new CloseGe());
		}
		nodes.add(new FixLeveling());
		if (vars.saveOnUpdate) {
			nodes.add(new SaveOnGameUpdate());
		}
		try {
			while (!this.taskComplete()) {
				Task node = nodes.getValid().orElse(null);
				if (node != null) {
					painter.setStatus(node.toString());
					node.execute();
				}
				General.sleep(22, 44);
			}
		}
		catch (OutOfSuppliesException | NoRequiredLevelException e) {
			Logger.getLogger().error(e.getMessage());
			Logger.getLogger().error((task.amount - task.getAmountLeft()) + " out of " + task.amount + " " + task.getProduct().getName() + " made.");
		}
		vars.tasks.remove(0);
	}

	private boolean taskComplete() {
		if (vars.task.stopLevel > 0 && vars.task.skill != null && Skills.getCurrentLevel(vars.task.skill) >= vars.task.stopLevel) {
			Logger.getLogger().info("Level " + vars.task.stopLevel + " was reached.");
			return true;
		}
		if (vars.task.getAmountLeft() <= 0) {
			Logger.getLogger().info("Task " + vars.task.amount + " " + vars.task.getProduct().getName() + " is completed.");
			return true;
		}
		return false;
	}

	@Override
	public void onEnd() {
		if (vars.painter != null) {
			vars.painter.printProgress();
		}
		if (vars.gui != null) {
			vars.gui.close();
		}
		Logger.getLogger().info("Script Ended: BEG Item Combiner");
	}

	@Override
	public void onResume() {
		vars.painter.onResume();
	}

	@Override
	public void onPause() {
		vars.painter.onPause();
	}

	@Override
	public void onBreakStart(long time) {
		vars.painter.onBreakStart(time);
	}

	@Override
	public void onBreakEnd() {
		vars.painter.onBreakEnd();
	}

	@Override
	public OVERRIDE_RETURN overrideKeyEvent(KeyEvent e) {
		return OVERRIDE_RETURN.SEND;
	}

	@Override
	public OVERRIDE_RETURN overrideMouseEvent(MouseEvent e) {
		return vars.painter != null ? vars.painter.overrideMouseEvent(e) : OVERRIDE_RETURN.PROCESS;
	}

	@Override
	public void onPaint(Graphics g) {
		if (vars.painter != null) {
			vars.painter.onPaint((Graphics2D) g);
		}
	}

	@Override
	public void startInventoryObserver() {

	}

	@Override
	public void inventoryItemGained(int id, int count) {
		RSItemDefinition item = RSItemDefinition.get(id);
		if (item != null && vars.task.getProduct().getName().equalsIgnoreCase(item.getName())) {
			vars.painter.incNumItems(count);
			vars.tasks.get(0).decAmountLeft(count);
		}
	}

	@Override
	public void passArguments(HashMap<String, String> args) {
		String customInput = args.get("custom_input");
		String autoStart = args.get("autostart");
		vars.arguments = autoStart != null ? autoStart : customInput;
	}

	private List<ItemCombinerTask> parseArguments() {
		String[] tasksString = vars.arguments.split("\\|");
		List<ItemCombinerTask> tasks = new ArrayList<>(tasksString.length);
		for (String task : tasksString) {
			task = task.trim().toLowerCase();
			SKILLS skill = null;
			List<ItemCombinerItem> items = new LinkedList<>();
			int masterId = -1;
			int childId = -1;
			int componentId = -1;
			int amount = -1;
			int stopLevel = -1;
			int inventoryTimeout = General.random(59053, 65302);
			boolean isAlchingProduct = false;
			boolean isRestocking = false;
			for (String args : task.split(",")) {
				args = args.trim();
				String[] keyValuePair = args.split("=");
				String field = keyValuePair[0].trim();
				String value = Strings.capitalize(keyValuePair[1].trim());

				switch (field) {
					case "skill":
						skill = SKILLS.valueOf(value);
						break;
					case "item":
						String[] item = value.split(Pattern.quote("-"));
						ItemCombinerItem i = new ItemCombinerItem(Strings.capitalize(item[1]), Integer.parseInt(item[0]),
							Integer.parseInt(item[2]), Integer.parseInt(item[3]), Integer.parseInt(item[4]));
						items.add(i);
						break;
					case "master_interface_id":
						masterId = Integer.parseInt(value);
						break;
					case "child_interface_id":
						childId = Integer.parseInt(value);
						break;
					case "component_interface_id":
						componentId = Integer.parseInt(value);
						break;
					case "amount":
						amount = Integer.parseInt(value);
						if (amount <= 0) {
							amount = Integer.MAX_VALUE;
						}
						break;
					case "stop_level":
						stopLevel = Integer.parseInt(value);
						break;
					case "inventory_timeout":
						inventoryTimeout = Integer.parseInt(value);
						break;
					case "mouse_speed":
						vars.mouseSpeed = Integer.parseInt(value);
						break;
					case "save_on_system_update":
						vars.saveOnUpdate = value.equalsIgnoreCase("true");
						break;
					case "antiban_reaction":
						vars.antibanReaction = value.equalsIgnoreCase("true");
						break;
					case "antiban_debug":
						vars.antibanDebug = value.equalsIgnoreCase("true");
						break;
					case "close_with_esc_button":
						vars.useEscBtn = value.equalsIgnoreCase("true");
						break;
					case "auto_bank_walking":
						vars.bankWalking = value.equalsIgnoreCase("true");
						break;
					case "auto_ge_walking":
						vars.geWalking = value.equalsIgnoreCase("true");
						break;
					case "disable_mouse_offscreen":
						vars.antibanMouseOffscreen = value.equalsIgnoreCase("false");
						break;
					case "change_tabs_with_keyboard":
						vars.changeTabsWithKeyboard = value.equalsIgnoreCase("true");
					case "alch":
						isAlchingProduct = value.equalsIgnoreCase("true");
						break;
					case "restock":
						isRestocking = value.equalsIgnoreCase("true");
						break;
					default:
						println("Invalid option \\" + field + "\\");
						break;
				}
			}
			if (items.size() > 0) {
				tasks.add(new ItemCombinerTask(skill, items, masterId, childId, componentId, amount, stopLevel,
					inventoryTimeout, isAlchingProduct, isRestocking));
			}
		}
		return tasks;
	}

	private void resetVars() {
		vars.noSuppliesCounter = 0;
		vars.noCoinsCounter = 0;
		vars.restockItems.clear();
		vars.currentRestockItem = null;
	}

}
