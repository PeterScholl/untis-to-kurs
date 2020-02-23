package org.unterrichtsportal.scholl.mathekalender2019;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/* zuletzt erreichte Strategien
 * MaxWin: 13
 * Strategien: 50, 56616, 131194
 */
public class Wichtel {
	static boolean gotsigint = false;
	static int BLAU = 1;
	static int GELB = 2;
	static int ROT = 3;
	static int EGAL = 0;
	static int RICHTIG = 1;
	static int FALSCH = 2;
	static int KEINTIPP = 0;
	int strategie; // strategie des wichtels 0-2^18-1
	int muetze; // Farbe der eigenen Mütze
	int[][] group = new int[9][3];
	int[] gewaehlteKombinationen = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1 }; // enthält die eigenen
																						// Gewinnkombinationen, -1 wenn
																						// weniger als 9
	int typ = 0;
	int minanzahl = 0; //Mindestanzhal zu wähnlende Strategien
	private Wichtel vorherWichtel = null;

	public Wichtel() {
		strategie = 0;
		muetze = EGAL;
	}

	public void setTyp(int t) {
		this.typ = t;
		switch (typ) {
		case (1):
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 3; j++) {
					group[i][j] = 3 * i + j;
				}
			}
			break;
		case (2):
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 3; j++) {
					group[i][j] = 9 * (i / 3) + i % 3 + 3 * j;
				}
			}
			break;
		case (3):
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 3; j++) {
					group[i][j] = i + 9 * j;
				}
			}
			break;
		default:
			for (int i = 0; i < 27; i++) {
				group[i % 3][i % 9] = 0;
			}
		}
	}

	public void setMuetze(int muetze) {
		this.muetze = muetze;
	}

	public int getStrategie() {
		return strategie;
	}

	public void groupsAusgeben() {
		System.out.println("Wichtel: " + typ);
		for (int i = 0; i < 9; i++) {
			System.out.print("g: " + i);
			for (int j = 0; j < 3; j++) {
				System.out.print("-" + group[i][j]);
			}
			System.out.println("");
		}
	}

	public void printGewaehlte() {
		System.out.println("Chosen by " + typ + ":");
		for (int i = 0; i < 9; i++) {
			System.out.print(" | " + gewaehlteKombinationen[i]);
		}
		System.out.println();
	}

	public void erklaereStrategie(int strat, String w1, String w2) {
		// muss neu geschrieben werden ...
		// Groups auswerten
		System.out.println("Strategie " + strat + ":");
		for (int i = 0; i < 9; i++)
			System.out.println("-" + gewaehlteKombinationen[i]);
		for (int mw1 = 1; mw1 < 4; mw1++) {
			for (int mw2 = 1; mw2 < 4; mw2++) {
				System.out.print(w1 + "(MW1): " + mw1 + " " + w2 + "(MW2): " + mw2 + ":");
				int beobachtung = 3 * (mw1 - 1) + (mw2 - 1);
				int tipp = (strat >> 2 * beobachtung) % 4;
				System.out.println("" + tipp);
			}
		}
	}

	public boolean nextStrategie() {
		while (true) {
			strategie++;
			if (strategie >= 1 << 18) {
				strategie = 0;
				gewaehlteKombinationen = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1 };
				return false;
			} else {
				// Prüfen ob Strategie zulaessig
				boolean zulaessig = true;
				// Dazu erst einmal die gewaehlten Strategien bestimmen und ...
				for (int i = 0; i < 9; i++) {
					int w = (strategie >> (2 * i)) % 4;
					if (w > 0) {
						gewaehlteKombinationen[i] = group[i][w - 1];
						// dann prüfen, ob diese geblockt sind
						if (vorherWichtel != null && vorherWichtel.istGeblockt(gewaehlteKombinationen[i])) {
							zulaessig = false;
							break;
						}
					} else {
						gewaehlteKombinationen[i] = -1;
					}
				}
				int count=9;
				for (int i=0; i<9;i++) {
					if (gewaehlteKombinationen[i]==-1) count--;
				}
				if (count<this.minanzahl) zulaessig=false;
				if (zulaessig)
					return true;
			}
		}
	}

	public static int gewinneZaehlen(Wichtel w1, Wichtel w2, Wichtel w3) {
		// Alle 27 Moeglichkeiten durchprobieren und Gewinne zaehlen
		// neu Programmieren, prüfen ob Strategie von w1 geblockt ist
		int gewinne = 0;
		for (int i = 0; i < 27; i++) {
			if (w3.istGewaehlt(i) && !w3.istGeblockt(i))
				gewinne++;
		}
		return gewinne;
	}

	public static void main(String[] args) {
		gotsigint = false;
		Signal.handle(new Signal("INT"), new SignalHandler() {
			// Signal handler method
			public void handle(Signal signal) {
				System.out.println("Got signal" + signal);
				gotsigint = true;
			}
		});
		Wichtel w1 = new Wichtel();
		w1.setTyp(1);
		Wichtel w2 = new Wichtel();
		w2.setTyp(2);
		w2.setMinanzahl(4); // Da Minanzahl 13, w3 5 tipps muss ein weiterer Zwerg min 4 Tipps abegeben
		Wichtel w3 = new Wichtel();
		w3.setTyp(3);
		w3.setMinanzahl(5); //Da mindestWins 13 muss ein Zwerg 5 Tipps abgeben
		// w1.erklaereStrategie(1365, "B", "C");
		// w2.erklaereStrategie(249660, "C", "A");
		// w3.erklaereStrategie(53264, "A", "B");
		// Erreichtes Maximum 13!!! 1365,249660,53264 (glaube ich )
		w1.setStrategie(0);
		w2.setStrategie(1365);
		w3.setStrategie(87423);
		w3.vorherWichtel = w2;
		w2.vorherWichtel = w1;
		w1.groupsAusgeben();
		w2.groupsAusgeben();
		w3.groupsAusgeben();
		System.out.println("w2 blockiert 3?"+w2.istGeblockt(3));
		w1.printGewaehlte();
		w2.printGewaehlte();
		w3.printGewaehlte();
		System.out.println("Gewinne:"+gewinneZaehlen(w1, w2, w3));
		//if (true) return;
		w1.setStrategie(0x0000);
		w2.setStrategie(0);
		w3.setStrategie(0);
		int maxWin = 0;
		boolean fertig = false;
		while (!fertig) {
			//System.out.println(w3.getStrategie());
			if (!w3.nextStrategie()) {
				if (!w2.nextStrategie()) {
					if (!w1.nextStrategie()) {
						fertig = true;
						break;
					}
					// w2.setStrategie(w1.getStrategie());
				}
				// w3.setStrategie(w2.getStrategie());
			}
			// Alle 27 Moeglichkeiten durchprobieren und Gewinne zaehlen
			int gewinne = gewinneZaehlen(w1, w2, w3);
			if (gewinne > maxWin) {
				maxWin = gewinne;
				System.out.println("New MaxWin: " + maxWin);
				System.out.println(
						"Strategien: " + w1.getStrategie() + ", " + w2.getStrategie() + ", " + w3.getStrategie());
				w1.printGewaehlte();
				w2.printGewaehlte();
				w3.printGewaehlte();
			}
			if (gotsigint) {
				System.out.println("MaxWin: " + maxWin);
				System.out.println(
						"Strategien: " + w1.getStrategie() + ", " + w2.getStrategie() + ", " + w3.getStrategie());
				gotsigint = false;
			}

		}

	}

	public void setStrategie(int strategie) {
		this.strategie = strategie;
		for (int i = 0; i < 9; i++) {
			int w = (strategie >> (2 * i)) % 4;
			if (w > 0) {
				gewaehlteKombinationen[i] = group[i][w - 1];
			} else {
				gewaehlteKombinationen[i] = -1;
			}
		}
	}

	public boolean istVerbrannt(int strategie) {
		for (int i = 0; i < 9; i++) {
			if (gewaehlteKombinationen[i] != -1) // aus der Gruppe i wurde ein Element gewählt
				for (int j = 0; j < 3; j++) {
					if (group[i][j] == strategie && gewaehlteKombinationen[i] != strategie) // Diese Strategie ist durch
						return true;
				}
		}
		if (vorherWichtel != null) {
			return vorherWichtel.istVerbrannt(strategie);
		} else {
			return false;
		}
	}

	public boolean istGeblockt(int strategie) {
		// Prüfen, ob die gegebene Strategie unter den eigenen verbrannten, oder denen
		// des vorhergehenden Wichtels
		for (int i = 0; i < 9; i++) {
			if (gewaehlteKombinationen[i] != -1) // aus der Gruppe i wurde ein Element gewählt
				for (int j = 0; j < 3; j++) {
					if (group[i][j] == strategie && gewaehlteKombinationen[i]!=strategie) // Diese Strategie ist durch
						return true;
				}
		}
		if (vorherWichtel != null) {
			return vorherWichtel.istVerbrannt(strategie);
		} else {
			return false;
		}
	}

	public boolean istGewaehlt(int strategie) {
		// Prüfen, ob die gegebene Strategie unter den eigenen gewählten ist, oder denen
		// des vorhergehenden Wichtels
		for (int i = 0; i < 9; i++) {
			if (gewaehlteKombinationen[i] == strategie)
				return true;
		}
		if (vorherWichtel != null) {
			return vorherWichtel.istGewaehlt(strategie);
		} else {
			return false;
		}
	}

	public void setMinanzahl(int minanzahl) {
		this.minanzahl = minanzahl;
	}
	
}