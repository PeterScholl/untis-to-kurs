package graphen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
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
	public static final int KantenFaerbeHotspots = 14;
	public static final int KanteFaerben = 15;

	public static final int Graph_einlesen = 16;
	public static final int Graph_speichern = 17;
	public static final int DBClean = 18;

	public static final int KnotenLoeschen = 19;


	private AbstrGraph graph;
	private HashMap<String, Punkt> knotenpunkte = new HashMap<String, Punkt>();
	private ArrayList<String[]> kanten = new ArrayList<String[]>();
	private ArrayList<Hotspot> hotspots = new ArrayList<Hotspot>(); // bereiche bei denen Clicks besondere Aktionen
																	// auslösen sollen
	private int[] grenzen = new int[4];
	// private boolean renewgrenzen = true;
	private String grabbed = null;
	private String kantenStart = null;
	private View v = null;
	private int nextpunktname = 0;
	private int liniendicke = 3;
	private boolean gitterZeichnen = true; // soll ein Karogitter gezeichnet werden
	// ImageValues
	private int imagewidth, imageheight; // Bildhöhe und Breite
	private double xstep, ystep; // Bildschrittweite pro Gitterpunkt
	private boolean debug=false;

	private class Punkt {
		private int x, y;
		private String[] args;

		public Punkt(String punkt, String[] args) {
			// debugln("Neuer Punkt: " + punkt);
			String[] koords = punkt.replaceAll("[() ]", "").split(",");
			if (koords.length != 2)
				throw new IllegalArgumentException("Could not parse String " + punkt + " to point!");
			// debug("Koords:"+koords[0]+","+koords[1]);
			this.x = Integer.parseInt(koords[0]);
			this.y = Integer.parseInt(koords[1]);
			this.args = args;
		}

		public int getX() {
			return x;
		}

		@SuppressWarnings("unused")
		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		@SuppressWarnings("unused")
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
		
		public String[] toStringArray() {
			return HilfString.appendArray(new String[] {"("+x+","+y+")"}, args);
		}
		
		public String toString() {
			return Arrays.toString(this.toStringArray());
		}
	}

	private class Hotspot {
		public int x, y, w, h, command;
		public String[] args;
		public Color color = Color.LIGHT_GRAY;
		public boolean disableIfFired = true;

		public Hotspot(int x, int y, int w, int h, int command, String[] args, boolean disableIfFired) {
			this(x, y, w, h, command, args, Color.LIGHT_GRAY, disableIfFired);
		}

		public Hotspot(int x, int y, int w, int h, int command, String[] args, Color color, boolean disableIfFired) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.command = command;
			this.args = args;
			this.color = color;
			this.disableIfFired = disableIfFired;
		}

		public boolean isInside(int x, int y) {
			return (x - this.x >= 0 && x - this.x <= w && y - this.y >= 0 && y - this.y <= h);
		}

		public boolean fireIfInside(int x, int y) {
			System.out.print("Hotspot abfeuern?");
			if (isInside(x, y)) {
				debug("ja! :" + command + ", " + Arrays.toString(args));
				// deaktivieren
				if (disableIfFired)
					this.x = -100;
				execute(command, args);
				return true;
			}
			return false;
		}
	}

	public Controller(AbstrGraph graph) {
		this.graph = graph;
		v = new View(this, "Facharbeit Graphen - v.0");
		this.graphNeuLaden();
		// hotspots.add(new Hotspot(20, 20, 30, 30, HotspotsLoeschen, new String[] {
		// "Hotspot !" })); //Testhotspot
	}

	private void graphNeuLaden() {
		ArrayList<String[]> knotenliste = this.graph.getKnotenPunkte();
		this.knotenpunkte = new HashMap<String, Punkt>();
		for (String[] knoten : knotenliste) {
			System.out.print("Knoten zum Einfügen: " + Arrays.toString(knoten));
			Punkt punkt = new Punkt(knoten[1], HilfString.subArray(knoten,2));
			debug(" gibt folgenden Punkt(" + knoten[0] + ") - " + punkt);
			this.knotenpunkte.put(knoten[0], punkt);
		}
		kanten = graph.getKnotenVerbindungen();
		debug("Grenzkoordinaten des Gitters neu bestimmen!");
		grenzen = this.gibGrenzKoordinatenDerKnoten();// Jeweils maximale Koordinaten bestimmen (Ecken des Bildes)
		// TODO: muss noch mehr zurückgesetzt werden?
		this.execute(HotspotsLoeschen, null);
		imagewidth = -1; // Neuberechnung erzwingen
		graphZeichnen();
	}

	public int[] gibGrenzKoordinatenDerKnoten() {
		int minx = Integer.MAX_VALUE;
		int miny = Integer.MAX_VALUE;
		int maxx = Integer.MIN_VALUE;
		int maxy = Integer.MIN_VALUE;
		if (knotenpunkte.size() > 0) {
			for (Punkt k : knotenpunkte.values()) {
				minx = (k.getX() < minx ? k.getX() : minx);
				miny = (k.getY() < miny ? k.getY() : miny);
				maxx = (k.getX() > maxx ? k.getX() : maxx);
				maxy = (k.getY() > maxy ? k.getY() : maxy);
			}
		} else {
			minx = 0;
			miny = 0;
			maxx = 10;
			maxy = 10;
		}
		return new int[] { minx - 1, miny - 1, maxx + 1, maxy + 1 };
	}

	private void updateImgValues() {
		int xmax = grenzen[2];
		int xmin = grenzen[0];
		int ymax = grenzen[3];
		int ymin = grenzen[1];
		xstep = (!(xmax == xmin) ? (imagewidth - 20.0) / (xmax - xmin) : 0); // Schrittweite bestimmen Pixel pro x
		ystep = (ymax != ymin ? (imageheight - 20.0) / (ymax - ymin) : 0);
		debug("xstep: " + xstep + " ystep: " + ystep + " Grenzen: " + Arrays.toString(grenzen));
	}

	public void graphZeichnen() {
		// hier soll der Graph auf View gezeichnet werden
		if (v == null) {
			System.err.println("Kein Canvas zum Zeichnen!");
			return;
		}
		BufferedImage img = v.getBufferedImage();
		Graphics g = img.getGraphics();
		if (img.getWidth() != imagewidth || img.getHeight() != imageheight) {
			imagewidth = img.getWidth();
			imageheight = img.getHeight();
			updateImgValues();
		}
		g.setColor(Color.white);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.setColor(Color.black);
		// int xmax = grenzen[2];
		int xmin = grenzen[0];
		// int ymax = grenzen[3];
		int ymin = grenzen[1];
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
		// debug("xstep: " + xstep + " ystep: " + ystep + " Width: " +
		// img.getWidth());
		for (String[] kante : kanten) { // alle Kanten Zeichnen
			// debug("Kante wird gezeichnet: "+Arrays.toString(kante));
			int abweichung = 10 * getZahlAusSringArray(kante, "-#");

			g.setColor(hatFarbe(kante));
			Punkt p1 = knotenpunkte.get(kante[0]);
			Punkt p2 = knotenpunkte.get(kante[1]);
			debug("Kante: " + Arrays.toString(kante) + " Punkt1: " + p1 + " Punkt2: " + p2);
			// debug("Zeichne Linie von "+p1.getX()+","+p1.getY()+" nach
			// "+p2.getX()+","+p2.getY());
			// Startpunkt der Linie
			int sx = Math.round((float) (10 + 1.0 * (p1.getX() - xmin) * xstep));
			int sy = Math.round((float) (img.getHeight() - (10. + 1.0 * (p1.getY() - ymin) * ystep)));
			// Endpunkt der Linie
			int ex = Math.round((float) (10. + 1.0 * (p2.getX() - xmin) * xstep));
			int ey = Math.round((float) (img.getHeight() - (10. + 1.0 * (p2.getY() - ymin) * ystep)));
			if (abweichung > 0) {
				this.bogenZeichnen(g, new int[] { sx, sy }, new int[] { ex, ey }, abweichung);
				// g.drawLine(sx, sy, ex, ey);
			} else { // dickere Linien zeichnen
				Graphics2D g2 = (Graphics2D) g;
				RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHints(rh);
				g2.setStroke(new BasicStroke(liniendicke));
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
		// debug("Position: " + Arrays.toString(pos) + " grabbed: " +
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
			// debug("Vergleich: " + akt.getX() + "-" + akt.getY() + " - " +
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
		debug("canvasPosToGitterpunkt: " + x + ", " + y);
		int xmin = grenzen[0];
		int ymin = grenzen[1];
		float posx = (float) ((x - 10) / xstep + xmin);
		float posy = (float) (((imageheight - y) - 10) / ystep + ymin);
		debug("Ergebnisposition" + Math.round(posx) + ", " + Math.round(posy));
		return new int[] { Math.round(posx), Math.round(posy) };
	}

	private int[] gitterpunktToCanvasPos(int x, int y) {
		int xmin = grenzen[0];
		int ymin = grenzen[1];
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
		case CanvasClicked: // z.B. beim neu Anlegen einer Kante
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
						"Liniendicke", JOptionPane.PLAIN_MESSAGE, null, possibilities, "" + liniendicke); // Anstelle
																											// von null
																											// könnte
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
					// debug("Neue Grenzen: "+s);
					int[] neuegrenzen = new int[4];
					String[] s2 = s.replaceAll("[ a-zA-Z]", "").split(",");
					for (int i = 0; i < neuegrenzen.length; i++)
						neuegrenzen[i] = Integer.parseInt(s2[i]);
					grenzen = neuegrenzen;
					updateImgValues();
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
			v.setEnableAlleMenueAktionen(false);
			if (HilfString.stringArrayEnthaelt(args, "multi") >= 0) {
				v.setStatusLine("Wähle die zu löschenden Kanten aus! - Zum Beenden auf das rote Quadrat klicken");
			} else {
				v.setStatusLine("Wähle die zu löschende Kante aus! - Zum Abbrechen auf das rote Quadrat klicken");
			}
			graphZeichnen();
			break;
		case KanteLoeschen:
			if (args != null && args.length > 1) {
				for (int i = kanten.size() - 1; i >= 0; i--) {
					String[] k = kanten.get(i);
					if (k[0].equals(args[0]) && k[1].equals(args[1])) {
						graph.execute(AbstrGraph.KanteLoeschen, kanten.get(i));
						kanten.remove(i);
					}
				}
				setzeMarkierungenDoppelteKanten();
				if (HilfString.stringArrayEnthaelt(args, "multi") == -1)
					this.execute(HotspotsLoeschen, null);
				graphZeichnen();
			}
			break;
		case HotspotsLoeschen:
			if (hotspots.size() > 0) {
				hotspots.clear();
				v.setEnableAlleMenueAktionen(true);
				v.setStatusLine("Fertig");
				graphZeichnen();
			}
			break;
		case VollstGraph:
			String s1 = stringErfragen("Gib die Anzahl der Knoten an", "vollständigen Graphen erzeugen", "10");
			if (graph.execute(Graph.VollstGraph, new String[] { s1 })) {
				this.graphNeuLaden();
				graphZeichnen();
				v.setStatusLine("Vollständiger Graph mit " + s1 + " Knoten");
			} else {
				v.setStatusLine("Vollständiger Graph - Argumentfehler: " + s1);
			}
			break;
		case BipartiterGraph:
			String s2 = stringErfragen("Gib die beiden Knotenanzahlen an", "bipartiten Graphen erzeugen", "3,3");
			if (graph.execute(Graph.BipartiterGraph, new String[] { s2 })) {
				this.graphNeuLaden();
				graphZeichnen();
				v.setStatusLine("Bipartiter Graph mit jeweils " + s2 + " Knoten");
			} else {
				v.setStatusLine("Bipartiter Graph - Argumentfehler: " + s2);
			}
			break;
		case KantenFaerbeHotspots:
			kantenHotspotsErzeugen(KanteFaerben, HilfString.appendString(args, "nodisableIfFired"));
			v.setEnableAlleMenueAktionen(false);
			v.setStatusLine("Wähle die zu färbende Kante aus! - Zum Beenden auf das rote Quadrat klicken");
			graphZeichnen();
			break;
		case KanteFaerben:
			if (args != null && args.length > 1) {
				for (int i = kanten.size() - 1; i >= 0; i--) {
					String[] k = kanten.get(i);
					if (k[0].equals(args[0]) && k[1].equals(args[1])) {
						Color newColor = JColorChooser.showDialog(v.getHauptfenster(), "Choose a color", hatFarbe(k));
						if (newColor != null) {
							debug(Integer.toHexString(newColor.getRGB()));
							setKantenFarbeAuf(i, Integer.toHexString(newColor.getRGB()));

						}
						break; // Nur eine Kante
					}
				}
				graphZeichnen();
			}
			break;
		case Graph_einlesen:
			File dateilesen = chooseFile(true);
			if (dateilesen != null) {
				graph.execute(AbstrGraph.LiesDatei, new String[] { dateilesen.getAbsolutePath() });
				this.graphNeuLaden();
			}
			break;
		case Graph_speichern:
			updateGraph();
			File dateispeichern = chooseFile(false);
			if (dateispeichern != null) {
				graph.execute(AbstrGraph.SchreibeDatei, new String[] { dateispeichern.getAbsolutePath() });
			}
			break;
		case DBClean:
			for (String name: knotenpunkte.keySet()) {
				Punkt p = knotenpunkte.get(name);
				String[] oldargs = p.getArgs();
				p.args = HilfString.duplikateLoeschen(oldargs);
				//Überflüssige Koordinaten entfernen
				int pos = HilfString.stringArrayElementPos(HilfString.subArray(p.args, 1), "(");
				while (pos>-1) {
					System.err.println("Doppelte Koordinaten in Punkt "+name+" gefunden: "+p.args[pos+1]);
					p.args=HilfString.removePosFromArray(p.args, pos+1);
					pos = HilfString.stringArrayElementPos(HilfString.subArray(p.args, 1), "(");
				}
				//Einträge in Args, die dem Namen entsprechen entfernen
				pos = HilfString.stringArrayElementPos(p.args, name);
				while (pos>-1) {
					System.err.println("Eintrag mit Namen des Knotens "+name+" in args gefunden!");
					p.args=HilfString.removePosFromArray(p.args, pos);
					pos = HilfString.stringArrayElementPos(p.args, name);
				}
				if (p.args.length!= oldargs.length) {
					updateGraphKnoten(name);
					System.err.println("Duplikate bei Punkt "+name+" gefunden:"+Arrays.toString(oldargs)+"->"+Arrays.toString(p.args));
				}
			}
			for (int i=0; i<kanten.size(); i++) {
				String[] oldkante = kanten.get(i);
				String[] newkante = HilfString.duplikateLoeschen(oldkante);
				if (oldkante.length!= newkante.length) {
					kanten.set(i, newkante);
					updateGraphKante(newkante);
					System.err.println("Duplikate bei Kante gefunden:"+Arrays.toString(oldkante)+"->"+Arrays.toString(newkante));
				}
			}
			break;
		case KnotenLoeschen:
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			pt = canvasPosToGitterpunkt(x, y);
			String gewKnoten = knotenAnGitterPos(pt[0], pt[1]);
			if (gewKnoten != null) {
				v.setStatusLine("Knoten "+gewKnoten+" geloescht!");
				graph.execute(AbstrGraph.LoescheKnoten, knotenInfosZu(gewKnoten));
				int [] grenzensichern = grenzen.clone();
				graphNeuLaden();
				grenzen = grenzensichern;
				updateImgValues();
				this.graphZeichnen();
			}
			break;

		default:

		}
	}

	/**
	 * aktualisiert den Graphen mit den Informationen, die im View erstellt worden
	 * sind
	 */
	private void updateGraph() {
		for (String[] k : kanten) {
			updateGraphKante(k);
		}
		for (String k : knotenpunkte.keySet()) {
			updateGraphKnoten(k);
		}
	}

	private void updateGraphKnoten(String knotenname) {
		String[] knoteninfos = knotenInfosZu(knotenname);
		if (knoteninfos != null) {
		debug("updateGraphKnoten: "+Arrays.toString(knoteninfos));
		graph.execute(AbstrGraph.UpdateKnoten, knoteninfos);
		}
	}
	
	private String[] knotenInfosZu(String knotenname) {
		Punkt p = knotenpunkte.get(knotenname);
		if (p == null)
			return null;
		int offset = 2;
		String[] knoteninfos = new String[knotenpunkte.get(knotenname).args.length + offset];
		knoteninfos[0] = knotenname;
		knoteninfos[1] = "(" + p.getX() + "," + p.getY() + ")";
		for (int i = offset; i < knoteninfos.length; i++)
			knoteninfos[i] = p.args[i - offset];
		return knoteninfos;		
	}
	
	/**
	 * Der Graph wird mit neuen Kanteninformationen gefüttert
	 * 
	 * @param k die Informationen zur Kante
	 */
	private void updateGraphKante(String[] k) {
		graph.execute(AbstrGraph.UpdateKante, k);
	}

	/**
	 * prüft ob ein Objekt (Kante oder Punkt) eine Farbe hat und gibt diese Zurück
	 * Farben werden als -f1, -f2 oder ein ähnlicher String im String[] gespeichert
	 * 
	 * @param Kante oder Punkt als Sring[]
	 * @return Farbe
	 */
	private Color hatFarbe(String[] objekt) {
		//debug("Controller - hat Farbe - objekt: " + Arrays.toString(objekt));
		if (objekt == null || objekt.length < 3)
			return farbenliste[0]; // Standardfarbe
		for (int i = 2; i < objekt.length; i++) {
			if (objekt[i].startsWith("-f")) { // Möglicherweise eine Farbe
				//debug("Farbe möglich");
				try {
					if (objekt[i].length() >= 8) { // HexFarbe
						//debug("Controller - hatFarbe - Hex-Farbe: " + objekt[i] + " Integer-Value: "+ Long.valueOf(objekt[i].substring(2), 16));
						return new Color(Long.valueOf(objekt[i].substring(2), 16).intValue(), true);
					} else {
						//debug("Controller - hatFarbe - keine Hex-Farbe");
						int fnr = Integer.parseInt(objekt[i].substring(2));
						if (fnr < farbenliste.length)
							return farbenliste[fnr];
					}
				} catch (Exception e) {
					// konnte nicht geparst werden
					System.err.println("Controller - hat Farbe:" + e.getMessage());
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
		debug("In Methode Controller - canvasClick - x: " + x + " y:" + y);
		if (hotspots.size() > 0) {
			for (int i = 0; i < hotspots.size(); i++) {
				if (hotspots.get(i).fireIfInside(x, y)) { // nur einen Hotspot abfeuern
					break;
				}
			}
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
				String[] kantenbeschr = new String[] { kantenStart, ziel };
				kanten.add(kantenbeschr);
				graph.execute(AbstrGraph.NeueKante, kantenbeschr);
				setzeMarkierungenDoppelteKanten();
				v.setStatusLine("Kante hinzugefügt: " + kantenStart + "-" + ziel);
			}
			kantenStart = null;
			this.graphZeichnen();
		}
	}

	private void kantenHotspotsErzeugen(int command, String[] argsin) {
		int noDisableAfterFire = HilfString.stringArrayEnthaelt(argsin, "nodisableIfFired");
		int offset = 0;
		if (noDisableAfterFire >= 0) { // Hotspot soll aktiv bleiben nach fire
			argsin[noDisableAfterFire] = argsin[argsin.length - 1];
			offset++; // Ein Argument weniger weitergeben
		}
		for (String[] k : kanten) {
			Punkt start = knotenpunkte.get(k[0]);
			Punkt ziel = knotenpunkte.get(k[1]);
			String[] args = new String[2];
			if (argsin != null && argsin.length > 0) {
				args = new String[argsin.length + 2 - offset];
				for (int i = 2; i < args.length; i++)
					args[i] = argsin[i - 2];
			}
			args[0] = k[0];
			args[1] = k[1];

			int[] startp = gitterpunktToCanvasPos(start.getX(), start.getY());
			int[] zielp = gitterpunktToCanvasPos(ziel.getX(), ziel.getY());

			// debug(""+(startp[0]+zielp[0])/2+", "+(startp[1]+zielp[1])/2+",
			// "+command+", "+Arrays.toString(args));
			hotspots.add(new Hotspot((startp[0] + zielp[0]) / 2, (startp[1] + zielp[1]) / 2, 5, 5, command, args,
					(noDisableAfterFire == -1)));
		}
		// if (argsin != null && argsin.length > 2 && argsin[2].equals("multi"))
		// Hotspot zum entfernen aller Hotspots erzeugen
		hotspots.add(new Hotspot(5, 5, 15, 15, HotspotsLoeschen, null, Color.RED, true));
	}

	// *******************************+ Hilfsmethoden
	// ************************************
	private String stringErfragen(String frage, String titel, String vorgabe) {
		return (String) JOptionPane.showInputDialog(v.getHauptfenster(), frage, titel, JOptionPane.PLAIN_MESSAGE, null,
				null, vorgabe);
	}

	/**
	 * alle Kanten werden durchlaufen und auf identische Kanten überprüft in die
	 * Argumente wird die Anzahl mit -#(anzahl) geschrieben
	 */
	private void setzeMarkierungenDoppelteKanten() {
		debug("In setzeMarkierungenDoppelteKanten");
		for (int i = 0; i < kanten.size(); i++) { // Alle Kanten durchlaufen
			int j = i - 1;
			while (j >= 0 && !istGleicheKante(kanten.get(i), kanten.get(j))) {
				j--;
			}
			if (j < 0) { // Dies ist die erste Kante - Markierung -#0 setzen
				setKantenAnzahlAuf(i, 0);
			} else { // Selbe Kante wie j
				setKantenAnzahlAuf(i, getKantenNummer(j) + 1);
				debug("Doppelte Kante gefunden :-)");
			}
		}
	}

	/**
	 * In die Kante mit der Kantennummer wird ein Argument mit der Kantennummer
	 * geschrieben (Form -#(anzahl))
	 * 
	 * @param kantennr Nummer der Kante
	 * @param nummer   dieser identischen Kante 0- erste, 1-zweite usw.
	 */
	private void setKantenAnzahlAuf(int kantennr, int nummer) {
		int pos = HilfString.stringArrayElementPos(kanten.get(kantennr), "-#");
		if (pos >= 0) {
			kanten.get(kantennr)[pos] = "-#" + nummer;
		} else { // anhängen
			String[] neu = new String[kanten.get(kantennr).length + 1];
			for (int i1 = 0; i1 < neu.length - 1; i1++)
				neu[i1] = kanten.get(kantennr)[i1];
			neu[neu.length - 1] = "-#" + nummer;
			kanten.set(kantennr, neu);
		}
	}

	/**
	 * Für die Kante mit der Kantennummer wird die Farbe gesetzt Argument -f
	 * 
	 * @param kantennr Nummer der Kante
	 * @param farbe    String, der die Farbe enthält (Zahl oder Hex)
	 */
	private void setKantenFarbeAuf(int kantennr, String farbe) {
		int pos = HilfString.stringArrayElementPos(kanten.get(kantennr), "-f");
		if (pos >= 0) {
			kanten.get(kantennr)[pos] = "-f" + farbe;
		} else { // anhängen
			kanten.set(kantennr, HilfString.appendString(kanten.get(kantennr), "-f" + farbe));
		}
	}

	/**
	 * liefert zur Kanten mit der kantennr aus der Liste, welche Nummer das ist (bei
	 * doppelten Kanten) 0 - erste (vielleicht einzige) Kante, 1-zweite usw.
	 * 
	 * @param kantennr Nummer der Kante in der Liste
	 * @return nummer der Vielfachen
	 */
	private int getKantenNummer(int kantennr) {
		return getZahlAusSringArray(kanten.get(kantennr), "-#");
	}

	private int getZahlAusSringArray(String[] array, String prefix) {
		int pos = HilfString.stringArrayElementPos(array, prefix);
		try {
			if (pos >= 0) {
				return Integer.parseInt(array[pos].substring(prefix.length()));
			}
		} catch (Exception e) {
			System.err.println("Umwandlung in Zahl nicht möglich!" + e.getStackTrace());
		}
		return -1;
	}

	private boolean istGleicheKante(String[] k1, String[] k2) {
		if (k1 == null)
			return k2 == null;
		if (k2 == null)
			return false;
		// beide von null verschieden
		if (k1.length < 2 || k2.length < 2)
			return false; // eigentlich gar keine Kanten
		return ((k1[0].equals(k2[0]) && k1[1].equals(k2[1])) || (k1[0].equals(k2[1]) && k1[1].equals(k2[0])));
	}

	private int[] orthogonalerVektor(int x, int y) {
		if (x == 0 && y == 0)
			return new int[] { 0, -1 };
		int xout = -y;
		int yout = x;
		if (yout < 0)
			return new int[] { xout, yout };
		else if (yout > 0)
			return new int[] { -xout, -yout };
		else if (xout < 0)
			return new int[] { -xout, -yout };
		else
			return new int[] { xout, yout };
	}

	private double norm(int[] vektor) {
		if (vektor == null || vektor.length == 0)
			return -1.0;
		int squaresum = 0;
		for (int i = 0; i < vektor.length; i++)
			squaresum += vektor[i] * vektor[i];
		return Math.sqrt(1.0 * squaresum);
	}

	private void bogenZeichnen(Graphics g, int[] start, int[] ziel, int abweichung) {
		int anzp = 10; // Anzahl der zu zeichnenden Punkte
		int[] diff = new int[] { ziel[0] - start[0], ziel[1] - start[1] };
		int[] orth = orthogonalerVektor(diff[0], diff[1]);
		double norm = norm(orth);
		int[][] punkte = new int[11][2];
		double b = norm(diff) / 2;

		for (int i = 0; i <= anzp; i++) {
			double x = 2.0 * b * i / anzp;
			// Parabel y=-a/b^2 * (x-b)² +a
			double y = -1.0 * abweichung * (x - b) * (x - b) / (b * b) + 1.0 * abweichung;
			int[] punkt = new int[2];
			punkt[0] = Math.round((float) (start[0] + x * diff[0] / (2 * b) + y * orth[0] / norm));
			punkt[1] = Math.round((float) (start[1] + x * diff[1] / (2 * b) + y * orth[1] / norm));
			punkte[i] = punkt;
		}
		for (int i = 0; i < anzp; i++) {
			// g.drawLine(punkte[i][0],punkte[i][1], punkte[i+1][0],punkte[i+1][1]);
			Graphics2D g2 = (Graphics2D) g;
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHints(rh);
			g2.setStroke(new BasicStroke(liniendicke));
			g2.draw(new Line2D.Float(punkte[i][0], punkte[i][1], punkte[i + 1][0], punkte[i + 1][1]));

		}
	}

	// ******************** Dateiaktionen ***********************
	// finden alle in Graph statt
	// nur die Dateiauswahl bleibt hier

	public File chooseFile(boolean read) {
		// debug("Working Directory: " + System.getProperty("user.dir"));
		// debug("\n| Datei einlesen |\n");

		// JFileChooser-Objekt erstellen
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		// Dialog zum Oeffnen von Dateien anzeigen
		int rueckgabeWert = JFileChooser.CANCEL_OPTION;
		if (read) {
			rueckgabeWert = chooser.showOpenDialog(v.getHauptfenster());
		} else {
			rueckgabeWert = chooser.showSaveDialog(v.getHauptfenster());
		}
		/* Abfrage, ob auf "Öffnen" geklickt wurde */
		if (rueckgabeWert == JFileChooser.APPROVE_OPTION) {
			// Ausgabe der ausgewaehlten Datei
			// debug("Die zu öffnende Datei ist: " +
			// chooser.getSelectedFile().getName());
		} else {
			System.out.println("Auswahl beendet - keine Datei gewählt");
			return null;
		}
		if (!read && chooser.getSelectedFile().exists()) {
		    int response = JOptionPane.showConfirmDialog(null, //
		            "Do you want to replace the existing file?", //
		            "Confirm", JOptionPane.YES_NO_OPTION, //
		            JOptionPane.QUESTION_MESSAGE);
		    if (response != JOptionPane.YES_OPTION) {
		        return null;
		    } 
		}
		return chooser.getSelectedFile();
	}

	private void debug(String text) {
		if (debug) System.out.println("C:"+text);
	}
	private void debug(String text, boolean aktuell) {
		if (debug) System.out.println("C:"+text);
	}
	private void debuge(String text) {
		if (debug) System.err.println("C"+text);
	}
}