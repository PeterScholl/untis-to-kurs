package facharbeit;

class Kante {
	private Knoten p1;
	private Knoten p2;
	private int gewicht;
	private String[] info = new String[0];

	public Kante(Knoten p1, Knoten p2, int e) {
		this.p1 = p1;
		this.p2 = p2;
		this.gewicht = e;
	}

	public Kante(Knoten p1, Knoten p2) {
		this(p1, p2, 0);
	}

	public Knoten getKnoten1() {
		return p1;
	}

	public Knoten getKnoten2() {
		return p2;
	}

	public boolean enthaeltKnoten(Knoten e) {
		if (e == p1 || e == p2) {
			return true;
		}
		return false;
	}

	public Knoten getGegenKnoten(Knoten e) {
		if (e == p1) {
			return p2;
		} else if (e == p2) {
			return p1;
		}
		return null;
	}

	public int getGewicht() {
		return this.gewicht;
	}

	// Punkte auf Kante/Gewicht/
	@Override
	public String toString() {
		return p1 + "-" + p2;
	}

	// **** für die Graphische Anbindung
	public String[] getInfo() {
		return info;
	}

	public void setInfo(String[] info) {
		this.info = info;
	}

}