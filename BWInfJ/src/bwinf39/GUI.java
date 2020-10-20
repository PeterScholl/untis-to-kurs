package bwinf39;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//Für Dateioperationen
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * In der Klasse GUI wird die Maschine dargestellt
 * 
 * @author scholl@unterrichtsportal.org
 * @version 28.09.2020
 */
public class GUI 
{
    // instance variables - replace the example below with your own
    private JFrame fenster;
    private JLabel statusLabel;
    private JButton[] faecher = new JButton[10];
    private JButton packen;
    private JTextArea outTextArea;
    private Aufgabe1 a1 = new Aufgabe1();
    private A3WoerterAufraeumen a3 = new A3WoerterAufraeumen();
    /**
     * Constructor for objects of class GUI
     */
    public GUI() {
        fensterErzeugen();
    }

    public void fensterErzeugen() {
        fenster = new JFrame("BWInf-GUI 39");

        menueLeisteErzeugen();

        Container contentPane = fenster.getContentPane();

        contentPane.setLayout(new BorderLayout());

        //dateinameLabel = new JLabel("Dateiname soll hier angezeigt werden");
        //contentPane.add(dateinameLabel, BorderLayout.NORTH);
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        JPanel b = new JPanel(); // b enthaelt die Buttons zum Packen
        b.setLayout(new GridLayout(1,10,30,10));

        for (int i = 0; i<10; i++) {
            faecher[i]=new JButton();
            faecher[i].setText(""+a1.gibInhaltFach(i));
            //faecher[i].setMargin(new Insets(0,0,0,0)); //no Padding
            faecher[i].setMaximumSize(new Dimension(60,30));
            faecher[i].setMinimumSize(new Dimension(60,30));
            faecher[i].setPreferredSize(new Dimension(60,30));
            faecher[i].setActionCommand(""+i);
            faecher[i].addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent ae)
                    {
                        JButton but = (JButton) ae.getSource();
                        a1.fach(Integer.parseInt(but.getActionCommand()));     
                        statusAktualisieren();
                    }
                });
            b.add(faecher[i]);
        }
        c.add(b);

        packen = new JButton();
        packen.setText("Packen");
        packen.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ae)
                {
                    a1.verpacken();
                    statusAktualisieren();
                }
            });
        c.add(packen);
        
        outTextArea = new JTextArea();
        outTextArea.setText("Textarea"); // Mit erstem Text füllen
        outTextArea.setLineWrap(true); //Zeilenumbruch aktivieren
        outTextArea.setEditable(true); // In das Textfeld darf geschrieben werden
        outTextArea.setFont(new Font("monospaced",Font.PLAIN,12)); // Zeichensatz wählen, bei dem die Zeichen feste Laenge haben
        outTextArea.setPreferredSize(new Dimension(400,200));
        
        // Textarea in eine Scrollpane setzen - so dass bei viel Text gescrollt werden kann
        JScrollPane scrollpane1 = new JScrollPane(outTextArea); 
        scrollpane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); //Vertikaler Scrollbalken wird immer angezeigt
        scrollpane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); //Horzizontal nur bei Bedarf
        //scrollpane1.setBounds(10, 50, 280, 190);

        // Scrollpane ins GUI (darin ist die Textarea, diese muss also nicht eingepflegt werden)
        contentPane.add(scrollpane1, BorderLayout.CENTER);

        contentPane.add(c, BorderLayout.NORTH);

        // b = new JPanel(); //??

        statusLabel = new JLabel("Ich bin das Status-Label");
        contentPane.add(statusLabel, BorderLayout.SOUTH);

        fenster.pack();
        fenster.setVisible(true);

    }

    private void menueLeisteErzeugen() {
        //Menü erzeugen
        JMenuBar menuezeile = new JMenuBar();
        fenster.setJMenuBar(menuezeile);

        //Menue zur Aufgabe Woerter aufraeumen (Nr3)
        JMenu woerterAufraeumenMenue = new JMenu("Wörter aufräumen");
        menuezeile.add(woerterAufraeumenMenue);
        
        JMenuItem wainit = new JMenuItem("initialisieren");
        wainit.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		a3.init();
        		a3.ordneLueckenWoerterZu();
        		outTextArea.setText(a3.toString());}
        });
        woerterAufraeumenMenue.add(wainit);
        
        //Datei-Menue
        JMenu dateimenue = new JMenu("Datei"); 
        menuezeile.add(dateimenue);
        JMenuItem oeffnenEintrag = new JMenuItem("Öffnen");
        oeffnenEintrag.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { dateiOeffnen(); }
            });
        dateimenue.add(oeffnenEintrag);

        JMenuItem beendenEintrag = new JMenuItem("Beenden");
        beendenEintrag.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {beenden(); }
            });
        dateimenue.add(beendenEintrag);

        //Spiel-Menue
        JMenu spielmenue = new JMenu("Spiel"); 
        menuezeile.add(spielmenue);
        JMenuItem addZeileZufallsQ = new JMenuItem("Zufallsqueue erzeugen");
        addZeileZufallsQ.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    //Queue neu erstellen mit Zufallszahlen
                    a1.erzeugeZufallsSchlange(50);
                }
            });
        spielmenue.add(addZeileZufallsQ);

        JMenuItem addZeileLeereQ = new JMenuItem("Queue leeren");
        addZeileLeereQ.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    //Queue neu erstellen mit Zufallszahlen
                    a1.leereSchlange();
                    statusAktualisieren();
                }
            });
        spielmenue.add(addZeileLeereQ);
        
        JMenuItem addZeileStart = new JMenuItem("Maschine starten");
        addZeileStart.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    a1.starteNeu();
                    statusAktualisieren();
                }
            });
        spielmenue.add(addZeileStart);

        //Hilfe-Menue
        JMenu hilfemenue = new JMenu("Hilfe"); 
        menuezeile.add(hilfemenue);
        JMenuItem infoEintrag = new JMenuItem("Info");
        infoEintrag.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {infoAusgeben();}
            });
        hilfemenue.add(infoEintrag);

    }

    /**
     * 'Datei oeffnen'-Funktion: Öffnet einen Dateiauswahldialog zur Auswahl einer Logical-datei 
     * und zeigt dieses an.
     */
    private void dateiOeffnen() {
        //Abzuarbeitender Code, wenn auf öffnen geclickt wurde  
        //Dateiauswahldialog starten - die Elemente einzeln an die Schlange anhängen
        JFileChooser dateiauswahldialog = new JFileChooser(System.getProperty("user.dir"));
        int ergebnis = dateiauswahldialog.showOpenDialog(null);

        if(ergebnis != JFileChooser.APPROVE_OPTION) {
            return;  // abgebrochen
        }
        File selektierteDatei = dateiauswahldialog.getSelectedFile();
        try {
            FileReader fr = new FileReader(selektierteDatei);
            BufferedReader br = new BufferedReader(fr);

            String zeile = br.readLine();
            while (zeile!=null) {
                a1.haengeElementAnSchlangeAn(Integer.parseInt(zeile));
                zeile=br.readLine();
            }
            br.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (java.lang.NumberFormatException ex) {
            System.out.println("Formatfehler in der Datei");
            JOptionPane.showMessageDialog(null,"Datei hat das falsche Format", 
                "Warnung", JOptionPane.ERROR_MESSAGE);

        }

        fenster.pack();
    }

    private void beenden() {
        //Abzuarbeitender Code, wenn auf öffnen geclickt wurde    
        System.out.println("Beenden!");
        System.exit(0);
    }

    private void infoAusgeben() {
        //Abzuarbeitender Code, wenn auf öffnen geclickt wurde    
        JOptionPane.showMessageDialog(null,"LuftballonVerpackungsMaschine zur\n"+
            "Anwendung von Queues\n"+
            "AlbertEinsteinGymnasium 2016.", "information", JOptionPane.INFORMATION_MESSAGE);

    }

    private void statusAktualisieren() {
        String txt = "";
        txt += "Summe: "+a1.gibInhaltAusgabefach()+" ";
        txt += "Packungen: "+a1.gibAnzahlBefuelltePackete()+" ";
        txt += "davon zu voll: "+a1.gibAnzahlFalscherPackete()+" ";
        txt += "Queue leer: "+a1.istSchlangeLeer();
        statusLabel.setText(txt);
        packen.setText("Packen ("+a1.gibInhaltAusgabefach()+")");
        for (int i=0; i<10; i++) {
            faecher[i].setText(""+a1.gibInhaltFach(i));
        }
    }
    
    public static void main(String[] args) {
    	new GUI();
    }
}

