package org.unterrichtsportal.scholl.mathekalender2019;
//Aufgabe 18 aus 2019
public class Bredelebacken {
	public static int lieferant = 2; // drei Lieferanten sind möglich

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] backanzahl = new int[] {0,0,0,0}; // ButterBredele, SpitzBredele, spitzBUben, Kokosbredele
		int stelle = 3;
		int maxHerz = 0;
		while (stelle >= 0) {
			backanzahl = next(backanzahl, stelle);
			if (zulaessig(backanzahl)) {
				stelle=3;
				// Werte prüfen
				if (countHerz(backanzahl)>= maxHerz) {
					maxHerz = countHerz(backanzahl);
					int[] b = backanzahl;
					System.out.println("MaxHerz: " + maxHerz + "bei: "+b[0]+","+b[1]+","+b[2]+","+b[3]);
				}
			} else {
				stelle--;
			}
		}

	}
	
	public static int countHerz(int[] b) {
		return 20*b[0]+15*b[1]+25*b[2]+10*b[3];
	}
	public static int[] next(int[] b, int stelle) {
		int[] a = new int[b.length];
		for (int i=0; i<a.length; i++) {
			if (i< stelle) a[i]=b[i];
			else if (i==stelle) a[i]=b[i]+1;
			else b[i]=0;
		}
		return a;
	}
	
	public static boolean zulaessig(int[] b) {
		int mehl = (b[0]+b[1]+b[2])*500;
		int zucker = 250*b[0]+200*(b[1]+b[2])+400*b[3];
		int butter = mehl/2;
		int eier = 4*b[0]+2*(b[1]+b[2])+3*b[3];
		int haselnuss = 250*b[1];
		int marmelade = b[2];
		int kokosnuss = 400*b[3];
		if (marmelade > 2) return false;
		switch (lieferant) {
			case 1:
				if (mehl>3500) return false;
				if (zucker> 2000) return false;
				if (butter>3000) return false;
				if (eier>30) return false;
				if (haselnuss>500) return false;
				if (kokosnuss > 0) return false;
				break;
			case 2:
				if (mehl>4000) return false;
				if (zucker> 2000) return false;
				if (butter>3000) return false;
				if (eier>22) return false;
				if (haselnuss>1000) return false;
				if (kokosnuss > 0) return false;
				break;
			case 3:
				if (mehl>3000) return false;
				if (zucker> 2000) return false;
				if (butter>1000) return false;
				if (eier>16) return false;
				if (haselnuss>500) return false;
				if (kokosnuss > 400) return false;
				break;
			default:
				return true;
		}
		return true;
	}

}
