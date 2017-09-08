package org.unterrichtsportal.untistokurs;

public class Unterricht {
	private int uNummer;
	private String klasse;
	private String lehrer;
	private String fachKurs;
	private String raum;
	private int tag;
	private int stunde;
	
	public Unterricht() {
		
	}
	
	public Unterricht(int uNummer, String klasse, String lehrer, String fachKurs, String raum, int tag, int stunde) {
		super();
		this.uNummer = uNummer;
		this.klasse = klasse;
		this.lehrer = lehrer;
		this.fachKurs = fachKurs;
		this.raum = raum;
		this.tag = tag;
		this.stunde = stunde;
	}

	public int getuNummer() {
		return uNummer;
	}

	public void setuNummer(int uNummer) {
		this.uNummer = uNummer;
	}

	public String getKlasse() {
		return klasse;
	}

	public void setKlasse(String klasse) {
		this.klasse = klasse;
	}

	public String getLehrer() {
		return lehrer;
	}

	public void setLehrer(String lehrer) {
		this.lehrer = lehrer;
	}

	public String getFachKurs() {
		return fachKurs;
	}

	public void setFachKurs(String fachKurs) {
		this.fachKurs = fachKurs;
	}

	public String getRaum() {
		return raum;
	}

	public void setRaum(String raum) {
		this.raum = raum;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getStunde() {
		return stunde;
	}

	public void setStunde(int stunde) {
		this.stunde = stunde;
	}

	@Override
	public String toString() {
		return "Unterricht [uNummer=" + uNummer + ", klasse=" + klasse + ", lehrer=" + lehrer + ", fachKurs=" + fachKurs
				+ ", raum=" + raum + ", tag=" + tag + ", stunde=" + stunde + "]";
	}
	
	

}
