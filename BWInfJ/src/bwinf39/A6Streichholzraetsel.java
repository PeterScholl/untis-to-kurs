package bwinf39;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import bwinf38Rd2Abbiegen.Turtle;

public class A6Streichholzraetsel {
	Punkt[] baseP, compP;
	SHolz[] baseSH, compSH;
	int[] punktZuordnung; // Zuordnung von punktZuordnung[BaseNr]=CompNr oder -1
	Turtle t;

	public class Punkt {
		private String name = "";
		private double x, y; // Zeichenpositionen dieses Punktes
		private boolean koordSet = false;
		private int zuordnung = -1;

		public Punkt(String n) {
			name = n;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

		public boolean isKoordSet() {
			return koordSet;
		}

		public void setKoordSet(boolean koordSet) {
			this.koordSet = koordSet;
		}

		public void setPunkt(double x2, double y2) {
			this.x = x2;
			this.y = y2;
			this.koordSet = true;
		}

		public boolean compare(Punkt p, double prec) {
			return (Math.abs(this.x - p.x) < prec && Math.abs(this.y - p.y) < prec);
		}

		public boolean compare(Punkt p) {
			return compare(p, 0.01);
		}

		public int getZuordnung() {
			return zuordnung;
		}

		public void setZuordnung(int zuordnung) {
			this.zuordnung = zuordnung;
		}

		@Override
		public String toString() {
			return "P" + name + "(" + Math.round(x*10)/10.0 + "," + Math.round(y*10)/10.0 + ") " + (this.zuordnung>-1?"ZO:"+this.zuordnung:"");
		}
	}

	public class SHolz {
		private Punkt start, ziel;
		private int richtung; // Richtung von Start zu Ziel (wie auf der Uhr 0 bei 12, 1 bei 1 usw.)

		/**
		 * @param start
		 * @param ziel
		 * @param richtung
		 */
		public SHolz(Punkt start, Punkt ziel, int richtung) {
			this.start = start;
			this.ziel = ziel;
			this.richtung = richtung;
		}

		public Punkt getStart() {
			return start;
		}

		public Punkt getZiel() {
			return ziel;
		}

		public int getRichtung() {
			return richtung;
		}

		public boolean setKoordsOfPointsIfPossible(int richtungsoffset, double laenge) {
			if (start.isKoordSet() && ziel.isKoordSet())
				return true; // nichts zu tun - erfolgreich
			else if (!start.isKoordSet() && !ziel.isKoordSet())
				return false; // nichts zu tun - erfolglos
			else if (start.isKoordSet()) { // Koordinaten des Zielpunktes können gesetzt werden
				double x = start.getX(); // Ausgangspunkt
				double y = start.getY();
				int richtung = (this.richtung + richtungsoffset) % 12;
				x += laenge * Math.sin(richtung * Math.PI / 6);
				y += laenge * Math.cos(richtung * Math.PI / 6);
				ziel.setPunkt(x, y);
			} else { // Koordinaten des Startpunktes können gesetzt werden
				double x = ziel.getX(); // Ausgangspunkt
				double y = ziel.getY();
				int richtung = (this.richtung + richtungsoffset) % 12;
				x -= laenge * Math.sin(richtung * Math.PI / 6);
				y -= laenge * Math.cos(richtung * Math.PI / 6);
				start.setPunkt(x, y);
			}
			return true; // Erfolg
		}

		@Override
		public String toString() {
			return "SHolz [" + start + "," + ziel + ", richtung=" + richtung + "]";
		}

		public boolean connects(Punkt p1, Punkt p2) {
			return (p1 == start && p2 == ziel) || (p2==start && p1==ziel);
		}
	}

	public A6Streichholzraetsel() {
		t = new Turtle(1200, 300); // Turtle erzeugen - mit Graphikbildschirm (x-Länge x y-Länge)
	}

	public static void main(String[] args) {
		A6Streichholzraetsel a = new A6Streichholzraetsel();
		//a.dateilesen(true, "/home/peter/git/BWInfJ/streichholzfig1.txt"); // basismuster einlesen
		//a.dateilesen(false, "/home/peter/git/BWInfJ/streichholzfig1b.txt"); // vergleichsmuster einlesen.
		a.dateilesen(true, ""); // basismuster einlesen
		a.dateilesen(false, ""); // vergleichsmuster einlesen.
		a.allenPunktenKoordinatenZuweisen(200.0, 200.0, true, 0, 40.0); // allen Punkten Koordinaten zuweisen,
																		// basispunkte, Richtungsänderung 0, laenge 50
		a.zeichneMuster(true); // basismuster
		a.allenPunktenKoordinatenZuweisen(400.0, 200.0, false, 0, 40.0); // allen Punkten Koordinaten zuweisen,
																			// basispunkte, Richtungsänderung 0, laenge
																			// 50
		a.zeichneMuster(false); // Vergleichsmuster
		System.out.println("\n---- jetzt mit Vergleichsplan ----\n");
		//a.allenPunktenKoordinatenZuweisen(a.baseSH, 6, 30, 0, false);
		//a.allenPunktenKoordinatenZuweisen(a.compSH, 1, 30, 0, false);
		System.out.println("Vergleiche Muster mit Streichholz base-1 auf comp-1 - gleiche SH-Anzahl: "
				+ a.vergleicheMuster(6, 1, true));
		// a.t.geheNach(600, 200);
		a.zeichneMuster(600, 200, true);
		a.t.setzeFarbe(Turtle.red);
		a.zeichneMuster(600, 200, false);
		// Vergleiche alle Muster und stelle auf bestes ein
		System.out.println(a.vergleicheAlleMuster()+" Streichhölzer können liegen bleiben");
		// a.t.geheNach(800, 200);
		a.t.setzeFarbe(Turtle.black);
		a.zeichneMuster(800, 198, true);
		a.t.setzeFarbe(Turtle.red);
		a.zeichneMuster(800, 200, false);
	}

	private int vergleicheAlleMuster() {
		int best=0;
		int bestBaseNr=0, bestCompNr=0;
		boolean bestDir=true;
		for (int i = 0; i < baseSH.length; i++) {
			for (int j = 0; j < compSH.length; j++) {
				//System.out.println("i,j: "+i+","+j);
				if (vergleicheMuster(i, j, true)>best) {
					best=vergleicheMuster(i,j,true);
					bestBaseNr=i;
					bestCompNr=j;
					bestDir=true;
				}
				if (vergleicheMuster(i, j, false)>best) {
					best=vergleicheMuster(i,j,false);
					bestBaseNr=i;
					bestCompNr=j;
					bestDir=false;
				}
			}
		}
		vergleicheMuster(bestBaseNr,bestCompNr,bestDir);
		//System.out.println(best+" Streichhölzer können liegen bleiben");
		return best;		
	}

	/**
	 * @param baseSHnr Nr des Streichholzes der Ausgangskonfiguration, dass mit
	 * @param compSHnr dieser Nr. des Streichholzes der Zielkonfiguration in Deckung sein soll.
	 * @param gleicheRichtung - true, wenn die Streichhölzer die deckungsgleich sind, ihre Richtung behalten sollen
	 * @return Anzahl Streichhölzer die liegen bleiben können
	 */
	private int vergleicheMuster(int baseSHnr, int compSHnr, boolean gleicheRichtung) {
		if (baseSHnr > baseSH.length || baseSHnr < 1)
			return -1; // Fehler
		if (compSHnr > compSH.length || compSHnr < 1)
			return -1; // Fehler
		// Punkten Koordinaten zuweisen
		int richtungsoffsetComp = (12-compSH[compSHnr].getRichtung()+baseSH[baseSHnr].getRichtung())%12;
		allenPunktenKoordinatenZuweisen(baseSH, baseSHnr, 30, 0, false);
		allenPunktenKoordinatenZuweisen(compSH, compSHnr, 30, richtungsoffsetComp, !gleicheRichtung);
		punkteZuordnen(0.01);
		return vergleicheAktuelleLage();
	}

	private boolean allenPunktenKoodrinatenZuweisen(SHolz[] sh, double laenge, int richtungsoffset) {
		Queue<SHolz> todo = new Queue<SHolz>();
		Queue<SHolz> checked = new Queue<SHolz>();
		for (int i = 0; i < sh.length; i++)
			todo.enqueue(sh[i]);
		while (!todo.isEmpty()) {
			SHolz t = todo.front();
			todo.dequeue();
			if (t.setKoordsOfPointsIfPossible(richtungsoffset, laenge)) {
				// alle checked wieder anhängen
				while (!checked.isEmpty()) {
					todo.enqueue(checked.front());
					checked.dequeue();
				}
				//System.out.println(t);
			} else {
				checked.enqueue(t); // muss ggf. nochmals geprüft werden
			}
		}
		if (!checked.isEmpty()) {
			System.out.println("FEHLER - es konnten nicht alle Streichhölzer bestimmt werden");
			return false;
		}
		return true;
	}

	private void allenPunktenKoordinatenZuweisen(SHolz[] sh, int startnr, double laenge, int richtungsoffset,
			boolean invers) {
		if (startnr < 1 || startnr > sh.length)
			return; // Fehler
		// alle Punkte zurücksetzen
		for (int i = 0; i < sh.length; i++) {
			sh[i].getStart().setKoordSet(false);
			sh[i].getZiel().setKoordSet(false);
		}
		// Startpunkt setzen
		if (invers) {
			sh[startnr].getZiel().setPunkt(0, 0);
			richtungsoffset += 6;
		} else {
			sh[startnr].getStart().setPunkt(0, 0);
		}
		allenPunktenKoodrinatenZuweisen(sh, laenge, richtungsoffset);
	}

	private void allenPunktenKoordinatenZuweisen(double x0, double y0, boolean base, int richtungsoffset,
			double laenge) {
		Punkt[] p;
		SHolz[] sh;
		if (base) {
			p = baseP;
			sh = baseSH;
		} else {
			p = compP;
			sh = compSH;
		}
		for (int i = 0; i < p.length; i++) {
			p[i].setKoordSet(false);
		}
		p[0].setX(x0);
		p[0].setY(y0);
		p[0].setKoordSet(true);
		allenPunktenKoodrinatenZuweisen(sh, laenge, richtungsoffset);
	}

	private void zeichneMuster(double xoff, double yoff, boolean base) {
		SHolz[] sh;
		if (base) {
			sh = baseSH;
		} else {
			sh = compSH;
		}
		for (int i = 0; i < sh.length; i++) {
			//System.out.println("Zeichne: " + sh[i]);
			t.hebeStift();
			t.geheNach(xoff + sh[i].getStart().getX(), yoff + sh[i].getStart().getY());
			t.senkeStift();
			t.geheNach(xoff + sh[i].getZiel().getX(), yoff + sh[i].getZiel().getY());
		}
	}

	private void zeichneMuster(boolean base) {
		zeichneMuster(0.0, 0.0, base);
	}

	private void punkteZuordnen(double prec) {
		for (int i = 0; i < baseP.length; i++) {
			baseP[i].setZuordnung(-1);
			if (baseP[i].isKoordSet()) {
				for (int j = 0; j < compP.length; j++) {
					if (compP[j].isKoordSet() && baseP[i].compare(compP[j],prec)) baseP[i].setZuordnung(j);
				}
			}
		}
		//System.out.println("Punktezuordnung: "+Arrays.toString(baseP));
	}

	/**
	 * Vergleicht die aktuelle Base (in aktueller Position und Zeichnung)
	 * @return die Anzahl Streichhölzer, die liegen bleiben können
	 */
	private int vergleicheAktuelleLage() {
		int ret = 0;
		//gehe alle Streichhölzer durch
		for (int i = 0; i < baseSH.length; i++) {			
		    //  * ermittle ob die Punkte dieses Streichholzes zugeordnet wurden
			if (baseSH[i].getStart().getZuordnung()>-1 && baseSH[i].getZiel().getZuordnung()>-1) {
				Punkt p1 = compP[baseSH[i].getStart().getZuordnung()];
				Punkt p2 = compP[baseSH[i].getZiel().getZuordnung()];
				//  * prüfe, ob es ein entsprechendes Streichholz in der Compareliste gibt
				for (int j=0; j<compSH.length; j++) {
					//     - wenn ja dann kann dieses liegenbleiben
					if (compSH[j].connects(p1,p2)) {
						ret+=1;
						break;
					}
				}
			}		
		}
		return ret;
	}
	/**
	 * liest eine Datei als Streichholzmuster ein - die erste Zeile enthält die
	 * Anzahl der Punkte jede weitere Zeile eine Streichholz in der Form
	 * StartpunktNr ZielpunktNr RichtungDesStreichholzes
	 * 
	 * @param base Wenn true wird das Ausgangsmuster gelesen sonst das
	 *             Vergleichsmuster
	 */
	public void dateilesen(boolean base, String dateiname) {
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

			int anzPunkte = Integer.parseInt(br.readLine());
			Punkt[] punkte = new Punkt[anzPunkte];
			for (int i = 0; i < punkte.length; i++) {
				punkte[i] = new Punkt("" + (i + 1));
			}

			String zeile = br.readLine();
			ArrayList<SHolz> hoelzer = new ArrayList<SHolz>();

			while (zeile != null) {
				// System.out.println(zeile);
				String[] werteStr = zeile.split(" ");
				int spnr = Integer.parseInt(werteStr[0]);
				int zpnr = Integer.parseInt(werteStr[1]);
				int ri = Integer.parseInt(werteStr[2]);
				hoelzer.add(new SHolz(punkte[spnr - 1], punkte[zpnr - 1], ri));
				zeile = br.readLine();
			}
			br.close();
			if (base) {
				baseP = punkte;
				baseSH = hoelzer.toArray(new SHolz[hoelzer.size()]);
			} else {
				compP = punkte;
				compSH = hoelzer.toArray(new SHolz[hoelzer.size()]);
			}
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		} catch (java.lang.NumberFormatException ex) {
			System.out.println("Formatfehler in der Datei");
			JOptionPane.showMessageDialog(null, "Datei hat das falsche Format", "Warnung", JOptionPane.ERROR_MESSAGE);

		}
	}

}
