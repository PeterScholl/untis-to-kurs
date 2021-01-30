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
public class View 
{
	public static final int PANEL_XMLtemplate=2;
	public static final int PANEL_Questions=1;
	// instance variables - replace the example below with your own
	private int temp=1;
    private JFrame fenster;
    private JPanel center;
    private JTextArea textarea;
    private JScrollPane scrollPaneQuestions,scrollPaneTextArea;
    private JLabel dateinameLabel, statusLabel;
    private JList<String> fragenliste = new JList<String>(new String[] {});
    private Controller controller = null;
    
    /**
     * Constructor for objects of class GUI
     */
    public View(Controller c, String title) {
    	this.controller=c;
        fensterErzeugen(title);
    }
    
    public void fensterErzeugen(String title) {
    	if (title==null) title="Fenster";
        fenster = new JFrame(title);
        

        //Menü erzeugen
        JMenuBar menuezeile = new JMenuBar();
        fenster.setJMenuBar(menuezeile);

        JMenu dateimenue = new JMenu("Datei"); //Datei-Menue
        menuezeile.add(dateimenue);
        JMenuItem oeffnenEintrag = new JMenuItem("Multiple-Choice-Datei Öffnen");
        oeffnenEintrag.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { mcDateiOeffnen(); }
            });
        dateimenue.add(oeffnenEintrag);

        JMenuItem xmlSchabloneOeffnenEintrag = new JMenuItem("XML-Schablone Öffnen");
        xmlSchabloneOeffnenEintrag.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { controller.execute(Controller.XMLTemplate_lesen,null); }
            });
        dateimenue.add(xmlSchabloneOeffnenEintrag);

        JMenuItem xmlSpeichernEintrag = new JMenuItem("XML-Datei speichern");
        xmlSpeichernEintrag.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {xmlSpeichern(); }
            });
        dateimenue.add(xmlSpeichernEintrag);

        JMenuItem beendenEintrag = new JMenuItem("Beenden");
        beendenEintrag.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {beenden(); }
            });
        dateimenue.add(beendenEintrag);

        JMenu quizmenue = new JMenu("Quiz"); //Menue für die direkte Bearbeitung der Quizzes
        menuezeile.add(quizmenue);
        JMenuItem quizLoeschenEintrag = new JMenuItem("Quiz leeren");
        quizLoeschenEintrag.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {quizLoeschen();}
            });
        quizmenue.add(quizLoeschenEintrag);

        JMenu hilfemenue = new JMenu("Hilfe"); //Datei-Menue
        menuezeile.add(hilfemenue);
        JMenuItem infoEintrag = new JMenuItem("Info");
        infoEintrag.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {infoAusgeben();}
            });
        hilfemenue.add(infoEintrag);
        
        Container contentPane = fenster.getContentPane();
        
        contentPane.setLayout(new BorderLayout());
        
        dateinameLabel = new JLabel("Dateiname soll hier angezeigt werden");
        contentPane.add(dateinameLabel, BorderLayout.NORTH);
        
        //Kern des Fensters ist (zunächst) eine Liste mit den Fragen
        center = new JPanel(new BorderLayout());
        scrollPaneQuestions = new JScrollPane();
        scrollPaneQuestions.setViewportView(fragenliste);
        fragenliste.setLayoutOrientation(JList.VERTICAL);
        DefaultListModel<String> listmodel = new DefaultListModel<String>();
        fragenliste.setModel(listmodel);
        center.add(scrollPaneQuestions, BorderLayout.CENTER);
        contentPane.add(center, BorderLayout.CENTER);
        
        //Vorbereiten einer Textarea für andere Ansichten
        textarea = new JTextArea();
        scrollPaneTextArea = new JScrollPane(textarea);
        
        
        
        statusLabel = new JLabel("Ich bin das Status-Label");
        contentPane.add(statusLabel, BorderLayout.SOUTH);

        fenster.pack();
        fenster.setVisible(true);

    }

    /**
     * 'Datei oeffnen'-Funktion: Öffnet einen Dateiauswahldialog zur Auswahl einer Logical-datei 
     * und zeigt dieses an.
     */
    private void mcDateiOeffnen() {
    	controller.execute(Controller.MC_lesen, null);
        fenster.pack();
    }

	private void xmlSpeichern() {
		// TODO Auto-generated method stub
		
	}
	
    private void beenden() {
        //Abzuarbeitender Code, wenn auf beenden geclickt wurde    
        System.out.println("Beenden!");
        System.exit(0);
    }

    private void infoAusgeben() {
        //Abzuarbeitender Code, wenn auf Info geclickt wurde    
        System.out.println("Info!");
        temp=3-temp;
        switchToPanel(temp);
    }

    public void quizLoeschen() {
		controller.execute(Controller.Quiz_loeschen, null);
	}

    //******** Von außen aufzurufende Methoden ***********//
    
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
		textarea.setText((inhalt));
		switchToPanel(PANEL_XMLtemplate);
	}
	
	public void switchToPanel(int p) {
		switch(p) {
		case PANEL_Questions:
			dateinameLabel.setText("Quiz-View");
			center.removeAll();
			center.add(scrollPaneQuestions);
			break;
		case PANEL_XMLtemplate:
			dateinameLabel.setText("XML-Template-View");
			center.removeAll();
			center.add(scrollPaneTextArea);
			//center.revalidate();
			//center.repaint();
		default:
			break;	
		}
		center.revalidate();
		center.repaint();
	}
	
}

