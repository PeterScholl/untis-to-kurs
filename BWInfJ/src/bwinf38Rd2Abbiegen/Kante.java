package bwinf38Rd2Abbiegen;

public class Kante {
	Punkt start,stop;

	/**
	 * @param start
	 * @param stop
	 */
	public Kante(Punkt start, Punkt stop) {
		this.start = start;
		this.stop = stop;
	}
	
	public Kante(String s) {
		String[] t = s.split(" ");
		this.start = new Punkt(t[0]);
		this.stop = new Punkt(t[1]);
	}
	
	public double length() {
		return start.abstand(stop);
	}
	
	public int getMaxXKoord() {
		return start.x > stop.x ? start.x : stop.x;
	}
	public int getMinXKoord() {
		return start.x < stop.x ? start.x : stop.x;
	}

	public int getMaxYKoord() {
		return start.y > stop.y ? start.y : stop.y;
	}
	public int getMinYKoord() {
		return start.y < stop.y ? start.y : stop.y;
	}

	
	@Override
	public boolean equals(Object obj) {
		return this.equals((Kante)obj);
	}
	
	public boolean equals(Kante k) {
		return (this.start.equals(k.start) && this.stop.equals(k.stop)) || (this.start.equals(k.stop) && this.stop.equals(k.start)) ;
	}

	/**
	 * @return the start
	 */
	public Punkt getStart() {
		return start;
	}

	/**
	 * @return the stop
	 */
	public Punkt getStop() {
		return stop;
	}

	public String toString() {
		return ""+start+" "+stop;
	}

	/**
	 * @param pos Punkt von dem aus man weg möchte
	 * @return das Andere Ende dieser Kante, wenn pos ein Punkt dieser Kante ist
	 */
	public Punkt gibZiel(Punkt pos) {
		if (start.equals(pos)) return stop;
		if (stop.equals(pos)) return start;
		return null;
	}

}
