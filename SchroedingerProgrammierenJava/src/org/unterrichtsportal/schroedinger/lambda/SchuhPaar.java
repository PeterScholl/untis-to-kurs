package org.unterrichtsportal.schroedinger.lambda;

public class SchuhPaar implements Comparable<SchuhPaar> {
	String farbe;
	int groesse;
	boolean mitStoeckeln;
	
	@Override
	public int compareTo(SchuhPaar st) {
		if (st instanceof SchuhPaar) {
			SchuhPaar s = (SchuhPaar)st;
		if (! (this.groesse == s.groesse)) {
			return ((Integer)this.groesse).compareTo(s.groesse);
		}
		if (!this.farbe.equals(s.farbe)) {
			return this.farbe.compareTo(s.farbe);
		}
		return ((Boolean)mitStoeckeln).compareTo(s.mitStoeckeln);
		}
		return -1;
	}
	
	
	
	
	public SchuhPaar(String farbe, int groesse, boolean mitStoeckeln) {
		super();
		this.farbe = farbe;
		this.groesse = groesse;
		this.mitStoeckeln = mitStoeckeln;
	}
	
	public String getFarbe() {
		return farbe;
	}

	public void setFarbe(String farbe) {
		this.farbe = farbe;
	}

	public int getGroesse() {
		return groesse;
	}


	public void setGroesse(int groesse) {
		this.groesse = groesse;
	}

	public boolean isMitStoeckeln() {
		return mitStoeckeln;
	}

	public void setMitStoeckeln(boolean mitStoeckeln) {
		this.mitStoeckeln = mitStoeckeln;
	}




	@Override
	public String toString() {
		return "SchuhPaar [farbe=" + farbe + ", groesse=" + groesse + ", mitStoeckeln=" + mitStoeckeln + "]";
	}


}
