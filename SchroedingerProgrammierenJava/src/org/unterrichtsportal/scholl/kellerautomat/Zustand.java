package org.unterrichtsportal.scholl.kellerautomat;

import java.util.HashMap;
import java.util.Stack;

public class Zustand {
	
	//private Klasse Destination als Zielangabe für einen Übergang
	private class Destination {
		private char kelleraktion;
		private Zustand ziel;
		
		public Destination(char kelleraktion, Zustand ziel) {
			this.kelleraktion = kelleraktion;
			this.ziel = ziel;
		}

		public char getKelleraktion() {
			return kelleraktion;
		}

		public Zustand getZiel() {
			return ziel;
		}
		
		public String toString() {
			if (ziel!=null) return ziel.name;
			return "-Ziel hat keinen Namen-";
		}
		
	}
	//Ende innere Klasse
	private boolean akzeptierend;
	private HashMap<String, Destination> uebergansfunktion = new HashMap<String, Destination>(); 
	private String name;
	private Stack<Character> keller;
	
	public Zustand(String name, Stack<Character> keller) {
		this.name = name;
		this.keller=keller;
		akzeptierend = false;
	}
	
	public void adduebergang(char buchstabe, char kellersymbol, char kelleraktion, Zustand ziel) {
		System.out.println("neuer Uebergang:"+buchstabe+" "+kellersymbol+" "+kelleraktion+" Zustand:"+ziel);
		uebergansfunktion.put(""+buchstabe+kellersymbol, new Destination(kelleraktion, ziel));
	}

	public boolean isAkzeptierend() {
		return akzeptierend;
	}

	public void setAkzeptierend(boolean akzeptierend) {
		this.akzeptierend = akzeptierend;
	}

	public String getName() {
		return name;
	}
	
	public Zustand gibZiel(char buchstabe) {
		String weg = ""+buchstabe+keller.peek();
		//System.out.println("Weg von "+name+": "+weg);
		Destination z = uebergansfunktion.get(weg);
		if (z==null) {
			//System.out.println("Kein Ziel zu diesem Weg gefunden");
			//System.out.println("Alle Wege:");
			//System.out.println(uebergansfunktion.toString());
			return null;
		}
		switch(z.getKelleraktion()) {
		case '.':
			System.out.print(" Kelleraktion: nop ");
			break;
		case '-':
			System.out.print(" Kelleraktion: pop ");
			if (keller.peek()=='#') {
				return null;
			} else {
				keller.pop();
			}
			break;
		default:
			System.out.print(" Kelleraktion: push("+z.getKelleraktion()+") ");
			keller.push(z.getKelleraktion());
		}
		return z.getZiel();
	}
	
	public boolean wegVorhanden(char buchstabe) {
		String weg = ""+buchstabe+keller.peek();
		Destination z = uebergansfunktion.get(weg);
		if (z==null) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public String toString() {
		String erg = "Zustand: "+name;
		erg += " aktuelle Kellervariable: "+keller.peek();
		return erg;
	}

}
