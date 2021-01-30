package aufgabenGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

public class Dateiaktionen {
	public static String liesTextDatei() {
		//System.out.println("Working Directory: " + System.getProperty("user.dir"));
		//System.out.println("\n| Datei einlesen |\n");

		// JFileChooser-Objekt erstellen
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		// Dialog zum Oeffnen von Dateien anzeigen
		int rueckgabeWert = chooser.showOpenDialog(null);

		/* Abfrage, ob auf "Öffnen" geklickt wurde */
		if (rueckgabeWert == JFileChooser.APPROVE_OPTION) {
			// Ausgabe der ausgewaehlten Datei
			//System.out.println("Die zu öffnende Datei ist: " + chooser.getSelectedFile().getName());
		} else {
			System.out.println("Programm beendet - keine Datei gewählt");
			return null;
		}

		// FileReader fr = new FileReader("src/bwinf38Rd2Abbiegen/"+args[0]);
		try {
			FileReader fr = null;
			fr = new FileReader(chooser.getSelectedFile().getAbsolutePath());

			BufferedReader reader = new BufferedReader(fr);

			//ArrayList<String> inhalt = new ArrayList<String>();
			StringBuffer inhalt = new StringBuffer();
			
			String line = reader.readLine();
			while (line != null) {
				//System.out.println("> "+line);
				//inhalt.add(line);
				inhalt.append(line+"\n");
				line = reader.readLine();
			}
	
			reader.close();
			return inhalt.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<String[]> liesDatensatz() {
		//System.out.println("Working Directory: " + System.getProperty("user.dir"));
		//System.out.println("\n| Datei einlesen |\n");

		// JFileChooser-Objekt erstellen
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		// Dialog zum Oeffnen von Dateien anzeigen
		int rueckgabeWert = chooser.showOpenDialog(null);

		/* Abfrage, ob auf "Öffnen" geklickt wurde */
		if (rueckgabeWert == JFileChooser.APPROVE_OPTION) {
			// Ausgabe der ausgewaehlten Datei
			//System.out.println("Die zu öffnende Datei ist: " + chooser.getSelectedFile().getName());
		} else {
			System.out.println("Programm beendet - keine Datei gewählt");
			return null;
		}

		// FileReader fr = new FileReader("src/bwinf38Rd2Abbiegen/"+args[0]);
		try {
			FileReader fr = null;
			fr = new FileReader(chooser.getSelectedFile().getAbsolutePath());

			BufferedReader reader = new BufferedReader(fr);
			int size=-1;

			ArrayList<String[]> inhalt = new ArrayList<String[]>();
			
			String line = reader.readLine();
			while (line != null) {
				//System.out.println("> "+line);
				String[] linesplit = line.split(";");
				if (size>=0 && size!=linesplit.length) {
					reader.close();
					throw new Exception("Anzahl Einträge sollte "+size+" sein: "+line);
				}
				size = linesplit.length;
				inhalt.add(linesplit);
				line = reader.readLine();
			}
	
			reader.close();
			return inhalt;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return null;
	}


}
