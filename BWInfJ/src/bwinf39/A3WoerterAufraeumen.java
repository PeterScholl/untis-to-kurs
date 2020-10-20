package bwinf39;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class A3WoerterAufraeumen {
	String lueckentext;
	String[] woerter;
	ArrayList<Luecke> luecken;
	boolean[][] passung;

	public class Luecke {
		int start, ende, wortnr;
		String schablone = "";

		/**
		 * @param start
		 * @param laenge
		 */
		public Luecke(int start, int laenge, String schablone) {
			this.start = start;
			this.ende = laenge;
			this.wortnr = -1;
			this.schablone = schablone;
		}

		public int getStart() {
			return start;
		}

		public void setStart(int start) {
			this.start = start;
		}

		public int getEnde() {
			return ende;
		}

		public void setEnde(int laenge) {
			this.ende = laenge;
		}

		public int getWortnr() {
			return wortnr;
		}

		public void setWortnr(int wortnr) {
			this.wortnr = wortnr;
		}

		public String getSchablone() {
			return schablone;
		}

		public void setSchablone(String schablone) {
			this.schablone = schablone;
		}

		public boolean passt(String wort) {
			// System.out.println(this.schablone+":"+wort+":");
			if (wort.length() != schablone.length())
				return false;
			for (int i = 0; i < wort.length(); i++) {
				// System.out.print(schablone.charAt(i)+"-"+wort.charAt(i)+":");
				if (schablone.charAt(i) != '_' && (schablone.charAt(i) != wort.charAt(i))) {
					// System.out.println("false");
					return false;
				}
				// System.out.println("true");
			}
			// System.out.println("gesamt: true");
			return true;
		}

		@Override
		public String toString() {
			return "L[" + start + "," + ende + ":" + wortnr + "]";
		}

		public boolean istZugeordnet() {
			return wortnr != -1;
		}

	}

	public static void main(String[] args) {
		// Testphase mit GUI
		boolean g = true;
		if (g) {
			new GUI();
		} else {
			A3WoerterAufraeumen a3 = new A3WoerterAufraeumen();
			a3.init();
			a3.ordneLueckenWoerterZu();
			System.out.println(a3);
		}
	}

	public void init() {
		// Abzuarbeitender Code, wenn die Aufgabe gestartet wird
		// Dateiauswahldialog starten - die Elemente aus der Datei in die Attribute
		// schreiben
		
		// alle Attribute zurücksetzen
		woerter = null;
		luecken = null;
		passung = null;
		lueckentext = "";
		
		JFileChooser dateiauswahldialog = new JFileChooser(System.getProperty("user.dir"));
		int ergebnis = dateiauswahldialog.showOpenDialog(null);

		if (ergebnis != JFileChooser.APPROVE_OPTION) {
			return; // abgebrochen
		}
		File selektierteDatei = dateiauswahldialog.getSelectedFile();
		try {
			FileReader fr = new FileReader(selektierteDatei);
			BufferedReader br = new BufferedReader(fr);

			lueckentext = br.readLine();

			String zeile = br.readLine();
			woerter = zeile.split(" ");

			zeile = br.readLine();
			while (zeile != null) {
				System.out.println(zeile);
				zeile = br.readLine();
			}
			br.close();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		} catch (java.lang.NumberFormatException ex) {
			System.out.println("Formatfehler in der Datei");
			JOptionPane.showMessageDialog(null, "Datei hat das falsche Format", "Warnung", JOptionPane.ERROR_MESSAGE);

		}
		findeLuecken();
	}

	public void findeLuecken() {
		luecken = new ArrayList<Luecke>(); // es sollte so viele Luecken geben wie Wörter
		for (int i = 0; i < lueckentext.length(); i++) { // den ganzen Lückentext durchlaufen
			Character c = lueckentext.charAt(i);
			if (c == '_' || Character.isAlphabetic(c)) { // start gefunden
				int start = i;
				while (i < lueckentext.length() && (c == '_' || Character.isAlphabetic(c))) {
					i++;
					c = lueckentext.charAt(i);
				}
				luecken.add(new Luecke(start, i, lueckentext.substring(start, i)));
			}
		}
	}

	public void passungsMatrixFuellen() {
		passung = new boolean[woerter.length][luecken.size()];
		for (int i=0; i<woerter.length; i++) {
			for (int j=0; j<luecken.size(); j++) {
				if (luecken.get(j).passt(woerter[i])) {
					passung[i][j] = true;
				} else {
					passung[i][j] = false;
				}
				
			}
		}
	}
	
	public void ordneLueckenWoerterZu() {
		if (passung == null || passung.length < woerter.length || passung[0].length < luecken.size()) passungsMatrixFuellen();
		boolean[] wzugeordnet = new boolean[woerter.length];
		boolean[] lzugeordnet = new boolean[luecken.size()];
		for (int i=0; i<woerter.length; i++) wzugeordnet[i] = false;
		for (int i=0; i<luecken.size(); i++) lzugeordnet[i] = false;
		boolean zugeordnet = true;
		while (zugeordnet) { // solange mindestens eine Zurdnung stattgefunden hat
			zugeordnet = false;
			for (int w=0; w<woerter.length; w++) {
				int count = 0;
				int partner = -1;
				if (!wzugeordnet[w]) {
					for (int l=0; l<luecken.size(); l++) {
						if (!lzugeordnet[l] && passung[w][l] ) {
							partner = l;
							count++;
						}
					}
					if (count==1) { // genau einen Partner gefunden
						zugeordnet = true;
						luecken.get(partner).setWortnr(w);
						wzugeordnet[w] = true;
						lzugeordnet[partner] = true;
					}
				}
			}
			for (int l=0; l<luecken.size(); l++) {
				int count = 0;
				int partner = -1;
				if (!lzugeordnet[l]) {
					for (int w=0; w<woerter.length; w++) {
						if (!wzugeordnet[w] && passung[w][l] ) {
							if (partner > -1 && woerter[w].equals(woerter[partner])) count--; //identische Woerter
							partner = w;
							count++;
						}
					}
					if (count==1) { // genau einen Partner gefunden
						zugeordnet = true;
						luecken.get(l).setWortnr(partner);
						wzugeordnet[partner] = true;
						lzugeordnet[l] = true;
					}
				}
			}

		}
	}
	
	public String loesungstextGefuellt() {
		char[] out = lueckentext.toCharArray();
		for (Luecke l : luecken) {
			if (l.istZugeordnet()) {
				String w = woerter[l.getWortnr()];
				for (int j=l.getStart(); j<l.getEnde(); j++) {
					out[j]=w.charAt(j-l.getStart());
				}
			}
		}
		return new String(out);
	}

	@Override
	public String toString() {
		String out = "A3WoerterAufraeumen [lueckentext=" + lueckentext + ", woerter=" + Arrays.toString(woerter)
				+ ", luecken=" + luecken + "]\n";
		out += "Lösung: ";
		out += loesungstextGefuellt();
		return out;
	}

}
