package appwindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import extractors.data.DataSelector;
import extractors.data.FeatureExtractor;
import output.util.AvailabilityAnalyzer;

public class AppWindow {

	private StyledDocument console;
	private DataSelector ds = null;
	private JTextArea queryText;
	private JComboBox<String> comboBox;
	private JFrame frmTemplateCreator;
	private JTextField textSliceSize;
	@SuppressWarnings("unused")
	private String outputDir;
	private String lastDir;
	private JTextField text_partialPercent;
	private JTextField textStepSize;
	private JTextField textStepLimit;
	private SimpleAttributeSet errorText;
	private static final boolean DEBUG = true;
	@SuppressWarnings("unused")
	private static final boolean PRODUCTION = false;
	private boolean operationMode = DEBUG;

	private JTextField outputDirectory;
	JProgressBar progressBar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppWindow window = new AppWindow();
					window.frmTemplateCreator.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AppWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTemplateCreator = new JFrame();
		frmTemplateCreator.setResizable(false);
		frmTemplateCreator.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				try {
					ds = new DataSelector();
					printToConsole("Connected To Database.");
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException | SQLException e) {
					printErrorToConsole("ERROR: " + e.toString());
				}
				
				try {
					ds.maintenanceQuery("ALTER TABLE `collection2`.`userdata` DROP COLUMN `LastName` , DROP COLUMN `FirstName`");
				} catch (SQLException e1) {
					//be silent
				}
				
			}
		});
		
		
		
		frmTemplateCreator.setTitle("Darpa Project Mockup Tool");
		frmTemplateCreator.setBounds(100, 100, 800, 540);
		frmTemplateCreator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTemplateCreator.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		panel_1.setPreferredSize(new Dimension(9, 150));
		frmTemplateCreator.getContentPane().add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(null);

		JPanel panel_9 = new JPanel();
		panel_9.setMinimumSize(new Dimension(10, 100));
		panel_9.setBounds(0, 2, 794, 148);
		panel_1.add(panel_9);
		panel_9.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setMinimumSize(new Dimension(25, 100));
		panel_9.add(scrollPane, BorderLayout.CENTER);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JTextPane consolePane = new JTextPane();
		consolePane.setEditable(false);
		consolePane.setContentType("text/html");
		scrollPane.setViewportView(consolePane);
		console = consolePane.getStyledDocument();

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 0, 794, 2);
		panel_1.add(separator);

		JPanel panel_7 = new JPanel();
		frmTemplateCreator.getContentPane().add(panel_7, BorderLayout.CENTER);
		panel_7.setLayout(new BoxLayout(panel_7, BoxLayout.X_AXIS));

		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2),
				new EtchedBorder(EtchedBorder.LOWERED, null, null)));
		panel_7.add(panel_5);
		panel_5.setLayout(new BorderLayout(0, 0));

		queryText = new JTextArea();
		queryText.setFont(new Font("Courier New", Font.PLAIN, 12));
		queryText.setText("SELECT * FROM debug;");
		queryText.setColumns(80);
		panel_5.add(queryText);
		queryText.setRows(4);
		queryText.setLineWrap(true);
		queryText.setWrapStyleWord(true);

		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_5.add(panel_2, BorderLayout.NORTH);

		JLabel lblDataSelectionQuery = new JLabel("Data Selection Query:");
		panel_2.add(lblDataSelectionQuery);
		
		JPanel panel_10 = new JPanel();
		panel_5.add(panel_10, BorderLayout.SOUTH);
		GridBagLayout gbl_panel_10 = new GridBagLayout();
		gbl_panel_10.columnWidths = new int[]{168, 0, 0, 0, 0};
		gbl_panel_10.rowHeights = new int[]{24, 0};
		gbl_panel_10.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_10.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_10.setLayout(gbl_panel_10);
		
		final JCheckBox chckbxSupress = new JCheckBox("Suppress Standard Output");
		chckbxSupress.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				
			}
		});
		GridBagConstraints gbc_chckbxSupress = new GridBagConstraints();
		gbc_chckbxSupress.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxSupress.anchor = GridBagConstraints.WEST;
		gbc_chckbxSupress.gridx = 0;
		gbc_chckbxSupress.gridy = 0;
		panel_10.add(chckbxSupress, gbc_chckbxSupress);
		
		final JCheckBox useCustomDir = new JCheckBox("");
		GridBagConstraints gbc_useCustomDir = new GridBagConstraints();
		gbc_useCustomDir.insets = new Insets(0, 0, 0, 5);
		gbc_useCustomDir.gridx = 2;
		gbc_useCustomDir.gridy = 0;
		panel_10.add(useCustomDir, gbc_useCustomDir);
		
		outputDirectory = new JTextField();
		GridBagConstraints gbc_outputDirectory = new GridBagConstraints();
		gbc_outputDirectory.fill = GridBagConstraints.HORIZONTAL;
		gbc_outputDirectory.gridx = 3;
		gbc_outputDirectory.gridy = 0;
		panel_10.add(outputDirectory, gbc_outputDirectory);
		outputDirectory.setColumns(10);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2),
				new EtchedBorder(EtchedBorder.LOWERED, null, null)));
		panel_7.add(panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[] { 10, 72, 45, 50, 45, 45, 45, 5 };
		gbl_panel_3.rowHeights = new int[] { 35, 0, 15, 25, 0, 15, 25, 0, 35,
				0, 35, 0, 35, 0, 38 };
		gbl_panel_3.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 1.0 };
		gbl_panel_3.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
		panel_3.setLayout(gbl_panel_3);

		JLabel lblDataSelection = new JLabel("Data Selection:");
		lblDataSelection.setFont(new Font("SansSerif", Font.PLAIN, 12));
		GridBagConstraints gbc_lblDataSelection = new GridBagConstraints();
		gbc_lblDataSelection.insets = new Insets(0, 5, 5, 5);
		gbc_lblDataSelection.anchor = GridBagConstraints.WEST;
		gbc_lblDataSelection.gridwidth = 2;
		gbc_lblDataSelection.gridx = 0;
		gbc_lblDataSelection.gridy = 0;
		panel_3.add(lblDataSelection, gbc_lblDataSelection);

		final JButton btnQuery = new JButton("Query Database");
		GridBagConstraints gbc_btnQuery = new GridBagConstraints();
		gbc_btnQuery.gridwidth = 3;
		gbc_btnQuery.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnQuery.insets = new Insets(5, 0, 5, 5);
		gbc_btnQuery.gridx = 4;
		gbc_btnQuery.gridy = 0;
		panel_3.add(btnQuery, gbc_btnQuery);

		JSeparator separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_1.gridwidth = 8;
		gbc_separator_1.insets = new Insets(0, 0, 5, 0);
		gbc_separator_1.gridx = 0;
		gbc_separator_1.gridy = 1;
		panel_3.add(separator_1, gbc_separator_1);

		JLabel lblTemplateCreation = new JLabel("Template Creation:");
		lblTemplateCreation.setFont(new Font("SansSerif", Font.PLAIN, 12));
		GridBagConstraints gbc_lblTemplateCreation = new GridBagConstraints();
		gbc_lblTemplateCreation.gridwidth = 3;
		gbc_lblTemplateCreation.anchor = GridBagConstraints.WEST;
		gbc_lblTemplateCreation.insets = new Insets(5, 5, 5, 5);
		gbc_lblTemplateCreation.gridx = 0;
		gbc_lblTemplateCreation.gridy = 2;
		panel_3.add(lblTemplateCreation, gbc_lblTemplateCreation);

		JSeparator separator_7 = new JSeparator();
		separator_7.setOrientation(SwingConstants.VERTICAL);
		GridBagConstraints gbc_separator_7 = new GridBagConstraints();
		gbc_separator_7.fill = GridBagConstraints.VERTICAL;
		gbc_separator_7.insets = new Insets(0, 5, 5, 5);
		gbc_separator_7.gridx = 0;
		gbc_separator_7.gridy = 3;
		panel_3.add(separator_7, gbc_separator_7);

		final JCheckBox chckbxAvailabilityAnalysis = new JCheckBox(
				"Availability Analysis");
		chckbxAvailabilityAnalysis
				.setToolTipText("After creating template files, analyze features for availability.");
		GridBagConstraints gbc_chckbxAvailabilityAnalysis = new GridBagConstraints();
		gbc_chckbxAvailabilityAnalysis.gridwidth = 2;
		gbc_chckbxAvailabilityAnalysis.anchor = GridBagConstraints.WEST;
		gbc_chckbxAvailabilityAnalysis.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxAvailabilityAnalysis.gridx = 1;
		gbc_chckbxAvailabilityAnalysis.gridy = 3;
		panel_3.add(chckbxAvailabilityAnalysis, gbc_chckbxAvailabilityAnalysis);

		final JButton btnCreateTemplates = new JButton("Create Templates");
		GridBagConstraints gbc_btnCreateTemplates = new GridBagConstraints();
		gbc_btnCreateTemplates.gridwidth = 3;
		gbc_btnCreateTemplates.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCreateTemplates.insets = new Insets(0, 0, 5, 5);
		gbc_btnCreateTemplates.gridx = 4;
		gbc_btnCreateTemplates.gridy = 3;
		panel_3.add(btnCreateTemplates, gbc_btnCreateTemplates);

		btnCreateTemplates.setAlignmentX(Component.CENTER_ALIGNMENT);

		JSeparator separator_2 = new JSeparator();
		GridBagConstraints gbc_separator_2 = new GridBagConstraints();
		gbc_separator_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_2.gridwidth = 8;
		gbc_separator_2.insets = new Insets(0, 0, 5, 0);
		gbc_separator_2.gridx = 0;
		gbc_separator_2.gridy = 4;
		panel_3.add(separator_2, gbc_separator_2);

		JLabel lblTestVectorCreation = new JLabel("Test Vector Creation:");
		lblTestVectorCreation.setFont(new Font("SansSerif", Font.PLAIN, 12));
		GridBagConstraints gbc_lblTestVectorCreation = new GridBagConstraints();
		gbc_lblTestVectorCreation.gridwidth = 3;
		gbc_lblTestVectorCreation.anchor = GridBagConstraints.WEST;
		gbc_lblTestVectorCreation.insets = new Insets(5, 5, 5, 5);
		gbc_lblTestVectorCreation.gridx = 0;
		gbc_lblTestVectorCreation.gridy = 5;
		panel_3.add(lblTestVectorCreation, gbc_lblTestVectorCreation);

		JSeparator separator_6 = new JSeparator();
		separator_6.setOrientation(SwingConstants.VERTICAL);
		GridBagConstraints gbc_separator_6 = new GridBagConstraints();
		gbc_separator_6.gridheight = 9;
		gbc_separator_6.fill = GridBagConstraints.VERTICAL;
		gbc_separator_6.insets = new Insets(0, 5, 0, 5);
		gbc_separator_6.gridx = 0;
		gbc_separator_6.gridy = 6;
		panel_3.add(separator_6, gbc_separator_6);

		JLabel lblForEvery = new JLabel("Slice Size/Unit:");
		GridBagConstraints gbc_lblForEvery = new GridBagConstraints();
		gbc_lblForEvery.insets = new Insets(0, 5, 5, 5);
		gbc_lblForEvery.anchor = GridBagConstraints.WEST;
		gbc_lblForEvery.gridx = 1;
		gbc_lblForEvery.gridy = 6;
		panel_3.add(lblForEvery, gbc_lblForEvery);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.gridx = 2;
		gbc_panel.gridy = 6;
		panel_3.add(panel, gbc_panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		textSliceSize = new JTextField();
		textSliceSize.setText("1");
		panel.add(textSliceSize);
		textSliceSize.setColumns(3);

		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {
				"Minutes", "Seconds", "Characters" }));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.gridwidth = 2;
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 3;
		gbc_comboBox.gridy = 6;
		panel_3.add(comboBox, gbc_comboBox);

		JSeparator separator_5 = new JSeparator();
		GridBagConstraints gbc_separator_5 = new GridBagConstraints();
		gbc_separator_5.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_5.anchor = GridBagConstraints.WEST;
		gbc_separator_5.gridwidth = 6;
		gbc_separator_5.insets = new Insets(0, 0, 5, 5);
		gbc_separator_5.gridx = 1;
		gbc_separator_5.gridy = 7;
		panel_3.add(separator_5, gbc_separator_5);

		final JCheckBox checkBoxWrapWindows = new JCheckBox("Wrap Answers");
		checkBoxWrapWindows
				.setToolTipText("If generating vectors for large time slices, consider checking this box so that slices can wrap around answer boundaries.");
		GridBagConstraints gbc_checkBoxWrapWindows = new GridBagConstraints();
		gbc_checkBoxWrapWindows.gridwidth = 2;
		gbc_checkBoxWrapWindows.anchor = GridBagConstraints.WEST;
		gbc_checkBoxWrapWindows.insets = new Insets(0, 0, 5, 5);
		gbc_checkBoxWrapWindows.gridx = 1;
		gbc_checkBoxWrapWindows.gridy = 8;
		panel_3.add(checkBoxWrapWindows, gbc_checkBoxWrapWindows);

		JSeparator separator_3 = new JSeparator();
		GridBagConstraints gbc_separator_3 = new GridBagConstraints();
		gbc_separator_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_3.gridwidth = 6;
		gbc_separator_3.insets = new Insets(0, 0, 5, 5);
		gbc_separator_3.gridx = 1;
		gbc_separator_3.gridy = 9;
		panel_3.add(separator_3, gbc_separator_3);

		final JCheckBox chckbxAllowPartials = new JCheckBox("Allow Partials");
		chckbxAllowPartials
				.setToolTipText("Allow vectorization of partial scans that are less than the specified scan window.");
		GridBagConstraints gbc_chckbxAllowPartials = new GridBagConstraints();
		gbc_chckbxAllowPartials.gridwidth = 2;
		gbc_chckbxAllowPartials.anchor = GridBagConstraints.WEST;
		gbc_chckbxAllowPartials.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxAllowPartials.gridx = 1;
		gbc_chckbxAllowPartials.gridy = 10;
		panel_3.add(chckbxAllowPartials, gbc_chckbxAllowPartials);

		JLabel lblOfSelectedLength = new JLabel("Threshold");
		lblOfSelectedLength
				.setToolTipText("Allows inclusion of partials based on percent of target scan length.\r\n\t-Ex. a value of 90 will include scans with or exceeding 90% of the scan target length.\r\n\t-Setting this to 0 will allow all partial scans regardless of size.\r\n");
		GridBagConstraints gbc_lblOfSelectedLength = new GridBagConstraints();
		gbc_lblOfSelectedLength.anchor = GridBagConstraints.EAST;
		gbc_lblOfSelectedLength.insets = new Insets(0, 0, 5, 5);
		gbc_lblOfSelectedLength.gridx = 3;
		gbc_lblOfSelectedLength.gridy = 10;
		panel_3.add(lblOfSelectedLength, gbc_lblOfSelectedLength);

		JPanel panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.insets = new Insets(0, 0, 5, 5);
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 4;
		gbc_panel_4.gridy = 10;
		panel_3.add(panel_4, gbc_panel_4);
		panel_4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		text_partialPercent = new JTextField();
		text_partialPercent.setText("0");
		text_partialPercent.setColumns(2);
		panel_4.add(text_partialPercent);

		JSeparator separator_4 = new JSeparator();
		GridBagConstraints gbc_separator_4 = new GridBagConstraints();
		gbc_separator_4.gridwidth = 6;
		gbc_separator_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_4.insets = new Insets(0, 0, 5, 5);
		gbc_separator_4.gridx = 1;
		gbc_separator_4.gridy = 11;
		panel_3.add(separator_4, gbc_separator_4);

		final JCheckBox chckbxIncrementalPasses = new JCheckBox(
				"Incremental Passes");
		GridBagConstraints gbc_chckbxIncrementalPasses = new GridBagConstraints();
		gbc_chckbxIncrementalPasses.gridwidth = 2;
		gbc_chckbxIncrementalPasses.anchor = GridBagConstraints.WEST;
		gbc_chckbxIncrementalPasses.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxIncrementalPasses.gridx = 1;
		gbc_chckbxIncrementalPasses.gridy = 12;
		panel_3.add(chckbxIncrementalPasses, gbc_chckbxIncrementalPasses);

		JLabel lblStep = new JLabel("Step:");
		GridBagConstraints gbc_lblStep = new GridBagConstraints();
		gbc_lblStep.anchor = GridBagConstraints.EAST;
		gbc_lblStep.insets = new Insets(0, 0, 5, 5);
		gbc_lblStep.gridx = 3;
		gbc_lblStep.gridy = 12;
		panel_3.add(lblStep, gbc_lblStep);

		JPanel panel_6 = new JPanel();
		GridBagConstraints gbc_panel_6 = new GridBagConstraints();
		gbc_panel_6.insets = new Insets(0, 0, 5, 5);
		gbc_panel_6.fill = GridBagConstraints.BOTH;
		gbc_panel_6.gridx = 4;
		gbc_panel_6.gridy = 12;
		panel_3.add(panel_6, gbc_panel_6);
		panel_6.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		textStepSize = new JTextField();
		textStepSize.setText("1");
		textStepSize.setColumns(2);
		panel_6.add(textStepSize);

		JLabel lblLimit = new JLabel("Limit:");
		GridBagConstraints gbc_lblLimit = new GridBagConstraints();
		gbc_lblLimit.anchor = GridBagConstraints.EAST;
		gbc_lblLimit.insets = new Insets(0, 0, 5, 5);
		gbc_lblLimit.gridx = 5;
		gbc_lblLimit.gridy = 12;
		panel_3.add(lblLimit, gbc_lblLimit);

		JPanel panel_8 = new JPanel();
		GridBagConstraints gbc_panel_8 = new GridBagConstraints();
		gbc_panel_8.insets = new Insets(0, 0, 5, 5);
		gbc_panel_8.fill = GridBagConstraints.BOTH;
		gbc_panel_8.gridx = 6;
		gbc_panel_8.gridy = 12;
		panel_3.add(panel_8, gbc_panel_8);
		panel_8.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		textStepLimit = new JTextField();
		textStepLimit.setText("10");
		textStepLimit.setColumns(2);
		panel_8.add(textStepLimit);

		JSeparator separator_8 = new JSeparator();
		GridBagConstraints gbc_separator_8 = new GridBagConstraints();
		gbc_separator_8.gridwidth = 6;
		gbc_separator_8.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_8.insets = new Insets(0, 0, 5, 5);
		gbc_separator_8.gridx = 1;
		gbc_separator_8.gridy = 13;
		panel_3.add(separator_8, gbc_separator_8);
		
		progressBar = new JProgressBar();
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.gridwidth = 3;
		gbc_progressBar.insets = new Insets(0, 0, 0, 5);
		gbc_progressBar.gridx = 1;
		gbc_progressBar.gridy = 14;
		panel_3.add(progressBar, gbc_progressBar);

		final JButton btnCreateTestVectors = new JButton("Create Test Vectors");

		GridBagConstraints gbc_btnCreateTestVectors = new GridBagConstraints();
		gbc_btnCreateTestVectors.gridwidth = 3;
		gbc_btnCreateTestVectors.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCreateTestVectors.insets = new Insets(0, 0, 0, 5);
		gbc_btnCreateTestVectors.gridx = 4;
		gbc_btnCreateTestVectors.gridy = 14;
		panel_3.add(btnCreateTestVectors, gbc_btnCreateTestVectors);

		btnCreateTemplates.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (!btnCreateTemplates.isEnabled())
					return;
				SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {

					public String doInBackground() {
						btnCreateTemplates.setEnabled(false);
						FeatureExtractor extractor = new FeatureExtractor(ds
								.getData());
						extractor.setQuery(ds.getQuery());
						if(useCustomDir.isSelected())
							extractor.setCustomOutputDirectory(outputDirectory.getText());
						extractor.setSuppressSystemOut(chckbxSupress.isSelected());
						try {
							extractor.loadModules();
							if (!ds.isEmpty())
								extractor.createTemplates();
							else if (ds.isEmpty())
								throw new Exception("No Data Loaded.");
						} catch (Exception e) {
							if (operationMode == DEBUG)
								e.printStackTrace();
							return "ERROR : " + e.toString();
						}
						extractor.flushModules();
						lastDir = extractor.getOutputDirectory();
						if (chckbxAvailabilityAnalysis.isSelected()) {
							SwingWorker<String, Void> aaWorker = new SwingWorker<String, Void>() {
								public String doInBackground() {
									printToConsole("Computing Template Feature Availability...");
									new AvailabilityAnalyzer(lastDir).run();
									return "Availability Analysis Complete.";
								}

								public void done() {
									try {
										printToConsole(get().toString());
									} catch (InterruptedException ex) {
										ex.printStackTrace();
									} catch (ExecutionException ex) {
										ex.printStackTrace();
									}
								}
							};
							aaWorker.execute();
						}
						return null;
					}

					public void done() {
						btnCreateTemplates.setEnabled(true);
						try {
							if (get() == null)
								printToConsole("Template Creation Complete.");
							else
								printErrorToConsole(get());
						} catch (InterruptedException ex) {
							ex.printStackTrace();
						} catch (ExecutionException ex) {
							ex.printStackTrace();
						}
					}
				};
				worker.execute();
			}

		});

		btnCreateTestVectors.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (!btnCreateTestVectors.isEnabled())
					return;
				SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {

					public String doInBackground() {
						btnCreateTestVectors.setEnabled(false);
						FeatureExtractor extractor = new FeatureExtractor(ds
								.getData());
						extractor.setQuery(ds.getQuery());
						if(useCustomDir.isSelected())
							extractor.setCustomOutputDirectory(outputDirectory.getText());
						extractor.setSuppressSystemOut(chckbxSupress.isSelected());
						if (!ds.getData().isEmpty()) {
							try {
								extractor.loadModules();
								int sliceSize = Integer.parseInt(textSliceSize
										.getText().trim());
								int partialPercent = Integer
										.parseInt(text_partialPercent.getText()
												.trim());
								int stepSize = Integer.parseInt(textStepSize
										.getText().trim());
								int stepLimit = Integer.parseInt(textStepLimit
										.getText().trim());

								extractor.createTestVectors(comboBox
										.getSelectedItem().toString(),
										sliceSize, checkBoxWrapWindows
												.isSelected(),
										chckbxAllowPartials.isSelected(),
										partialPercent, chckbxIncrementalPasses
												.isSelected(), stepSize,
										stepLimit);
							} catch (Exception e) {
								if (operationMode == DEBUG)
									e.printStackTrace();
								return "ERROR: " + e.toString();
							}
						}
						extractor.flushModules();
						return null;
					}

					public void done() {
						btnCreateTestVectors.setEnabled(true);
						try {
							if (get() == null)
								printToConsole("Vector Creation Complete.");
							else
								printErrorToConsole(get());
						} catch (InterruptedException ex) {
							ex.printStackTrace();
						} catch (ExecutionException ex) {
							ex.printStackTrace();
						}
					}

				};
				worker.execute();
			}
		});

		btnQuery.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (!btnQuery.isEnabled())
					return;
				SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
					
					public String doInBackground() {
						progressBar.setIndeterminate(true);
						btnQuery.setEnabled(false);
						btnCreateTemplates.setEnabled(false);
						btnCreateTestVectors.setEnabled(false);
						printToConsole("Fetching Data...");
						ds.setQuery(queryText.getText());
						try {
							ds.query();
						} catch (SQLException e) {
							return "ERROR : " + e.toString();
						}
						return null;
					}

					public void done() {
						progressBar.setIndeterminate(false);
						btnQuery.setEnabled(true);
						btnCreateTemplates.setEnabled(true);
						btnCreateTestVectors.setEnabled(true);
						try {
							if (get() == null)
								printToConsole("Data Loaded Successfully.");
							else
								printErrorToConsole(get());
						} catch (InterruptedException ex) {
							ex.printStackTrace();
						} catch (ExecutionException ex) {
							ex.printStackTrace();
						}
					}

				};
				worker.execute();
			}
		});

		errorText = new SimpleAttributeSet();
		StyleConstants.setForeground(errorText, Color.RED);
		StyleConstants.setFontSize(errorText, 14);
		StyleConstants.setFontFamily(errorText, "SansSerif");

		DefaultCaret caret = (DefaultCaret) consolePane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

	}

	private void printToConsole(String msg) {
		try {
			console.insertString(console.getLength(), msg + "\n", null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void printErrorToConsole(String msg) {
		try {
			console.insertString(console.getLength(), msg + "\n", errorText);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
