package aufgabenGenerator;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;

/**
 * In der Klasse GUI wird das Logical dargestellt
 * 
 * @author scholl@unterrichtsportal.org
 * @version 16.05.2016
 */
public class View implements MouseListener, KeyListener {
	public static final int PANEL_XMLtemplate = 2;
	public static final int PANEL_Questions = 1;
	public static final int PANEL_Database = 3;
	public static final int PANEL_MultiChoice = 4;

	private JFrame fenster;
	private JPanel center;
	private JTextArea textareaXML, textareaMC;
	private JScrollPane scrollPaneQuestions, scrollPaneTextAreaXML, scrollPaneTextAreaMC;
	private JLabel dateinameLabel, statusLabel;
	private JList<String> fragenliste = new JList<String>(new String[] {});
	private Controller controller = null;
	private Font generalfont = new Font("Dialog", Font.BOLD, 16);
	private JTextArea textareaDB;
	private JScrollPane scrollPaneTextAreaDB;
	private boolean debug=true;
	private final static String infotext = "Tool zum einfachen Erstellen von moodle-Tests\nVersion 0.2 von Peter Scholl\npeter.scholl@aeg-online.de\n"
			+ "\nHinweis: bei der Datensatz-Verwendung werden die Felder durch #1,#2,... angegeben, dabei steht #1 für "
			+ "den Wert in der ersten Spalte.";

	/**
	 * Constructor for objects of class GUI
	 */
	public View(Controller c, String title) {
		this.controller = c;
		fensterErzeugen(title);
	}

	public void fensterErzeugen(String title) {
		Hilfsfunktionen.setUIFont(new javax.swing.plaf.FontUIResource(generalfont));
		if (title == null)
			title = "Fenster";
		fenster = new JFrame(title);
		fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Menü erzeugen
		JMenuBar menuezeile = new JMenuBar();
		fenster.setJMenuBar(menuezeile);

		JMenu dateimenue = new JMenu("Datei"); // Datei-Menue
		menuezeile.add(dateimenue);
		JMenuItem oeffnenEintrag = new JMenuItem("Multiple-Choice-Datei Öffnen");
		oeffnenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mcDateiOeffnen();
			}
		});
		dateimenue.add(oeffnenEintrag);

		JMenuItem xmlSchabloneOeffnenEintrag = new JMenuItem("XML-Datei öffnen");
		xmlSchabloneOeffnenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.XMLTemplate_lesen, null);
			}
		});
		dateimenue.add(xmlSchabloneOeffnenEintrag);

		JMenuItem datensatzLadenEintrag = new JMenuItem("Datensatz laden");
		datensatzLadenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.Datensatz_lesen, null);
			}
		});
		dateimenue.add(datensatzLadenEintrag);

		JMenuItem xmlSpeichernEintrag = new JMenuItem("XML-Datei speichern");
		xmlSpeichernEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.XML_speichern, null);
			}
		});
		dateimenue.add(xmlSpeichernEintrag);

		JMenuItem beendenEintrag = new JMenuItem("Beenden");
		beendenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				beenden();
			}
		});
		dateimenue.add(beendenEintrag);

		JMenu quizmenue = new JMenu("Quiz"); // Menue für die direkte Bearbeitung der Quizzes
		menuezeile.add(quizmenue);
		JMenuItem quizLoeschenEintrag = new JMenuItem("Quiz leeren");
		quizLoeschenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quizLoeschen();
			}
		});
		quizmenue.add(quizLoeschenEintrag);

		JMenuItem quizToMCEintrag = new JMenuItem("Quiz nach MC-Datei konvertieren");
		quizToMCEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.QuizToMC, null );
			}
		});
		quizmenue.add(quizToMCEintrag);

		JMenuItem mcToQuizEintrag = new JMenuItem("MultiChoice nach Quiz konvertieren");
		mcToQuizEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.MCToQuiz, new String[] { textareaMC.getText() });
			}
		});
		quizmenue.add(mcToQuizEintrag);

		JMenuItem xmlToQuizEintrag = new JMenuItem("XML nach Quiz konvertieren");
		xmlToQuizEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.XMLToQuiz, new String[] { textareaXML.getText() });
			}
		});
		quizmenue.add(xmlToQuizEintrag);

		JMenuItem xmlToQuizMitDatensatzEintrag = new JMenuItem("XML2Quiz Datensatz");
		xmlToQuizMitDatensatzEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.XMLToQuizDS,
						new String[] { textareaXML.getText(), textareaDB.getText() });
			}
		});
		quizmenue.add(xmlToQuizMitDatensatzEintrag);

		JMenu ansichtmenue = new JMenu("Ansicht"); // Datei-Menue
		menuezeile.add(ansichtmenue);
		JMenuItem switchToXMLViewEintrag = new JMenuItem("XML-Ansicht");
		switchToXMLViewEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchToPanel(PANEL_XMLtemplate);
			}
		});
		ansichtmenue.add(switchToXMLViewEintrag);

		JMenuItem switchToQuizViewEintrag = new JMenuItem("Quiz-Ansicht");
		switchToQuizViewEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchToPanel(PANEL_Questions);
			}
		});
		ansichtmenue.add(switchToQuizViewEintrag);

		JMenuItem switchToDBViewEintrag = new JMenuItem("Database-Ansicht");
		switchToDBViewEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchToPanel(PANEL_Database);
			}
		});
		ansichtmenue.add(switchToDBViewEintrag);

		JMenuItem switchToMCViewEintrag = new JMenuItem("MultipleChoice-Ansicht");
		switchToMCViewEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchToPanel(PANEL_MultiChoice);
			}
		});
		ansichtmenue.add(switchToMCViewEintrag);

		ansichtmenue.addSeparator();

		JMenuItem schriftGroesserEintrag = new JMenuItem("Schrift +");
		schriftGroesserEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				increaseFontSize(fenster, 1);
				;
			}
		});
		ansichtmenue.add(schriftGroesserEintrag);

		JMenuItem schriftKleinerEintrag = new JMenuItem("Schrift -");
		schriftKleinerEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				increaseFontSize(fenster, -1);
				;
			}
		});
		ansichtmenue.add(schriftKleinerEintrag);
		
		JMenu quickmenue = new JMenu("Quick"); //Quick-Menue für schnelle vorgänge
		menuezeile.add(quickmenue);
		JMenuItem mcToXMLFile = new JMenuItem("MultiChoice laden und direkt in XML-File konvertieren");
		mcToXMLFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.Quiz_loeschen, null);
				mcDateiOeffnen();
				controller.execute(Controller.MCToQuiz, new String[] { textareaMC.getText() });
				controller.execute(Controller.XML_speichern, null);
			}
		});
		quickmenue.add(mcToXMLFile);
		

		JMenu hilfemenue = new JMenu("Hilfe"); // Datei-Menue
		menuezeile.add(hilfemenue);
		JMenuItem infoEintrag = new JMenuItem("Info");
		infoEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoAusgeben();
			}
		});
		hilfemenue.add(infoEintrag);

		JMenuItem mcBeispielEintrag = new JMenuItem("MultipleChoice Vorlage");
		mcBeispielEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.MCBeispielAusgeben, null);
			}
		});
		hilfemenue.add(mcBeispielEintrag);

		JMenuItem testEintrag = new JMenuItem("Testfunktion");
		testEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				testfunktion();
			}
		});
		hilfemenue.add(testEintrag);

		Container contentPane = fenster.getContentPane();

		contentPane.setLayout(new BorderLayout());

		dateinameLabel = new JLabel("Dateiname soll hier angezeigt werden");
		contentPane.add(dateinameLabel, BorderLayout.NORTH);

		// Kern des Fensters ist (zunächst) eine Liste mit den Fragen
		center = new JPanel(new BorderLayout());
		scrollPaneQuestions = new JScrollPane();
		scrollPaneQuestions.setViewportView(fragenliste);
		fragenliste.setLayoutOrientation(JList.VERTICAL);
		DefaultListModel<String> listmodel = new DefaultListModel<String>();
		fragenliste.setModel(listmodel);
		// fragenliste.addMouseListener(new TestMouseListener()); // nur zu Testzwecken
		fragenliste.addMouseListener(this);
		fragenliste.addKeyListener(this);
		center.add(scrollPaneQuestions, BorderLayout.CENTER);
		contentPane.add(center, BorderLayout.CENTER);

		// Vorbereiten einer Textarea für XML-Ansicht
		textareaXML = new JTextArea();
		textareaXML.setLineWrap(true);
		textareaXML.setWrapStyleWord(false);
		scrollPaneTextAreaXML = new JScrollPane(textareaXML);

		// Vorbereiten einer Textarea für Database-Ansicht
		textareaDB = new JTextArea();
		scrollPaneTextAreaDB = new JScrollPane(textareaDB);

		// Vorbereiten einer Textarea für MultipleChoice-Ansicht
		textareaMC = new JTextArea();
		scrollPaneTextAreaMC = new JScrollPane(textareaMC);

		statusLabel = new JLabel("Ich bin das Status-Label");
		contentPane.add(statusLabel, BorderLayout.SOUTH);

		// Hilfsfunktionen.fensterZentrieren(fenster);
		fenster.setLocation(200, 200);
		fenster.setPreferredSize(new Dimension(1200, 600));
		increaseFontSize(fenster, 0); // Alle Komponenten auf den gleichen Font setzen
		fenster.pack();
		fenster.setVisible(true);

	}

	/**
	 * 'Datei oeffnen'-Funktion: Öffnet einen Dateiauswahldialog zur Auswahl einer
	 * Logical-datei und zeigt dieses an.
	 */
	private void mcDateiOeffnen() {
		controller.execute(Controller.MC_lesen, null);
		fenster.pack();
	}

	private void beenden() {
		// Abzuarbeitender Code, wenn auf beenden geclickt wurde
		System.out.println("Beenden!");
		System.exit(0);
	}

	private void infoAusgeben() {
		// Abzuarbeitender Code, wenn auf Info geclickt wurde
		System.out.println("Info!");
		this.showInfoBox(infotext, "Info", 0);
	}

	private void testfunktion() {
		System.out.println("Testfunktion ausführen - XML-Parsen");
		controller.execute(Controller.Testfunktion, new String[] { textareaXML.getText() });
	}

	public void quizLoeschen() {
		controller.execute(Controller.Quiz_loeschen, null);
	}

	// ******** Von außen aufzurufende Methoden ***********//

	public void setStatusLine(String text) {
		statusLabel.setText(text);
	}

	public void writeList(String[] list) {
		DefaultListModel<String> listmodel = (DefaultListModel<String>) fragenliste.getModel();
		listmodel.removeAllElements();
		for (int i = 0; i < list.length; i++) {
			listmodel.addElement(list[i]);
		}
	}

	public void fillTextArea(String inhalt) {
		textareaXML.setText((inhalt));
		switchToPanel(PANEL_XMLtemplate);
	}

	public void fillDBArea(String inhalt) {
		textareaDB.setText((inhalt));
		switchToPanel(PANEL_Database);
	}

	public void fillMCArea(String inhalt) {
		textareaMC.setText((inhalt));
		switchToPanel(PANEL_MultiChoice);
	}

	public void switchToPanel(int p) {
		switch (p) {
		case PANEL_Questions:
			dateinameLabel.setText("Quiz-View");
			center.removeAll();
			center.add(scrollPaneQuestions);
			break;
		case PANEL_XMLtemplate:
			dateinameLabel.setText("XML-Template-View");
			center.removeAll();
			center.add(scrollPaneTextAreaXML);
			// center.revalidate();
			// center.repaint();
			break;
		case PANEL_Database:
			dateinameLabel.setText("Database-View");
			center.removeAll();
			center.add(scrollPaneTextAreaDB);
			break;
		case PANEL_MultiChoice:
			dateinameLabel.setText("MultipleChoice-View");
			center.removeAll();
			center.add(scrollPaneTextAreaMC);
			break;
		default:
			break;
		}
		center.revalidate();
		center.repaint();
	}

	public void increaseFontSize(Container parent, int inc) {
		generalfont = generalfont.deriveFont((float) (1.0 * generalfont.getSize() + (1.0 * inc)));
		Hilfsfunktionen.setUIFont(new javax.swing.plaf.FontUIResource(generalfont));
		increaseFontSizeRek(parent, inc);
	}

	public void increaseFontSizeRek(Container parent, int inc) {
		if (parent instanceof JMenu) {
			int icount = ((JMenu) parent).getItemCount();
			// System.out.println("JMenu found - Anz Component: "+icount);
			for (int i = 0; i < icount; i++)
				if (((JMenu) parent).getItem(i) != null)
					((JMenu) parent).getItem(i).setFont(generalfont);
		} else {
			for (Component c : parent.getComponents()) {
				// System.out.println(c.toString());
				Font font = c.getFont();
				// System.out.println("Font: " + font);
				if (font != null) {
					c.setFont(generalfont);
				}

				if (c instanceof Container)
					increaseFontSizeRek((Container) c, inc);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getSource() instanceof JList) {
			@SuppressWarnings("unchecked")
			JList<String> theList = (JList<String>) e.getSource();
			int index = theList.locationToIndex(e.getPoint());
			controller.execute(Controller.Question_anzeigen, new String[] { "" + index });
			if (index >= 0) {
				Object o = theList.getModel().getElementAt(index);
				System.out.println("Double-clicked on: " + o.toString());
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		//System.out.println("Pressed: " + e);
		if (e.isPopupTrigger() && e.getSource().equals(fragenliste)) {
			//System.out.println("Pop-UP-Menu der Fragenliste öffnen! - Mouse pressed");
			this.doPopMenuFragenliste(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//System.out.println("Released: " + e);
		if (e.isPopupTrigger() && e.getSource().equals(fragenliste)) {
			//System.out.println("Pop-UP-Menu der Fragenliste öffnen! - Mouse released");
			this.doPopMenuFragenliste(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(fragenliste)) {
			System.out.println("Key in Liste gedrückt - bei Element " + fragenliste.getSelectedIndex());
			int code = e.getKeyCode();

			switch (code) {
			case KeyEvent.VK_UP:
				System.out.println("UP " + fragenliste.getSelectedIndex());
				break;
			case KeyEvent.VK_DOWN:
				System.out.println("DOWN " + fragenliste.getSelectedIndex());
				break;
			case KeyEvent.VK_DELETE:
				System.out.println("Del " + fragenliste.getSelectedValue());
				System.out.println("Delete Indices: " + Arrays.toString(fragenliste.getSelectedIndices()));
				String frage = "Sind Sie sicher, dass Sie die gewählten " + fragenliste.getSelectedIndices().length
						+ " Elemente löschen wollen?";
				if (JOptionPane.YES_OPTION == Hilfsfunktionen.sindSieSicher(fenster, frage)) {
					String[] args = new String[fragenliste.getSelectedIndices().length];
					for (int i = 0; i < args.length; i++)
						args[i] = "" + fragenliste.getSelectedIndices()[i];
					controller.execute(Controller.Delete_Questions, args);
				} else {
					this.setStatusLine("Löschen abgebrochen");
				}
			}
		}

	}

	private void doPopMenuFragenliste(MouseEvent e) {
		JPopupMenu menu = new JPopupMenu();
		
		JMenuItem questionToXMLEintrag = new JMenuItem("Frage nach XML");
		questionToXMLEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int index = fragenliste.locationToIndex(e.getPoint());				
				controller.execute(Controller.QuestionToXML, new String[] {""+index});
			}
		});
		menu.add(questionToXMLEintrag);

		JMenuItem quizToXMLEintrag = new JMenuItem("Quiz nach XML");
		quizToXMLEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				controller.execute(Controller.QuizToXML, null);
			}
		});
		menu.add(quizToXMLEintrag);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
	
	/**
	 * Erzeugt eine Infobox mit der Nachricht und dem Titel, die für timerms Millisekunden
	 * angezeigt wird, wenn timerms>0 ist
	 * @param message Nachricht
	 * @param title Title der Box
	 * @param timerms wenn größer als 0, verschwindet die Box nach dieser Zeit von ms
	 */
	public void showInfoBox(String message, String title, int timerms) {
		JDialog d = createDialog(fenster, message, title, timerms);
		d.setLocationRelativeTo(fenster);
		//JFrame parent = fenster;
		//d.setLocation(parent.getX() + parent.getWidth()/2, parent.getY());
		debug("Setting Dialog visible");
		long time = System.nanoTime();
		d.setVisible(true);
		debug("Dialog - back form being visible " + (System.nanoTime() - time) + "ns");
	}

	private static JDialog createDialog(final JFrame frame, String message, String title, int timerms) {
		final JDialog modelDialog = new JDialog(frame, title, Dialog.ModalityType.DOCUMENT_MODAL);
		modelDialog.setBounds(132, 132, 500, 200);
		modelDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Container dialogContainer = modelDialog.getContentPane();
		dialogContainer.setLayout(new BorderLayout());
		// Textarea mit dem Text erzeugen
		JTextArea textarea = new JTextArea(message);
		// textarea.setFont(View.generalfont);
		textarea.setFont(new Font("monospaced", frame.getFont().getStyle(), frame.getFont().getSize()));
		textarea.setAlignmentX(JTextArea.CENTER_ALIGNMENT);
		textarea.setEditable(false);
		textarea.setWrapStyleWord(true);
		textarea.setLineWrap(true);
		dialogContainer.add(textarea, BorderLayout.CENTER);
		JPanel panel1 = new JPanel();
		panel1.setLayout(new FlowLayout());
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modelDialog.setVisible(false);
			}
		});
		modelDialog.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				okButton.requestFocusInWindow();
			}
		});
		if (timerms > 0) {
			Timer timer = new Timer(timerms, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					modelDialog.setVisible(false);
					modelDialog.dispose();
				}
			});
			timer.setRepeats(false);
			timer.start();
		}

		panel1.add(okButton);
		dialogContainer.add(panel1, BorderLayout.SOUTH);
		return modelDialog;
	}
	private void debug(String text) {
		if (debug)
			System.out.println("V:" + text);
	}

}
