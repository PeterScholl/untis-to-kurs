package bwinf38Rd2Abbiegen;

import java.util.ArrayList;

public class Weg {
	ArrayList<Punkt> punkte = new ArrayList<Punkt>();
	
	public void addPunkt(Punkt p) {
		punkte.add(p);
	}
	
	public void removePunkt() {
		punkte.remove(punkte.size()-1);
	}
	
	public boolean hatPunkt(Punkt p) {
		for (Punkt p1: punkte) { 
			if (p.equals(p1)) return true;
		}
		return false;
	}
	
	public double laenge() {
		double l=0.0;
		for (int i=0; i<punkte.size()-1; i++) {
			l+=punkte.get(i).abstand(punkte.get(i+1));
		}
		return l;
	}
	
	public int anzAbbiegen() {
		int n=0;
		for (int i=1; i<punkte.size()-1;i++) {
			Punkt p1 = punkte.get(i-1);
			Punkt p2 = punkte.get(i);
			Punkt p3 = punkte.get(i+1);
			if ( p2.abbiegen(p1,p3) ) n++;
		}
		return n;
	}

	@Override
	public String toString() {
		return "Weg [punkte=" + punkte + ", laenge()=" + laenge() + ", anzAbbiegen()=" + anzAbbiegen() + "]";
	}
	
	public Weg clone() {
		Weg result = new Weg();
		for (int i=0; i<punkte.size(); i++) {
			result.addPunkt(punkte.get(i));
		}
		return result;
	}

	public Punkt gibStandpunkt() {
		return punkte.get(punkte.size()-1);
	}

	public ArrayList<Punkt> getPunkte() {
		return punkte;
	}
	
	

}
