package org.unterrichtsportal.scholl.kellerautomat;

import java.util.HashMap;
import java.util.Stack;
import java.io.*;

public class Kellerautomat {
	private HashMap<String, Zustand> zustaende = new HashMap<String, Zustand>();
	private Zustand startZustand;
	private Zustand aktuellerZustand;
	private Stack<Character> keller = new Stack<Character>();
	
	public Kellerautomat() {
		keller.push('#'); //Kellerautomat mit leerem Kellerspeicher
	}
	
	public boolean readFile(String name) {
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(name);
			br = new BufferedReader(fr);
			String zeile;
			while (br != null && !(zeile = br.readLine()).equals("END")) {
				System.out.println(zeile);
				String zname;
				Zustand zust;
				switch (zeile.charAt(0)) {
				case '#': //Kommentar - einfach überspringen
					break;
				case 'S':
					zname = zeile.substring(2).split(",", 1)[0];
					if ((zust = zustaende.get(zname))!= null) {
						System.out.println("Zustand "+zname+" existierte schon");
						startZustand = zust;
					} else {
						startZustand = new Zustand(zname, keller);
						zustaende.put(zname, startZustand);
					}
					break;
				case 'F':
					zname = zeile.substring(2).split(",", 1)[0];
					if ((zust = zustaende.get(zname))!= null) {
						System.out.println("Zustand "+zname+" existierte schon");
						zust.setAkzeptierend(true);
					} else {
						zust = new Zustand(zname, keller);
						zust.setAkzeptierend(true);
					}
					break;
				case 'D':
					String[] input = zeile.substring(2).split(",");
					if (input.length != 5) {
						System.err.println("Syntax-Fehler bei Zeile "+zeile);
					} else {
						Zustand z1 = zustaende.get(input[0]);
						if (z1==null) { 
							z1 = new Zustand(input[0],keller);
							zustaende.put(input[0], z1);
						}
						Zustand z2 = zustaende.get(input[4]);
						if (z2==null) {
							z2 = new Zustand(input[4],keller);
							zustaende.put(input[4], z2);
						}					    
						z1.adduebergang(input[1].charAt(0), input[2].charAt(0), input[3].charAt(0), z2);
					}
					break;
				default:
					System.err.println("Fehlerhafte Syntax im Automatenfile");
					return false;
				}
			}
			fr.close();
			br.close();
			if (startZustand==null) return false;
			return true;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public void testeWort(String wort) {
		String meinWort = wort;
		aktuellerZustand = startZustand;
		int maxloop = 20;
		while (aktuellerZustand.gibZiel('E')!=null && maxloop > 0) { //sofortiger weg vorhanden
			//Achtung hier besteht Loop-Problematik daher
			maxloop--;
			System.out.println(aktuellerZustand+" Weg: E");
			aktuellerZustand = aktuellerZustand.gibZiel('E');
		}
		while (aktuellerZustand != null && meinWort.length() > 0) {
			Zustand ziel = aktuellerZustand.gibZiel(meinWort.charAt(0));
			System.out.println(aktuellerZustand+" Weg: "+meinWort.charAt(0)+" Ziel: "+ (ziel==null ? "-" : ziel.getName()));
			aktuellerZustand = ziel;
			meinWort = meinWort.substring(1);
			//System.out.println("Wort: "+meinWort);
			maxloop = 20;
			while (aktuellerZustand != null && (ziel = aktuellerZustand.gibZiel('E'))!=null && maxloop > 0) { //sofortiger weg vorhanden
				//Achtung hier besteht Loop-Problematik daher
				maxloop--;
				System.out.println(aktuellerZustand+" Weg: E Ziel: "+ziel.getName());
				aktuellerZustand = ziel;
			}

		}
		System.out.println("akzeptierter Endzustand ("+((aktuellerZustand!=null) ? aktuellerZustand.getName() : new String("-"))+"): "+(aktuellerZustand!=null && aktuellerZustand.isAkzeptierend()));
		
	}

}
