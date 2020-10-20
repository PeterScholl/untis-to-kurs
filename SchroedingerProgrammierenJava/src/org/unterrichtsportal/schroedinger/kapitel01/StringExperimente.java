package org.unterrichtsportal.schroedinger.kapitel01;

public class StringExperimente {

	public StringExperimente() {
	}
	public static void main(String[] args) {
		// erstesExperiment();
		zweitesExperiment("Tischlerei", "Fischerei");
	}

	private static void zweitesExperiment(String s1, String s2) {
		//laengstes gemeinsames Suffix zweier Strings ausgeben
		int i = 0;
		while (i<s1.length() && ! s2.endsWith(s1.substring(i))) i++;
		System.out.println(s1.substring(i));		
	}
	
	private static void erstesExperiment() {
		// Bei Strings niemals == oder != verwenden
		String name = "Schrödinger";
		String nochEinName = "Schrödi" + new String("nger");
		if (name == nochEinName) {
			System.out.println("Funktioniert nicht immer");
		} else {
			System.out.println("Tja inhaltlich sind die aber gleich");
			System.out.println("name: "+name);
			System.out.println("nochEinName: "+nochEinName);
		}
		if (name.equals(nochEinName)) {
			System.out.println("So geht's richtig");
		}		
	}

}
