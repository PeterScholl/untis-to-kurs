/**
 * 
 */
package bwinf38Rd2Geburtstag;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author peter Diese Klasse soll den kürzesten Term aus einer Anzahl
 *         vorgegebener Rechenmethoden und einer einzigen Ziffer bestimmen, die
 *         einen vorgegebenen Wert ergibt Bedeutung im String: E - Ziffer wird
 *         angehängt (Opperand wird dann erst später ausgewertet) + - Opperand
 *         wird addiert - - Opperand wird subtrahiert * - Opperand wird
 *         multipliziert / - Opperand wird dividiert
 */
public class Geburtstag {
	private int wert = 2020; // value we are interested to reach
	private int ziffer = 1; // the figure used
	private int aktlaengeT; // how many digits are actually used - max
	private HashMap<Integer, Term> zahlenT = new HashMap<Integer, Term>();

	/**
	 * @param wert
	 * @param ziffer
	 */
	public Geburtstag(int wert, int ziffer) {
		this.wert = wert;
		this.ziffer = ziffer;
		zahlenT.put(ziffer, new Term(ziffer,1));
		aktlaengeT=1;
	}


		
	public void extendT() { // Erweitert das Zahlenfeld um einen Schritt
		int neueAnz = this.aktlaengeT+1; // Neue Anzahl an max. verwendeten Ziffern
		for (int i=1; i<neueAnz;i++) { // Alle Anzahl kombinationen (i, neueAnz-i) durchgehen
			// Array aller Zahlen, die mit i Ziffern zu erzeugen sind
			ArrayList<Integer> zi = new ArrayList<Integer>();
			// Array aller Zahlen, die mit n-i Ziffern zu erzeugen sind
			ArrayList<Integer> zni = new ArrayList<Integer>();
			for (Integer key: zahlenT.keySet()) {
				if (this.zahlenT.get(key).getAnzZiffern() == i) zi.add(key);
				if (this.zahlenT.get(key).getAnzZiffern() == neueAnz-i) zni.add(key);
			}
			// Alle Operatoren durchgehen und alle Kombinationen
			for (char a : "+-*/".toCharArray()) {
				for (int z1 : zi) {
					for (int z2 : zni) {
						Term t;
						try {
							t = new Term(zahlenT.get(z1), zahlenT.get(z2), a);
							if (!zahlenT.containsKey(t.getWert())) zahlenT.put(t.getWert(), t);
						} catch (StringPartWithFloatValue | IllegalOperand e) {
							//e.printStackTrace();
						}
					}
				}
			}
		}
		Term t = new Term(this.ziffer,neueAnz);
		zahlenT.put(t.getWert(), t);
		aktlaengeT+=1;
	}
		
	@Override	
	public String toString() {
		return "Geburtstag [wert=" + wert + ", ziffer=" + ziffer + ", zahlenT=" + zahlenT + "]";
	}
	
	
	public int getWert() {
		return wert;
	}

	public void setWert(int wert) {
		this.wert = wert;
	}

	public int getZiffer() {
		return ziffer;
	}
	

	public int getAktlaengeT() {
		return aktlaengeT;
	}

	public boolean successT() {
		return zahlenT.containsKey(wert);
	}

	public String termZuWert(int i) {
		return zahlenT.get(i).toString();
	}

	
}

class StringPartWithFloatValue extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	StringPartWithFloatValue() {
		super("Bei der Berechnung des Strings wird eine Kommazahl erreicht");
	}
}

class IllegalString extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalString() {
		super("Ungueltiger String - z.B. unbekannte Operation");
	}
}
