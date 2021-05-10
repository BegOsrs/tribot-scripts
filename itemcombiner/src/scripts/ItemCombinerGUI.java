package scripts;

import org.tribot.api.General;
import org.tribot.api2007.Skills.SKILLS;
import scripts.data.ItemCombinerInterfaceIds;
import scripts.data.ItemCombinerItem;
import scripts.data.ItemCombinerSkills;
import scripts.data.ItemCombinerTask;
import scripts.api.beg.logging.Logger;
import scripts.api.beg.utils.FileUtil;
import scripts.api.beg.utils.ImageUtil;
import scripts.api.beg.utils.Strings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static scripts.data.ItemCombinerVariables.vars;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemCombinerGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JComboBox skillsComboBox;
	private JSpinner masterIdSpinner;
	private JSpinner childIdSpinner;
	private JSpinner componentIdSpinner;
	private JSpinner amountSpinner;
	private JSpinner stopLevelSpinner;

	private DefaultListModel<String> taskListModel;

	private JSlider mouseSpeedSlider;
	private JCheckBox saveProgressCheckbox;
	private JCheckBox reactionTimeCheckbox;
	private JCheckBox printDebugCheckbox;

	private boolean inputOver;

	private JLabel skillWarning;
	private JLabel masterWarning;
	private JLabel childWarning;

	private JComboBox findIdsComboBox;

	private JSpinner timeoutSpinner;

	private JCheckBox escButtonCheckbox;
	private JTextField scriptArgs;
	private JTable itemsTable;

	private JCheckBox alchCheckBox;
	private JCheckBox restockCheckBox;
	private JCheckBox mouseOffscreenCheckbox;
	private JCheckBox f6buttonCheckBox;
	private JCheckBox bankWalkingCheckbox;
	private JCheckBox geWalkingCheckbox;

	public ItemCombinerGUI() {
		inputOver = false;
		createAndShowGui();
	}

	private static void open(URI uri) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(uri);
			}
			catch (Exception e) {
				Logger.getLogger().error("Error occurred when opening link.");
			}
		}
		else {
			System.out.println("Your desktop doesn't support this functionality.");
		}
	}

	private void createAndShowGui() {
		try {
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("OptionPane.messageFont", new Font("Trebuchet MS", Font.PLAIN, 14));
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		setIconImage(ImageUtil.getImageFromURL("https://i.imgur.com/XOmh5UP.png"));
		setResizable(false);
		setTitle("BEG Items Combiner");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 634);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
		});

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menuFile = new JMenu("File");
		menuFile.setForeground(Color.GRAY);
		menuBar.add(menuFile);

		JMenuItem menuLoadTasks = new JMenuItem("Load tasks");
		menuLoadTasks.setAccelerator(KeyStroke.getKeyStroke("control L"));
		menuLoadTasks.addActionListener((e) -> {
			List<ItemCombinerTask> loadedTasks = FileUtil.loadData("Combine", this);
			if (loadedTasks != null) {
				Iterator<ItemCombinerTask> it;
				for (it = loadedTasks.iterator(); it.hasNext(); ) {
					ItemCombinerTask task = it.next();
					vars.tasks.add(task);
					taskListModel.addElement(task.toString());
				}
			}
		});
		menuFile.add(menuLoadTasks);

		JMenuItem menuSaveTasks = new JMenuItem("Save tasks");
		menuSaveTasks.setAccelerator(KeyStroke.getKeyStroke("control S"));
		menuSaveTasks.addActionListener((e) -> {
			String msg;
			if (taskListModel.getSize() > 0) {
				String filename = JOptionPane.showInputDialog(this, "Insert the filename:", "File name", JOptionPane.PLAIN_MESSAGE);
				if (filename == null || filename.isEmpty()) {
					msg = "Unable to save file: Invalid file name";
				}
				else {
					boolean saved = FileUtil.saveData("Combine", filename, vars.tasks);
					msg = saved ? "File saved" : "Failed to save file";
				}
			}
			else {
				msg = "Unable to save file: No tasks found";
			}
			JOptionPane.showMessageDialog(this, msg, "Save tasks", JOptionPane.PLAIN_MESSAGE);
		});
		menuFile.add(menuSaveTasks);

		JMenu menuHelp = new JMenu("Help");
		menuHelp.setForeground(Color.GRAY);
		menuBar.add(menuHelp);

		JMenuItem menuDiscord = new JMenuItem("Discord");
		menuDiscord.setAccelerator(KeyStroke.getKeyStroke("control shift D"));
		menuDiscord.addActionListener((e) -> {
			int result = JOptionPane.showConfirmDialog(this, "Would you like to open a link to join my discord server?", "Add 'BegOSRS' on Skype", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				try {
					open(new URI("https://discord.gg/wqPWQ269vu"));
				}
				catch (URISyntaxException ex) {
					ex.printStackTrace();
				}
			}
		});
		menuHelp.add(menuDiscord);

		JMenuItem menuTribotProfile = new JMenuItem("Tribot profile");
		menuTribotProfile.setAccelerator(KeyStroke.getKeyStroke("control shift P"));
		menuTribotProfile.addActionListener((e) -> {
			int result = JOptionPane.showConfirmDialog(this, "Would you like to open my TriBot's profile link?", "Attempting to open a link on browser", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				try {
					open(new URI("https://community.tribot.org/profile/198168-beg/"));
				}
				catch (URISyntaxException ex) {
					ex.printStackTrace();
				}
			}
		});
		menuHelp.add(menuTribotProfile);

		JMenuItem menuScriptThread = new JMenuItem("Script thread");
		menuScriptThread.setAccelerator(KeyStroke.getKeyStroke("control shift T"));
		menuScriptThread.addActionListener((e) -> {
			int result = JOptionPane.showConfirmDialog(this, "Would you like to open Script's thread on TriBot?", "Attempting to open a link on browser", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				try {
					open(new URI("https://tribot.org/forums/topic/71378-free-beg-item-combiner/"));
				}
				catch (URISyntaxException ex) {
					ex.printStackTrace();
				}
			}
		});
		menuHelp.add(menuScriptThread);

		JMenu menuAbout = new JMenu("About");
		menuAbout.setForeground(Color.GRAY);
		menuBar.add(menuAbout);

		JTextPane txtpnOldschoolRunescapeAio = new JTextPane();
		txtpnOldschoolRunescapeAio.setText("OldSchool Runescape item combiner script.\r\nCreated and maintained by TriBot's User Beg.");
		menuAbout.add(txtpnOldschoolRunescapeAio);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{93, 67, 45, 0, 0};
		gbl_contentPane.rowHeights = new int[]{78, 0, 0, 169, 23, 35, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.anchor = GridBagConstraints.NORTH;
		gbc_tabbedPane.gridheight = 3;
		gbc_tabbedPane.gridwidth = 4;
		gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_tabbedPane.fill = GridBagConstraints.HORIZONTAL;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		contentPane.add(tabbedPane, gbc_tabbedPane);

		JPanel taskSettingsPanel = new JPanel();
		tabbedPane.addTab("Tasks", null, taskSettingsPanel, null);
		GridBagLayout gbl_taskSettingsPanel = new GridBagLayout();
		gbl_taskSettingsPanel.columnWidths = new int[]{108, 17, 148, 98};
		gbl_taskSettingsPanel.rowHeights = new int[]{23, 0, 114, 23, 23, 0};
		gbl_taskSettingsPanel.columnWeights = new double[]{1.0, 0.0, 1.0, 1.0};
		gbl_taskSettingsPanel.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0};
		taskSettingsPanel.setLayout(gbl_taskSettingsPanel);

		JLabel skillLabel = new JLabel("  Skill:");
		GridBagConstraints gbc_skillLabel = new GridBagConstraints();
		gbc_skillLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_skillLabel.insets = new Insets(0, 0, 5, 5);
		gbc_skillLabel.gridx = 0;
		gbc_skillLabel.gridy = 0;
		taskSettingsPanel.add(skillLabel, gbc_skillLabel);

		skillWarning = new JLabel();
		GridBagConstraints gbc_skillWarning = new GridBagConstraints();
		gbc_skillWarning.insets = new Insets(0, 0, 5, 5);
		gbc_skillWarning.gridx = 1;
		gbc_skillWarning.gridy = 0;
		taskSettingsPanel.add(skillWarning, gbc_skillWarning);

		skillsComboBox = new JComboBox(ItemCombinerSkills.values());
		skillsComboBox.setSelectedIndex(-1);

		GridBagConstraints gbc_skillsComboBox = new GridBagConstraints();
		gbc_skillsComboBox.gridwidth = 2;
		gbc_skillsComboBox.fill = GridBagConstraints.BOTH;
		gbc_skillsComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_skillsComboBox.gridx = 2;
		gbc_skillsComboBox.gridy = 0;
		taskSettingsPanel.add(skillsComboBox, gbc_skillsComboBox);

		JLabel lblItems = new JLabel("  Items:");
		GridBagConstraints gbc_lblItems = new GridBagConstraints();
		gbc_lblItems.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblItems.insets = new Insets(0, 0, 5, 5);
		gbc_lblItems.gridx = 0;
		gbc_lblItems.gridy = 1;
		taskSettingsPanel.add(lblItems, gbc_lblItems);

		JScrollPane itemsListScroll = new JScrollPane();
		GridBagConstraints gbc_itemsListScroll = new GridBagConstraints();
		gbc_itemsListScroll.fill = GridBagConstraints.BOTH;
		gbc_itemsListScroll.gridwidth = 4;
		gbc_itemsListScroll.insets = new Insets(0, 0, 5, 0);
		gbc_itemsListScroll.gridx = 0;
		gbc_itemsListScroll.gridy = 2;
		taskSettingsPanel.add(itemsListScroll, gbc_itemsListScroll);

		DefaultTableModel itemsTableModel = new DefaultTableModel(new Object[]{"Items Name", "Amount per Inventory", "Restock Price", "Restock Amount", "Restock At Amount"}, 10);
		itemsTable = new JTable(itemsTableModel);
		itemsTable.setFillsViewportHeight(true);
		itemsListScroll.setViewportView(itemsTable);
		itemsTable.getTableHeader().setReorderingAllowed(false);
		TableColumnModel tcm = itemsTable.getColumnModel();
		//	TableColumn nameColumn = tcm.getColumn(0);
		//	TableColumn invAmountColumn = tcm.getColumn(1);
		TableColumn priceColumn = tcm.getColumn(2);
		TableColumn totalAmountColumn = tcm.getColumn(3);
		TableColumn restockAtColumn = tcm.getColumn(4);
		tcm.removeColumn(priceColumn);
		tcm.removeColumn(totalAmountColumn);
		tcm.removeColumn(restockAtColumn);

		JLabel masterInterface1Label = new JLabel("  Master interface id:");
		GridBagConstraints gbc_masterInterface1Label = new GridBagConstraints();
		gbc_masterInterface1Label.fill = GridBagConstraints.HORIZONTAL;
		gbc_masterInterface1Label.insets = new Insets(0, 0, 5, 5);
		gbc_masterInterface1Label.gridx = 0;
		gbc_masterInterface1Label.gridy = 3;
		taskSettingsPanel.add(masterInterface1Label, gbc_masterInterface1Label);

		masterWarning = new JLabel();
		GridBagConstraints gbc_masterWarning = new GridBagConstraints();
		gbc_masterWarning.insets = new Insets(0, 0, 5, 5);
		gbc_masterWarning.gridx = 1;
		gbc_masterWarning.gridy = 3;
		taskSettingsPanel.add(masterWarning, gbc_masterWarning);

		masterIdSpinner = new JSpinner();
		masterIdSpinner.setModel(new SpinnerNumberModel(new Integer(-1), new Integer(-1), null, new Integer(1)));
		GridBagConstraints gbc_master1IdSpinner = new GridBagConstraints();
		gbc_master1IdSpinner.fill = GridBagConstraints.BOTH;
		gbc_master1IdSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_master1IdSpinner.gridx = 2;
		gbc_master1IdSpinner.gridy = 3;
		taskSettingsPanel.add(masterIdSpinner, gbc_master1IdSpinner);

		JLabel childInterfaceLabel = new JLabel("  Child interface id:");
		GridBagConstraints gbc_childInterfaceLabel = new GridBagConstraints();
		gbc_childInterfaceLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_childInterfaceLabel.insets = new Insets(0, 0, 5, 5);
		gbc_childInterfaceLabel.gridx = 0;
		gbc_childInterfaceLabel.gridy = 4;
		taskSettingsPanel.add(childInterfaceLabel, gbc_childInterfaceLabel);

		childWarning = new JLabel();
		GridBagConstraints gbc_childWarning = new GridBagConstraints();
		gbc_childWarning.insets = new Insets(0, 0, 5, 5);
		gbc_childWarning.gridx = 1;
		gbc_childWarning.gridy = 4;
		taskSettingsPanel.add(childWarning, gbc_childWarning);

		childIdSpinner = new JSpinner();
		childIdSpinner.setModel(new SpinnerNumberModel(-1, -1, null, 1));
		GridBagConstraints gbc_chil1dSpinner = new GridBagConstraints();
		gbc_chil1dSpinner.fill = GridBagConstraints.BOTH;
		gbc_chil1dSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_chil1dSpinner.gridx = 2;
		gbc_chil1dSpinner.gridy = 4;
		taskSettingsPanel.add(childIdSpinner, gbc_chil1dSpinner);

		JLabel findInterfaceIdsLabel = new JLabel("Find interface ids:");
		GridBagConstraints gbc_findInterfaceIdsLabel = new GridBagConstraints();
		gbc_findInterfaceIdsLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_findInterfaceIdsLabel.insets = new Insets(0, 0, 5, 0);
		gbc_findInterfaceIdsLabel.gridx = 3;
		gbc_findInterfaceIdsLabel.gridy = 4;
		taskSettingsPanel.add(findInterfaceIdsLabel, gbc_findInterfaceIdsLabel);

		JLabel lblComponentInterfaceId = new JLabel("  Component interface id:");
		GridBagConstraints gbc_lblComponentInterfaceId = new GridBagConstraints();
		gbc_lblComponentInterfaceId.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblComponentInterfaceId.insets = new Insets(0, 0, 0, 5);
		gbc_lblComponentInterfaceId.gridx = 0;
		gbc_lblComponentInterfaceId.gridy = 5;
		taskSettingsPanel.add(lblComponentInterfaceId, gbc_lblComponentInterfaceId);

		componentIdSpinner = new JSpinner();
		componentIdSpinner.setModel(new SpinnerNumberModel(-1, -1, null, 1));
		GridBagConstraints gbc_component1IdSpinner = new GridBagConstraints();
		gbc_component1IdSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_component1IdSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_component1IdSpinner.gridx = 2;
		gbc_component1IdSpinner.gridy = 5;
		taskSettingsPanel.add(componentIdSpinner, gbc_component1IdSpinner);
		GridBagConstraints gbc_masterInterface2Label = new GridBagConstraints();
		gbc_masterInterface2Label.fill = GridBagConstraints.HORIZONTAL;
		gbc_masterInterface2Label.insets = new Insets(0, 0, 5, 5);
		gbc_masterInterface2Label.gridx = 0;
		gbc_masterInterface2Label.gridy = 7;

		findIdsComboBox = new JComboBox(ItemCombinerInterfaceIds.values());
		findIdsComboBox.setSelectedIndex(-1);
		findIdsComboBox.addItemListener((e) -> {
			ItemCombinerInterfaceIds selectedInterface = (ItemCombinerInterfaceIds) e.getItem();
			masterIdSpinner.setValue(selectedInterface.masterId);
			childIdSpinner.setValue(selectedInterface.childId);
			componentIdSpinner.setValue(selectedInterface.componentId);
		});
		GridBagConstraints gbc_findIdsComboBox = new GridBagConstraints();
		gbc_findIdsComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_findIdsComboBox.gridx = 3;
		gbc_findIdsComboBox.gridy = 5;
		taskSettingsPanel.add(findIdsComboBox, gbc_findIdsComboBox);


		JPanel generalSettingsPanel = new JPanel();
		tabbedPane.addTab("Settings", null, generalSettingsPanel, null);
		GridBagLayout gbl_generalSettingsPanel = new GridBagLayout();
		gbl_generalSettingsPanel.columnWidths = new int[]{112, 101, 147, 103, 0};
		gbl_generalSettingsPanel.rowHeights = new int[]{32, 0, 22, 0, 22, 0, 0, 0};
		gbl_generalSettingsPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_generalSettingsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		generalSettingsPanel.setLayout(gbl_generalSettingsPanel);

		JLabel mouseSpeedLabel = new JLabel("  Mouse speed:");
		GridBagConstraints gbc_mouseSpeedLabel = new GridBagConstraints();
		gbc_mouseSpeedLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_mouseSpeedLabel.insets = new Insets(0, 0, 5, 5);
		gbc_mouseSpeedLabel.gridx = 0;
		gbc_mouseSpeedLabel.gridy = 0;
		generalSettingsPanel.add(mouseSpeedLabel, gbc_mouseSpeedLabel);

		mouseSpeedSlider = new JSlider(75, 150, General.random(80, 100));
		mouseSpeedSlider.setPaintLabels(true);
		mouseSpeedSlider.setPaintTicks(true);
		mouseSpeedSlider.setMajorTickSpacing(10);
		mouseSpeedSlider.setMinorTickSpacing(1);
		mouseSpeedSlider.createStandardLabels(10);
		GridBagConstraints gbc_mouseSpeedSlider = new GridBagConstraints();
		gbc_mouseSpeedSlider.anchor = GridBagConstraints.NORTH;
		gbc_mouseSpeedSlider.gridwidth = 3;
		gbc_mouseSpeedSlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_mouseSpeedSlider.insets = new Insets(0, 0, 5, 0);
		gbc_mouseSpeedSlider.gridx = 1;
		gbc_mouseSpeedSlider.gridy = 0;
		generalSettingsPanel.add(mouseSpeedSlider, gbc_mouseSpeedSlider);

		JLabel systemUpdateLabel = new JLabel("  System update:");
		GridBagConstraints gbc_systemUpdateLabel = new GridBagConstraints();
		gbc_systemUpdateLabel.fill = GridBagConstraints.BOTH;
		gbc_systemUpdateLabel.insets = new Insets(0, 0, 5, 5);
		gbc_systemUpdateLabel.gridx = 0;
		gbc_systemUpdateLabel.gridy = 1;
		generalSettingsPanel.add(systemUpdateLabel, gbc_systemUpdateLabel);

		saveProgressCheckbox = new JCheckBox("Save progress");
		saveProgressCheckbox.setSelected(true);
		GridBagConstraints gbc_saveProgressCheckbox = new GridBagConstraints();
		gbc_saveProgressCheckbox.gridwidth = 3;
		gbc_saveProgressCheckbox.insets = new Insets(0, 0, 5, 0);
		gbc_saveProgressCheckbox.fill = GridBagConstraints.BOTH;
		gbc_saveProgressCheckbox.gridx = 1;
		gbc_saveProgressCheckbox.gridy = 1;
		generalSettingsPanel.add(saveProgressCheckbox, gbc_saveProgressCheckbox);

		JLabel antibanLabel = new JLabel("  Anti-ban:");
		GridBagConstraints gbc_antibanLabel = new GridBagConstraints();
		gbc_antibanLabel.fill = GridBagConstraints.BOTH;
		gbc_antibanLabel.insets = new Insets(0, 0, 5, 5);
		gbc_antibanLabel.gridx = 0;
		gbc_antibanLabel.gridy = 2;
		generalSettingsPanel.add(antibanLabel, gbc_antibanLabel);

		printDebugCheckbox = new JCheckBox("Print debug");
		printDebugCheckbox.setSelected(true);
		GridBagConstraints gbc_printDebugCheckbox = new GridBagConstraints();
		gbc_printDebugCheckbox.insets = new Insets(0, 0, 5, 5);
		gbc_printDebugCheckbox.fill = GridBagConstraints.BOTH;
		gbc_printDebugCheckbox.gridx = 1;
		gbc_printDebugCheckbox.gridy = 2;
		generalSettingsPanel.add(printDebugCheckbox, gbc_printDebugCheckbox);

		reactionTimeCheckbox = new JCheckBox("Disable reaction time");
		reactionTimeCheckbox.setSelected(true);
		GridBagConstraints gbc_reactionTimeCheckbox = new GridBagConstraints();
		gbc_reactionTimeCheckbox.fill = GridBagConstraints.BOTH;
		gbc_reactionTimeCheckbox.insets = new Insets(0, 0, 5, 5);
		gbc_reactionTimeCheckbox.gridx = 2;
		gbc_reactionTimeCheckbox.gridy = 2;
		generalSettingsPanel.add(reactionTimeCheckbox, gbc_reactionTimeCheckbox);

		mouseOffscreenCheckbox = new JCheckBox("Disable mouse offscreen");
		GridBagConstraints gbc_mouseOffscreenCheckbox = new GridBagConstraints();
		gbc_mouseOffscreenCheckbox.fill = GridBagConstraints.HORIZONTAL;
		gbc_mouseOffscreenCheckbox.insets = new Insets(0, 0, 5, 0);
		gbc_mouseOffscreenCheckbox.gridx = 3;
		gbc_mouseOffscreenCheckbox.gridy = 2;
		generalSettingsPanel.add(mouseOffscreenCheckbox, gbc_mouseOffscreenCheckbox);

		JLabel timeoutLabel = new JLabel("  Inventory timeout:");
		GridBagConstraints gbc_timeoutLabel = new GridBagConstraints();
		gbc_timeoutLabel.fill = GridBagConstraints.BOTH;
		gbc_timeoutLabel.insets = new Insets(0, 0, 5, 5);
		gbc_timeoutLabel.gridx = 0;
		gbc_timeoutLabel.gridy = 3;
		generalSettingsPanel.add(timeoutLabel, gbc_timeoutLabel);

		timeoutSpinner = new JSpinner();
		timeoutSpinner.setModel(new SpinnerNumberModel(General.random(61983, 66056), 0, 120000, 100));
		GridBagConstraints gbc_timeoutSpinner = new GridBagConstraints();
		gbc_timeoutSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_timeoutSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_timeoutSpinner.gridx = 1;
		gbc_timeoutSpinner.gridy = 3;
		generalSettingsPanel.add(timeoutSpinner, gbc_timeoutSpinner);

		JLabel secondsLabel = new JLabel("(milliseconds)");
		GridBagConstraints gbc_secondsLabel = new GridBagConstraints();
		gbc_secondsLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_secondsLabel.insets = new Insets(0, 0, 5, 5);
		gbc_secondsLabel.gridx = 2;
		gbc_secondsLabel.gridy = 3;
		generalSettingsPanel.add(secondsLabel, gbc_secondsLabel);

		JLabel closingBankLabel = new JLabel("  Bank:");
		GridBagConstraints gbc_closingBankLabel = new GridBagConstraints();
		gbc_closingBankLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_closingBankLabel.insets = new Insets(0, 0, 5, 5);
		gbc_closingBankLabel.gridx = 0;
		gbc_closingBankLabel.gridy = 4;
		generalSettingsPanel.add(closingBankLabel, gbc_closingBankLabel);

		escButtonCheckbox = new JCheckBox("Close with esc button");
		escButtonCheckbox.setSelected(true);
		GridBagConstraints gbc_escButtonCheckbox = new GridBagConstraints();
		gbc_escButtonCheckbox.fill = GridBagConstraints.HORIZONTAL;
		gbc_escButtonCheckbox.insets = new Insets(0, 0, 5, 5);
		gbc_escButtonCheckbox.gridx = 1;
		gbc_escButtonCheckbox.gridy = 4;
		generalSettingsPanel.add(escButtonCheckbox, gbc_escButtonCheckbox);

		JLabel alchProductLabel = new JLabel("  Alchemy:");

		bankWalkingCheckbox = new JCheckBox("Auto walking");
		bankWalkingCheckbox.setSelected(true);
		GridBagConstraints gbc_bankWalkingCheckbox = new GridBagConstraints();
		gbc_bankWalkingCheckbox.fill = GridBagConstraints.HORIZONTAL;
		gbc_bankWalkingCheckbox.insets = new Insets(0, 0, 5, 5);
		gbc_bankWalkingCheckbox.gridx = 2;
		gbc_bankWalkingCheckbox.gridy = 4;
		generalSettingsPanel.add(bankWalkingCheckbox, gbc_bankWalkingCheckbox);

		geWalkingCheckbox = new JCheckBox("At grand exchange");
		GridBagConstraints gbc_geWalkingCheckbox = new GridBagConstraints();
		gbc_geWalkingCheckbox.fill = GridBagConstraints.HORIZONTAL;
		gbc_geWalkingCheckbox.insets = new Insets(0, 0, 5, 0);
		gbc_geWalkingCheckbox.gridx = 3;
		gbc_geWalkingCheckbox.gridy = 4;
		generalSettingsPanel.add(geWalkingCheckbox, gbc_geWalkingCheckbox);
		GridBagConstraints gbc_alchProductLabel = new GridBagConstraints();
		gbc_alchProductLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_alchProductLabel.insets = new Insets(0, 0, 5, 5);
		gbc_alchProductLabel.gridx = 0;
		gbc_alchProductLabel.gridy = 5;
		generalSettingsPanel.add(alchProductLabel, gbc_alchProductLabel);

		alchCheckBox = new JCheckBox("Alch product");
		GridBagConstraints gbc_alchCheckBox = new GridBagConstraints();
		gbc_alchCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_alchCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_alchCheckBox.gridx = 1;
		gbc_alchCheckBox.gridy = 5;
		generalSettingsPanel.add(alchCheckBox, gbc_alchCheckBox);

		JLabel restockLabel = new JLabel("  Restock:");

		JCheckBox f6buttonCheckBox = new JCheckBox("Use F6 button");
		GridBagConstraints gbc_f6buttonCheckBox = new GridBagConstraints();
		gbc_f6buttonCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_f6buttonCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_f6buttonCheckBox.gridx = 2;
		gbc_f6buttonCheckBox.gridy = 5;
		generalSettingsPanel.add(f6buttonCheckBox, gbc_f6buttonCheckBox);
		GridBagConstraints gbc_restockLabel = new GridBagConstraints();
		gbc_restockLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_restockLabel.insets = new Insets(0, 0, 0, 5);
		gbc_restockLabel.gridx = 0;
		gbc_restockLabel.gridy = 6;
		generalSettingsPanel.add(restockLabel, gbc_restockLabel);

		restockCheckBox = new JCheckBox("Restock from G.E.");
		restockCheckBox.addActionListener((e) -> {
			if (restockCheckBox.isSelected()) {
				tcm.addColumn(priceColumn);
				tcm.addColumn(totalAmountColumn);
				tcm.addColumn(restockAtColumn);
			}
			else {
				tcm.removeColumn(priceColumn);
				tcm.removeColumn(totalAmountColumn);
				tcm.removeColumn(restockAtColumn);
			}
		});
		GridBagConstraints gbc_restockCheckBox = new GridBagConstraints();
		gbc_restockCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_restockCheckBox.insets = new Insets(0, 0, 0, 5);
		gbc_restockCheckBox.gridx = 1;
		gbc_restockCheckBox.gridy = 6;
		generalSettingsPanel.add(restockCheckBox, gbc_restockCheckBox);
		MatteBorder mb = new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY);
		TitledBorder tb = new TitledBorder(mb, "Tasks", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION);
		taskListModel = new DefaultListModel<String>();

		JPanel taskPanel = new JPanel();
		GridBagConstraints gbc_taskPanel = new GridBagConstraints();
		gbc_taskPanel.insets = new Insets(0, 0, 5, 0);
		gbc_taskPanel.gridwidth = 4;
		gbc_taskPanel.fill = GridBagConstraints.BOTH;
		gbc_taskPanel.gridx = 0;
		gbc_taskPanel.gridy = 3;
		taskPanel.setBorder(tb);
		contentPane.add(taskPanel, gbc_taskPanel);
		taskPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel taskOptionsPanel = new JPanel();
		taskPanel.add(taskOptionsPanel);
		GridBagLayout gbl_taskOptionsPanel = new GridBagLayout();
		gbl_taskOptionsPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gbl_taskOptionsPanel.rowHeights = new int[]{0, 0};
		gbl_taskOptionsPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_taskOptionsPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		taskOptionsPanel.setLayout(gbl_taskOptionsPanel);

		JLabel amountLabel = new JLabel("Amount:");
		GridBagConstraints gbc_amountLabel = new GridBagConstraints();
		gbc_amountLabel.insets = new Insets(0, 0, 0, 5);
		gbc_amountLabel.gridx = 0;
		gbc_amountLabel.gridy = 0;
		taskOptionsPanel.add(amountLabel, gbc_amountLabel);

		amountSpinner = new JSpinner();
		amountSpinner.setModel(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));
		amountSpinner.setPreferredSize(new Dimension(75, 20));
		GridBagConstraints gbc_amountSpinner = new GridBagConstraints();
		gbc_amountSpinner.fill = GridBagConstraints.BOTH;
		gbc_amountSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_amountSpinner.gridx = 1;
		gbc_amountSpinner.gridy = 0;
		taskOptionsPanel.add(amountSpinner, gbc_amountSpinner);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 0, 5);
		gbc_horizontalStrut.gridx = 2;
		gbc_horizontalStrut.gridy = 0;
		taskOptionsPanel.add(horizontalStrut, gbc_horizontalStrut);

		JLabel stopLevelLabel = new JLabel("Stop level:");
		GridBagConstraints gbc_stopLevelLabel = new GridBagConstraints();
		gbc_stopLevelLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_stopLevelLabel.insets = new Insets(0, 0, 0, 5);
		gbc_stopLevelLabel.gridx = 3;
		gbc_stopLevelLabel.gridy = 0;
		taskOptionsPanel.add(stopLevelLabel, gbc_stopLevelLabel);

		stopLevelSpinner = new JSpinner();
		stopLevelSpinner.setModel(new SpinnerNumberModel(-1, -1, 99, 1));
		stopLevelSpinner.setPreferredSize(new Dimension(75, 20));
		GridBagConstraints gbc_stopLevelSpinner = new GridBagConstraints();
		gbc_stopLevelSpinner.fill = GridBagConstraints.BOTH;
		gbc_stopLevelSpinner.gridx = 4;
		gbc_stopLevelSpinner.gridy = 0;
		taskOptionsPanel.add(stopLevelSpinner, gbc_stopLevelSpinner);
		JList<String> taskList = new JList<String>(taskListModel);
		taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane listScrollPane = new JScrollPane(taskList);
		listScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listScrollPane.setPreferredSize(new Dimension(500, 100));
		taskPanel.add(listScrollPane);

		JButton addButton = new JButton("Add task");
		addButton.addActionListener((e) -> {
			ItemCombinerTask task = this.newTask();
			if (task != null) {
				taskListModel.addElement(task.toString());
				vars.tasks.add(task);
			}
		});

		JLabel scriptArgsLabel = new JLabel("Script arguments:");
		GridBagConstraints gbc_scriptArgsLabel = new GridBagConstraints();
		gbc_scriptArgsLabel.anchor = GridBagConstraints.SOUTH;
		gbc_scriptArgsLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_scriptArgsLabel.insets = new Insets(0, 0, 5, 5);
		gbc_scriptArgsLabel.gridx = 0;
		gbc_scriptArgsLabel.gridy = 4;
		contentPane.add(scriptArgsLabel, gbc_scriptArgsLabel);

		scriptArgs = new JTextField();
		GridBagConstraints gbc_scriptArgs = new GridBagConstraints();
		gbc_scriptArgs.anchor = GridBagConstraints.NORTH;
		gbc_scriptArgs.gridwidth = 4;
		gbc_scriptArgs.insets = new Insets(0, 0, 5, 0);
		gbc_scriptArgs.fill = GridBagConstraints.HORIZONTAL;
		gbc_scriptArgs.gridx = 0;
		gbc_scriptArgs.gridy = 5;
		contentPane.add(scriptArgs, gbc_scriptArgs);
		scriptArgs.setColumns(10);

		GridBagConstraints gbc_addButton = new GridBagConstraints();
		gbc_addButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_addButton.insets = new Insets(0, 0, 0, 5);
		gbc_addButton.gridx = 2;
		gbc_addButton.gridy = 6;
		contentPane.add(addButton, gbc_addButton);

		JButton editButton = new JButton("Edit task");
		editButton.addActionListener((e) -> {
			int index = taskList.getSelectedIndex();
			if (index > -1) {
				ItemCombinerTask task = this.newTask();
				if (task != null) {
					taskListModel.set(index, task.toString());
					vars.tasks.set(index, task);
				}
			}
		});
		GridBagConstraints gbc_editButton = new GridBagConstraints();
		gbc_editButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_editButton.insets = new Insets(0, 0, 0, 5);
		gbc_editButton.gridx = 1;
		gbc_editButton.gridy = 6;
		contentPane.add(editButton, gbc_editButton);

		JButton deleteButton = new JButton("Delete task");
		deleteButton.addActionListener((e) -> {
			int index = taskList.getSelectedIndex();
			if (index > -1) {
				taskListModel.remove(index);
				vars.tasks.remove(index);
			}
		});
		GridBagConstraints gbc_deleteButton = new GridBagConstraints();
		gbc_deleteButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_deleteButton.insets = new Insets(0, 0, 0, 5);
		gbc_deleteButton.gridx = 0;
		gbc_deleteButton.gridy = 6;
		contentPane.add(deleteButton, gbc_deleteButton);

		JButton startButton = new JButton("Start");
		startButton.addActionListener((e) -> {
			if (!Strings.isEmpty(scriptArgs.getText()) || taskListModel.getSize() > 0) {
				inputOver = true;
				dispose();
			}
			else {
				JOptionPane.showMessageDialog(this, "No tasks to perform.", "Start script", JOptionPane.PLAIN_MESSAGE);
			}
		});
		getRootPane().setDefaultButton(startButton);
		startButton.requestFocus();
		GridBagConstraints gbc_startButton = new GridBagConstraints();
		gbc_startButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_startButton.gridx = 3;
		gbc_startButton.gridy = 6;
		contentPane.add(startButton, gbc_startButton);

		setVisible(true);
	}

	private ItemCombinerTask newTask() {
		Object selectedSkill = skillsComboBox.getSelectedItem();
		SKILLS skill = selectedSkill == null || selectedSkill == ItemCombinerSkills.NONE ? null : SKILLS.valueOf(selectedSkill.toString());
		final TableModel itemsModel = itemsTable.getModel();
		final int rowsCount = itemsModel.getRowCount();
		List<ItemCombinerItem> items = new ArrayList<>(rowsCount);
		for (int i = 0; i < rowsCount; i++) {
			Object name = itemsModel.getValueAt(i, 0);
			Object invAmount = itemsModel.getValueAt(i, 1);
			if (name == null && invAmount != null) {
				JOptionPane.showMessageDialog(this, "Row " + (i + 1) + " - Items's name is not set.");
				return null;
			}
			if (name != null && invAmount == null) {
				JOptionPane.showMessageDialog(this, "Row " + (i + 1) + " - Items's amount is not set.");
				return null;
			}
			if (name == null || Strings.isEmpty(name.toString()) && Strings.isEmpty(invAmount.toString())) {
				continue;
			}

			String itemName = Strings.capitalize(name.toString().toLowerCase().trim());
			int itemAmount = 0;
			try {
				itemAmount = Integer.parseInt(invAmount.toString());
			}
			catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Amount per inventory of item \"" + itemName + "\" is not a number.");
				return null;
			}
			int restockPrice = 0;
			int restockAmount = 0;
			int restockAtAmount = 0;
			if (restockingEnable()) {
				Object price = itemsModel.getValueAt(i, 2);
				Object amount = itemsModel.getValueAt(i, 3);
				Object amountAt = itemsModel.getValueAt(i, 4);
				if (price == null) {
					JOptionPane.showMessageDialog(this, "Row " + (i + 1) + " - Items's restock price is not set.");
					return null;
				}
				if (amount == null) {
					JOptionPane.showMessageDialog(this, "Row " + (i + 1) + " - Items's restock amount is not set.");
					return null;
				}
				if (amountAt == null) {
					JOptionPane.showMessageDialog(this, "Row " + (i + 1) + " - Items's restock at amount is not set.");
					return null;
				}
				try {
					restockPrice = Integer.parseInt(price.toString());
				}
				catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(this, "Row " + (i + 1) + " - Restock price is not a number.");
					return null;
				}
				try {
					restockAmount = Integer.parseInt(amount.toString());
				}
				catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(this, "Row " + (i + 1) + " - Restock amount is not a number.");
					return null;
				}
				try {
					restockAtAmount = Integer.parseInt(amountAt.toString());
				}
				catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(this, "Row " + (i + 1) + " - Restock at amount is not a number.");
					return null;
				}
			}
			items.add(new ItemCombinerItem(itemName, itemAmount, restockPrice, restockAmount, restockAtAmount));
		}
		if (items.size() < 2) {
			JOptionPane.showMessageDialog(this, "A task has to have at least 2 items.");
			return null;
		}
		int masterId = (int) masterIdSpinner.getValue();
		int childId = adjustChildId(items.get(items.size() - 1).getName(), (int) childIdSpinner.getValue());
		int componentId = (int) componentIdSpinner.getValue();
		int amount = (int) amountSpinner.getValue();
		if (amount <= 0) {
			amount = Integer.MAX_VALUE;
		}
		int stopLevel = (int) stopLevelSpinner.getValue();
		int inventoryTimeout = (int) timeoutSpinner.getValue();
		boolean alchingProduct = alchingEnable();
		boolean restocking = restockingEnable();

		return new ItemCombinerTask(skill, items, masterId, childId, componentId, amount, stopLevel, inventoryTimeout, alchingProduct, restocking);
	}

	private int adjustChildId(String product, int childInterfaceId) {
		if (product.equalsIgnoreCase("shortbow (u)") || product.equalsIgnoreCase("longbow (u)")) {
			childInterfaceId++;
		}
		childIdSpinner.setValue(childInterfaceId);
		return childInterfaceId;
	}

	public boolean isInputOver() {
		return inputOver;
	}

	public int getMouseSpeed() {
		return mouseSpeedSlider.getValue();
	}

	public boolean isSaveOnUpdateEnable() {
		return saveProgressCheckbox.isSelected();
	}

	public boolean isReactionEnable() {
		return !reactionTimeCheckbox.isSelected();
	}

	public boolean isPrintDebugEnable() {
		return printDebugCheckbox.isSelected();
	}

	public boolean useEscButton() {
		return escButtonCheckbox.isSelected();
	}

	public boolean isBankWalkerEnable() {
		return bankWalkingCheckbox.isSelected();
	}

	public boolean isGeWalkerEnable() {
		return geWalkingCheckbox.isSelected();
	}

	public boolean alchingEnable() {
		return alchCheckBox.isSelected();
	}

	public boolean restockingEnable() {
		return restockCheckBox.isSelected();
	}

	public boolean isMouseOffscreenEnable() {
		return !mouseOffscreenCheckbox.isEnabled();
	}

	public boolean isF6buttonEnable() {
		return f6buttonCheckBox.isEnabled();
	}

	public String getScriptArguments() {
		return scriptArgs.getText();
	}

	public void close() {
		dispose();
		inputOver = true;
	}

}
