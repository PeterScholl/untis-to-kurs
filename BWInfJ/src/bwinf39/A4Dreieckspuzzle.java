package bwinf39;

public class A4Dreieckspuzzle {
	public static final int NN=1;
	public static final int NO=2;
	public static final int SO=3;
	public static final int SS=4;
	public static final int SW=5;
	public static final int NW=6;

	public static int zeileVonFeld(int feld) {
		if (feld < 0) return -1;
		int zeile = 0;
		int grenze = 0;
		while (feld > grenze) {
			zeile++;
			grenze+=2*zeile+1;
		}
		return zeile;
	}
	
	public static int spalteVonFeld(int feld) {
		if (feld < 0) return -1;
		int zeile = 0;
		int grenze = 0;
		while (feld > grenze) {
			zeile++;
			grenze+=2*zeile+1;
		}
		return feld-grenze+2*zeile;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
