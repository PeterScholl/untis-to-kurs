package graphen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JColorChooser;
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
	public static final int SetEnableViewActions = 20;
	public static final int AnsichtAnGraphAnpassen = 21;
	public static final int GraphDatenNeuLaden = 22;
	public static final int InfoAusgeben = 23;

	public static final int BefehlAnmelden = 24;
	public static final int BefehlAnGraph = 25;

	public static final int LoescheKnotenArgument = 29;
	public static final int LoescheKantenArgument = 30;
	public static final int UpdateGraphDaten = 31; // aktualisiert die Daten des Graph-Objekts
	public static final int KantenArgumentUpdate = 26; // args = {start,ziel,neuesArgument}
	public static final int KnotenArgumentUpdate = 32; // args = {name,neuesArgument}
	public static final int KantenPosAbsToRel = 27; // Alle aboluten Kantenpositionen -P werden zu relativen Umgewandelt
	public static final int KnotenPosAbsToRel = 33; // Alle aboluten Knotenpositionen -P werden zu relativen Umgewandelt
	public static final int KantenDragHotspotsErzeugen = 28;
	public static final int KnotenDragHotspotsErzeugen = 34;
	public static final int StringErfragen = 35;

	private GraphInt graph;
	private HashMap<String, Punkt> knotenpunkte = new HashMap<String, Punkt>();
	private ArrayList<String[]> kanten = new ArrayList<String[]>();
	private ArrayList<Hotspot> hotspots = new ArrayList<Hotspot>(); // bereiche bei denen Clicks besondere Aktionen
																	// auslösen sollen
	private int[] grenzen = new int[4];
	// private boolean renewgrenzen = true;
	private String grabbedPkt = null;
	private Hotspot grabbedHS = null;
	private String marked = null;
	private String kantenStart = null;
	private View v = null;
	private int nextpunktname = 0;
	private int liniendicke = 3;
	private boolean gitterZeichnen = true; // soll ein Karogitter gezeichnet werden
	// ImageValues
	private int imagewidth, imageheight; // Bildhöhe und Breite
	private double xstep, ystep; // Bildschrittweite pro Gitterpunkt
	private boolean debug = !true;
	private String[] result; //stores result of Operations

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
			return HilfString.appendArray(new String[] { "(" + x + "," + y + ")" }, args);
		}

		public String toString() {
			return Arrays.toString(this.toStringArray());
		}
	}

	private class Hotspot {
		public int x, y, w, h, command, dragcommand;
		public String[] args, dragargs;
		public Color color = Color.LIGHT_GRAY;
		public boolean disableIfFired = true;
		public boolean isGrabbable;
		public Hotspot child = null;

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
				fire();
				return true;
			}
			return false;
		}

		public void fire() {
			execute(command, args);
			if (child != null)
				child.fire();
		}

		public void draggedTo(int x2, int y2) {
			this.x = x2;
			this.y = y2;
			if (dragcommand > 0 && dragargs != null) {
				execute(dragcommand, HilfString.appendString(dragargs, "-P" + x2 + "," + y2));
			}
		}
	}

	public Controller(GraphInt graph) {
		ContInt.controllerAnmelden(this);
		this.graph = graph;
		v = new View(this, "Facharbeit Graphen - v.0");
		graph.execute(GraphInt.BefehleAnmelden, null);
		this.graphNeuLaden();
		this.execute(AnsichtAnGraphAnpassen, null);
		// hotspots.add(new Hotspot(20, 20, 30, 30, HotspotsLoeschen, new String[] {
		// "Hotspot !" })); //Testhotspot
	}

	private void graphNeuLaden() {
		ArrayList<String[]> knotenliste = this.graph.getKnotenPunkte();
		if (knotenliste == null)
			return;
		this.knotenpunkte = new HashMap<String, Punkt>();
		for (String[] knoten : knotenliste) {
			debug("Knoten zum Einfügen: " + Arrays.toString(knoten));
			Punkt punkt = new Punkt(knoten[1], HilfString.subArray(knoten, 2));
			debug(" gibt folgenden Punkt(" + knoten[0] + ") - " + punkt);
			this.knotenpunkte.put(knoten[0], punkt);
		}
		kanten = graph.getKnotenVerbindungen();
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
		g.setFont(new Font("Dialog", Font.BOLD, 16));
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
		// Kanten zeichnen
		for (String[] kante : kanten) { // alle Kanten Zeichnen
			// debug("Kante wird gezeichnet: "+Arrays.toString(kante));
			int abweichung = 10 * getZahlAusSringArray(kante, "-#"); // für das Zeichnen eines Bogens

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
			// wenn Text vorhanden Zeichnen
			String text = HilfString.stringArrayElement(kante, "-T");
			if (text != null && text.length() > 2) {
				int absTpos = HilfString.stringArrayElementPos(kante, "-P");
				int relTpos = HilfString.stringArrayElementPos(kante, "-p");
				int[] finalpos = new int[] { (sx + ex) / 2, (sy + ey) / 2 };
				if (relTpos > -1) {
					int[] trans = HilfString.intKoordsAusString(kante[relTpos]);
					if (trans != null && trans.length == 2) {
						finalpos[0] += trans[0];
						finalpos[1] += trans[1];
					}
				}
				if (absTpos > -1) {
					int[] abskoord = HilfString.intKoordsAusString(kante[absTpos]);
					if (abskoord != null && abskoord.length == 2) {
						finalpos[0] = abskoord[0];
						finalpos[1] = abskoord[1];
					}
				}
				g.drawString(text.substring(2), finalpos[0], finalpos[1]);
			}
		}
		// alle Knoten zeichnen
		for (String pname : knotenpunkte.keySet()) {
			Punkt p = knotenpunkte.get(pname);
			if (pname.equals(grabbedPkt) || pname.equals(kantenStart)) {
				g.setColor(Color.red);
			} else {
				debug("Knoten hat Farbe: " + hatFarbe(p.getArgs()));
				g.setColor(hatFarbe(p.getArgs()));
			}
			int sx = Math.round((float) (10 + 1.0 * (p.getX() - xmin) * xstep));
			int sy = Math.round((float) (img.getHeight() - (10. + 1.0 * (p.getY() - ymin) * ystep)));
			g.fillOval(sx - radius, sy - radius, 2 * radius, 2 * radius);
			String text = HilfString.stringArrayElement(p.args, "-T");
			if (text != null && text.length() > 2) {
				int absTpos = HilfString.stringArrayElementPos(p.args, "-P");
				int relTpos = HilfString.stringArrayElementPos(p.args, "-p");
				int[] finalpos = new int[] { sx,sy};
				if (relTpos > -1) {
					int[] trans = HilfString.intKoordsAusString(p.args[relTpos]);
					if (trans != null && trans.length == 2) {
						finalpos[0] += trans[0];
						finalpos[1] += trans[1];
					}
				}
				if (absTpos > -1) {
					int[] abskoord = HilfString.intKoordsAusString(p.args[absTpos]);
					if (abskoord != null && abskoord.length == 2) {
						finalpos[0] = abskoord[0];
						finalpos[1] = abskoord[1];
					}
				}
				g.drawString(text.substring(2), finalpos[0]+5, finalpos[1]);
				//g.drawString(text.substring(2), sx + 5, sy);
			}

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
	 * Wird aufgerufen, wenn der Mouse-butten gedrückt wird Schreibt den Knoten an
	 * den Bildschirmkoordinaten (x,y) in die Variable grabbed, sonst wird null dort
	 * eingetragen
	 * 
	 * @param x - Bildschirmkoordiante x
	 * @param y - Bildschirmkoordinate y
	 */
	public void grabPos(int x, int y) {
		// TODO - war da ein Hotspot -> dann grabbedHotspot
		if (hotspots.size() > 0) {
			for (Hotspot h : hotspots) {
				if (h.isGrabbable && h.isInside(x, y)) {
					grabbedHS = h;
					break;
				}
			}
		}
		if (grabbedHS == null) { // Dort war kein Hotspot

			int[] pos = canvasPosToGitterpunkt(x, y);
			grabbedPkt = knotenAnGitterPos(pos[0], pos[1]);
			// debug("Position: " + Arrays.toString(pos) + " grabbed: " +
			// grabbed);
			if (grabbedPkt != null) {
				v.setStatusLine("Punkt: " + grabbedPkt);
				graphZeichnen();
			}
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
		if (grabbedHS != null) { // Hotspot bewegen
			grabbedHS.draggedTo(x, y);
			this.graphZeichnen();
		} else if (hotspots.size() == 0 && grabbedPkt != null) { // Punkt Grab, wenn keine Hotspots
			int[] pos = canvasPosToGitterpunkt(x, y);
			Punkt alt = knotenpunkte.get(grabbedPkt);
			int altx = alt.getX();
			int alty = alt.getY();
			alt.setXY(pos[0], pos[1]);
			if (alt == null || alt.getX() != altx || alt.getY() != alty) { // neu zeichnen
				debug("Knoten aktualisieren: "
						+ Arrays.toString(HilfString.appendArray(new String[] { grabbedPkt }, alt.toStringArray())));
				graph.execute(GraphInt.UpdateKnoten,
						HilfString.appendArray(new String[] { grabbedPkt }, alt.toStringArray()));
				this.graphZeichnen();
			}
		}
	}

	public void released(int x, int y) {
		grabbedPkt = null;
		grabbedHS = null;
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
			// this.neuerPunkt(pt[0], pt[1]);
			debug("Neuer Punkt: " + Arrays.toString(pt));
			graph.execute(GraphInt.NeuerKnoten, new String[] { "", "(" + pt[0] + "," + pt[1] + ")" }); // Leerer Name
																										// wird
																										// automatisch
																										// gesetzt
			this.graphNeuLaden(); // Zeichnen passiert dann automatisch
			break;
		case CanvasClicked: // z.B. beim neu Anlegen einer Kante
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			canvasClick(x, y, null);
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
						graph.execute(GraphInt.KanteLoeschen, kanten.get(i));
						graphNeuLaden();
						// kanten.remove(i);
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
			String s1 = v.stringErfragen("Gib die Anzahl der Knoten an", "vollständigen Graphen erzeugen", "10");
			if (graph.execute(GraphInt.VollstGraph, new String[] { s1 })) {
				this.graphNeuLaden();
				graphZeichnen();
				v.setStatusLine("Vollständiger Graph mit " + s1 + " Knoten");
			} else {
				v.setStatusLine("Vollständiger Graph - Argumentfehler: " + s1);
			}
			break;
		case BipartiterGraph:
			String s2 = v.stringErfragen("Gib die beiden Knotenanzahlen an", "bipartiten Graphen erzeugen", "3,3");
			if (graph.execute(GraphInt.BipartiterGraph, new String[] { s2 })) {
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
		case KantenArgumentUpdate:
			if (args != null && args.length > 2 && args[2].length() > 1) {
				for (int i = kanten.size() - 1; i >= 0; i--) {
					String[] k = kanten.get(i);
					if (k[0].equals(args[0]) && k[1].equals(args[1])) { // Kante gefunden
						kanten.set(i, HilfString.updateArray(kanten.get(i), args[2].substring(0, 2), args[2]));
						break; // Nur eine Kante
					}
				}
				graphZeichnen();
			}
			break;
		case KnotenArgumentUpdate:
			if (args != null && args.length > 1 && args[1].length() > 1) {
				Punkt p = knotenpunkte.get(args[0]);
				if (p != null) { // Knoten gefunden
					p.args = HilfString.updateArray(p.args, args[1].substring(0, 2), args[1]);
				}
				graphZeichnen();
			}
			break;
		case KantenDragHotspotsErzeugen:
			v.setEnableAlleMenueAktionen(false);
			kantenDragHotspotsErzeugen();
			v.setStatusLine("Kantentexte verschieben - anschliessend in das Rote Quadrat klicken");
			this.graphZeichnen();
			break;
		case KnotenDragHotspotsErzeugen:
			v.setEnableAlleMenueAktionen(false);
			knotenDragHotspotsErzeugen();
			v.setStatusLine("Knotentexte verschieben - anschliessend in das Rote Quadrat klicken");
			this.graphZeichnen();
			break;
		case KantenPosAbsToRel:
			kantenPosAbsToRel();
			break;
		case KnotenPosAbsToRel:
			knotenPosAbsToRel();
			break;
		case Graph_einlesen:
			File dateilesen = v.chooseFile(true);
			if (dateilesen != null) {
				graph.execute(GraphInt.LiesDatei, new String[] { dateilesen.getAbsolutePath() });
				this.graphNeuLaden();
			}
			break;
		case Graph_speichern:
			updateGraph();
			File dateispeichern = v.chooseFile(false);
			if (dateispeichern != null) {
				graph.execute(GraphInt.SchreibeDatei, new String[] { dateispeichern.getAbsolutePath() });
			}
			break;
		case DBClean:
			for (String name : knotenpunkte.keySet()) {
				Punkt p = knotenpunkte.get(name);
				String[] oldargs = p.getArgs();
				p.args = HilfString.duplikateLoeschen(oldargs);
				// Überflüssige Koordinaten entfernen
				int pos = HilfString.stringArrayElementPos(HilfString.subArray(p.args, 1), "(");
				while (pos > -1) {
					System.err.println("Doppelte Koordinaten in Punkt " + name + " gefunden: " + p.args[pos + 1]);
					p.args = HilfString.removePosFromArray(p.args, pos + 1);
					pos = HilfString.stringArrayElementPos(HilfString.subArray(p.args, 1), "(");
				}
				// Einträge in Args, die dem Namen entsprechen entfernen
				pos = HilfString.stringArrayElementPos(p.args, name);
				while (pos > -1) {
					System.err.println("Eintrag mit Namen des Knotens " + name + " in args gefunden!");
					p.args = HilfString.removePosFromArray(p.args, pos);
					pos = HilfString.stringArrayElementPos(p.args, name);
				}
				if (p.args.length != oldargs.length) {
					updateGraphKnoten(name);
					System.err.println("Duplikate bei Punkt " + name + " gefunden:" + Arrays.toString(oldargs) + "->"
							+ Arrays.toString(p.args));
				}
			}
			for (int i = 0; i < kanten.size(); i++) {
				String[] oldkante = kanten.get(i);
				String[] newkante = HilfString.duplikateLoeschen(oldkante);
				if (oldkante.length != newkante.length) {
					kanten.set(i, newkante);
					updateGraphKante(newkante);
					System.err.println("Duplikate bei Kante gefunden:" + Arrays.toString(oldkante) + "->"
							+ Arrays.toString(newkante));
				}
			}
			break;
		case KnotenLoeschen:
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			pt = canvasPosToGitterpunkt(x, y);
			String gewKnoten = knotenAnGitterPos(pt[0], pt[1]);
			if (gewKnoten != null) {
				v.setStatusLine("Knoten " + gewKnoten + " geloescht!");
				graph.execute(GraphInt.LoescheKnoten, knotenInfosZu(gewKnoten));
				graphNeuLaden();
				updateImgValues();
				this.graphZeichnen();
			}
			break;
		case SetEnableViewActions:
			if (args != null && args.length > 0 && args[0].equals("false")) {
				v.setEnableAlleMenueAktionen(false);
			}
			v.setEnableAlleMenueAktionen(true);
			break;
		case AnsichtAnGraphAnpassen:
			debug("execute - Grenzkoordinaten des Gitters neu bestimmen!");
			grenzen = this.gibGrenzKoordinatenDerKnoten();// Jeweils maximale Koordinaten bestimmen (Ecken des Bildes)
			this.updateImgValues();
			this.graphZeichnen();
			break;
		case GraphDatenNeuLaden:
			debug("execute - Graphdaten neu laden");
			this.graphNeuLaden();
			this.graphZeichnen();
			break;
		case InfoAusgeben:
			if (HilfString.stringArrayElementPos(args, "long") > -1) {
				int timer = 0;
				try {
					timer = Integer.parseInt(HilfString.stringArrayElement(args, "-D").substring(2));
				} catch (Exception e) {
					debug("Unable to parse Int in Info-Ausgeben: " + e.getMessage());
				}
				v.showInfoBox(args[0], "Information", timer);
			} else if (args != null && args.length > 0) {
				v.setStatusLine(args[0]);
			}
			break;
		case BefehlAnmelden:
			v.befehlInGraphMenue(args);
			break;
		case BefehlAnGraph:
			v.setEnableAlleMenueAktionen(false);
			graph.execute(GraphInt.AngemeldeterBefehl,
					(marked == null ? args : HilfString.appendString(args, "-P" + marked)));
			v.setEnableAlleMenueAktionen(true);
			break;
		case LoescheKnotenArgument:
			if (args != null && args[0].length() > 1 && args[0].startsWith("-")) {
				for (String k : knotenpunkte.keySet()) {
					knotenpunkte.get(k).args = HilfString.removeElementsFromArray(knotenpunkte.get(k).args, args[0]);
				}
				updateGraph();
				this.graphZeichnen();
			}
			break;
		case LoescheKantenArgument:
			if (args != null && args[0].length() > 1 && args[0].startsWith("-")) {
				for (int i = 0; i < kanten.size(); i++) {
					kanten.set(i, HilfString.removeElementsFromArray(kanten.get(i), args[0]));
				}
				updateGraph();
				this.graphZeichnen();
			}
			break;
		case UpdateGraphDaten:
			updateGraph();
			break;
		case StringErfragen:
			result = new String[] {v.stringErfragen(args[0], args[1], args[2])};
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
			debug("updateGraphKnoten: " + Arrays.toString(knoteninfos));
			graph.execute(GraphInt.UpdateKnoten, knoteninfos);
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
		graph.execute(GraphInt.UpdateKante, k);
	}

	/**
	 * prüft ob ein Objekt (Kante oder Punkt) eine Farbe hat und gibt diese Zurück
	 * Farben werden als -f1, -f2 oder ein ähnlicher String im String[] gespeichert
	 * 
	 * @param Kante oder Punkt als Sring[]
	 * @return Farbe
	 */
	private Color hatFarbe(String[] objekt) {
		// debug("Controller - hat Farbe - objekt: " + Arrays.toString(objekt));
		if (objekt == null)
			return farbenliste[0]; // Standardfarbe
		for (int i = 0; i < objekt.length; i++) {
			if (objekt[i].startsWith("-f")) { // Möglicherweise eine Farbe
				// debug("Farbe möglich");
				try {
					if (objekt[i].length() >= 8) { // HexFarbe
						// debug("Controller - hatFarbe - Hex-Farbe: " + objekt[i] + " Integer-Value:
						// "+Long.valueOf(objekt[i].substring(2), 16));
						return new Color(Long.valueOf(objekt[i].substring(2), 16).intValue(), true);
					} else {
						// debug("Controller - hatFarbe - keine Hex-Farbe");
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
		debug("Zoom - alte Grenzen: " + Arrays.toString(grenzen));
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
		debug("Zoom - neue Grenzen: " + Arrays.toString(grenzen));
		updateImgValues();
		graphZeichnen();
	}

	/**
	 * Arbeitet alle Clicks in das Zeichenfenster ab
	 * 
	 * @param x
	 * @param y
	 */
	private void canvasClick(int x, int y, MouseEvent e) {
		debug("In Methode Controller - canvasClick - x: " + x + " y:" + y);
		if (hotspots.size() > 0) {
			for (int i = 0; i < hotspots.size(); i++) {
				if (hotspots.get(i).fireIfInside(x, y)) { // nur einen Hotspot abfeuern
					break;
				}
			}
		}
		int[] pt = canvasPosToGitterpunkt(x, y);
		graphclicked(pt, e);
	}

	private void graphclicked(int[] pt, MouseEvent e) {
		if (kantenStart != null) { // Versuch eine Kante zu wählen
			String ziel = knotenAnGitterPos(pt[0], pt[1]);
			if (ziel == null) {
				v.setStatusLine("Kein Zielpunkt");
			} else {
				String[] kantenbeschr = new String[] { kantenStart, ziel };
				// kanten.add(kantenbeschr);
				graph.execute(GraphInt.NeueKante, kantenbeschr);
				setzeMarkierungenDoppelteKanten();
				v.setStatusLine("Kante hinzugefügt: " + kantenStart + "-" + ziel);
			}
			kantenStart = null;
			this.graphNeuLaden();
		} else {
			// Falls dort ein Knoten ist
			marked = knotenAnGitterPos(pt[0], pt[1]);
			v.setStatusLine((marked == null ? "Kein Knoten markiert" : "Markierter Knoten: " + marked));
		}
	}

	private void kantenHotspotsErzeugen(int command, String[] argsin) {
		int noDisableAfterFire = HilfString.stringArrayEnthaelt(argsin, "nodisableIfFired");
		// TODO: Umgang mit args hier im Code mit den HilfString-Methoden sauber
		// gestalten
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

	private void kantenDragHotspotsErzeugen() {
		for (String[] k : kanten) {
			if (HilfString.stringArrayElementPos(k, "-T") > -1) { // Kante hat Text
				System.out.println("Kante hat Text" + Arrays.toString(k));
				Punkt start = knotenpunkte.get(k[0]);
				Punkt ziel = knotenpunkte.get(k[1]);
				String[] args = new String[] { k[0], k[1] };

				int[] startp = gitterpunktToCanvasPos(start.getX(), start.getY());
				int[] zielp = gitterpunktToCanvasPos(ziel.getX(), ziel.getY());

				Hotspot h = new Hotspot((startp[0] + zielp[0]) / 2, (startp[1] + zielp[1]) / 2, 5, 5, -1, null, false);
				h.isGrabbable = true;
				h.dragcommand = KantenArgumentUpdate;
				h.dragargs = args;
				hotspots.add(h);
			}

		}
		// Hotspot zum entfernen aller Hotspots erzeugen
		Hotspot c = new Hotspot(-10, -10, 0, 0, KantenPosAbsToRel, null, true);
		Hotspot c2 = new Hotspot(-10, -10, 0, 0, SetEnableViewActions, new String[] { "true" }, true);
		c.child = c2;
		Hotspot c3 = new Hotspot(-10, -10, 0, 0, UpdateGraphDaten, null, true);
		c2.child = c3;
		Hotspot m = new Hotspot(5, 5, 15, 15, HotspotsLoeschen, null, Color.RED, true);
		m.child = c;
		hotspots.add(m);
	}

	private void knotenDragHotspotsErzeugen() {
		for (String k : knotenpunkte.keySet()) {
			Punkt p = knotenpunkte.get(k);
			if (HilfString.stringArrayElementPos(p.getArgs(), "-T") > -1) { // Knoten hat Text
				debug("Knoten hat Text" + p.toString());
				//int sx = Math.round((float) (10 + 1.0 * (p.getX() - xmin) * xstep));
				//int sy = Math.round((float) (img.getHeight() - (10. + 1.0 * (p.getY() - ymin) * ystep)));
				int[] cpos = gitterpunktToCanvasPos(p.getX(),p.getY());
				Hotspot h = new Hotspot(cpos[0], cpos[1], 5, 5, -1, null, false);
				h.isGrabbable = true;
				h.dragcommand = KnotenArgumentUpdate;
				h.dragargs = new String[] { k };
				hotspots.add(h);
			}
		}
		// Hotspot zum entfernen aller Hotspots erzeugen
		Hotspot c = new Hotspot(-10, -10, 0, 0, KnotenPosAbsToRel, null, true);
		Hotspot c2 = new Hotspot(-10, -10, 0, 0, SetEnableViewActions, new String[] { "true" }, true);
		c.child = c2;
		Hotspot c3 = new Hotspot(-10, -10, 0, 0, UpdateGraphDaten, null, true);
		c2.child = c3;
		Hotspot m = new Hotspot(5, 5, 15, 15, HotspotsLoeschen, null, Color.RED, true);
		m.child = c;
		hotspots.add(m);
	}

	private void kantenPosAbsToRel() {
		for (int i = 0; i < kanten.size(); i++) {
			String[] k = kanten.get(i);
			if (HilfString.stringArrayElementPos(k, "-P") > -1) { // Kante hat absolute Position
				System.out.println("Kante hat absolute Position: " + Arrays.toString(k));
				Punkt start = knotenpunkte.get(k[0]);
				Punkt ziel = knotenpunkte.get(k[1]);

				int[] startp = gitterpunktToCanvasPos(start.getX(), start.getY());
				int[] zielp = gitterpunktToCanvasPos(ziel.getX(), ziel.getY());

				int[] abspos = HilfString.intKoordsAusString(HilfString.stringArrayElement(k, "-P"));
				if (abspos != null) {
					int[] relpos = new int[] { abspos[0] - ((startp[0] + zielp[0]) / 2),
							abspos[1] - ((startp[1] + zielp[1]) / 2) };
					kanten.set(i, HilfString.updateArray(HilfString.removeElementsFromArray(k, "-P"), "-p",
							"-p" + relpos[0] + "," + relpos[1]));
				}
			}
		}
		updateGraph();
	}

	private void knotenPosAbsToRel() {
		for (String k : knotenpunkte.keySet()) { // alle Knoten durchlaufen
			Punkt p = knotenpunkte.get(k);
			if (HilfString.stringArrayElementPos(p.args, "-P") > -1) { // Knotentext hat absolute Position
				System.out.println("Knotentext hat absolute Position: " +k+" : "+p);
				int[] abspos = HilfString.intKoordsAusString(HilfString.stringArrayElement(p.args, "-P"));
				int[] cpos = gitterpunktToCanvasPos(p.getX(), p.getY());
				if (abspos != null) {
					int[] relpos = new int[] { abspos[0] - cpos[0], abspos[1] - cpos[1] };
					p.args = HilfString.updateArray(HilfString.removeElementsFromArray(p.args, "-P"), "-p",
							"-p" + relpos[0] + "," + relpos[1]);
				}
			}
		}
		updateGraph();
	}

	// *******************************+ Hilfsmethoden
	// ************************************

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

	private void debug(String text) {
		if (debug)
			System.out.println("C:" + text);
	}

	private void debug(String text, boolean aktuell) {
		if (debug)
			System.out.println("C:" + text);
	}

	private void debuge(String text) {
		if (debug)
			System.err.println("C:" + text);
	}

	public void testfunktion() {
	}

	public String[] getResult() {
		return result;
	}

}