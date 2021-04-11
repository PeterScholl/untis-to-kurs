package aufgabenGenerator;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.html.HTMLEditorKit;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Locale;

/**
 * Klasse um zu einer Frage (Question) eine Darstellung anzuzeigen
 * 
 * @author peter
 *
 */
public class ViewQuestion {
	private Question q;
	private JFrame frame;
	private JTextField nameTextField;
	private JEditorPane questionInHTMLView;
	private JLabel nameLabel;
	private JTextArea questionTextField;
	private JLabel questionLabel;
	private JList<String> antwortliste = new JList<String>(new String[] {});

	public ViewQuestion(Question q) {
		if (q == null)
			throw (new IllegalArgumentException("Keine Objekt vom Typ Question übergeben!"));
		this.q = q;
		initComponents();

	}

	static void addComponent(Container cont, GridBagLayout gbl, Component c, int x, int y, int width, int height,
			double weightx, double weighty, int gridbagconstraints) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = gridbagconstraints;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbl.setConstraints(c, gbc);
		cont.add(c);
	}

	static void addComponent(Container cont, GridBagLayout gbl, Component c, int x, int y, int width, int height,
			double weightx, double weighty) {
		addComponent(cont, gbl, c, x, y, width, height, weightx, weighty, GridBagConstraints.BOTH);
	}

	private void initComponents() {
		frame = new JFrame("Show and Edit Question");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = frame.getContentPane();
		// Create the components.
		JPanel mainPanel = createMainQuestionTab();
		JPanel optionPanel = createOptionsPanel();
		JPanel answerPanel = createAnswerPanel();
		JLabel label = new JLabel("Edit the Question", JLabel.CENTER);

		// Lay them out.
		Border padding = BorderFactory.createEmptyBorder(20, 20, 5, 20);
		mainPanel.setBorder(padding);
		optionPanel.setBorder(padding);
		answerPanel.setBorder(padding);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Question Content", null, mainPanel, "Main Content of the Question-Object"); // tooltip text
		tabbedPane.addTab("Question Options", null, optionPanel, "Feature Options of the Question-Object"); // tooltip
																											// text
		tabbedPane.addTab("Answers", null, answerPanel, "Show and Edit the answers"); // tooltip text

		c.add(tabbedPane, BorderLayout.CENTER);
		c.add(label, BorderLayout.PAGE_END);
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		frame.setSize(600, 400);
		frame.setVisible(true);
	}

	/** Creates the panel shown by the first tab. */
	private JPanel createMainQuestionTab() {
		JPanel c = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		c.setLayout(gbl);

		nameTextField = new JTextField(30);
		nameTextField.setText(this.q.getName());
		nameTextField.setCaretPosition(nameTextField.getText().length());
		nameTextField.moveCaretPosition(0);
		nameLabel = new JLabel("Name: ");
		questionTextField = new JTextArea(3, 0);
		questionTextField.setText(q.getQuestiontext());
		questionLabel = new JLabel("Frage: ");

		// Liste der Antworten erzeugen
		JScrollPane scrollPaneAnswers = new JScrollPane();
		scrollPaneAnswers.setViewportView(antwortliste);
		antwortliste.setLayoutOrientation(JList.VERTICAL);
		DefaultListModel<String> listmodel = new DefaultListModel<String>();
		antwortliste.setModel(listmodel);
		antwortliste.addMouseListener(new TestMouseListener()); // nur zu Testzwecken
		// antwortliste.addMouseListener(this);
		// antwortliste.addKeyListener(this);

		// die einzelnen Antworten hinzufügen
		listmodel.removeAllElements();
		for (XMLObject ans : q.toXML().getAllChildren("answer")) {
			listmodel.addElement(Hilfsfunktionen.mcanswerToMcText(ans));
		}

		// HTML-Ansicht der Frage erstellen
		questionInHTMLView = new JEditorPane();
		questionInHTMLView.setEditorKit(new HTMLEditorKit());
		questionInHTMLView.setEditable(false);
		questionInHTMLView.setContentType("text/html");
		// Base64 encoded images werden aus irgendeinem Grund nicht direkt angezeigt
		// also erst in Datei im Ordner /tmp schreiben!
		// nach : https://stackoverflow.com/questions/51103717/jeditorpane-content-type-for-html-embedded-base64-images
		String sitestring = "<html><body>" + Hilfsfunktionen.removeCData(q.getQuestiontext()) + "</body></html>";
		ArrayList<XMLObject> files = q.toXML().getChild("questiontext").getAllChildren("file");
		System.out.println("Diese Frage hat " + files.size() + " Files!");
		for (XMLObject t : files) {
			System.out.println("Filename: " + t.getAttribute("name") + " Path: " + t.getAttribute("path")
					+ " Encoding: " + t.getAttribute("encoding"));
			// in die entsprechende Datei in /tmp schreiben
		    byte[] decodedImg = Base64.getDecoder().decode(t.getContent().getBytes(StandardCharsets.UTF_8));
		    Path destinationFile = Paths.get("/tmp", t.getAttribute("name"));
			// Jetzt muss jeder src="@@PlUGINFILE@@..." ersetzt werden durch
			// src="data:image/png;base64,<base64codierterString>"
		    // bzw. den korrekten Pfad
			try {
		        Path path = Files.write(destinationFile, decodedImg);
				
		        //entsprechende kodierung des Dateinamens mit %-Zeichen 
				String u = URLEncoder.encode(t.getAttribute("name"),StandardCharsets.UTF_8.toString());
				u = "@@PLUGINFILE@@/"+u.replace("+", "%20"); //Leerzeichen nicht durch + sondern %20 ersetzen
				//System.out.println(u);
				//System.out.println(sitestring);
				sitestring = sitestring.replaceAll(u, path.toAbsolutePath().toUri().toString());
				//System.out.println(sitestring);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}

		}
		questionInHTMLView.setText(sitestring);
	
	
		// x y w h wx wy

		addComponent(c, gbl, nameLabel, 0, 0, 1, 1, 0, 0);
		addComponent(c, gbl, nameTextField, 1, 0, 2, 1, 1.0, 0);
		addComponent(c, gbl, questionLabel, 0, 1, 1, 1, 0, 0);
		addComponent(c, gbl, questionTextField, 1, 1, 2, 1, 1.0, 0);
		addComponent(c, gbl, new JLabel("Antworten: "), 0, 2, 1, 1, 0, 1.0);
		addComponent(c, gbl, scrollPaneAnswers, 1, 2, 2, 1, 1.0, 0);
		addComponent(c, gbl, new JLabel("Question(HTML)"), 0, 3, 1, 1, 0, 1.0);
		addComponent(c, gbl, questionInHTMLView, 1, 3, 2, 1, 1.0, 0);
		addComponent(c, gbl, new JButton("Cancel"), 0, 4, 1, 1, 0, 0);
		addComponent(c, gbl, new JButton("Save"), 1, 4, 1, 1, 0, 0);

		return c;
	}

	/** Creates the panel shown by the second tab. */
	private JPanel createOptionsPanel() {
		/*
		 * final int numButtons = 4; JRadioButton[] radioButtons = new
		 * JRadioButton[numButtons]; final ButtonGroup group = new ButtonGroup();
		 * 
		 * JButton showItButton = null;
		 * 
		 * final String defaultMessageCommand = "default"; final String yesNoCommand =
		 * "yesno"; final String yeahNahCommand = "yeahnah"; final String yncCommand =
		 * "ync";
		 * 
		 * radioButtons[0] = new JRadioButton("OK (in the L&F's words)");
		 * radioButtons[0].setActionCommand(defaultMessageCommand);
		 * 
		 * radioButtons[1] = new JRadioButton("Yes/No (in the L&F's words)");
		 * radioButtons[1].setActionCommand(yesNoCommand);
		 * 
		 * radioButtons[2] = new JRadioButton("Yes/No " +
		 * "(in the programmer's words)");
		 * radioButtons[2].setActionCommand(yeahNahCommand);
		 * 
		 * radioButtons[3] = new JRadioButton("Yes/No/Cancel " +
		 * "(in the programmer's words)"); radioButtons[3].setActionCommand(yncCommand);
		 * 
		 * for (int i = 0; i < numButtons; i++) { group.add(radioButtons[i]); }
		 * radioButtons[0].setSelected(true);
		 * 
		 * showItButton = new JButton("Show it!"); showItButton.addActionListener(new
		 * ActionListener() { public void actionPerformed(ActionEvent e) { String
		 * command = group.getSelection().getActionCommand();
		 * 
		 * //ok dialog if (command == defaultMessageCommand) {
		 * JOptionPane.showMessageDialog(frame, "Eggs aren't supposed to be green.");
		 * 
		 * //yes/no dialog } else if (command == yesNoCommand) { int n =
		 * JOptionPane.showConfirmDialog( frame, "Would you like green eggs and ham?",
		 * "An Inane Question", JOptionPane.YES_NO_OPTION); if (n ==
		 * JOptionPane.YES_OPTION) { setLabel("Ewww!"); } else if (n ==
		 * JOptionPane.NO_OPTION) { setLabel("Me neither!"); } else {
		 * setLabel("Come on -- tell me!"); }
		 * 
		 * //yes/no (not in those words) } else if (command == yeahNahCommand) {
		 * Object[] options = {"Yes, please", "No way!"}; int n =
		 * JOptionPane.showOptionDialog(frame, "Would you like green eggs and ham?",
		 * "A Silly Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
		 * null, options, options[0]); if (n == JOptionPane.YES_OPTION) {
		 * setLabel("You're kidding!"); } else if (n == JOptionPane.NO_OPTION) {
		 * setLabel("I don't like them, either."); } else {
		 * setLabel("Come on -- 'fess up!"); }
		 * 
		 * //yes/no/cancel (not in those words) } else if (command == yncCommand) {
		 * Object[] options = {"Yes, please", "No, thanks", "No eggs, no ham!"}; int n =
		 * JOptionPane.showOptionDialog(frame, "Would you like some green eggs to go " +
		 * "with that ham?", "A Silly Question", JOptionPane.YES_NO_CANCEL_OPTION,
		 * JOptionPane.QUESTION_MESSAGE, null, options, options[2]); if (n ==
		 * JOptionPane.YES_OPTION) { setLabel("Here you go: green eggs and ham!"); }
		 * else if (n == JOptionPane.NO_OPTION) { setLabel("OK, just the ham, then."); }
		 * else if (n == JOptionPane.CANCEL_OPTION) {
		 * setLabel("Well, I'm certainly not going to eat them!"); } else {
		 * setLabel("Please tell me what you want!"); } } return; } });
		 * 
		 * return createPane(simpleDialogDesc + ":", radioButtons, showItButton);
		 */
		return new JPanel();
	}

	private JPanel createAnswerPanel() {
		return new JPanel();
	}

}
