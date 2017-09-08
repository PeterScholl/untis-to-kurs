
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
		JOptionPane.showMessageDialog(null, info, "Info", JOptionPane.OK_OPTION);
		//Create a file chooser
		final JFileChooser fc = new JFileChooser();
		//In response to a button click:
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
    		boolean result = t.readFile(file.getAbsolutePath());
    		if (! result 	) {
    			System.out.println("File konnte nicht geladen werden");
    		} else {
    			//Zielklasse auswählen

    	        String[] stufen = new String[]{"EF","Q1", "Q2"};
    	        String message    = "Zielstufe auswählen:";
    	        String title      = "Stufenwahl";
    	        Object gewaehlteStufeO = JOptionPane.showInputDialog(null, message, title, JOptionPane.PLAIN_MESSAGE, null, stufen, stufen[0]);
    	        if (gewaehlteStufeO == null) {
    	        	System.out.println("keine Stufe ausgewaehlt");
    	        	System.exit(1);

    	        }
    	        String gewaehlteStufe = gewaehlteStufeO.toString();
    	        System.out.println(gewaehlteStufe);
    	        
    			//File transformieren
    			String zieldatei = ""+file.getParent()+"\\raumplan_"+gewaehlteStufe+".txt";
    			System.out.println("Zieldatei: "+zieldatei);
    			t.writeFile(zieldatei, gewaehlteStufe);
    		}
		} else {
        	System.out.println("keinen passenden File gewählt");
        	System.exit(1);
        }
	}
	


}
