package graphen;

import java.util.ArrayList;

public class Knoten {
	private static ArrayList<Knoten> alleKnoten;
	private String name;
	private boolean marked=false;
	private int farbe=0;
	/**
	 * @param name
	 */
	public Knoten(String name) throws IllegalArgumentException {
		if (Knoten.gibKnotenMitName(name)!=null) { // diesen Knoten gibt es schon
			throw(new IllegalArgumentException("Knoten mit Name \""+name+"\" schon vorhanden"));
		}
		this.name = name;
		Knoten.alleKnoten.add(this);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isMarked() {
		return marked;
	}
	public void setMarked(boolean marked) {
		this.marked = marked;
	}
	public int getFarbe() {
		return farbe;
	}
	public void setFarbe(int farbe) {
		this.farbe = farbe;
	}
	
	public static Knoten gibKnotenMitName(String name) {
		if (alleKnoten==null) {
			alleKnoten = new ArrayList<Knoten>();
			return null;
		}
		for (Knoten k : alleKnoten ) {
			if (k.getName().equals(name)) return k;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "[" + name + ""+ (marked?"*":"") + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (marked ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Knoten))
			return false;
		Knoten other = (Knoten) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
	
}
