
package org.unterrichtsportal.untistokurs;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * @author peter.scholl@aeg-online.de
 *
 */
public class TrafoUntisKurs {
	static String info = "Dies ist die Version vom 27.8.2017 \n Achtung Lehrerwechsel müssen händisch erledigt werden"
			+ "\n zunächst wird die GPU0001.txt aus Untis ausgewählt (Stundenplan)\nDann die Zielklassen auswählen"
			+ "\n\nInput in UTF-8 \n Output in ANSI-Format cp1252"; 

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Transformator t = new Transformator();
		ProtokollFenster.textAusgeben("Dies ist die Version vom 08.09.2017");
		ProtokollFenster.textAusgeben("Achtung Lehrerwechsel müssen händisch erledigt werden" );
		ProtokollFenster.textAusgeben("zunächst bitte die GPU0001.txt aus Untis auswählen (Stundenplan)");
		ProtokollFenster.textAusgeben("Dann die Zielklassenstufe auswählen");
		ProtokollFenster.textAusgeben(" ");
		ProtokollFenster.textAusgeben("Inputfile in UTF-8");
		ProtokollFenster.textAusgeben("Output in ANSI-Format cp1252");
		//JOptionPane.showMessageDialog(null, info, "Info", JOptionPane.OK_OPTION);
		//Create a file chooser
		final JFileChooser fc = new JFileChooser();
		//In response to a button click:
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
    		boolean result = t.readFile(file.getAbsolutePath());
    		if (! result 	) {
    			ProtokollFenster.textAusgeben("File konnte nicht geladen werden");
    		} else {
    			//Zielklasse auswählen

    	        String[] stufen = new String[]{"EF+Q1+Q2","EF","Q1", "Q2"};
    	        String message    = "Zielstufe auswählen:";
    	        String title      = "Stufenwahl";
    	        Object gewaehlteStufeO = JOptionPane.showInputDialog(null, message, title, JOptionPane.PLAIN_MESSAGE, null, stufen, stufen[0]);
    	        if (gewaehlteStufeO == null) {
    	        	ProtokollFenster.textAusgeben("keine Stufe ausgewaehlt");
    	        	System.exit(1);

    	        } else if (gewaehlteStufeO.toString().equals("EF+Q1+Q2")) {
    	        	for (String i: new String[] {"EF","Q1","Q2"}) {
    	        		String zieldatei = ""+file.getParent()+File.separator+"raumplan_"+i+".txt";
    	        		ProtokollFenster.textAusgeben("Zieldatei: "+zieldatei);
    	        		t.writeFile(zieldatei, i);
    	        	}
    	        } else {
    	        	String gewaehlteStufe = gewaehlteStufeO.toString();
    	        	ProtokollFenster.textAusgeben(gewaehlteStufe);

    	        	//File transformieren

    	        	String zieldatei = ""+file.getParent()+File.separator+"raumplan_"+gewaehlteStufe+".txt";
    	        	ProtokollFenster.textAusgeben("Zieldatei: "+zieldatei);
    	        	t.writeFile(zieldatei, gewaehlteStufe);
    	        }
    		}
		} else {
        	ProtokollFenster.textAusgeben("keinen passenden File gewählt");
        	System.exit(1);
        }
		//System.exit(0);
	}
	


}
