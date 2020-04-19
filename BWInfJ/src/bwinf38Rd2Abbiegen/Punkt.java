package bwinf38Rd2Abbiegen;

public class Punkt {
	int x,y; //Koordinaten des Punktes

	/**
	 * @param x
	 * @param y
	 */
	public Punkt(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Punkt(String s) {
		String[] t = s.split("[(,)]");
		this.x = Integer.parseInt(t[1]);
		this.y = Integer.parseInt(t[2]);
	}
	
	public double abstand(Punkt p) {
		return (Math.sqrt((p.x-x)*(p.x-x) + (p.y-y)*(p.y-y)));
	}
	
	
	@Override
	public boolean equals(Object obj) {
		return this.equals((Punkt)obj);
	}

	public boolean equals(Punkt p) {
		return this.x==p.x && this.y==p.y;
	}
	
	public String toString() {
		return "("+this.x+","+this.y+")";
	}

	/**
	 * @param von Punkt von dem man kommt
	 * @param nach Punkt zu dem man geht
	 * @return true wenn auf dem Weg von von nach nach im eigenen Punkt abgebogen wird
	 */
	public boolean abbiegen(Punkt von, Punkt nach) {
		return !((y-von.y)*(nach.x-x) == (nach.y-y)*(x-von.x)); 
	}

}
