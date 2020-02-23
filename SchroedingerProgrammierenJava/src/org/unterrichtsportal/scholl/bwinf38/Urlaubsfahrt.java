// Idee von Peter - Rohversion (Dateieinlesen) von Stefan Cames

package org.unterrichtsportal.scholl.bwinf38;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Urlaubsfahrt {

	public static final String eingabeFile = "./fahrt2.txt";

	public static double verbr; // Verbrauch (in Liter) - auf 100 Km
	public static int tankgroesse; // Tankgröße (in Liter)
	public static double fuellung; // verbleibende Tankfüllung (in Liter)
	public static int laenge; // Gesamtlänge (in km)
	public static int anzTankstellen; // Anzahl Tankstellen auf der Reiseroute
	public static String[][] tankstellen; // eingelesene Tankstellen
	public static String[][] angefahreneTankstellen; // notieren des besten Ergebnisses

	public static double restreichweite; // Restreichweite (in km)

	public static void main(String[] args) throws NumberFormatException, IOException {

		System.out.println("Working Directory: " + System.getProperty("user.dir"));
		System.out.println("\n| Datei einlesen |\n");

		FileReader fr = new FileReader(eingabeFile);
		BufferedReader reader = new BufferedReader(fr);

		verbr = Integer.parseInt(reader.readLine());
		tankgroesse = Integer.parseInt(reader.readLine());
		fuellung = Double.parseDouble(reader.readLine());
		laenge = Integer.parseInt(reader.readLine());
		anzTankstellen = Integer.parseInt(reader.readLine());

		restreichweite = fuellung / (verbr / 100); // Restreichweite

		tankstellen = new String[anzTankstellen][2];

		System.out.println("Verbrauch: " + verbr + " Tankgroesse: " + tankgroesse + " Fuellung: " + fuellung
				+ " damit Restreichweite: " + restreichweite + " Streckenlaenge: " + laenge + " AnzTankstellen: "
				+ anzTankstellen + "\n");

		for (int i = 0; i < anzTankstellen; i++) {
			String[] splitted = reader.readLine().split(" ");
			tankstellen[i][0] = splitted[0];
			tankstellen[i][1] = splitted[splitted.length - 1];
		}
		
		reader.close();

		/*
		 * for (String[] s : tankstellen) { // Debug - zur Kontrolle Tankstellen
		 * ausgeben System.out.println(s[0] + " | " + s[1]); }
		 */

		System.out.println("--- Anzahl Tankstops ermitteln (gierig) ----");
		//TODO: das geht besser mit nur einer Schleife, die alle Tankstellen durchläuft
		double pos = 0, nextpos = 0; // Aktuelle Position in km
		int anzstops = 0; // Anzahl der minimalen Tankstops
		boolean tankstelleErreichbar = true;
		// Restreichweite ist bekannt (restreichweite)
		while (pos + restreichweite < laenge && tankstelleErreichbar) {
			tankstelleErreichbar = false;
			for (String[] s : tankstellen) { // alle Tankstellen durchlaufen
				if (Double.parseDouble(s[0]) > pos && Double.parseDouble(s[0]) <= pos + restreichweite) {
					// Tankstelle liegt vor uns und ist erreichbar
					// wird verbessert durch die am weitesten entfernt liegende Tankstelle
					nextpos = Double.parseDouble(s[0]);
					tankstelleErreichbar = true;
				}
			}
			if (tankstelleErreichbar) {
				pos = nextpos; // Wir halten an der am weitesten entfernten Tankstelle
				anzstops++; // zählen diesen Stop und
				restreichweite = tankgroesse / verbr * 100; // Volltanken
				System.out.print("*" + pos);
			}
		}
		System.out.println("while-Ende");
		if (tankstelleErreichbar) {
			System.out.println("Möglich mit " + anzstops + " Tankstops");
		} else {
			System.out.println("Keine Lösung möglich");
			System.exit(0);
		}

		System.out.println("--- günstigste Tour ermitteln - (rekursiv) ---");
		System.out.println("Ergebnis: "+kosten(0, fuellung / (verbr / 100), 0, anzstops));
	}

	/**
	 * Ermittelt die günstigste Verbindung von der aktuellen Position, bei
	 * vorhandener Restreichweite usw.
	 * 
	 * @param aktpos
	 * @param restreichweite
	 * @param maxstops
	 * @return
	 */
	public static UrlaubsfahrtTour kosten(double aktpos, double restreichweite, double preisLetzterTank, int maxstops) {
		double infinity = Double.MAX_VALUE;
		if (aktpos + restreichweite >= laenge) { // Ziel ohne (weiteren) Tankstop erreichbar
			// Den Resttankinhalt an letzter Tankstelle verkaufen
			// return -(laenge-restreichweite-aktpos)/100*verbr*preisLetzterTank;
			UrlaubsfahrtTour t = new UrlaubsfahrtTour(
					(laenge - restreichweite - aktpos) / 100 * verbr * preisLetzterTank);
			return t;
		} else if (restreichweite + aktpos + maxstops * (100 * tankgroesse / verbr) < laenge) { // Ziel nicht mehr
																								// erreichbar
			return new UrlaubsfahrtTour(infinity);
		} else { // alle möglichen Tankstellen in Restreichweite ausprobieren
			UrlaubsfahrtTour minFahrt = new UrlaubsfahrtTour(infinity); // Minimale Lösung/Kosten in dieser Variable speichern
			for (String[] s : tankstellen) { // durchläuft alle vorhandenen Tankstellen
				int tpos = Integer.parseInt(s[0]); // Position der aktuellen Tankstelle
				if ((tpos > aktpos) && (tpos <= aktpos + restreichweite)) { // Tankstelle ist möglich
					// Wie wären die Kosten, wenn wir hier tanken würden?
					double tempkosten;
					if (Double.parseDouble(s[1]) < preisLetzterTank) {
						// Tankinhalt zum alten Preis verkaufen und volltanken, da diese Tankstelle
						// günstiger
						tempkosten = -(restreichweite - (tpos - aktpos)) / 100 * verbr * preisLetzterTank
								+ Double.parseDouble(s[1]) * tankgroesse;
					} else { // einfach volltanken
						tempkosten = (tankgroesse - (restreichweite - (tpos - aktpos)) / 100 * verbr)
								* Double.parseDouble(s[1]);
					}
					// Kosten für die Restfahrt ab hier dazu addieren
					// Aktuelle Position = diese Tankstelle, Maxreichweite, PreisDieserTankstele,
					// ein Stop weniger möglich
					UrlaubsfahrtTour tempFahrt = kosten(tpos, (100 * tankgroesse / verbr), Double.parseDouble(s[1]),
							maxstops - 1);
					if (tempFahrt.getKosten()<infinity && tempFahrt.getAngefahreneTankstellen().length < maxstops-1) { //Fehler
						System.out.println("Fehler: "+tempFahrt); //gültige Tour mit zu wenigen Stops
						System.out.println(
								"Maxstops: " + maxstops + " - AnzTS: " + tempFahrt.getAngefahreneTankstellen().length +
								" minKosten: "+minFahrt.getKosten()+" tempkosten_vor: "+tempkosten+" s:"+s[0]+"-"+s[1]);
						System.exit(1);
					}
					if (tempFahrt.getKosten() < infinity) { //Wenn eine gültige Restfahrt erhalten wurde
						tempkosten += tempFahrt.getKosten(); //Kosten berechnen
					} else {
						tempkosten = infinity; //sonst bleibt dir Fahrt ungültig
					}
					/* System.out.println(
							"Maxstops: " + maxstops + " - AnzTS: " + tempFahrt.getAngefahreneTankstellen().length +
							" minKosten: "+minKosten+" tempkosten: "+tempkosten+" s:"+s[0]+"-"+s[1]); // */
					if (minFahrt.getKosten() > tempkosten) { // diese Lösung ist die bisher beste
						minFahrt = new UrlaubsfahrtTour(tempFahrt); // also speichern
						minFahrt.addAngefahreneTankstelle(s);
						minFahrt.setKosten(tempkosten);
						//System.out.println("Speichern: " + minFahrt);
					}
				}
			}
			return minFahrt;
		}
	}

}