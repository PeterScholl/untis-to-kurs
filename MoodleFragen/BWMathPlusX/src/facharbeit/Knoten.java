package facharbeit;

class Knoten {
	private String name;
	private int x; // Koordinaten um den Graphen zu zeichnen
	private int y;
	private int abstand = Integer.MAX_VALUE; // für Dijkstra
	private Knoten vonKnoten; // für Dijkstra
	private String[] info = new String[0];

	public Knoten(String name) {
		this.name = name;
	}

	public Knoten(String name, int x, int y) {
		this(name);
		this.x = x;
		this.y = y;
	}

	public String getName() {
		return this.name;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return this.x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return this.y;
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getAbstand() {
		return abstand;
	}

	public void setAbstand(int abstand) {
		this.abstand = abstand;
	}

	public Knoten getVonKnoten() {
		return vonKnoten;
	}

	public void setVonKnoten(Knoten vonKnoten) {
		this.vonKnoten = vonKnoten;
	}

	@Override
	public String toString() {
		return name
				+ (abstand < Integer.MAX_VALUE ? "/" + abstand + " von " + (vonKnoten == null ? "-" : vonKnoten.name)
						: "");
	}

	// **** für die Graphische Anbindung
	public String[] getInfo() {
		return info;
	}

	public void setInfo(String[] info) {
		this.info = info;
	}

}
