package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.tribot.api.Timing;
import org.tribot.api.util.Screenshots;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.script.interfaces.EventBlockingOverride.OVERRIDE_RETURN;
import scripts.data.VariablesCooker;
import scripts.task.Task;
import scripts.api.beg.logging.Logger;
import scripts.api.beg.utils.FileUtil;
import scripts.api.beg.utils.ImageUtil;

public class BegCookerPaint {

	// Local constants
	private static final Font DEFAULT_FONT = new Font("Verdana", Font.BOLD, 11);
	private static final Rectangle TOGGLE_AREA = new Rectangle(497, 322, 22, 21),
		SAVE_AREA = new Rectangle(327, 459, 61, 15),
		SCREENSHOT_AREA = new Rectangle(390, 459, 61, 15),
		RESET_AREA = new Rectangle(452, 459, 61, 15);
	private static final Image PAINT_IMAGE = ImageUtil.getImageFromURL("https://i.imgur.com/RMG5aHQ.png"),
		NO_PAINT_IMAGE = ImageUtil.getImageFromURL("https://i.imgur.com/EMFB0U1.png"),
		SAVE_IMAGE = ImageUtil.getImageFromURL("https://i.imgur.com/ONuGzi7.png"),
		SCREENSHOT_IMAGE = ImageUtil.getImageFromURL("https://i.imgur.com/XsoOrMs.png"),
		RESET_IMAGE = ImageUtil.getImageFromURL("https://i.imgur.com/mD8LVU9.png"),
		HIGHLIGHT_IMAGE = ImageUtil.getImageFromURL("https://i.imgur.com/HbQbRXd.png");
	// Private instance fields
	private final VariablesCooker vars;
	private final Color DEFAULT_COLOR;
	private boolean showPaint, showHighlight, onPause, onBreak;

	private long timeRan, startTime, startPauseTime, startBreakTime,
		timeOnPause, timeOnBreak;

	private int startLvl, startXP, currentLvl, gainedLvl, gainedXP, xpToLevel,
		xpPerHour, totalAmountDone, taskAmountDone, supplyPerHour, amount,
		amountLeft, lastResetAmount, highlightX;

	private String status, product, stopAtLevel;

	public BegCookerPaint(VariablesCooker vars, Color defaultColor) {
		this.vars = vars;
		this.DEFAULT_COLOR = defaultColor;
		this.showPaint = true;
		this.showHighlight = false;
		this.onPause = false;
		this.onBreak = false;
		this.timeRan = 0L;
		this.startTime = System.currentTimeMillis();
		this.startPauseTime = 0L;
		this.startBreakTime = 0L;
		this.timeOnPause = 0L;
		this.timeOnBreak = 0L;
		this.startLvl = Skills.getActualLevel(SKILLS.COOKING);
		this.startXP = Skills.getXP(SKILLS.COOKING);
		this.currentLvl = startLvl;
		this.gainedLvl = 0;
		this.gainedXP = 0;
		this.xpToLevel = Skills.getXPToNextLevel(SKILLS.COOKING);
		this.xpPerHour = 0;
		this.amount = 0;
		this.totalAmountDone = 0;
		this.taskAmountDone = 0;
		this.lastResetAmount = 0;
		this.supplyPerHour = 0;
		this.amountLeft = 0;
		this.highlightX = 0;
		this.status = "Initializing";
		this.product = "";
		this.stopAtLevel = "";
	}

	public void reset() {
		lastResetAmount = totalAmountDone;
		startTime = System.currentTimeMillis();
		startLvl = Skills.getActualLevel(SKILLS.COOKING);
		startXP = Skills.getXP(SKILLS.COOKING);
		timeOnBreak = 0L;
		timeOnPause = 0L;
	}

	public void draw(Graphics2D g2D) {
		if (!onBreak && !onPause) {
			timeRan = this.getTimeRan();
			gainedXP = this.getGainedXp();
			xpToLevel = Skills.getXPToNextLevel(SKILLS.COOKING);
			currentLvl = Skills.getCurrentLevel(SKILLS.COOKING);
			xpPerHour = (int) (gainedXP * 3600000d / timeRan);
			supplyPerHour = (int) ((totalAmountDone - lastResetAmount) * 3600000d / timeRan);
			gainedLvl = currentLvl - startLvl;
			amountLeft = amount - taskAmountDone;
		}

		if (showPaint) {
			g2D.setFont(DEFAULT_FONT);
			g2D.setColor(DEFAULT_COLOR);
			g2D.drawImage(PAINT_IMAGE, -15, 295, null);
			g2D.drawImage(SAVE_IMAGE, 327, 459, null);
			g2D.drawImage(SCREENSHOT_IMAGE, 390, 459, null);
			g2D.drawImage(RESET_IMAGE, 452, 459, null);
			g2D.drawImage(showHighlight ? HIGHLIGHT_IMAGE : null, highlightX, 459, null);

			g2D.drawString("Cooked: " + (totalAmountDone - lastResetAmount) + " (" + supplyPerHour + "/Hour)", 235, 365);
			g2D.drawString("XP gained: " + gainedXP + " (" + xpPerHour + "/Hour)", 235, 383);
			g2D.drawString("XP to next level: " + xpToLevel, 235, 401);
			g2D.drawString("Lvls gained: " + gainedLvl + " (Current Lvl: " + currentLvl + ")", 235, 419);
			g2D.drawString("Task: " + (amountLeft == 0 ? "None" : (amountLeft > 1000000000 ? "Infinite" : amountLeft) + " " + product + stopAtLevel), 235, 437);
			g2D.drawString("Status: " + status, 235, 455);
			g2D.drawString("Runtime: " + Timing.msToString(timeRan), 176, 472);
		}
		else {
			g2D.drawImage(NO_PAINT_IMAGE, -15, 295, null);
		}

	}

	public void printProgress() {
		Logger.getLogger().info("Stats: ");
		Logger.getLogger().info("   - Time ran: " + Timing.msToString(System.currentTimeMillis() - startTime));
		Logger.getLogger().info("   - XP gained: " + getGainedXp() + " (" + (int) (getGainedXp() * 3600000d / getTimeRan()) + "/Hour)");
	}

	public void setCurrentTask(Task task) {
		vars.firstSupply = task.getFirstSupply();
		vars.secondSupply = task.getSecondSupply();
		vars.logs = task.getLogs();
		vars.product = task.getProduct();
		vars.keybind = task.getKeybind();
		product = task.getProduct();
		stopAtLevel = task.getStopLevel() > 0 ? "Lvl " + task.getStopLevel() : "";
		amount = task.getAmountLeft();
		taskAmountDone = 0;
	}

	public void increaseSuppliesMade(int amount) {
		this.totalAmountDone += amount;
		this.taskAmountDone += amount;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getAmountToCook() {
		return amount - taskAmountDone;
	}

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
						Logger.getLogger().error("Unable to save: invalid file name.");
					}
					else {
						FileUtil.saveData("Cooking", filename, vars.tasks);
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

	public void onPause() {
		onPause = true;
		startPauseTime = System.currentTimeMillis();
		status = "On pause";
	}

	public void onResume() {
		onPause = false;
		timeOnPause += System.currentTimeMillis() - startPauseTime;
		status = "";
	}

	public void onBreakStart(long time) {
		onBreak = true;
		startBreakTime = System.currentTimeMillis();
		status = "On break for " + Timing.msToString(time);
	}

	public void onBreakEnd() {
		onBreak = false;
		timeOnBreak += System.currentTimeMillis() - startBreakTime;
		status = "";
	}

	private long getTimeRan() {
		return System.currentTimeMillis() - startTime - timeOnBreak - timeOnPause;
	}

	private int getGainedXp() {
		return Skills.getXP(SKILLS.COOKING) - startXP;
	}

	private void takeScreenshot() {
		LocalDateTime d = LocalDateTime.now();
		String characterName = Player.getRSPlayer().getName();
		String fileName = characterName + " " + d.getDayOfMonth() + "-"
			+ d.getMonthValue() + "-" + d.getYear() + " " + "("
			+ d.getHour() + "h" + d.getMinute() + "m" + d.getSecond()
			+ "s)" + ".png";
		if (Screenshots.take(fileName, true, true)) {
			String currentPath = System.getProperty("user.dir");
			Path defaultPath = Paths.get(currentPath + File.separator + "screenshots" + File.separator + fileName);
			Path myPath = Paths.get(currentPath + File.separator + "beg_scripts" + File.separator + "Cooking"
				+ File.separator + characterName + File.separator + "screenshots" + File.separator);
			try {
				Files.createDirectories(myPath);
				Files.move(defaultPath, Paths.get(myPath + File.separator + fileName), StandardCopyOption.REPLACE_EXISTING);
				Logger.getLogger().info("Saved screenshot at: " + myPath + File.separator + fileName);
			}
			catch (IOException e) {
				Logger.getLogger().info("Saved screenshot at: " + defaultPath);
			}
		}
		else {
			Logger.getLogger().error("Failed to save the screenshot.");
		}
	}

	private Image getImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		}
		catch (IOException e) {
			return null;
		}
	}

}
