package scripts.gui;

import org.tribot.api.General;
import scripts.task.Task;
import scripts.task.TaskCooker;
import scripts.api.beg.constants.skills.Cooking;
import scripts.api.beg.constants.skills.Firemaking;
import scripts.api.beg.logging.Logger;
import scripts.api.beg.utils.FileUtil;
import scripts.api.beg.utils.ImageUtil;
import scripts.api.beg.utils.Strings;
import scripts.api.beg.antiban.AntiBan;
import scripts.api.beg.resources.ResourcesManager;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GUICooker {

	// Constants
	private final static Image AVATAR_IMAGE = ResourcesManager.getAvatarIcon(); // TODO
	private final static Image TITLE = ImageUtil.getImageFromURL("https://i.imgur.com/OPlGD8m.png");
	private final static Font plain_font = new Font("Trebuchet MS", Font.PLAIN, 14);
	private final static Font bold_font = new Font("Trebuchet MS", Font.BOLD, 14);
	private final static Border thickBorder = new LineBorder(Color.DARK_GRAY, 1);
	private final static String SUPPLY_MSG = "Choose the product";
	private final static String LOGS_MSG = "Choose the logs to use";
	private final List<Task> tasks;
	// Private variables
	private boolean guiCreated;
	private JFrame frame;
	private JSpinner amountValue, stopLvl;
	private JSlider mouseSpeed;
	private JCheckBox saveOnUpdate, antiBanReaction, antiBanDebug, toogleRoof, useBankEsc, useBindkeys, fireType,
		burthoupeType, rangeType, makeItemType, stopAtLvl;
	private JComboBox<String> productList, logsList;
	private JList<String> taskList;
	private DefaultListModel<String> taskListModel;
	private JMenuItem loadProfile;
	private boolean inputOver;
	private JTextField scriptArgs;

	public GUICooker() {
		EventQueue.invokeLater(this::createGUI);
		inputOver = false;
		guiCreated = false;
		tasks = new LinkedList<>();
	}

	public void open() {
		while (!guiCreated) {
			General.sleep(100);
		}
		frame.setVisible(true);
	}

	private void createGUI() {
		try {
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("OptionPane.messageFont", plain_font);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		frame = new JFrame("Welcome to Beg's AIO cooker");
		// Create and set up the content pane.
		frame.setJMenuBar(this.fillJMenuBar());
		frame.getContentPane().add(this.createContentPane());
		frame.setIconImage(AVATAR_IMAGE);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		frame.pack();
		frame.setVisible(false);

		this.showDefaultGUI();

		guiCreated = true;
	}

	private JMenu fillFileMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setBackground(Color.LIGHT_GRAY);
		fileMenu.setOpaque(true);

		loadProfile = new JMenuItem("Load     ", KeyEvent.VK_L);
		loadProfile.setAccelerator(KeyStroke.getKeyStroke("control L"));
		loadProfile.addActionListener((e) -> {
			List<Task> loadTasks = FileUtil.loadData("Cooking", frame);
			if (loadTasks == null) {
				return;
			}
			tasks.addAll(loadTasks);
			Iterator<Task> it;
			Task task;
			for (it = loadTasks.iterator(); it.hasNext(); ) {
				task = it.next();
				taskListModel.addElement(task.toString());
			}
			loadProfile.setEnabled(false);

		});
		JMenuItem saveProfile = new JMenuItem("Save     ", KeyEvent.VK_S);
		saveProfile.setAccelerator(KeyStroke.getKeyStroke("control S"));
		saveProfile.addActionListener((e) -> {
			if (taskListModel.getSize() > 0) {
				String filename = JOptionPane.showInputDialog(frame,
					"Insert the filename:", "File name",
					JOptionPane.PLAIN_MESSAGE);
				if (filename == null || filename.isEmpty()) {
					Logger.getLogger().error("Unable to save: invalid file name.");
				}
				else {
					FileUtil.saveData("Cooking", filename, tasks);
				}
			}
			else {
				Logger.getLogger().error("Unable to save file: No tasks found.");
			}
		});

		fileMenu.add(loadProfile);
		fileMenu.add(saveProfile);

		return fileMenu;
	}

	private JMenu fillHelpMenu() {
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setBackground(Color.LIGHT_GRAY);
		helpMenu.setOpaque(true);

		JMenuItem openDiscordLink = new JMenuItem("Discord      ", KeyEvent.VK_D);
		openDiscordLink.setAccelerator(KeyStroke.getKeyStroke("control shift D"));
		openDiscordLink.addActionListener((e) ->
		{
			try {
				int result = JOptionPane.showConfirmDialog(frame, "Would you like to open a link to join my discord server?", "Join Beg on Discord", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					java.awt.Desktop.getDesktop().browse(new URI("https://discord.gg/wqPWQ269vu"));
				}
			}
			catch (URISyntaxException | IOException ex) {
				Logger.getLogger().error("Error occurred when opening link.");
			}
		});
		JMenuItem openTribotUserLink = new JMenuItem("TriBot User Profile      ", KeyEvent.VK_P);
		openTribotUserLink.setAccelerator(KeyStroke.getKeyStroke("control shift P"));
		openTribotUserLink.addActionListener((e) ->
		{
			try {
				int result = JOptionPane.showConfirmDialog(frame, "Would you like to open my TriBot's profile link?", "Attempting to open a link on browser", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					java.awt.Desktop.getDesktop().browse(new URI("https://community.tribot.org/profile/198168-beg/"));
				}
			}
			catch (URISyntaxException | IOException ex) {
				Logger.getLogger().error("Error occurred when opening link.");
			}
		});
		JMenuItem openScriptThreadLink = new JMenuItem("TriBot Script Thread      ", KeyEvent.VK_T);
		openScriptThreadLink.setAccelerator(KeyStroke.getKeyStroke("control shift T"));
		openScriptThreadLink.addActionListener((e) ->
		{
			try {
				int result = JOptionPane.showConfirmDialog(frame, "Would you like to open Script's thread on TriBot?", "Attempting to open a link on browser", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					java.awt.Desktop.getDesktop().browse(new URI("https://tribot.org/forums/topic/61733-"
						+ "free-aio-beg-cooker-abc2l-10-fish-pizzas-pies-wines-fire-"
						+ "range-make-item-task-based-save-load-progress/"));
				}
			}
			catch (URISyntaxException | IOException ex) {
				Logger.getLogger().error("Error occurred when opening link.");
			}
		});

		helpMenu.add(openDiscordLink);
		helpMenu.add(openTribotUserLink);
		helpMenu.add(openScriptThreadLink);

		return helpMenu;
	}

	private JMenu fillAboutMenu() {
		JMenu aboutMenu = new JMenu("About");
		aboutMenu.setBackground(Color.LIGHT_GRAY);
		aboutMenu.setOpaque(true);
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
		textPane.setText("OldSchool Runescape AIO Cooking script.\n"
			+ "Created and maintained by TriBot's User Beg.\n"
			+ "HOW TO USE: Simply select the type of cooking you want,\n"
			+ "the neccessary supplies and add one or multiple tasks.\n"
			+ "NOTE: Select an amount of zero to set the maximum amount.\n"
			+ "The script will then stop when supplies are depleted.");

		aboutMenu.add(textPane);

		return aboutMenu;
	}

	private JMenuBar fillJMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fillFileMenu());
		menuBar.add(Box.createHorizontalStrut(2));
		menuBar.add(fillHelpMenu());
		menuBar.add(Box.createHorizontalStrut(2));
		menuBar.add(fillAboutMenu());
		return menuBar;
	}

	private JPanel createContentPane() {
		// We create a JPanel to place everything on.
		JPanel totalGUI = new JPanel();
		totalGUI.setLayout(new BoxLayout(totalGUI, BoxLayout.X_AXIS));
		totalGUI.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel leftGUI = new JPanel();
		leftGUI.setLayout(new BoxLayout(leftGUI, BoxLayout.Y_AXIS));
		leftGUI.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		leftGUI.add(fillImagePanel());
		leftGUI.add(fillScriptArgsPanel());
		leftGUI.add(Box.createVerticalStrut(25));
		leftGUI.add(fillTaskPanel());

		JPanel rightGUI = new JPanel();
		rightGUI.setLayout(new BoxLayout(rightGUI, BoxLayout.Y_AXIS));
		rightGUI.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		rightGUI.add(fillOptionsPanel());
		rightGUI.add(fillButtonPanel());

		totalGUI.add(leftGUI);
		totalGUI.add(rightGUI);

		return totalGUI;
	}

	private JPanel fillScriptArgsPanel() {
		JPanel scriptArgsPanel = new JPanel();
		scriptArgsPanel.setLayout(new BoxLayout(scriptArgsPanel, BoxLayout.X_AXIS));

		JLabel scriptArgsLabel = new JLabel("Arguments:");
		scriptArgsLabel.setFont(bold_font);
		scriptArgsPanel.add(scriptArgsLabel);
		scriptArgs = new JTextField();
		scriptArgs.setFont(plain_font);
		scriptArgsPanel.add(scriptArgs);

		return scriptArgsPanel;
	}

	private JPanel fillImagePanel() {
		JPanel imagePanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				super.paintComponent(g2d);
				g.drawImage(TITLE, 0, 0, null);
			}
		};
		int width = TITLE == null ? 0 : TITLE.getWidth(null);
		int height = TITLE == null ? 0 : TITLE.getHeight(null) + 52;
		imagePanel.setMinimumSize(new Dimension(width, height));
		imagePanel.setPreferredSize(new Dimension(width, height));
		imagePanel.setMaximumSize(new Dimension(width, height));

		return imagePanel;
	}

	private JPanel fillTaskPanel() {
		JPanel taskPanel = new JPanel();
		taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));

		JPanel taskListPanel = new JPanel();
		MatteBorder mb = new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY);
		TitledBorder tb = new TitledBorder(mb, "Tasks", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION);
		taskListPanel.setBorder(tb);

		taskListModel = new DefaultListModel<String>();
		taskList = new JList<String>(taskListModel);
		taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane listScrollPane = new JScrollPane(taskList);
		listScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		listScrollPane.setPreferredSize(new Dimension(250, 216));

		taskListPanel.add(listScrollPane);

		JPanel taskButtonPanel = new JPanel(new BorderLayout(20, 0));
		taskButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

		JButton editTaskButton = new JButton("Edit task");
		editTaskButton.setFont(plain_font);
		editTaskButton.setOpaque(true);
		editTaskButton.setBorder(thickBorder);
		editTaskButton.addActionListener((e) -> editTaskButtonActionListener());
		editTaskButton.setPreferredSize(new Dimension(95, 10));

		JButton deleteTaskButton = new JButton("Delete task");
		deleteTaskButton.setFont(plain_font);
		deleteTaskButton.setOpaque(true);
		deleteTaskButton.setBorder(thickBorder);
		deleteTaskButton.setPreferredSize(new Dimension(95, 25));
		deleteTaskButton.addActionListener((e) -> deleteTaskButtonActionListener());

		taskButtonPanel.add(editTaskButton, BorderLayout.LINE_START);
		taskButtonPanel.add(deleteTaskButton, BorderLayout.CENTER);

		taskPanel.add(taskListPanel);
		taskPanel.add(taskButtonPanel);

		return taskPanel;
	}

	private JPanel fillOptionsPanel() {

		JPanel totalOptionPanel = new JPanel();
		totalOptionPanel.setLayout(new BoxLayout(totalOptionPanel, BoxLayout.Y_AXIS));

		JPanel extraOptionsPanel = new JPanel();
		extraOptionsPanel.setLayout(new BoxLayout(extraOptionsPanel, BoxLayout.Y_AXIS));
		extraOptionsPanel.add(Box.createVerticalStrut(20));
		extraOptionsPanel.add(fillExtraOptionsPanel());
		extraOptionsPanel.add(Box.createVerticalStrut(2));

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.setBorder(new TitledBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY), "task options", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION));

		optionsPanel.add(Box.createVerticalStrut(10));
		optionsPanel.add(fillTypePanel());
		optionsPanel.add(Box.createVerticalStrut(15));
		optionsPanel.add(fillProductPanel());
		optionsPanel.add(Box.createVerticalStrut(15));
		optionsPanel.add(fillLogsSupplyPanel());
		optionsPanel.add(Box.createVerticalStrut(15));
		optionsPanel.add(fillAmountPanel());
		optionsPanel.add(Box.createVerticalStrut(15));
		optionsPanel.add(fillStopByLevelPanel());

		totalOptionPanel.add(extraOptionsPanel);
		totalOptionPanel.add(optionsPanel);

		return totalOptionPanel;
	}

	private JPanel fillStopByLevelPanel() {
		JPanel stopAtLvlPanel = new JPanel();
		stopAtLvlPanel.setLayout(new BoxLayout(stopAtLvlPanel, BoxLayout.X_AXIS));

		stopLvl = new JSpinner(new SpinnerNumberModel(1, 0, 99, 1));
		stopLvl.setFont(plain_font);
		stopLvl.setEnabled(false);

		stopAtLvl = new JCheckBox("Stop at level:");
		stopAtLvl.setFont(plain_font);
		stopAtLvl.addItemListener((e) ->
		{
			if (e.getStateChange() != ItemEvent.SELECTED) {
				stopLvl.setValue(0);
			}
			stopLvl.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
		});

		stopAtLvlPanel.add(stopAtLvl);
		stopAtLvlPanel.add(stopLvl);

		return stopAtLvlPanel;
	}

	private JPanel fillExtraOptionsPanel() {
		JPanel mouseSpeedPanel = new JPanel();
		mouseSpeedPanel.setLayout(new BoxLayout(mouseSpeedPanel, BoxLayout.X_AXIS));

		JLabel mouseSpeedLabel = new JLabel("Mouse speed:");
		mouseSpeedLabel.setFont(bold_font);
		mouseSpeedPanel.add(mouseSpeedLabel);
		mouseSpeedPanel.add(Box.createHorizontalStrut(20));

		mouseSpeed = new JSlider(SwingConstants.HORIZONTAL, 75, 150, General.random(90, 115));
		mouseSpeed.setMajorTickSpacing(10);
		mouseSpeed.setMinorTickSpacing(1);
		mouseSpeed.setPaintTicks(true);
		mouseSpeed.createStandardLabels(10);
		mouseSpeed.setPaintLabels(true);
		mouseSpeedPanel.add(mouseSpeed);

		JPanel extraOptionsPanel = new JPanel();
		extraOptionsPanel.setLayout(new BoxLayout(extraOptionsPanel, BoxLayout.Y_AXIS));

		JPanel saveOnUpdatePanel = new JPanel();
		saveOnUpdatePanel.setLayout(new BoxLayout(saveOnUpdatePanel, BoxLayout.X_AXIS));

		JLabel saveOnUpdateLabel = new JLabel("System Update:");
		saveOnUpdateLabel.setFont(bold_font);
		saveOnUpdatePanel.add(saveOnUpdateLabel);
		saveOnUpdatePanel.add(Box.createHorizontalStrut(8));

		saveOnUpdate = new JCheckBox("Save progress", true);
		saveOnUpdate.setFont(plain_font);
		saveOnUpdate.add(Box.createHorizontalStrut(290));
		saveOnUpdatePanel.add(saveOnUpdate);

		JPanel antiBanPanel = new JPanel();
		antiBanPanel.setLayout(new BoxLayout(antiBanPanel, BoxLayout.X_AXIS));

		JLabel antiBanLabel = new JLabel("Anti-Ban:");
		antiBanLabel.setFont(bold_font);
		antiBanPanel.add(antiBanLabel);
		antiBanPanel.add(Box.createHorizontalStrut(49));

		antiBanReaction = new JCheckBox("Reaction time", false);
		antiBanReaction.setFont(plain_font);
		antiBanReaction.addItemListener((e) -> AntiBan.getAntiBan().setEnableReactionSleep(e.getStateChange() == ItemEvent.SELECTED));
		antiBanPanel.add(antiBanReaction);

		antiBanDebug = new JCheckBox("Print debug", true);
		antiBanDebug.setFont(plain_font);
		antiBanDebug.addItemListener((e) -> AntiBan.getAntiBan().setPrintDebug(e.getStateChange() == ItemEvent.SELECTED));
		antiBanDebug.add(Box.createHorizontalStrut(175));
		antiBanPanel.add(antiBanDebug);

		JPanel roofsPanel = new JPanel();
		roofsPanel.setLayout(new BoxLayout(roofsPanel, BoxLayout.X_AXIS));

		JLabel roofsLabel = new JLabel("Roofs:");
		roofsLabel.setFont(bold_font);
		roofsPanel.add(roofsLabel);
		roofsPanel.add(Box.createHorizontalStrut(68));

		toogleRoof = new JCheckBox("Toogle roofs", true);
		toogleRoof.setFont(plain_font);
		toogleRoof.add(Box.createHorizontalStrut(290));
		roofsPanel.add(toogleRoof);

		JPanel keyboardPanel = new JPanel();
		keyboardPanel.setLayout(new BoxLayout(keyboardPanel, BoxLayout.X_AXIS));

		JLabel keyboardLabel = new JLabel("Keyboard:");
		keyboardLabel.setFont(bold_font);
		keyboardPanel.add(keyboardLabel);
		keyboardPanel.add(Box.createHorizontalStrut(48));

		useBankEsc = new JCheckBox("Use to close bank", true);
		useBankEsc.setFont(plain_font);
		keyboardPanel.add(useBankEsc);

		useBindkeys = new JCheckBox("Use to select options", true);
		useBindkeys.setFont(plain_font);
		keyboardPanel.add(useBindkeys);

		extraOptionsPanel.add(mouseSpeedPanel);
		extraOptionsPanel.add(Box.createVerticalStrut(10));
		extraOptionsPanel.add(saveOnUpdatePanel);
		extraOptionsPanel.add(Box.createVerticalStrut(10));
		extraOptionsPanel.add(antiBanPanel);
		extraOptionsPanel.add(Box.createVerticalStrut(10));
		extraOptionsPanel.add(roofsPanel);
		extraOptionsPanel.add(Box.createVerticalStrut(10));
		extraOptionsPanel.add(keyboardPanel);
		extraOptionsPanel.add(Box.createVerticalStrut(10));

		return extraOptionsPanel;
	}

	private JPanel fillTypePanel() {
		JPanel typePanel = new JPanel();
		typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.X_AXIS));
		JLabel typeLabel = new JLabel("Type:");
		typeLabel.setFont(bold_font);
		typePanel.add(typeLabel);

		typePanel.add(Box.createHorizontalStrut(67));

		burthoupeType = new JCheckBox(Task.ROGUES_DEN_COOKING);
		burthoupeType.setFont(plain_font);
		burthoupeType.addActionListener((e) -> burthoupeTypeActionListener());
		typePanel.add(burthoupeType);

		fireType = new JCheckBox(Task.FIRE_COOKING);
		fireType.setFont(plain_font);
		fireType.addActionListener((e) -> fireTypeActionListener());
		typePanel.add(fireType);

		rangeType = new JCheckBox(Task.RANGE_COOKING);
		rangeType.setFont(plain_font);
		rangeType.addActionListener((e) -> rangeTypeActionListener());
		typePanel.add(rangeType);

		makeItemType = new JCheckBox(Task.MAKE_ITEM_COOKING);
		makeItemType.setFont(plain_font);
		makeItemType.addActionListener((e) -> makeItemTypeActionListener());
		typePanel.add(makeItemType);

		return typePanel;
	}

	private JPanel fillProductPanel() {
		JPanel productPanel = new JPanel();
		productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.X_AXIS));

		JLabel supplyLabel = new JLabel("Product:");
		supplyLabel.setFont(bold_font);
		productPanel.add(supplyLabel);

		productPanel.add(Box.createHorizontalStrut(53));

		productList = new JComboBox<String>();
		productList.addItem(SUPPLY_MSG);
		productList.setFont(plain_font);
		productPanel.add(productList);

		return productPanel;
	}

	private JPanel fillLogsSupplyPanel() {
		JPanel logsPanel = new JPanel();
		logsPanel.setLayout(new BoxLayout(logsPanel, BoxLayout.X_AXIS));

		JLabel logsLabel = new JLabel("Logs:");
		logsLabel.setFont(bold_font);
		logsPanel.add(logsLabel);

		logsPanel.add(Box.createHorizontalStrut(75));

		logsList = new JComboBox<String>();
		logsList.addItem(LOGS_MSG);
		for (Firemaking.Logs logs : Firemaking.Logs.values()) {
			logsList.addItem(logs.logsName);
		}
		logsList.setFont(plain_font);
		logsPanel.add(logsList);

		return logsPanel;
	}

	private JPanel fillAmountPanel() {
		JPanel amountPanel = new JPanel();
		amountPanel.setLayout(new BoxLayout(amountPanel, BoxLayout.X_AXIS));

		JLabel amountLabel = new JLabel("Amount:");
		amountLabel.setFont(bold_font);

		amountValue = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		amountValue.setFont(plain_font);

		amountPanel.add(amountLabel);
		amountPanel.add(Box.createHorizontalStrut(53));
		amountPanel.add(amountValue);

		return amountPanel;
	}

	private JPanel fillButtonPanel() {
		JPanel buttonPanel = new JPanel(new BorderLayout(20, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

		JButton resetButton = new JButton("Reset");
		resetButton.setFont(plain_font);
		resetButton.setPreferredSize(new Dimension(90, 25));
		resetButton.setOpaque(true);
		resetButton.setBorder(thickBorder);
		resetButton.addActionListener((e) -> resetButtonActionListener());
		buttonPanel.add(resetButton, BorderLayout.LINE_START);

		JButton taskButton = new JButton("Add task");
		taskButton.setFont(plain_font);
		taskButton.setPreferredSize(new Dimension(90, 25));
		taskButton.setOpaque(true);
		taskButton.setBorder(thickBorder);
		taskButton.addActionListener((e) -> taskButtonActionListener(taskListModel.getSize()));
		buttonPanel.add(taskButton, BorderLayout.CENTER);

		JButton startButton = new JButton("Start");
		startButton.setFont(plain_font);
		startButton.setPreferredSize(new Dimension(90, 25));
		startButton.setOpaque(true);
		startButton.setBorder(thickBorder);
		startButton.addActionListener((e) -> startButtonActionListener());
		buttonPanel.add(startButton, BorderLayout.LINE_END);

		return buttonPanel;
	}

	private void startButtonActionListener() {
		if (!Strings.isEmpty(scriptArgs.getText()) || taskListModel.getSize() > 0) {
			inputOver = true;
			frame.dispose();
		}
		else {
			JOptionPane.showMessageDialog(frame, "No tasks to perform", "Start script", JOptionPane.PLAIN_MESSAGE);
		}
	}

	private void resetButtonActionListener() {
		tasks.clear();
		taskListModel.removeAllElements();
		loadProfile.setEnabled(true);
		antiBanDebug.setSelected(true);
		showDefaultGUI();
	}

	private void taskButtonActionListener(int index) {
		String logsName = null;
		String type = null;

		if (productList.getSelectedIndex() < 1) {
			error("You must select your options first.", "Invalid task...");
		}
		else if (fireType.isSelected()) {
			if (logsList.getSelectedIndex() < 1) {
				error("You must select your options first.", "Invalid task...");
			}
			else {
				logsName = (String) logsList.getSelectedItem();
				type = fireType.getText();
			}
		}
		else if (burthoupeType.isSelected()) {
			type = burthoupeType.getText();
		}
		else if (rangeType.isSelected()) {
			type = rangeType.getText();
		}
		else if (makeItemType.isSelected()) {
			type = makeItemType.getText();
		}
		else {
			error("Cooking method is not selected.", "Invalid task...");
			return;
		}

		String selectedItem = ((String) productList.getSelectedItem()).replace("(", "").replace(")", "");
		Cooking.Food food = Strings.getEnumFromString(Cooking.Food.class, selectedItem);
		int stopLevel = stopAtLvl.isSelected() ? (int) stopLvl.getValue() : 0;
		int taskAmount = (Integer) amountValue.getValue() <= 0 ? Integer.MAX_VALUE : (Integer) amountValue.getValue();

		Task task = new TaskCooker(type, food.getFirstSupply(), food.getSecondSupply(), food.getProduct(),
			logsName, taskAmount, stopLevel, food.getKeybind());

		taskListModel.add(index, task.toString());
		tasks.add(index, task);
	}

	private void editTaskButtonActionListener() {
		int index = taskList.getSelectedIndex();
		if (index < 0) {
			return;
		}
		taskListModel.remove(index);
		this.taskButtonActionListener(index);
		tasks.remove(index + 1);
	}

	private void deleteTaskButtonActionListener() {
		int index = taskList.getSelectedIndex();
		if (index < 0) {
			return;
		}
		tasks.remove(index);
		taskListModel.remove(index);
		loadProfile.setEnabled(taskListModel.isEmpty());
	}

	private void burthoupeTypeActionListener() {
		if (burthoupeType.isSelected()) {
			productList.removeAllItems();
			productList.addItem(SUPPLY_MSG);
			for (Cooking.Food food : Cooking.Food.values()) {
				if (food.getType() == Cooking.FIRE_RANGE_TYPE) {
					productList.addItem(food.getProduct());
				}
			}

			productList.setEnabled(true);
			logsList.setEnabled(false);
			logsList.setSelectedIndex(0);
			fireType.setSelected(false);
			rangeType.setSelected(false);
			makeItemType.setSelected(false);
			amountValue.setValue(0);
		}
		else {
			showDefaultGUI();
		}
	}

	private void fireTypeActionListener() {
		if (fireType.isSelected()) {
			productList.removeAllItems();
			productList.addItem(SUPPLY_MSG);
			for (Cooking.Food food : Cooking.Food.values()) {
				if (food.getType() == Cooking.FIRE_RANGE_TYPE) {
					productList.addItem(food.getProduct());
				}
			}

			productList.setEnabled(true);
			logsList.setEnabled(true);
			logsList.setSelectedIndex(0);
			burthoupeType.setSelected(false);
			rangeType.setSelected(false);
			makeItemType.setSelected(false);
			amountValue.setValue(0);
		}
		else {
			showDefaultGUI();
		}
	}

	private void rangeTypeActionListener() {
		if (rangeType.isSelected()) {
			productList.removeAllItems();
			productList.addItem(SUPPLY_MSG);
			for (Cooking.Food food : Cooking.Food.values()) {
				if (food.getType() == Cooking.FIRE_RANGE_TYPE ||
					food.getType() == Cooking.RANGE_TYPE) {
					productList.addItem(food.getProduct());
				}
			}

			productList.setEnabled(true);
			logsList.setEnabled(false);
			logsList.setSelectedIndex(0);
			fireType.setSelected(false);
			burthoupeType.setSelected(false);
			makeItemType.setSelected(false);
			amountValue.setValue(0);
		}
		else {
			showDefaultGUI();
		}
	}

	private void makeItemTypeActionListener() {
		if (makeItemType.isSelected()) {
			productList.removeAllItems();
			productList.addItem(SUPPLY_MSG);
			for (Cooking.Food food : Cooking.Food.values()) {
				if (food.getType() != Cooking.MAKE_ITEM_TYPE) {
					continue;
				}
				String product = food.getProduct();
				String foodName = food.name();
				if (foodName.contains("_JUG")) {
					product = product + " (Jug)";
				}
				else if (foodName.contains("_BUCKET")) {
					product = product + " (Bucket)";
				}
				productList.addItem(product);
			}

			productList.setEnabled(true);
			logsList.setEnabled(false);
			logsList.setSelectedIndex(0);
			fireType.setSelected(false);
			burthoupeType.setSelected(false);
			rangeType.setSelected(false);
			amountValue.setValue(0);
		}
		else {
			showDefaultGUI();
		}
	}

	private void showDefaultGUI() {
		fireType.setEnabled(true);
		burthoupeType.setEnabled(true);
		rangeType.setEnabled(true);
		makeItemType.setEnabled(true);

		fireType.setSelected(false);
		burthoupeType.setSelected(false);
		rangeType.setSelected(false);
		makeItemType.setSelected(false);

		productList.setEnabled(false);
		logsList.setEnabled(false);

		productList.setSelectedIndex(0);
		logsList.setSelectedIndex(0);

		amountValue.setValue(0);
		stopLvl.setValue(0);
	}


	private void error(String text, String title) {
		JOptionPane.showMessageDialog(frame, text, title, JOptionPane.ERROR_MESSAGE);
	}

	public int getMouseSpeed() {
		return mouseSpeed.getValue();
	}

	public boolean inputOver() {
		return inputOver && !frame.isVisible();
	}

	public boolean isSaveOnUpdateEnable() {
		return saveOnUpdate.isSelected();
	}

	public boolean isReactionEnable() {
		return antiBanReaction.isSelected();
	}

	public boolean isPrintDebugEnable() {
		return antiBanDebug.isSelected();
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void close() {
		frame.dispose();
		inputOver = true;
	}

	public boolean isToggleRoofsEnable() {
		return toogleRoof.isSelected();
	}

	public boolean useBankEsc() {
		return useBankEsc.isSelected();
	}

	public boolean useBindkeys() {
		return useBindkeys.isSelected();
	}

	public String getArguments() {
		return scriptArgs.getText();
	}

}