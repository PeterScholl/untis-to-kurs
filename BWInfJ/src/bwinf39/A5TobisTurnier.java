package bwinf39;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class A5TobisTurnier {
	Spieler[] spielerliste;	
	
	public class Spieler {
		int spielstaerke =0;
		String name="";
		
		public Spieler(int s) {
			this.spielstaerke = s;
		}
		
		/**
		 * Berechnet die Wahrscheinlichkeit, dass dieser Spieler gegen einen Spieler b 
		 * gewinnt und gibt sie als Bruch aus
		 * @param b Gegenspieler
		 * @return Wahrscheinlichkeit gegen b zu gewinnen als Bruch
		 */
		public Fraction pGewinntGegenSpieler(Spieler b) {
			return (new Fraction(this.spielstaerke,this.spielstaerke+b.spielstaerke));
		}
		
		public boolean gewinntSimulSpielGegenSpieler(Spieler b) {
			double r = java.lang.Math.random();
			return r < ((double)this.spielstaerke)/(this.spielstaerke+b.spielstaerke);
		}

		public int getSpielstaerke() {
			return spielstaerke;
		}

		public void setSpielstaerke(int spielstaerke) {
			this.spielstaerke = spielstaerke;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "Spieler " + name + ": " + spielstaerke;
		}
	}
	
	public static void main(String[] args) {
		A5TobisTurnier dies = new A5TobisTurnier();
		dies.dateiEinlesen();
		dies.tests();
	}

	public void tests() {
		// kleine Tests
		Spieler s1 = new Spieler(40);
		Spieler s2 = new Spieler(80);
		System.out.println("Spieler1 gewinnt gegen Spieler2 mit Wkeit: "+s1.pGewinntGegenSpieler(s2));
		for (int i = 0; i < 10; i++) {
			System.out.println(i+": In dieser Simulation hat Spieler1 gewonnen: "+s1.gewinntSimulSpielGegenSpieler(s2));			
		}
		// Tests mit eingelesenen Daten
		System.out.println("Spielerliste: "+java.util.Arrays.toString(spielerliste));
		for (int i = 0; i < 10; i++) {
			System.out.println(i+": In dieser KO Simulation gewinnt: "+this.simuliereKO(spielerliste));			
		}
		// Simulation von KO-Runden
		Spieler bester = this.gibBestenSpieler(spielerliste);
		int count=0;
		int anzSim=1000000;
		for (int i=0; i<anzSim; i++) {
			if (this.simuliereKO(spielerliste).equals(bester)) count++;			
		}
		System.out.println("In "+anzSim+" Simulationen hat "+count+" Mal der beste Spieler "+bester+" gewonnen");
		// Simulation von 5xKO-Runden
		count=0;
		for (int i=0; i<anzSim; i++) {
			if (this.simuliereKO(spielerliste,5).equals(bester)) count++;			
		}
		System.out.println("In "+anzSim+" Simulationen (5xKO) hat "+count+" Mal der beste Spieler "+bester+" gewonnen");
		// Simulation von Ligaspielen
		count=0;
		for (int i=0; i<anzSim; i++) {
			if (this.simuliereLiga(spielerliste).equals(bester)) count++;			
		}
		System.out.println("In "+anzSim+" Liga-Simulationen hat "+count+" Mal der beste Spieler "+bester+" gewonnen");
	}
	
	/**
	 * simuliert ein KO-Turnier
	 * @param reihenfolge Spieler in geordneter Reihenfolge 
	 * @param anz Anzahl der Spiele die Gespielt werden (sollte ungerade sein!)
	 * @return Spieler der Gewonnen hat
	 */
	public Spieler simuliereKO(Spieler[] reihenfolge, int anz) {
		if (reihenfolge.length==1) return reihenfolge[0];
		if (reihenfolge.length%2 == 1) return null; // ungerade Spielerzahl
		Spieler[] naechsteRunde = new Spieler[reihenfolge.length/2];
		for (int i=0; i<naechsteRunde.length; i++) { // Spiele durchfuehren/simulieren
			int count=0;
			for (int j=0; j<anz; j++) count=reihenfolge[2*i].gewinntSimulSpielGegenSpieler(reihenfolge[2*i+1])?count+1:count;
			naechsteRunde[i]=count>(anz-1)/2?reihenfolge[2*i]:reihenfolge[2*i+1];
		}
		return simuliereKO(naechsteRunde);		
	}

	/**
	 * simuliert ein KO-Turnier
	 * @param reihenfolge Spieler in geordneter Reihenfolge 
	 * @return Spieler der Gewonnen hat
	 */
	public Spieler simuliereKO(Spieler[] reihenfolge) {
		return simuliereKO(reihenfolge,1);
	}
	
	/**
	 * Simuliert ein Ligaspiel - jeder gegen jeden und gibt den Gewinner zurück
	 * @param reihenfolge Liste der Spieler
	 * @return der Spieler der gewonnen hat (bei Gleichstand der erste in der Liste)
	 */
	public Spieler simuliereLiga(Spieler[] reihenfolge) {
		int[] siege = new int[reihenfolge.length];
		for (int i=0; i<reihenfolge.length; i++) { // Jeder Spieler
			for (int j=i+1; j<reihenfolge.length; j++) { // spielt gegen jeden anderen
				if (reihenfolge[i].gewinntSimulSpielGegenSpieler(reihenfolge[j])) {
					siege[i]++;
				} else {
					siege[j]++;
				}
			}
		}
		//Maximum finden - das erste
		int maxsiege = 0;
		Spieler ret = null;
		for (int i = 0; i < siege.length; i++) {
			if (siege[i]>maxsiege) {
				maxsiege=siege[i];
				ret = reihenfolge[i];
			}
		}
		return ret;
	}
	
	public void dateiEinlesen() {
		JFileChooser dateiauswahldialog = new JFileChooser(System.getProperty("user.dir"));
		int ergebnis = dateiauswahldialog.showOpenDialog(null);

		if (ergebnis != JFileChooser.APPROVE_OPTION) {
			return; // abgebrochen
		}
		File selektierteDatei = dateiauswahldialog.getSelectedFile();
		try {
			FileReader fr = new FileReader(selektierteDatei);
			BufferedReader br = new BufferedReader(fr);

			int anzahl = Integer.parseInt(br.readLine());
			spielerliste = new Spieler[anzahl];

			String zeile = br.readLine();
			int i=0;
			while (zeile != null && i<anzahl) {
				System.out.println(zeile);
				spielerliste[i]=new Spieler(Integer.parseInt(zeile));
				spielerliste[i++].setName(""+i);
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
	
	public Spieler gibBestenSpieler(Spieler[] liste) {
		if (liste.length==0) return null;
		Spieler best = liste[0];
		for (int i=1; i<liste.length; i++) {
			best = best.getSpielstaerke()<liste[i].getSpielstaerke()?liste[i]:best;
		}
		return best;
	}
}
