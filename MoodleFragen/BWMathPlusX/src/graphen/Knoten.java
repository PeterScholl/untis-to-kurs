package graphen;

import java.util.ArrayList;

public class Knoten {
	private static ArrayList<Knoten> alleKnoten = new ArrayList<Knoten>();
	private String name;
	private String[] args;
	private boolean marked = false;
	private int farbe = 0;

	/**
	 * @param name
	 */
	public Knoten(String name) throws IllegalArgumentException {
		this(new String[] { name });
	}

	public Knoten(String[] knotendef) throws IllegalArgumentException {
		String nameneu = "";
		if (knotendef != null && knotendef.length > 0) {
			nameneu = knotendef[0];
			args = new String[knotendef.length - 1];
			for (int i = 0; i < args.length; i++)
				args[i] = knotendef[i + 1];
		}
		if (nameneu.equals("")) {
			int add = 0;
			while (Knoten.gibKnotenMitName(nameneu) != null) { // diesen Knoten gibt es schon
				// throw(new IllegalArgumentException("Knoten mit Name \""+name+"\" schon
				// vorhanden"));
				nameneu = name + (add++);
			}
		}

		this.name = nameneu;
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

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public static Knoten gibKnotenMitName(String name) {
		if (alleKnoten == null) {
			alleKnoten = new ArrayList<Knoten>();
			return null;
		}
		for (Knoten k : alleKnoten) {
			if (k.getName().equals(name))
				return k;
		}
		return null;
	}

	public String[] toStringArray() {
		String[] ret = new String[args.length + 1];
		ret[0] = name;
		for (int i = 1; i < ret.length; i++) {
			ret[i] = args[i - 1];
		}
		return ret;
	}

	public static void leereAlleKnoten() {
		alleKnoten = new ArrayList<Knoten>();
	}

	@Override
	public String toString() {
		return "[" + name + "" + (marked ? "*" : "") + "]";
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

	public static void entferneKnoten(Knoten loeschKnoten) {
		alleKnoten.remove(loeschKnoten);
	}

}
