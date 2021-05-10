package scripts;

import org.tribot.api.Timing;
import org.tribot.api.util.Screenshots;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.script.interfaces.EventBlockingOverride.OVERRIDE_RETURN;
import scripts.api.beg.utils.FileUtil;
import scripts.api.beg.utils.ImageUtil;
import scripts.api.game.grandexchange.GrandExchange;
import scripts.data.ItemCombinerRestockItem;
import scripts.api.beg.logging.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Locale;

import static scripts.data.ItemCombinerVariables.vars;

public class ItemCombinerPainter {

	// Local constants
	private static final Font FONT = new Font("Verdana", Font.BOLD, 11);

	private static final Rectangle TOGGLE_AREA = new Rectangle(477, 344, 20, 14);
	private static final Rectangle SAVE_AREA = new Rectangle(328, 459, 61, 15);
	private static final Rectangle SCREENSHOT_AREA = new Rectangle(390, 459, 61, 15);
	private static final Rectangle RESET_AREA = new Rectangle(452, 459, 61, 15);

	private static final String FILENAME = "%s_%d-%d-%d_(%dh%dm%ds).png";

	// Private instance fields
	private final Image paintImage;
	private final Image noPaintImage;
	private final Image screenshotImage;
	private final Image resetImage;
	private final Image saveImage;
	private final Image highlightImage;
	private final Color DEFAULT_COLOR;
	private boolean showPaint, showHighlight, onPause, onBreak;

	private long timeRan, startTime, startPauseTime, startBreakTime, timeOnPause, timeOnBreak;

	private int startLvl, startXP, currentLvl, gainedLvl, gainedXP, xpToLevel, xpPerHour, numItems, numItemsTask,
		supplyPerHour, amount, amountLeft, lastResetAmount, highlightX, totalGainedXP, totalGainedLvls;

	private String status;

	private long endBreakTime;

	public ItemCombinerPainter(Color defaultColor) {
		this.DEFAULT_COLOR = defaultColor;
		this.startTime = System.currentTimeMillis();
		this.status = "Initializing";
		this.paintImage = ImageUtil.getImageFromURL("https://i.imgur.com/s5HUgA8.png");
		this.noPaintImage = ImageUtil.getImageFromURL("https://i.imgur.com/edv60Ce.png");
		this.screenshotImage = ImageUtil.getImageFromURL("https://i.imgur.com/ek6JoMV.png");
		this.resetImage = ImageUtil.getImageFromURL("https://i.imgur.com/C0h9WYb.png");
		this.highlightImage = ImageUtil.getImageFromURL("https://i.imgur.com/uPMKATU.png");
		this.saveImage = ImageUtil.getImageFromURL("https://i.imgur.com/TPWGpu0.png");
	}

	public void reset() {
		lastResetAmount = numItems;
		startTime = System.currentTimeMillis();
		startLvl = vars.task.skill == null ? 0 : Skills.getActualLevel(vars.task.skill);
		startXP = vars.task.skill == null ? 0 : Skills.getXP(vars.task.skill);
		timeOnBreak = 0L;
		timeOnPause = 0L;
	}

	public void onPaint(Graphics2D g2D) {
		if (onBreak) {
			status = "On break for " + Timing.msToString(endBreakTime - System.currentTimeMillis());
		}
		if (onPause) {
			status = "On pause for " + Timing.msToString(System.currentTimeMillis() - startPauseTime);
		}
		if (!onBreak && !onPause) {
			timeRan = this.getTimeRan();
			gainedXP = totalGainedXP + this.getGainedXp();
			xpToLevel = vars.task.skill == null ? 0 : Skills.getXPToNextLevel(vars.task.skill);
			currentLvl = vars.task.skill == null ? 0 : Skills.getCurrentLevel(vars.task.skill);
			xpPerHour = (int) (gainedXP * 3600000d / timeRan);
			supplyPerHour = (int) ((numItems - lastResetAmount) * 3600000d / timeRan);
			gainedLvl = totalGainedLvls + currentLvl - startLvl;
			amountLeft = amount - numItemsTask;
		}
		if (showPaint) {
			g2D.setFont(FONT);
			g2D.setColor(DEFAULT_COLOR);
			g2D.drawImage(paintImage, -15, 295, null);
			g2D.drawImage(saveImage, 328, 459, null);
			g2D.drawImage(screenshotImage, 390, 459, null);
			g2D.drawImage(resetImage, 452, 459, null);
			g2D.drawImage(showHighlight ? highlightImage : null, highlightX, 459, null);
			g2D.drawString("Items: " + (numItems - lastResetAmount) + " (" + supplyPerHour + "/Hour)", 15, 385);
			if (vars.task.skill != null) {
				g2D.drawString("Exp gained: " + gainedXP + " (" + xpPerHour + "/Hour)", 15, 405);
				g2D.drawString("Exp to next level: " + xpToLevel, 15, 425);
				g2D.drawString("Lvls gained: " + gainedLvl + " (Current Lvl: " + currentLvl + ")", 15, 445);
			}
			g2D.drawString("Inventory timeout: " + vars.task.inventoryTimeout + " ms", 250, 385);
			g2D.drawString("Interface ids: " + vars.task.masterId + ", " + vars.task.childId + ", "
				+ vars.task.componentId, 250, 405);
			g2D.drawString("Task: " + (amountLeft == 0 ? "None" : (amountLeft > 1000000000 ? "+oo" : amountLeft) + " "
				+ vars.task.getProduct().getName() + (vars.task.stopLevel > 0 ? ", L " + vars.task.stopLevel : "")), 250, 425);
			g2D.drawString("Status: " + status, 8, 472);
			if (vars.task.isRestocking) {
				g2D.drawString("Bank screen open: " + Banking.isBankScreenOpen(), 0, 60);
				g2D.drawString("Grand Exchange screen open: " + GrandExchange.isOpen(), 0, 80);
				g2D.drawString("Restock Items list:", 0, 100);
				int i = 110;
				for (ItemCombinerRestockItem item : vars.restockItems.values()) {
					g2D.drawString("Name: " + item.getItem().getName() + "; Price: " + item.getPrice() + " ", 0, i);
					i += 20;
				}
			}
			g2D.drawString("Runtime: " + Timing.msToString(timeRan), 370, 455);
		}
		else {
			g2D.drawImage(noPaintImage, -15, 295, null);
		}
	}

	public void incNumItems(int count) {
		this.numItems += count;
		this.numItemsTask += count;
	}

	public int getAmountLeft() {
		return amount - numItemsTask;
	}

	public void printProgress() {
		Logger.getLogger().info("   - Time ran: " + Timing.msToString(System.currentTimeMillis() - startTime));
		Logger.getLogger().info("   - XP gained: " + getGainedXp() + " (" + (int) (getGainedXp() * 3600000d / getTimeRan()) + "/Hour)");
		Logger.getLogger().info("   - Items made: " + numItems + " (" + (int) (numItems * 3600000d / timeRan) + "/Hour)");
	}

	private long getTimeRan() {
		return System.currentTimeMillis() - startTime - timeOnBreak - timeOnPause;
	}

	private int getGainedXp() {
		return vars.task.skill == null ? 0 : Skills.getXP(vars.task.skill) - startXP;
	}

	public void setCurrentTask() {
		amount = vars.task.getAmountLeft();
		totalGainedLvls += gainedLvl;
		totalGainedXP += gainedXP;
		startLvl = vars.task.skill == null ? 0 : Skills.getActualLevel(vars.task.skill);
		startXP = vars.task.skill == null ? 0 : Skills.getXP(vars.task.skill);
		numItemsTask = 0;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	//TODO improve code
	public OVERRIDE_RETURN overrideMouseEvent(MouseEvent e) {
		try {
			Point point = e.getPoint();
			int id = e.getID();
			if (TOGGLE_AREA.contains(point)) {
				if (id == MouseEvent.MOUSE_CLICKED) {
					e.consume();
					showPaint = !showPaint;
					return OVERRIDE_RETURN.DISMISS;
				}
				else if (id == MouseEvent.MOUSE_PRESSED) {
					return OVERRIDE_RETURN.DISMISS;
				}
			}
			else if (this.showPaint && SAVE_AREA.contains(point)) {
				showHighlight = true;
				highlightX = 327;
				if (id == MouseEvent.MOUSE_CLICKED) {
					e.consume();
					String filename = JOptionPane.showInputDialog(new JFrame(), "Insert the filename:", "File name", JOptionPane.PLAIN_MESSAGE);
					if (filename == null || filename.isEmpty()) {
						Logger.getLogger().error("Unable to save: Invalid file name.");
					}
					else {
						FileUtil.saveData("Combine", filename, vars.tasks);
					}
					return OVERRIDE_RETURN.DISMISS;
				}
				else if (id == MouseEvent.MOUSE_PRESSED) {
					return OVERRIDE_RETURN.DISMISS;
				}
			}
			else if (this.showPaint && SCREENSHOT_AREA.contains(point)) {
				showHighlight = true;
				highlightX = 390;
				if (id == MouseEvent.MOUSE_CLICKED) {
					e.consume();
					this.takeScreenshot();
					return OVERRIDE_RETURN.DISMISS;
				}
				else if (id == MouseEvent.MOUSE_PRESSED) {
					return OVERRIDE_RETURN.DISMISS;
				}
			}
			else if (this.showPaint && RESET_AREA.contains(point)) {
				showHighlight = true;
				highlightX = 452;
				if (id == MouseEvent.MOUSE_CLICKED) {
					e.consume();
					this.reset();
					return OVERRIDE_RETURN.DISMISS;
				}
				else if (id == MouseEvent.MOUSE_PRESSED) {
					return OVERRIDE_RETURN.DISMISS;
				}
			}
			else {
				showHighlight = false;
			}

			return OVERRIDE_RETURN.PROCESS;

		}
		catch (Exception ex) {
			showHighlight = false;
			return OVERRIDE_RETURN.DISMISS;
		}
	}

	private void takeScreenshot() {
		LocalDateTime d = LocalDateTime.now();
		String characterName = Player.getRSPlayer().getName();
		String fileName = String.format(Locale.US, FILENAME, characterName, d.getDayOfMonth(), d.getMonthValue(),
			d.getYear(), d.getHour(), d.getMinute(), d.getSecond());
		if (Screenshots.take(fileName, true, true)) {
			String currentPath = System.getProperty("user.dir");
			Path defaultPath = Paths.get(currentPath + File.separator + "screenshots" + File.separator + fileName);
			Path myPath = Paths.get(currentPath + File.separator + "beg_scripts" + File.separator + "Combine"
				+ File.separator + characterName + File.separator + "screenshots" + File.separator);
			try {
				Files.createDirectories(myPath);
				Files.move(defaultPath,
					Paths.get(myPath + File.separator + fileName),
					StandardCopyOption.REPLACE_EXISTING);
				Logger.getLogger().info("Saved screenshot at: " + myPath
					+ File.separator + fileName);
			}
			catch (IOException e) {
				Logger.getLogger().info("Saved screenshot at: " + defaultPath);
			}
		}
		else {
			Logger.getLogger().error("Failed to save the screenshot.");
		}
	}

	public void onPause() {
		onPause = true;
		startPauseTime = System.currentTimeMillis();
	}

	public void onResume() {
		onPause = false;
		timeOnPause += System.currentTimeMillis() - startPauseTime;
		status = "";
	}


	public void onBreakStart(long time) {
		onBreak = true;
		startBreakTime = System.currentTimeMillis();
		endBreakTime = startBreakTime + time;
	}

	public void onBreakEnd() {
		onBreak = false;
		timeOnBreak += System.currentTimeMillis() - startBreakTime;
		status = "";
	}

}
