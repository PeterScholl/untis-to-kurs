package graphen;

import java.util.Arrays;
import java.util.HashMap;

public class Controller {
	private AbstrGraph graph;
	private HashMap<String, Punkt> knotenpunkte = new HashMap<String, Punkt>();
	private HashMap<String, String> kanten = new HashMap<String, String>();
	private int[] grenzen = new int[4];
	private boolean renewgrenzen = true;
	private Turtle t = new Turtle(700, 300, this);
	private String grabbed = null;

	private class Punkt {
		private int x, y;

		public Punkt(String punkt) {
			System.out.println("Neuer Punkt: " + punkt);
			String[] koords = punkt.replaceAll("[() ]", "").split(",");
			if (koords.length != 2)
				throw new IllegalArgumentException("Could not parse String " + punkt + " to point!");
			// System.out.println("Koords:"+koords[0]+","+koords[1]);
			this.x = Integer.parseInt(koords[0]);
			this.y = Integer.parseInt(koords[1]);
		}

		public Punkt(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
	}

	public Controller(AbstrGraph graph) {
		this.graph = graph;
		HashMap<String, String> knotenliste = graph.getKnotenPunkte();
		for (String key : knotenliste.keySet()) {
			this.knotenpunkte.put(key, new Punkt(knotenliste.get(key)));
		}
		kanten = graph.getKnotenVerbindungen();
	}

	public int[] gibGrenzKoordinatenDerKnoten() {
		int minx = Integer.MAX_VALUE;
		int miny = Integer.MAX_VALUE;
		int maxx = Integer.MIN_VALUE;
		int maxy = Integer.MIN_VALUE;
		for (Punkt k : knotenpunkte.values()) {
			minx = (k.getX() < minx ? k.getX() : minx);
			miny = (k.getY() < miny ? k.getY() : miny);
			maxx = (k.getX() > maxx ? k.getX() : maxx);
			maxy = (k.getY() > maxy ? k.getY() : maxy);
		}
		return new int[] { minx, miny, maxx, maxy };
	}

	public void graphZeichnen() {
		// hier soll der Graph gezeichnet werden
		t.clearScreen();
		t.setColor(Turtle.black);
		if (renewgrenzen) {
			grenzen = this.gibGrenzKoordinatenDerKnoten();// Jeweils maximale Koordinaten bestimmen (Ecken des Bildes)
			renewgrenzen = false;
		}
		int xmax = grenzen[2];
		int xmin = grenzen[0];
		int ymax = grenzen[3];
		int ymin = grenzen[1];
		System.out
				.println("kleinster Punkt: (" + xmin + "," + ymin + ") - groesster Punkt: (" + xmax + "," + ymax + ")");
		double xstep = (!(xmax == xmin) ? (t.getMaxX() - 20.0) / (xmax - xmin) : 0); // Schrittweite bestimmen Pixel pro
																						// x
		double ystep = (ymax != ymin ? (t.getMaxY() - 20.0) / (ymax - ymin) : 0);
		double radius = Math.min(5, Math.min(ystep / 8, xstep / 8)); // Radius eines Punktes
		System.out.println("xstep: " + xstep + " ystep: " + ystep + " t.getMaxX: " + t.getMaxX());
		for (String start : kanten.keySet()) { // alle Kanten Zeichnen
			Punkt p1 = knotenpunkte.get(start);
			Punkt p2 = knotenpunkte.get(kanten.get(start));
			// System.out.println("Zeichne Linie von "+p1.getX()+","+p1.getY()+" nach
			// "+p2.getX()+","+p2.getY());
			t.hebeStift();
			// Zum Startpunkt gehen
			double sx = 10 + 1.0 * (p1.getX() - xmin) * xstep;
			double sy = t.getMaxY() - (10. + 1.0 * (p1.getY() - ymin) * ystep);
			// System.out.println("Startpunkt: "+sx+","+sy);
			t.geheNach(sx, sy);
			t.senkeStift();
			t.fillCircle(radius); // Dort einen Punkt zeichnen
			// Linie zum Zielpunkt ziehen
			t.geheNach(10. + 1.0 * (p2.getX() - xmin) * xstep, t.getMaxY() - (10. + 1.0 * (p2.getY() - ymin) * ystep));
			t.fillCircle(radius);
		}
	}

	public void grabPos(int x, int y) {
		int[] pos = turtlePosToPoint(x, y);
		for (String name : knotenpunkte.keySet()) {
			Punkt akt = knotenpunkte.get(name);
			System.out.println("Vergleich: " + akt.getX() + "-" + akt.getY() + " - " + name + " : " + (akt.getX() == x)
					+ " und " + (akt.getY() == y) + " : " + x + "," + y);
			if (akt.getX() == pos[0] && akt.getY() == pos[1]) {
				System.out.println("found");
				grabbed = name;
				break;
			}
		}
		System.out.println("Position: " + Arrays.toString(pos) + " grabbed: " + grabbed);
	}

	public void dragged(int x, int y) {
		if (grabbed != null) {
			int[] pos = turtlePosToPoint(x, y);
			Punkt alt = knotenpunkte.put(grabbed, new Punkt(pos[0], pos[1]));
			if (alt == null || alt.getX() != pos[0] || alt.getY() != pos[1]) { // neu zeichnen
				this.graphZeichnen();
			}
		}
	}

	public void released(int x, int y) {
		grabbed = null;
	}

	private int[] turtlePosToPoint(int x, int y) {
		int xmax = grenzen[2];
		int xmin = grenzen[0];
		int ymax = grenzen[3];
		int ymin = grenzen[1];
		// System.out.println("kleinster Punkt: ("+xmin+","+ymin+") - groesster Punkt:
		// ("+xmax+","+ymax+")");
		double xstep = (!(xmax == xmin) ? (t.getMaxX() - 20.0) / (xmax - xmin) : 0); // Schrittweite bestimmen Pixel pro
																						// x
		double ystep = (ymax != ymin ? (t.getMaxY() - 20.0) / (ymax - ymin) : 0);
		float posx = (float) ((x - 10) / xstep + xmin);
		float posy = (float) (((t.getMaxY() - y) - 10) / ystep + ymin);
		return new int[] { Math.round(posx), Math.round(posy) };
	}

}
