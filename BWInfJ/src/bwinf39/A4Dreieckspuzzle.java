package bwinf39;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class A4Dreieckspuzzle {
	private Feld meinFeld;
	private Karte[] karten;
	public static final int NN = 0;
	public static final int NO = 1;
	public static final int SO = 2;
	public static final int SS = 3;
	public static final int SW = 4;
	public static final int NW = 5;

	// **** innere Klasse Karte ***
	private class Karte {
		private int[] figuren = new int[3]; // bei pos=0 NN,SO,SW
		private int pos = 0;

		public Karte(int f1, int f2, int f3) {
			figuren[0] = f1;
			figuren[1] = f2;
			figuren[2] = f3;
		}

		public void setPos(int p) {
			if (p >= 0 && p <= 5)
				pos = p;
		}

		public void turn(int t) {
			pos = (pos + t) % 6;
		}

		public int getFig(int pos) {
			if (((6+pos - this.pos) % 2) == 1)
				return 0;
			return figuren[(((pos - this.pos) / 2) + 6) % 3];
		}
	}
	// **** ENDE innere Klasse Karte ***

	// **** innere Klasse Feld ****
	private class Feld {
		private int zeile, spalte;
		private int[] followerrichtung = new int[] {};
		private Feld[] follower = new Feld[] {};
		private int[] neighbourrichtung = new int[] {};
		private Feld[] neighbours = new Feld[] {};
		Karte k = null;

		public Feld(int max) {
			this.zeile = 0;
			this.spalte = 0;
			this.followerrichtung = new int[] { SS };
			this.follower = new Feld[] { new Feld(this, max, 1, 1) };
			this.initNeighbours(this);
		}

		private void initNeighbours(Feld root) {
			// Welche Lage hat mein Feld
			int[] richtungen;
			if (this.spalte % 2 == 0)
				richtungen = new int[] { NO, SS, NW };
			else
				richtungen = new int[] { NN, SO, SW };
			Feld[] nTemp = new Feld[3];
			int count = 0; // Zähler für Nachbarn
			for (int i = 0; i < richtungen.length; i++) {
				if (!this.inFollower(richtungen[i])) { // da könnte ein Nachbar sein, der kein Follower ist
					Feld t = null;
					switch (richtungen[i]) {
					case NN:
						t = root.gibFeldIn(this.zeile - 1, this.spalte - 1);
						break;
					case NO:
					case SO:
						t = root.gibFeldIn(this.zeile, this.spalte + 1);
						break;
					case SS:
						t = root.gibFeldIn(this.zeile + 1, this.spalte + 1);
						break;
					case SW:
					case NW:
						t = root.gibFeldIn(this.zeile, this.spalte - 1);
						break;
					}
					if (t != null) { // neighbour gefunden
						nTemp[count] = t;
						richtungen[count] = richtungen[i];
						count++;
					}
				}
			}
			if (count > 0) {
				neighbourrichtung = new int[count];
				neighbours = new Feld[count];
				for (int i1 = 0; i1 < count; i1++) {
					neighbourrichtung[i1] = richtungen[i1];
					neighbours[i1] = nTemp[i1];
				}
			}
			// Bei allen Followern sollte jetzt auch noch initialisiert werden
			for (Feld feld : this.follower) {
				feld.initNeighbours(root);
			}
		}

		private void setKarte(Karte k) {
			this.k = k;
			// sinnvolle Grundposition
			k.setPos(1 - (spalte % 2));
		}

		private void removeKarte() {
			this.k = null;
		}

		private boolean inFollower(int richtung) {
			for (int i = 0; i < this.followerrichtung.length; i++) {
				if (richtung == this.followerrichtung[i])
					return true;
			}
			return false;
		}

		private Feld(Feld root, int max, int zeile, int spalte) {
			this.zeile = zeile;
			this.spalte = spalte;
			// System.out.println("Neues Feld: "+max+"-"+zeile+"-"+spalte);
			if ((spalte == 0) && (zeile < max)) {
				this.followerrichtung = new int[] { SS };
				this.follower = new Feld[] { new Feld(root, max, zeile + 1, 1) };
			} else if (spalte == 1) {
				this.followerrichtung = new int[] { SO, SW };
				this.follower = new Feld[] { new Feld(root, max, zeile, 2), new Feld(root, max, zeile, 0) };
			} else if (spalte < 2*zeile && spalte > 1) {
				if (spalte % 2 == 0) { // gerade spalten
					this.followerrichtung = new int[] { NO };
					this.follower = new Feld[] { new Feld(root, max, zeile, spalte + 1) };
				} else { // ungerade Spalten
					this.followerrichtung = new int[] { SO };
					this.follower = new Feld[] { new Feld(root, max, zeile, spalte + 1) };
				}
			} else { // Zeilenende oder Spaltenende - keine Nachfolger
				this.followerrichtung = new int[] {};
				this.follower = new Feld[] {};
			}
		}

		public void traverseOut(int l) {
			for (int i = 0; i < l; i++)
				System.out.print("-");
			System.out.println("zeile: " + this.zeile + " spalte: " + this.spalte + " followerlength: "
					+ follower.length + " neighbourlength: " + neighbours.length);
			for (Feld feld : follower) {
				feld.traverseOut(l + 1);
			}
		}

		public ArrayList<Feld> traverse() {
			ArrayList<Feld> out = new ArrayList<Feld>();
			out.add(this);
			for (Feld feld : follower) {
				out.addAll(feld.traverse());
			}
			return out;
		}

		public Feld gibFeldIn(int zeile, int spalte) {
			if (this.zeile == zeile && this.spalte == spalte)
				return this;
			if (follower.length == 1)
				return follower[0].gibFeldIn(zeile, spalte);
			if (follower.length == 0)
				return null;
			Feld t = follower[0].gibFeldIn(zeile, spalte);
			if (t != null)
				return t;
			return follower[1].gibFeldIn(zeile, spalte);
			/*
			 * System.out.println(this + ".gibFeld("+zeile+","+spalte+")"+
			 * "followerlength: "+follower.length); if (zeile > this.zeile &&
			 * follower.length > 0) { System.out.println("hier"); return
			 * follower[0].gibFeldIn(zeile, spalte); } else if (zeile == this.zeile) { if
			 * (spalte == this.spalte) { return this; } else if (follower.length == 2) {
			 * return this.follower[0].gibFeldIn(zeile, spalte); } else if (follower.length
			 * == 1) { return this.follower[0].gibFeldIn(zeile, spalte); } } return null;
			 */
		}

		public int gibFigur(int richtung) {
			if (this.k == null)
				return 0;
			return k.getFig(richtung);
		}

		public boolean hatKarte() {
			return k != null;
		}

		public boolean passtAktuelleKarte() {
			if (!hatKarte())
				return true;
			for (int i = 0; i < followerrichtung.length; i++) {
				if (follower[i].hatKarte() && this.gibFigur(followerrichtung[i])
						+ follower[i].gibFigur(A4Dreieckspuzzle.gegenrichtung(followerrichtung[i])) != 0) {
					return false;
				}
			}
			for (int i = 0; i < neighbourrichtung.length; i++) {
				if (neighbours[i].hatKarte() && this.gibFigur(neighbourrichtung[i])
						+ neighbours[i].gibFigur(A4Dreieckspuzzle.gegenrichtung(neighbourrichtung[i])) != 0) {
					return false;
				}
			}
			return true;
		}

		public void turn() {
			if (this.hatKarte())
				k.turn(2);
		}

		@Override
		public String toString() {
			return "Feld [zeile=" + zeile + ", spalte=" + spalte + ", followerrichtung="
					+ Arrays.toString(followerrichtung) + "]";
		}
		
		public String gibZeileAlsString(int nr, int laenge) {
			String out="";
			int l,w;
			switch(nr) {
			case 1: // nur NN
				w = this.gibFigur(NN);
				if (w>0) out+="+"+w;
				else if (w<0) out+=""+w;
				l = (laenge-out.length())/2;
				return addLeerzeichen(l, addLeerzeichen(-laenge+out.length()+l, out));
			case 2: // XO und XW
				int wo = this.gibFigur(NO)+this.gibFigur(SO);
				int ww = this.gibFigur(NW)+this.gibFigur(SW);
				if (wo>0) out+="+"+wo;
				else if (wo<0) out+=""+wo;
				String out2 = ""+ww;
				if (ww>0) out2="+"+out2;
				l = (laenge-out.length()-out2.length());
				return out2+addLeerzeichen(-l, out);
			case 3: // nur SS
				w = this.gibFigur(SS);
				if (w>0) out+="+"+w;
				else if (w<0) out+=""+w;
				l = (laenge-out.length())/2;
				return addLeerzeichen(l, addLeerzeichen(-laenge+out.length()+l, out));				
			}
			return out;
		}
		
		private String addLeerzeichen(int anz, String b) {
			if (anz==0) return b;
			else if (anz<0) return addLeerzeichen(anz+1," "+b);
			else return addLeerzeichen(anz-1,b+" ");
		}
		
		public int gibMaxZeile() {
			int z = this.zeile;
			for (Feld f : this.follower) {
				int t = f.gibMaxZeile();
				z = (t>z)?t:z;
			}
			return z;
		}
		
		public String gibKomplettesFeldAlsString(int laenge) {
			String out="";
			int anzZeilen=this.gibMaxZeile()-this.zeile+1;
			for (int i=0; i<anzZeilen; i++) { // Alle Zeilen des Feldes durcharbeiten
				for (int k=1;k<=3;k++) { // Jede Karte besteht aus drei Zeilen
					out+=addLeerzeichen((anzZeilen-i-1)*laenge, "");
					for (int j=0; j<2*i+1; j++) { //dort vorhandenen Spalten
						out+=this.gibFeldIn(zeile+i, spalte+j).gibZeileAlsString(k, laenge);
					}
					out+="\n";
				}
			}
			return out;
		}

	}

	// **** Ende innere Klasse Feld ****

	public static void main(String[] args) {
		A4Dreieckspuzzle d = new A4Dreieckspuzzle();
		d.init();
	}

	public static int gegenrichtung(int i) {
		return ((i + 3) % 6);
	}

	private void init() {
		//this.dateilesen("");
		this.dateilesen("/home/peter/git/BWInfJ/datenfiles/puzzle3.txt");
		boolean[] used = new boolean[karten.length]; // hoffentlich alle mit false initialisiert
		ArrayList<Feld> tempArray = meinFeld.traverse();
		System.out.println("Traversiertes Feld: " + Arrays.toString(tempArray.toArray()));
		Feld[] felder = new Feld[tempArray.size()];
		for (int i1 = 0; i1 < felder.length; i1++)
			felder[i1] = tempArray.get(i1);
		bruteForce(0, felder, karten, used);
	}
	
	/**
	 * liest eine Datei als Streichholzmuster ein - die erste Zeile enthält die
	 * Anzahl der Punkte jede weitere Zeile eine Streichholz in der Form
	 * StartpunktNr ZielpunktNr RichtungDesStreichholzes
	 * 
	 * @param base Wenn true wird das Ausgangsmuster gelesen sonst das
	 *             Vergleichsmuster
	 */
	public void dateilesen(String dateiname) {
		File selektierteDatei = null;
		if (dateiname == "") {
			JFileChooser dateiauswahldialog = new JFileChooser(System.getProperty("user.dir"));
			int ergebnis = dateiauswahldialog.showOpenDialog(null);

			if (ergebnis != JFileChooser.APPROVE_OPTION) {
				return; // abgebrochen
			}
			selektierteDatei = dateiauswahldialog.getSelectedFile();
		} else {
			selektierteDatei = new File(dateiname);
		}
		try {
			FileReader fr = new FileReader(selektierteDatei);
			BufferedReader br = new BufferedReader(fr);

			@SuppressWarnings("unused")
			int anzFiguren = Integer.parseInt(br.readLine());
			int anzFelder = Integer.parseInt(br.readLine());
			
			meinFeld = new Feld(((int)Math.sqrt(anzFelder))-1);
			karten = new Karte[anzFelder];

			String zeile = br.readLine();
			int i=0;

			while (zeile != null && i<anzFelder) {
				System.out.println(zeile);
				String[] werteStr = zeile.split(" ");
				int f1 = Integer.parseInt(werteStr[0]);
				int f2 = Integer.parseInt(werteStr[1]);
				int f3 = Integer.parseInt(werteStr[2]);
				karten[i++] = new Karte(f1, f2, f3);
				zeile = br.readLine();
			}
			br.close();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		} catch (java.lang.NumberFormatException ex) {
			System.out.println("Formatfehler in der Datei");
			JOptionPane.showMessageDialog(null, "Datei hat das falsche Format", "Warnung", JOptionPane.ERROR_MESSAGE);

		}
	}


	private void bruteForce(int feldnr, Feld[] felder, Karte[] karten, boolean[] used) {
		//System.out.print(feldnr + "-");
		for (int i = 0; i < karten.length; i++) { // Wir wollen alle Karten auf diesem Feld ausprobieren
			if (!used[i]) { // die nicht schon in Benutzung sind
				felder[feldnr].setKarte(karten[i]);
				used[i] = true;
				for (int j = 0; j < 3; j++) { // in allen Positionen
					if (felder[feldnr].passtAktuelleKarte()) { // aktuelle Karte passt zu allen Nachbarn
						if (feldnr == (felder.length - 1)) { // Erfolg
							System.out.println("\nErfolg!!");
							System.out.print(meinFeld.gibKomplettesFeldAlsString(8));
						} else { // weiterprobieren
							bruteForce(feldnr + 1, felder, karten, used);
						}
					}
					felder[feldnr].turn(); // Karte drehen
				}
				felder[feldnr].removeKarte();
				used[i] = false;
			}
		}
	}

}
