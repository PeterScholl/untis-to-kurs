package org.unterrichtsportal.scholl.kellerautomat;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class Automatentest {

	public static void main(String[] args) {
		Kellerautomat k = new Kellerautomat();

		//Create a file chooser
		final JFileChooser fc = new JFileChooser();
		//In response to a button click:
		int returnVal = fc.showOpenDialog(new JFrame("test"));
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
    		boolean result = k.readFile(file.getAbsolutePath());
    		if (! result 	) {
    			System.out.println("File konnte nicht geladen werden");
    		} else {
    			System.out.println("Teste Wort: aabbcccc");
    			k.testeWort("aabbcccc");
    			System.out.println("Teste Wort: aa");
    			k.testeWort("aa");
    		}
		} else {
        	System.out.println("keinen passenden File gewählt");
        	System.exit(1);
        }
           
		
		
	}

}
