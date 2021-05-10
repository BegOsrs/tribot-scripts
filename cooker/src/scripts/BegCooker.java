package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Options;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Arguments;
import org.tribot.script.interfaces.Breaking;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.EventBlockingOverride;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;
import org.tribot.script.interfaces.Pausing;
import org.tribot.script.interfaces.Starting;
import scripts.cooker.Cooking;
import scripts.cooker.CookingOnFire;
import scripts.cooker.CookingOnInventory;
import scripts.cooker.CookingOnRange;
import scripts.cooker.CookingOnRoguesDen;
import scripts.cooker.Cooking;
import scripts.cooker.CookingOnFire;
import scripts.cooker.CookingOnInventory;
import scripts.cooker.CookingOnRange;
import scripts.cooker.CookingOnRoguesDen;
import static scripts.data.VariablesCooker.vars;
import scripts.gui.GUICooker;
import scripts.task.Task;
import scripts.task.TaskCooker;
import scripts.api.beg.exceptions.NoRequiredLevelException;
import scripts.api.beg.exceptions.OutOfSuppliesException;
import scripts.api.beg.logging.Logger;
import scripts.api.beg.utils.Strings;
import scripts.api.beg.antiban.AntiBan;

@ScriptManifest(
	authors = "Beg",
	category = "Cooking",
	name = "Local BEG cooker",
	description = "AIO task Based Cooking Script. "
		+ "Supports three methods of Cooking: - Fire Cooking (Burthoupe included); - Range Cooking; - Inventory Cooking. "
		+ "Supports all types of fish, all types of pizzas, all types of pies and wines. "
		+ "Most cooking ranges and banks covered. "
		+ "ABCL 10. "
		+ "Simple-to-use gui. "
		+ "Informative Painting. "
		+ "Ability to save/load progress."
)
public class BegCooker extends Script implements MessageListening07, Painting, EventBlockingOverride, Breaking,
	Pausing, Ending, Starting, Arguments {

	@Override
	public void serverMessageReceived(String msg) {
		if (msg.contains("successfully")
			|| msg.contains("You roast")
			|| msg.contains("to the pizza")
			|| msg.contains("accidentally burn")
			|| msg.contains("You mix the")
			|| msg.contains("pastry dough into the pie dish")
			|| msg.contains("You fill the pie with")
			|| msg.contains("chop the tuna")
			|| msg.contains("cook some")) {
			if (vars.painter != null) {
				vars.painter.increaseSuppliesMade(1);
			}
			if (vars.tasks != null) {
				Task task = vars.tasks.get(0);
				if (task != null) {
					task.decreaseAmountLeft(1);
				}
			}
		}
		if (msg.contains("squeeze the grapes")) {
			if (vars.painter != null) {
				vars.painter.increaseSuppliesMade(14);
			}
			if (vars.tasks != null) {
				Task task = vars.tasks.get(0);
				if (task != null) {
					task.decreaseAmountLeft(14);
				}
			}
		}
		vars.noRequiredLevel = msg.matches("You need.*level.*");
	}

	@Override
	public void onStart() {
		Logger.getLogger().info("Script Started: BEG cooker.");
		vars.gui = new GUICooker();
		vars.painter = new BegCookerPaint(vars, Color.BLACK);
	}

	@Override
	public void onEnd() {
		vars.painter.printProgress();
		vars.gui.close();
		AntiBan.getAntiBan().destroy();
		Logger.getLogger().info("Script Ended: BEG cooker.");
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
	public void onPaint(Graphics g) {
		if (vars.painter != null) {
			vars.painter.draw((Graphics2D) g);
		}
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
	public void run() {
		if (this.setUserInput()) {
			this.setUserOptions();
			vars.painter.reset();
			this.executeTasks();
		}
	}

	private boolean setUserInput() {
		try {
			vars.tasks = this.parseArguments();
		}
		catch (Exception e) {
			vars.gui.open();
			while (!vars.gui.inputOver()) {
				sleep(250, 500);
			}

			vars.mouseSpeed = vars.gui.getMouseSpeed();
			vars.saveOnSystemUpdate = vars.gui.isSaveOnUpdateEnable();
			vars.antibanDebug = vars.gui.isPrintDebugEnable();
			vars.antibanReaction = vars.gui.isReactionEnable();
			vars.toogleRoofs = vars.gui.isToggleRoofsEnable();
			vars.useBankEscBtn = vars.gui.useBankEsc();
			vars.useKeybinds = vars.gui.useBindkeys();

			vars.tasks = vars.gui.getTasks();
			if (vars.tasks.isEmpty()) {
				vars.arguments = vars.gui.getArguments();
				try {
					vars.tasks = this.parseArguments();
				}
				catch (Exception ex) {
					Logger.getLogger().error("Malformed script argument.");
				}
			}
		}

		return !vars.tasks.isEmpty();
	}

	private void setUserOptions() {
		if (!Banking.isBankScreenOpen()) {
			Interfaces.closeAll();
		}
		Logger.getLogger().info("Options:");
		Logger.getLogger().info("  - Mouse speed: " + vars.mouseSpeed);
		Logger.getLogger().info("  - Mouse speed: " + vars.mouseSpeed);
		Logger.getLogger().info("  - Save on system update: " + vars.saveOnSystemUpdate);
		Logger.getLogger().info("  - Print antiban debug: " + vars.antibanDebug);
		AntiBan.getAntiBan().setPrintDebug(vars.antibanDebug);
		Logger.getLogger().info("  - Enable antiban reaction: " + vars.antibanReaction);
		AntiBan.getAntiBan().setEnableReactionSleep(vars.antibanReaction);
		Logger.getLogger().info("  - Remove roofs: " + vars.toogleRoofs);
		Options.setRemoveRoofsEnabled(vars.toogleRoofs);
		Logger.getLogger().info("  - Use esc button to close bank: " + vars.useBankEscBtn);
		Logger.getLogger().info("  - Use keyboard to select options: " + vars.useKeybinds);
		if (!Banking.isBankScreenOpen()) {
			Interfaces.closeAll();
		}
	}


	private void executeTasks() {
		Logger.getLogger().info("Tasks:");
		for (Task task : vars.tasks) {
			Logger.getLogger().info("  - " + task);
		}
		while (this.hasTasksToDo()) {
			sleep(1000, 1250);
			this.doTask();
		}
		Logger.getLogger().info("No more tasks to be done.");
	}

	private boolean hasTasksToDo() {
		Logger.getLogger().info("Checking for tasks...");
		return !vars.tasks.isEmpty();
	}

	private void doTask() {
		Task task = vars.tasks.get(0);
		Logger.getLogger().info("Task: " + task);
		vars.painter.setCurrentTask(task);
		try {
			Cooking cooking = null;
			switch (task.getCookingType()) {
				case Task.FIRE_COOKING:
					cooking = new CookingOnFire();
					break;
				case Task.RANGE_COOKING:
					cooking = new CookingOnRange();
					break;
				case Task.MAKE_ITEM_COOKING:
					cooking = new CookingOnInventory();
					break;
				case Task.ROGUES_DEN_COOKING:
					cooking = new CookingOnRoguesDen();
					break;
				default:
					break;
			}
			if (cooking != null) {
				cooking.execute();
			}
		}
		catch (OutOfSuppliesException | NoRequiredLevelException e) {
			Logger.getLogger().error(e.getMessage());
			int amount = task.getAmount();
			int amountDone = amount - task.getAmountLeft();
			String product = task.getProduct();
			Logger.getLogger().error("Only made " + amountDone + " out of " + amount + " " + product + ".");
		}
		vars.tasks.remove(0);
	}

	@Override
	public void passArguments(HashMap<String, String> args) {
		String customInput = args.get("custom_input");
		String autoStart = args.get("autostart");
		vars.arguments = autoStart != null ? autoStart : customInput;
	}

	// type=range, supply=raw shrimps, product=shrimps, amount=1000, stop_level=0 | mouse_speed=100, save_on_system_update=true, antiban_reaction=false, antibug_debug=true, toogle_roofs=true, close_with_esc_button=false
	// type=rogues den, supply=<replace with your supply name>, product=<replace with your product name>, amount=<replace with the amount to cook>, stop_level=<replace with cooking level to stop>
	// type=make item, first_supply=<replace with your first supply name>, second_supply=<replace with your second supply name>, product=<replace with your product name>, amount=<replace with the amount to cook>, stop_level=<replace with cooking level to stop>
	// type=make item, first_supply=pot of flour, second_supply=jug of water, product=pizza scripts.base, amount=1000, stop_level=0
	// type=fire, supply=<replace with your supply name>, product=<replace with your product name>, logs=<replace with your logs name>, amount=<replace with the amount to cook>, stop_level=<replace with cooking level to stop>
	// type=range, supply=<replace with your supply name>, product=<replace with your product name>, amount=<replace with the amount to cook>, stop_level=<replace with cooking level to stop>
	// type=range, supply=raw lobster, product=lobster, amount=0, stop_level=99
	private List<Task> parseArguments() {

		String[] tasksString = vars.arguments.split("\\|");

		List<Task> tasks = new ArrayList<>(tasksString.length);
		for (String task : tasksString) {

			task = task.trim().toLowerCase();

			String type = "";
			String firstSupply = "";
			String secondSupply = "";
			String product = "";
			String logs = "";
			int amount = 0;
			int stopLevel = 0;
			String keybind = "";
			for (String args : task.split(",")) {
				args = args.trim();

				String[] keyValuePair = args.split("=");
				String field = keyValuePair[0].trim();
				String value = Strings.capitalize(keyValuePair[1].trim());

				switch (field) {
					case "type":
						type = value;
						break;
					case "supply":
					case "first_supply":
						firstSupply = value;
						break;
					case "second_supply":
						secondSupply = value;
						break;
					case "product":
						product = value;
						break;
					case "logs":
						logs = value;
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
					case "mouse_speed":
						vars.mouseSpeed = Integer.parseInt(value);
						break;
					case "save_on_system_update":
						vars.saveOnSystemUpdate = value.equalsIgnoreCase("true");
						break;
					case "antiban_reaction":
						vars.antibanReaction = value.equalsIgnoreCase("true");
						break;
					case "antibug_debug":
						vars.antibanDebug = value.equalsIgnoreCase("true");
						break;
					case "toogle_roofs":
						vars.toogleRoofs = value.equalsIgnoreCase("true");
						break;
					case "close_with_esc_button":
						vars.useBankEscBtn = value.equalsIgnoreCase("true");
						break;
					case "select_with_keyboard":
						vars.useKeybinds = value.equalsIgnoreCase("true");
						break;
					case "select_option":
						if (value.equals("1")) {
							keybind = " ";
						}
						else {
							keybind = value;
						}
						break;
					default:
						Logger.getLogger().error("Invalid option \\" + field + "\\");
						break;
				}
			}

			if (!Strings.isEmpty(type)) {
				tasks.add(new TaskCooker(type, firstSupply, secondSupply, product, logs, amount, stopLevel, keybind));
			}
		}
		return tasks;
	}

}
