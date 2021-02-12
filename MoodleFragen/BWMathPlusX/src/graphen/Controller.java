package graphen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JOptionPane;

public class Controller {
	private static final Color[] farbenliste = new Color[] { Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY,
			Color.GRAY, Color.GREEN };

	public static final int NeuerPunkt = 1;
	public static final int CanvasClicked = 2;
	public static final int NeueKante = 3;
	public static final int ZOOM = 4;
	public static final int LinienDickeAendern = 5;
	public static final int GitterZeichnen = 6;
	public static final int ZOOM_Einstellen = 7;
	public static final int Status_aendern = 8;
	public static final int KantenLoeschHotspots = 9;
	public static final int KanteLoeschen = 10;
	public static final int HotspotsLoeschen = 11;
	public static final int VollstGraph = 12;
	public static final int BipartiterGraph = 13;
	private AbstrGraph graph;
	private HashMap<String, Punkt> knotenpunkte = new HashMap<String, Punkt>();
	private ArrayList<String[]> kanten = new ArrayList<String[]>();
	private ArrayList<Hotspot> hotspots = new ArrayList<Hotspot>(); // bereiche bei denen Clicks besondere Aktionen
																	// auslösen sollen
	private int[] grenzen = new int[4];
	private boolean renewgrenzen = true;
	private String grabbed = null;
	private String kantenStart = null;
	private View v = new View(this, "Facharbeit Graphen - v.0");
	private int nextpunktname = 0;
	private int liniendicke = 3;
	private boolean gitterZeichnen = true; // soll ein Karogitter gezeichnet werden

	private class Punkt {
		private int x, y;
		private String[] args;

		public Punkt(String punkt, String[] args) {
			// System.out.println("Neuer Punkt: " + punkt);
			String[] koords = punkt.replaceAll("[() ]", "").split(",");
			if (koords.length != 2)
				throw new IllegalArgumentException("Could not parse String " + punkt + " to point!");
			// System.out.println("Koords:"+koords[0]+","+koords[1]);
			this.x = Integer.parseInt(koords[0]);
			this.y = Integer.parseInt(koords[1]);
			this.args = args;
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

		public void setXY(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public String[] getArgs() {
			return args;
		}
	}

	private class Hotspot {
		public int x, y, w, h, command;
		public String[] args;
		public Color color = Color.LIGHT_GRAY;

		public Hotspot(int x, int y, int w, int h, int command, String[] args) {
			this(x, y, w, h, command, args, Color.LIGHT_GRAY);
		}

		public Hotspot(int x, int y, int w, int h, int command, String[] args, Color color) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.command = command;
			this.args = args;
			this.color = color;
		}

		public boolean isInside(int x, int y) {
			return (x - this.x >= 0 && x - this.x <= w && y - this.y >= 0 && y - this.y <= h);
		}

		public void fireIfInside(int x, int y) {
			System.out.print("Hotspot abfeuern?");
			if (isInside(x, y)) {
				System.out.println("ja! :" + command + ", " + Arrays.toString(args));
				// deaktivieren
				this.x = -100;
				execute(command, args);
			}
		}
	}

	public Controller(AbstrGraph graph) {
		this.graph = graph;
		this.graphNeuLaden();
		// hotspots.add(new Hotspot(20, 20, 30, 30, HotspotsLoeschen, new String[] {
		// "Hotspot !" })); //Testhotspot
	}
	
	private void graphNeuLaden() {
		ArrayList<String[]> knotenliste = this.graph.getKnotenPunkte();
		this.knotenpunkte = new HashMap<String,Punkt>();
		for (String[] knoten : knotenliste) {
			Punkt punkt = new Punkt(knoten[1], knoten);
			this.knotenpunkte.put(knoten[0], punkt);
		}
		kanten = graph.getKnotenVerbindungen();
		renewgrenzen = true;
		//TODO: muss noch mehr zurückgesetzt werden?
		this.execute(HotspotsLoeschen, null);
		graphZeichnen();
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
		if (v == null)
			return;
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
		// System.out.println("kleinster Punkt: (" + xmin + "," + ymin + ") - groesster
		// Punkt: (" + xmax + "," + ymax + ")");
		double xstep = (!(xmax == xmin) ? (img.getWidth() - 20.0) / (xmax - xmin) : 0); // Schrittweite bestimmen Pixel
																						// pro
																						// x
		double ystep = (ymax != ymin ? (img.getHeight() - 20.0) / (ymax - ymin) : 0);
		int radius = Math.max(2, Math.round((float) Math.min(5, Math.min(ystep / 8, xstep / 8)))); // Radius eines
		if (gitterZeichnen) { // Koordinatengitter in light-Gray zeichnen
			g.setColor(new Color(240, 240, 240));
			for (int i = grenzen[0]; i <= grenzen[2]; i++) {
				g.drawLine(Math.round((float) (10 + 1.0 * (i - xmin) * xstep)), (img.getHeight() - 10),
						Math.round((float) (10. + 1.0 * (i - xmin) * xstep)), 10);
			}
			for (int j = grenzen[1]; j <= grenzen[3]; j++) {
				g.drawLine(10, Math.round((float) (img.getHeight() - (10. + 1.0 * (j - ymin) * ystep))),
						img.getWidth() - 10, Math.round((float) (img.getHeight() - (10. + 1.0 * (j - ymin) * ystep))));
			}
		}
		// Punktes
		// System.out.println("xstep: " + xstep + " ystep: " + ystep + " Width: " +
		// img.getWidth());
		for (String[] kante : kanten) { // alle Kanten Zeichnen
			//System.out.println("Kante wird gezeichnet: "+Arrays.toString(kante));
			int multifaktor = Math.max(1, getZahlAusSringArray(kante, "-#")+1);
			g.setColor(hatFarbe(kante));
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
			if (multifaktor*liniendicke <= 1) {
				this.bogenZeichnen(g, new int[] {sx, sy},  new int[] {ex,ey}, 30);
				//g.drawLine(sx, sy, ex, ey);
			} else { // dickere Linien zeichnen
				Graphics2D g2 = (Graphics2D) g;
				RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHints(rh);
				g2.setStroke(new BasicStroke(multifaktor*liniendicke));
				g2.draw(new Line2D.Float(sx, sy, ex, ey));
			}
		}
		// alle Knoten zeichnen
		for (String pname : knotenpunkte.keySet()) {
			Punkt p = knotenpunkte.get(pname);
			if (pname.equals(grabbed) || pname.equals(kantenStart)) {
				g.setColor(Color.red);
			} else {
				g.setColor(hatFarbe(p.getArgs()));
			}
			int sx = Math.round((float) (10 + 1.0 * (p.getX() - xmin) * xstep));
			int sy = Math.round((float) (img.getHeight() - (10. + 1.0 * (p.getY() - ymin) * ystep)));
			g.fillOval(sx - radius, sy - radius, 2 * radius, 2 * radius);
		}
		// Hotspots zeichnen
		if (hotspots.size() > 0) {
			for (Hotspot h : hotspots) {
				g.setColor(h.color);
				g.drawRect(h.x, h.y, h.w, h.h);
			}
		}
		g.setColor(Color.black);
		v.updateCanvas();
	}

	/**
	 * Schreibt den Knoten an den Bildschirmkoordinaten (x,y) in die Variable
	 * grabbed, sonst wird null dort eingetragen
	 * 
	 * @param x - Bildschirmkoordiante x
	 * @param y - Bildschirmkoordinate y
	 */
	public void grabPos(int x, int y) {
		int[] pos = canvasPosToGitterpunkt(x, y);
		grabbed = knotenAnGitterPos(pos[0], pos[1]);
		// System.out.println("Position: " + Arrays.toString(pos) + " grabbed: " +
		// grabbed);
		if (grabbed != null) {
			v.setStatusLine("Punkt: " + grabbed);
			graphZeichnen();
		}
	}

	/**
	 * liefert den Namen des Knoten an der Position (x,y) wenn es ihn gibt, sonst
	 * null
	 * 
	 * @param x x-Koordinate des gesuchten Knotens
	 * @param y y-Koordinate des gesuchten Knotens
	 * @return Namen des Knotens oder null
	 */
	public String knotenAnGitterPos(int x, int y) {
		for (String name : knotenpunkte.keySet()) {
			Punkt akt = knotenpunkte.get(name);
			// System.out.println("Vergleich: " + akt.getX() + "-" + akt.getY() + " - " +
			// name + " : " + (akt.getX() == x)
			// + " und " + (akt.getY() == y) + " : " + x + "," + y);
			if (akt.getX() == x && akt.getY() == y) {
				return name;
			}
		}
		return null;
	}

	public void dragged(int x, int y) {
		if (grabbed != null) {
			int[] pos = canvasPosToGitterpunkt(x, y);
			Punkt alt = knotenpunkte.get(grabbed);
			int altx = alt.getX();
			int alty = alt.getY();
			alt.setXY(pos[0], pos[1]);
			if (alt == null || alt.getX() != altx || alt.getY() != alty) { // neu zeichnen
				this.graphZeichnen();
			}
		}
	}

	public void released(int x, int y) {
		grabbed = null;
		this.graphZeichnen();
	}

	private int[] canvasPosToGitterpunkt(int x, int y) {
		int xmax = grenzen[2];
		int xmin = grenzen[0];
		int ymax = grenzen[3];
		int ymin = grenzen[1];
		// System.out.println("kleinster Punkt: (" + xmin + "," + ymin + ") - groesster
		// Punkt:" + xmax + "," + ymax + ")");
		// System.out.println("v.getMaxX: " + v.getMaxX() + " MaxY: " + v.getMaxY());
		double xstep = (!(xmax == xmin) ? (v.getMaxX() - 20.0) / (xmax - xmin) : 0); // Schrittweite bestimmen Pixel pro
																						// x
		double ystep = (ymax != ymin ? (v.getMaxY() - 20.0) / (ymax - ymin) : 0);
		// System.out.println("xstep: " + xstep + " ystep: " + ystep);
		float posx = (float) ((x - 10) / xstep + xmin);
		float posy = (float) (((v.getMaxY() - y) - 10) / ystep + ymin);
		return new int[] { Math.round(posx), Math.round(posy) };
	}

	private int[] gitterpunktToCanvasPos(int x, int y) {
		int xmax = grenzen[2];
		int xmin = grenzen[0];
		int ymax = grenzen[3];
		int ymin = grenzen[1];
		// System.out.println("kleinster Punkt: (" + xmin + "," + ymin + ") - groesster
		// Punkt:" + xmax + "," + ymax + ")");
		// System.out.println("v.getMaxX: " + v.getMaxX() + " MaxY: " + v.getMaxY());
		double xstep = (!(xmax == xmin) ? (v.getMaxX() - 20.0) / (xmax - xmin) : 0); // Schrittweite bestimmen Pixel pro
																						// x
		double ystep = (ymax != ymin ? (v.getMaxY() - 20.0) / (ymax - ymin) : 0);
		// System.out.println("xstep: " + xstep + " ystep: " + ystep);
		int sx = Math.round((float) (10 + 1.0 * (x - xmin) * xstep));
		int sy = Math.round((float) (v.getMaxY() - (10. + 1.0 * (y - ymin) * ystep)));
		return new int[] { sx, sy };
	}

	public void neuerPunkt(int x, int y) {
		String name = "" + nextpunktname++;
		while (knotenpunkte.keySet().contains(name)) {
			name = "" + nextpunktname++;
		}
		String koord = "(" + x + "," + y + ")";
		knotenpunkte.put(name, new Punkt(koord, new String[] { name, koord }));
	}

	public void execute(int befehl, String[] args) {
		int x, y;
		int[] pt;
		switch (befehl) {
		case NeuerPunkt:
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			pt = canvasPosToGitterpunkt(x, y);
			this.neuerPunkt(pt[0], pt[1]);
			this.graphZeichnen();
			break;
		case CanvasClicked:
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			canvasClick(x, y);
			break;
		case NeueKante:
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			pt = canvasPosToGitterpunkt(x, y);
			kantenStart = knotenAnGitterPos(pt[0], pt[1]);
			if (kantenStart != null) {
				v.setStatusLine("Zielknoten der Kante anklicken");
				this.graphZeichnen();
			}

			// Wenn dort ein Punkt ist - merken - zweiten Punkt suchen
			break;
		case ZOOM:
			// die Grenzen nue Berechnen und neu zeichnen
			try {
				int prozent = Integer.parseInt(args[0]);
				zoomGraph(1.0 * prozent / 100);
			} catch (Exception e) {
				System.err.println("Falsches oder kein Argument bei Execute ZOOM_in" + args);
			}
			break;
		case LinienDickeAendern:
			try {
				String[] possibilities = new String[10];
				for (int i = 0; i < 10; i++)
					possibilities[i] = "" + (i + 1);
				String s = (String) JOptionPane.showInputDialog(v.getHauptfenster(), "Bitte Liniendicke wählen",
						"Liniendicke", JOptionPane.PLAIN_MESSAGE, null, possibilities, ""+liniendicke); // Anstelle von null könnte
																								// auch ein Icon
																								// stehen
				// If a string was returned, say so.
				if ((s != null) && (s.length() > 0)) {
					liniendicke = Integer.parseInt(s);
					graphZeichnen();
				}
			} catch (Exception e) {
				System.err.println("Fehler in Liniendicke ändern");
			}
			break;
		case ZOOM_Einstellen:
			String s = "";
			try {
				// Anstelle von null könnte auch ein Icon stehen
				s = (String) JOptionPane.showInputDialog(v.getHauptfenster(),
						"Fenster einstellen\n x_min,y_min,x_max,y_max: ", "Fenster einstellen",
						JOptionPane.PLAIN_MESSAGE, null, null, Arrays.toString(grenzen).replaceAll("[\\[\\]]", ""));
				if ((s != null) && (s.length() > 0)) {
					// System.out.println("Neue Grenzen: "+s);
					int[] neuegrenzen = new int[4];
					String[] s2 = s.replaceAll("[ a-zA-Z]", "").split(",");
					for (int i = 0; i < neuegrenzen.length; i++)
						neuegrenzen[i] = Integer.parseInt(s2[i]);
					grenzen = neuegrenzen;
					v.setStatusLine("Neue Ansicht: " + Arrays.toString(grenzen).replaceAll("[\\[\\]]", ""));
					graphZeichnen();
				}
			} catch (Exception e) {
				v.setStatusLine("Fehlerhaftes Format: " + s);
				// System.err.println("Fehler in Zoom_einstellen");
			}
			break;
		case GitterZeichnen:
			this.gitterZeichnen = !this.gitterZeichnen;
			graphZeichnen();
			break;
		case Status_aendern:
			if (args != null && args.length > 0)
				v.setStatusLine(args[0]);
			break;
		case KantenLoeschHotspots:
			kantenHotspotsErzeugen(KanteLoeschen, args);
			if (stringArrayEnthaelt(args, "multi") >=0) {
				v.setStatusLine("Wähle die zu löschenden Kanten aus! - Zum Beenden auf das rote Quadrat klicken");
			} else {
				v.setStatusLine("Wähle die zu löschende Kante aus!");
			}
			graphZeichnen();
			break;
		case KanteLoeschen:
			if (args != null && args.length > 1) {
				for (int i = kanten.size() - 1; i >= 0; i--) {
					String[] k = kanten.get(i);
					if (k[0].equals(args[0]) && k[1].equals(args[1]))
						kanten.remove(i);
				}
				// TODO - multi sollte an beliebiger Position stehen können - Methode Schreiben
				if (stringArrayEnthaelt(args, "multi")==-1)
					hotspots.clear();
				graphZeichnen();
			}
			break;
		case HotspotsLoeschen:
			if (hotspots.size() > 0) {
				hotspots.clear();
				v.setStatusLine("Fertig");
				graphZeichnen();
			}
			break;
		case VollstGraph:
			String s1 = stringErfragen("Gib die Anzahl der Knoten an", "vollständigen Graphen erzeugen", "10");
			if (graph.execute(Graph.VollstGraph, new String[] {s1})) {
				this.graphNeuLaden();
				graphZeichnen();
				v.setStatusLine("Vollständiger Graph mit "+s1+" Knoten");
			} else {
				v.setStatusLine("Vollständiger Graph - Argumentfehler: "+s1);
			}
			break;
		case BipartiterGraph:
			String s2 = stringErfragen("Gib die beiden Knotenanzahlen an", "bipartiten Graphen erzeugen", "3,3");
			if (graph.execute(Graph.BipartiterGraph, new String[] {s2})) {
				this.graphNeuLaden();
				graphZeichnen();
				v.setStatusLine("Bipartiter Graph mit jeweils "+s2+" Knoten");
			} else {
				v.setStatusLine("Bipartiter Graph - Argumentfehler: "+s2);
			}
		default:

		}
	}

	/**
	 * prüft ob ein Objekt (Kante oder Punkt) eine Farbe hat und gibt diese Zurück
	 * Farben werden als -f1, -f2 oder ein ähnlicher String im String[] gespeichert
	 * 
	 * @param Kante oder Punkt als Sring[]
	 * @return Farbe
	 */
	private Color hatFarbe(String[] objekt) {
		if (objekt == null || objekt.length < 3)
			return farbenliste[0]; // Standardfarbe
		for (int i = 2; i < objekt.length; i++) {
			if (objekt[i].startsWith("-f")) { // Möglicherweise eine Farbe
				try {
					int fnr = Integer.parseInt(objekt[i].substring(2));
					if (fnr < farbenliste.length)
						return farbenliste[fnr];

				} catch (Exception e) {
					// konnte nicht geparst werden
				}
				return farbenliste[0]; // Standardfarbe
			}
		}
		return farbenliste[0];
	}

	private void zoomGraph(double d) {
		int xmax = grenzen[2];
		int xmin = grenzen[0];
		int ymax = grenzen[3];
		int ymin = grenzen[1];
		grenzen[2] = (int) ((xmax + xmin) / 2 + d * (xmax - xmin) / 2);
		grenzen[0] = (int) ((xmax + xmin) / 2 - d * (xmax - xmin) / 2);
		grenzen[3] = (int) ((ymax + ymin) / 2 + d * (ymax - ymin) / 2);
		grenzen[1] = (int) ((ymax + ymin) / 2 - d * (ymax - ymin) / 2);
		if (d < 1.0) { // Zoom in - die Grenzen werden Enger
			grenzen[2] = Math.min(grenzen[2], xmax - 1);
			grenzen[0] = Math.max(grenzen[0], xmin + 1);
			grenzen[3] = Math.min(grenzen[3], ymax - 1);
			grenzen[1] = Math.max(grenzen[1], ymin + 1);
		} else if (d > 1.0) { // Zoom out - die Grenzen werden weiter
			grenzen[2] = Math.max(grenzen[2], xmax + 1);
			grenzen[0] = Math.min(grenzen[0], xmin - 1);
			grenzen[3] = Math.max(grenzen[3], ymax + 1);
			grenzen[1] = Math.min(grenzen[1], ymin - 1);
		}
		graphZeichnen();
	}

	/**
	 * Arbeitet alle Clicks in das Zeichenfenster ab
	 * 
	 * @param x
	 * @param y
	 */
	private void canvasClick(int x, int y) {
		System.out.println("In Methode Controller - canvasClick - x: " + x + " y:" + y);
		if (hotspots.size() > 0) {
			// Array erst kopieren, da hotspots evtl. während der Aktion gelöscht werden
			System.out.println("Hotspot - size > 0: " + hotspots.size());
			Hotspot[] aktiveHotspots = new Hotspot[hotspots.size()];
			for (int i = 0; i < aktiveHotspots.length; i++)
				aktiveHotspots[i] = hotspots.get(i);
			for (Hotspot h : aktiveHotspots)
				h.fireIfInside(x, y);
		}
		int[] pt = canvasPosToGitterpunkt(x, y);
		graphclicked(pt);
	}

	private void graphclicked(int[] pt) {
		if (kantenStart != null) { // Versuch eine Kante zu wählen
			String ziel = knotenAnGitterPos(pt[0], pt[1]);
			if (ziel == null) {
				v.setStatusLine("Kein Zielpunkt");
			} else {
				kanten.add(new String[] { kantenStart, ziel });
				setzeMarkierungenDoppelteKanten();
				v.setStatusLine("Kante hinzugefügt: " + kantenStart + "-" + ziel);
			}
			kantenStart = null;
			this.graphZeichnen();
		}
	}

	private void kantenHotspotsErzeugen(int command, String[] argsin) {
		for (String[] k : kanten) {
			Punkt start = knotenpunkte.get(k[0]);
			Punkt ziel = knotenpunkte.get(k[1]);
			String[] args = new String[2];
			if (argsin != null && argsin.length > 0) {
				args = new String[argsin.length + 2];
				for (int i = 0; i < argsin.length; i++)
					args[i + 2] = argsin[i];
			}
			args[0] = k[0];
			args[1] = k[1];

			int[] startp = gitterpunktToCanvasPos(start.getX(), start.getY());
			int[] zielp = gitterpunktToCanvasPos(ziel.getX(), ziel.getY());

			// System.out.println(""+(startp[0]+zielp[0])/2+", "+(startp[1]+zielp[1])/2+",
			// "+command+", "+Arrays.toString(args));
			hotspots.add(new Hotspot((startp[0] + zielp[0]) / 2, (startp[1] + zielp[1]) / 2, 5, 5, command, args));
		}
		if (argsin != null && argsin.length > 2 && argsin[2].equals("multi")) {
			// Hotspot zum entfernen aller Hotspots erzeugen
			hotspots.add(new Hotspot(5, 5, 15, 15, HotspotsLoeschen, null, Color.RED));
		}
	}

	// *******************************+ Hilfsmethoden
	// ************************************
	/**
	 * prüft ob ein String in einem Array vorhanden ist, liefert die Position oder -1 wenn nicht vorhanden
	 * @param array das zu durchsuchende String[]
	 * @param suche den zu suchenden String
	 * @return die Position des Strings im Array oder -1
	 */
	private int stringArrayEnthaelt(String[] array, String suche) {
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i].equals(suche))
					return i;
			}
		}
		return -1;
	}

	private String stringArrayElement(String[] array, String startsWith) {
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i].startsWith(startsWith))
					return array[i];
			}
		}
		return ""; // vielleicht besser null?
	}

	private int stringArrayElementPos(String[] array, String startsWith) {
		if (array != null || startsWith != null || startsWith.length()>0) {
			for (int i = 0; i < array.length; i++) {
				if (array[i].startsWith(startsWith))
					return i;
			}
		}
		return -1;
	}
	
	private String stringErfragen(String frage, String titel, String vorgabe) {
		return (String) JOptionPane.showInputDialog(v.getHauptfenster(),
				frage, titel,
				JOptionPane.PLAIN_MESSAGE, null, null, vorgabe);
	}
	
	/**
	 * alle Kanten werden durchlaufen und auf identische Kanten überprüft
	 * in die Argumente wird die Anzahl mit -#(anzahl) geschrieben
	 */
	private void setzeMarkierungenDoppelteKanten() {
		System.out.println("In setzeMarkierungenDoppelteKanten");
		for (int i=0; i<kanten.size(); i++) { //Alle Kanten durchlaufen
			int j=i-1;
			while (j>=0 && !istGleicheKante(kanten.get(i), kanten.get(j))) {
				j--;				
			}
			if (j<0) { //Dies ist die erste Kante - Markierung -#0 setzen
				setKantenAnzahlAuf(i, 0);
			} else { //Selbe Kante wie j
				setKantenAnzahlAuf(i, getKantenNummer(j)+1);		
				System.out.println("Doppelte Kante gefunden :-)");
			}
		}
	}
		
	/**
	 * In die Kante mit der Kantennummer wird ein Argument mit der Kantennummer
	 * geschrieben (Form -#(anzahl))
	 * @param kantennr Nummer der Kante
	 * @param nummer dieser identischen Kante 0- erste, 1-zweite usw.
	 */
	private void setKantenAnzahlAuf(int kantennr, int nummer) {
		int pos = stringArrayElementPos(kanten.get(kantennr), "-#");
		if (pos>=0) {
			kanten.get(kantennr)[pos]="-#"+nummer;
		} else { //anhängen
			String[] neu = new String[kanten.get(kantennr).length+1];
			for (int i1=0; i1<neu.length-1;i1++) neu[i1]=kanten.get(kantennr)[i1];
			neu[neu.length-1]="-#"+nummer;
			kanten.set(kantennr, neu);
		}
	}
	
	/**
	 * liefert zur Kanten mit der kantennr aus der Liste, welche Nummer das ist (bei doppelten Kanten)
	 * 0 - erste (vielleicht einzige) Kante, 1-zweite usw.
	 * @param kantennr Nummer der Kante in der Liste
	 * @return nummer der Vielfachen
	 */
	private int getKantenNummer(int kantennr) {
		return getZahlAusSringArray(kanten.get(kantennr), "-#");
	}
	
	private int getZahlAusSringArray(String[] array, String prefix) {
		int pos = stringArrayElementPos(array, prefix);
		try {
			if (pos>=0) {
				return Integer.parseInt(array[pos].substring(prefix.length()));
			}
		} catch (Exception e) {
			System.err.println("Umwandlung in Zahl nicht möglich!"+e.getStackTrace());
		}
		return -1;
	}

	private boolean istGleicheKante(String[] k1, String[] k2) {
		if (k1==null) return k2==null;
		if (k2==null) return false;
		// beide von null verschieden
		if (k1.length<2 || k2.length<2) return false; // eigentlich gar keine Kanten
		return ((k1[0].equals(k2[0]) && k1[1].equals(k2[1])) || (k1[0].equals(k2[1])&&k1[1].equals(k2[0])));		
	}
	
	private int[] orthogonalerVektor(int x, int y) {
		if (x==0 && y==0) return new int[] {0,-1};
		int xout = -y;
		int yout = x;
		if (yout<0) return new int[] {xout,yout};
		else if (yout > 0) return new int[] {-xout,-yout};
		else if (xout < 0) return new int[] {-xout,-yout};
		else return new int[] {xout,yout};
	}
	
	private double norm(int[] vektor) {
		if (vektor == null || vektor.length==0) return -1.0;
		int squaresum = 0;
		for (int i=0; i<vektor.length; i++) squaresum+=vektor[i]*vektor[i];
		return Math.sqrt(1.0*squaresum);
	}
	
	private int[] vecAdd(int[] a, int[] b) {
		//TODO check input values;
		return new int[] {a[0]+b[0],a[1]+b[1]};
	}
	
	private void bogenZeichnen(Graphics g, int[] start, int[] ziel, int abweichung) {
		int anzp = 10; //Anzahl der zu zeichnenden Punkte
		int[] diff = new int[] {ziel[0]-start[0],ziel[1]-start[1]};
		int[] orth = orthogonalerVektor(diff[0], diff[1]);
		double norm = norm(orth);
		int[][] punkte = new int[11][2];
		double b = norm(diff)/2;
		
		for (int i=0; i<=anzp; i++) {
			double x = 2.0*b*i/anzp;
			//Parabel y=-a/b^2 * (x-b)² +a
			double y = -1.0*abweichung*(x-b)*(x-b)/(b*b)+1.0*abweichung;
			int[] punkt = new int[2];
			punkt[0]=Math.round((float)(start[0]+x*diff[0]/(2*b)+y*orth[0]/norm));
			punkt[1]=Math.round((float)(start[1]+x*diff[1]/(2*b)+y*orth[1]/norm));
			punkte[i]=punkt;
		}
		for (int i=0; i<anzp; i++) g.drawLine(punkte[i][0],punkte[i][1], punkte[i+1][0],punkte[i+1][1]);		
	}
	
	
}
