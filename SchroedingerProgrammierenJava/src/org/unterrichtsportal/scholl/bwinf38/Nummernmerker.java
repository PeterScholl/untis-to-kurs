package org.unterrichtsportal.scholl.bwinf38;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * 
 * @author peter
 *
 */

/*
 * Nummer1: 005480000005179734 Nummer2: 03495929533790154412660 Nummer3:
 * 5319974879022725607620179 Nummer4: 9088761051699482789038331267 Nummer5:
 * 011000000011000100111111101011
 */

public class Nummernmerker {
	static String[] vorrat = new String[] { 
			"005480000005179734", 
			"03495929533790154412660",
			"5319974879022725607620179", 
			"9088761051699482789038331267", 
			"011000000011000100111111101011" };

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Nummernmerker nm = new Nummernmerker();
		for (String j : vorrat) {
			System.out.print(j+": ");
			String[] a = nm.bestNummerRekursiv(j);
			for (String i : a) {
				System.out.print(i + " ");
			}
			System.out.println(" - "+nm.anzNullStrings(a)+" Blöcke beginnen mit 0");
		}
	}



	public String[] bestNummerRekursiv(String nr) {
		if (nr.length() < 2) { //Es kann eigentlich nie ein Blick mit weniger als zwei Zeichen vorkommen
			System.out.println("Fehler !! Teil zu Kurz");
			return new String[] { nr };
		} else if (nr.length() <= 4) { // Wenn man 2 bis 4 Zeichen hat ist man schon fertig
			return new String[] { nr };
		} else if (nr.length() == 5) { // Bei Länge 5 gibt es nur zwei Möglichkeiten
			// ... zerlegen in 2 und 3 Zeichen
			String[] a1 = new String[] { nr.substring(0, 2), nr.substring(2) };
			// oder 3 und 2 Zeichen
			String[] a2 = new String[] { nr.substring(0, 3), nr.substring(3) };

			if (anzNullStrings(a1) > anzNullStrings(a2)) { // Prüfen welche besser ist
				return a2;
			} else {
				return a1;
			}
		} else { // in allen anderen Fällen
			// Zerlegen in zwei Zeichen und die beste Zerlegung des Rests
			String[] a1 = Stream.concat(Arrays.stream(new String[] { nr.substring(0, 2) }),
					Arrays.stream(bestNummerRekursiv(nr.substring(2)))).toArray(String[]::new);
			// oder zerlegen in drei Zeichen und die beste Zerlegung des Rests
			String[] a2 = Stream.concat(Arrays.stream(new String[] { nr.substring(0, 3) }),
					Arrays.stream(bestNummerRekursiv(nr.substring(3)))).toArray(String[]::new);
			// oder zerlegen in vier Zeichen und die beste Zerlegung des Rests
			String[] a3 = Stream.concat(Arrays.stream(new String[] { nr.substring(0, 4) }),
					Arrays.stream(bestNummerRekursiv(nr.substring(4)))).toArray(String[]::new);
			// Prüfen, welche Zerlegung die beste ist
			// ich beginne mit a3, weil diese theoretisch die wenigsten
			// Teilstrings enthält, da mit 4 der größtmögliche Teil abgetrennt
			// wurde
			if ((anzNullStrings(a3) <= anzNullStrings(a2)) && (anzNullStrings(a3) <= anzNullStrings(a1))) {
				return a3; //a3 ist eine der idealsten Lösungen
			} else if (anzNullStrings(a2) <= anzNullStrings(a3)) {
				return a2; //a2 ist eine der idealsten Lösungen wenn a3 nicht dazu gehört
			} else {
				return a1; //a1 ist die beste Lösung
			}

		}

	}

	/**
	 * Zählt wie viele Strings in einem String[] mit "0" beginnen
	 * 
	 * @param a String[] bei dem die einzelnen Strings geprüft werden sollen
	 * @return	gibt die Anzahl der Strings, die mit "0" beginnen zurück
	 */
	public int anzNullStrings(String[] a) { 
		int count = 0; // Zähler auf 0 setzen
		for (String i : a) { // alle Strings des Arrays durchlaufen
			if (i.startsWith("0"))
				count++; // Zählen, wenn der String mit 0 beginnt
		}
		return count;
	}

}
