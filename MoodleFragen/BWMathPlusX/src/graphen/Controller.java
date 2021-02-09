package graphen;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Controller {
	public static final int NeuerPunkt = 1;
	public static final int GraphClicked = 2;
	public static final int NeueKante = 3;
	private AbstrGraph graph;
	private HashMap<String, Punkt> knotenpunkte = new HashMap<String, Punkt>();
	private ArrayList<String[]> kanten = new ArrayList<String[]>();
	private int[] grenzen = new int[4];
	private boolean renewgrenzen = true;
	private String grabbed = null;
	private String kantenStart = null;
	private View v = new View(this, "Facharbeit Graphen - v.0");
	private int nextpunktname = 0;

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
		renewgrenzen = true;
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
		// hier soll der Graph auf View gezeichnet werden
		if (v==null) return;
		BufferedImage img = v.getBufferedImage();
		Graphics g = img.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.setColor(Color.black);
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
		double xstep = (!(xmax == xmin) ? (img.getWidth() - 20.0) / (xmax - xmin) : 0); // Schrittweite bestimmen Pixel
																						// pro
																						// x
		double ystep = (ymax != ymin ? (img.getHeight() - 20.0) / (ymax - ymin) : 0);
		int radius = Math.round((float) Math.min(5, Math.min(ystep / 8, xstep / 8))); // Radius eines Punktes
		System.out.println("xstep: " + xstep + " ystep: " + ystep + " Width: " + img.getWidth());
		for (String[] kante : kanten) { // alle Kanten Zeichnen
			Punkt p1 = knotenpunkte.get(kante[0]);
			Punkt p2 = knotenpunkte.get(kante[1]);
			// System.out.println("Zeichne Linie von "+p1.getX()+","+p1.getY()+" nach
			// "+p2.getX()+","+p2.getY());
			// Startpunkt der Linie
			int sx = Math.round((float) (10 + 1.0 * (p1.getX() - xmin) * xstep));
			int sy = Math.round((float) (img.getHeight() - (10. + 1.0 * (p1.getY() - ymin) * ystep)));
			// Endpunkt der Linie
			int ex = Math.round((float) (10. + 1.0 * (p2.getX() - xmin) * xstep));
			int ey = Math.round((float) (img.getHeight() - (10. + 1.0 * (p2.getY() - ymin) * ystep)));
			g.drawLine(sx, sy, ex, ey);
			
		}
		//alle Knoten zeichnen
		for (String pname : knotenpunkte.keySet()) {
			Punkt p = knotenpunkte.get(pname);
			if (pname.equals(grabbed) || pname.equals(kantenStart)) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.black);
			}
			int sx = Math.round((float) (10 + 1.0 * (p.getX() - xmin) * xstep));
			int sy = Math.round((float) (img.getHeight() - (10. + 1.0 * (p.getY() - ymin) * ystep)));
			g.fillOval(sx - radius, sy - radius, 2 * radius, 2 * radius);
		}
		g.setColor(Color.black);
		v.updateCanvas();
	}

	/**
	 * Schreibt den Knoten an den Bildschirmkoordinaten (x,y) in die Variable grabbed, sonst wird null dort eingetragen
	 * @param x - Bildschirmkoordiante x
	 * @param y - Bildschirmkoordinate y
	 */
	public void grabPos(int x, int y) {
		int[] pos = mausPosToPoint(x, y);
		grabbed = knotenAnPos(pos[0], pos[1]);
		System.out.println("Position: " + Arrays.toString(pos) + " grabbed: " + grabbed);
		if (grabbed!= null) {
			v.setStatusLine("Punkt: "+grabbed);
			graphZeichnen();
		}
	}
	
	/**
	 * liefert den Namen des Knoten an der Position (x,y) wenn es ihn gibt, sonst null
	 * @param x x-Koordinate des gesuchten Knotens
	 * @param y y-Koordinate des gesuchten Knotens
	 * @return Namen des Knotens oder null
	 */
	public String knotenAnPos(int x, int y) {
		for (String name : knotenpunkte.keySet()) {
			Punkt akt = knotenpunkte.get(name);
			System.out.println("Vergleich: " + akt.getX() + "-" + akt.getY() + " - " + name + " : " + (akt.getX() == x)
					+ " und " + (akt.getY() == y) + " : " + x + "," + y);
			if (akt.getX() == x && akt.getY() == y) {
				return name;
			}
		}		
		return null;
	}

	public void dragged(int x, int y) {
		if (grabbed != null) {
			int[] pos = mausPosToPoint(x, y);
			Punkt alt = knotenpunkte.put(grabbed, new Punkt(pos[0], pos[1]));
			if (alt == null || alt.getX() != pos[0] || alt.getY() != pos[1]) { // neu zeichnen
				this.graphZeichnen();
			}
		}
	}

	public void released(int x, int y) {
		grabbed = null;
		this.graphZeichnen();
	}

	private int[] mausPosToPoint(int x, int y) {
		int xmax = grenzen[2];
		int xmin = grenzen[0];
		int ymax = grenzen[3];
		int ymin = grenzen[1];
	    System.out.println("kleinster Punkt: ("+xmin+","+ymin+") - groesster Punkt:"+xmax+","+ymax+")");
	    System.out.println("v.getMaxX: "+v.getMaxX()+" MaxY: "+v.getMaxY());
		double xstep = (!(xmax == xmin) ? (v.getMaxX() - 20.0) / (xmax - xmin) : 0); // Schrittweite bestimmen Pixel pro
																						// x
		double ystep = (ymax != ymin ? (v.getMaxY() - 20.0) / (ymax - ymin) : 0);
		System.out.println("xstep: "+xstep+" ystep: "+ystep);
		float posx = (float) ((x - 10) / xstep + xmin);
		float posy = (float) (((v.getMaxY() - y) - 10) / ystep + ymin);
		return new int[] { Math.round(posx), Math.round(posy) };
	}
	
	public void neuerPunkt(int x, int y) {
		String name = ""+nextpunktname++;
		while (knotenpunkte.keySet().contains(name)) {
			name = ""+nextpunktname++;
		}
		knotenpunkte.put(name, new Punkt(x, y));		
	}

	public void execute(int befehl, String[] args) {
		int x,y;
		int[] pt;
		switch (befehl) {
		case NeuerPunkt:
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			pt = mausPosToPoint(x, y);
			this.neuerPunkt(pt[0], pt[1]);
			this.graphZeichnen();
			break;
		case GraphClicked:
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			pt = mausPosToPoint(x, y);
			graphclicked(pt);
			break;
		case NeueKante:
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			pt = mausPosToPoint(x, y);
			kantenStart = knotenAnPos(pt[0], pt[1]);
			if (kantenStart != null) {
				v.setStatusLine("Zielknoten der Kante anklicken");
				this.graphZeichnen();
			}
					
			//Wenn dort ein Punkt ist - merken - zweiten Punkt suchen
			break;
		default:
				
		}
	}

	private void graphclicked(int[] pt) {
		if (kantenStart != null) { //Versuch eine Kante zu zeichnen
			String ziel = knotenAnPos(pt[0], pt[1]);
			if (ziel == null) {
				v.setStatusLine("Kein Zielpunkt");
			} else {
				kanten.add(new String[] {kantenStart, ziel});
				v.setStatusLine("Kante hinzugefügt: "+kantenStart+"-"+ziel);
			}
			kantenStart=null;
			this.graphZeichnen();
		}
		
		
	}

}
