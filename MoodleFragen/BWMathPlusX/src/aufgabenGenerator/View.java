package aufgabenGenerator;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * In der Klasse GUI wird das Logical dargestellt
 * 
 * @author scholl@unterrichtsportal.org
 * @version 16.05.2016
 */
public class View 
{
    // instance variables - replace the example below with your own
    private JFrame fenster;
    private JLabel dateinameLabel, statusLabel;
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
    }

	public void setStatusLine(String text) {
		statusLabel.setText(text);		
	}
}

