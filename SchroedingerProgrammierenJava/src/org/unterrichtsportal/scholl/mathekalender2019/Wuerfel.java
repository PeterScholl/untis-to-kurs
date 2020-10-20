package org.unterrichtsportal.scholl.mathekalender2019;

import java.util.Random;

public class Wuerfel {
	int oben = 1;
	int unten = 6;
	int westen = 3;
	int osten = 4;
	int norden = 2;
	int sueden = 5;

	public Wuerfel() {

	}

	public void kippeOsten() {
		int temp = unten;
		unten = osten;
		osten = oben;
		oben = westen;
		westen = temp;
	}

	public void kippeWesten() {
		int temp = unten;
		unten = westen;
		westen = oben;
		oben = osten;
		osten = temp;
	}

	public void kippeNorden() {
		int temp = unten;
		unten = norden;
		norden = oben;
		oben = sueden;
		sueden = temp;
	}

	public void kippeSueden() {
		int temp = unten;
		unten = sueden;
		sueden = oben;
		oben = norden;
		norden = temp;
	}

	public String toString() {
		String out = "";
		out += " " + norden + " \n";
		out += "" + westen + "" + oben + "" + osten + "\n";
		out += " " + sueden + " \n";
		return out;
	}

	public int getOben() {
		return oben;
	}

	public int getUnten() {
		return unten;
	}

	public int getWesten() {
		return westen;
	}

	public int getOsten() {
		return osten;
	}

	public int getNorden() {
		return norden;
	}

	public int getSueden() {
		return sueden;
	}

	public static void main(String args[]) {
		Wuerfel w = new Wuerfel();
		Random zfl = new Random();
		int s = 8;
		int n = 0, c = 0, we = 0;
		String weg="";
		while (s < 22) {
			weg = "";
			for (int i = 0; i < zfl.nextInt(12); i++)
				w.kippeNorden();
			for (int i = 0; i < zfl.nextInt(12); i++)
				w.kippeOsten();
			System.out.println(w);
			n=0; c=0; we=0;
			s = w.getOben();
			for (int i = 0; i < 4; i++) {
				if (zfl.nextInt(2) == 1 && n < 2 || i - n >= 2) {
					w.kippeNorden();
					n++;
					weg += "N";
				} else {
					w.kippeOsten();
					we++;
					weg += "W";
				}
				if (c % 40 == 39)
					weg += "\n";
				s += w.getOben();
				c++;
			}
		}
		
		System.out.println("n: " + n + " we: " + we + " c: " + c + " s: " + s);
		System.out.print(w);
		System.out.println(weg);
	}
}
