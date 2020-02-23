package bwinf38Rd2Geburtstag;

public class Starter {
	
	//TODO: Wichtiges Problem - Die Darstellung E der Zahl 11 und die Darstellung +E- der Zahl 11 laesst sich jeweils anders erweitern mit E
	// denn EE ist 111 und +E-E ist 1+11-11 = 1
	// gibt es noch weitere solcher Unterschiede?! Ja +- ist 0 und -+ ist 0 bei Erweiterung mit E kommt etwas ganz anderes heraus.
	// Es müssent tatsächlich alle Darstellungen bestimmter länge geprüft werden :-(
	// oder es genügt die Zahlen für alle möglichen Endungen zu speichern?! Nein die Anzahl der Es ist noch wichtig

	public static void main(String[] args) {
		Geburtstag g = new Geburtstag(2020, 2);
		System.out.println(g);
		while (g.getAktlaengeT() < 40 && !g.successT()) {
			g.extendT();
			if (g.getAktlaengeT()<5) {
				System.out.println(g.toString());
			} else {
				System.out.println("Laenge: "+g.getAktlaengeT());
			}
			if (g.successT()) System.out.println("2020: "+g.termZuWert(2020));
		}
		

	}

}
