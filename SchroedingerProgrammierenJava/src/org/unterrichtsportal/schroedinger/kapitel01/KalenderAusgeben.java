package org.unterrichtsportal.schroedinger.kapitel01;

public class KalenderAusgeben {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int tage = 0; //Anzahl Tage des Monats
		int tageBeginn = 0; //Wie viele leere Tage ausgeben
		if (args.length != 2) {
			System.err.println("Die Argumente müssen Monat und Tag sein");
			System.exit(1);
		}
		switch (args[0].toLowerCase()) { // Monat überprüfen
		case "januar":
		case "märz":
		case "mai":
		case "juli":
		case "august":
		case "oktober":
		case "dezember":
			tage=31;
			break;
		case "februar":
			tage=28;
			break;
		case "april":
		case "juni":
		case "september":
		case "november":
			tage=30;
			break;
		default:
			System.err.println("Der Monat "+args[0]+" ist nicht bekannt!");
			System.exit(1);
		}
		switch (args[1].toLowerCase()) {
		case "mo":
			tageBeginn = 0;
			break;
		case "di":
			tageBeginn = 1;
			break;
		case "mi":
			tageBeginn = 2;
			break;
		case "do":
			tageBeginn = 3;
			break;
		case "fr":
			tageBeginn = 4;
			break;
		case "sa":
			tageBeginn = 5;
			break;
		case "so":
			tageBeginn = 6;
			break;
		default:
			System.err.println("Der Tag "+args[1]+" ist nicht bekannt");
			System.exit(1);
		}
		
		System.out.println("|MO|DI|MI|DO|FR|SA|SO|");
		int zeilenwechsel = 0;
		for (int i=1-tageBeginn;i<=tage;i++) {
			if (i<1) {
				System.out.print("|  ");
			} else if (i<10) {
				System.out.print("| "+i);				
			} else {
				System.out.print("|"+i);
			}
		    if (++zeilenwechsel==7) {
		    	System.out.println("|");
		    	zeilenwechsel=0;
		    }
		}
		while (zeilenwechsel !=0 ) {
			System.out.print("|  ");
		    if (++zeilenwechsel==7) {
		    	System.out.println("|");
		    	zeilenwechsel=0;
		    }			
		}

	}

}
