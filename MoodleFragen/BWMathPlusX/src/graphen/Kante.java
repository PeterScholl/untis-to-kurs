package graphen;

public class Kante {
	private Knoten start,ziel;
	private int gewicht;
	private boolean gerichtet = false;
	/**
	 * @param start
	 * @param ziel
	 * @param gewicht
	 * @param gerichtet
	 */
	public Kante(Knoten start, Knoten ziel, int gewicht, boolean gerichtet) {
		this.start = start;
		this.ziel = ziel;
		this.gewicht = gewicht;
		this.gerichtet = gerichtet;
	}
	
	public Kante(Knoten start, Knoten ziel) {
		this(start,ziel,0,false);
	}
	
	public Kante(Knoten start, Knoten ziel, int gewicht) {
		this(start,ziel,gewicht,false);
	}

	public Knoten getStart() {
		return start;
	}

	public void setStart(Knoten start) {
		this.start = start;
	}

	public Knoten getZiel() {
		return ziel;
	}

	public void setZiel(Knoten ziel) {
		this.ziel = ziel;
	}

	public int getGewicht() {
		return gewicht;
	}

	public void setGewicht(int gewicht) {
		this.gewicht = gewicht;
	}

	public boolean isGerichtet() {
		return gerichtet;
	}

	public void setGerichtet(boolean gerichtet) {
		this.gerichtet = gerichtet;
	}
	
	public boolean hatKnoten(Knoten k) {
		if (k==null) return false;
		return k.equals(start) || k.equals(ziel);
	}
	
	public Knoten gibAnderenKnoten(Knoten k) {
		if (!this.hatKnoten(k)) return null;
		return (k.equals(start)?ziel:start);
	}
	
	@Override
	public String toString() {
		return "[" + start + (gerichtet?"->":"--") + ziel + (gewicht>0?"("+gewicht+")":"") + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (gerichtet ? 1231 : 1237);
		result = prime * result + gewicht;
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((ziel == null) ? 0 : ziel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Kante))
			return false;
		Kante other = (Kante) obj;
		if (gerichtet != other.gerichtet)
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} 
		if (ziel == null) {
			if (other.ziel != null)
				return false;
		} 
		if (ziel != null && start != null && ziel.equals(other.ziel) && start.equals(other.start))
			return true;
		if (ziel != null && start != null && ziel.equals(other.start) && start.equals(other.ziel))
			return true;
		return false;
	}
	
	
		

}
