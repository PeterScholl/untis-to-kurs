package facharbeit;

import java.util.*;

import graphen.ContInt;
import graphen.GraphInt;
import graphen.HilfString;

class Graph implements GraphInt {
	private String name;
	private ArrayList<Kante> kanten = new ArrayList<Kante>();
	private ArrayList<Knoten> knoten = new ArrayList<Knoten>();

	public Graph(String name) {
		this.name = name;
	}

	public void kanteHinzufuegen(Kante e) {
		kanten.add(e);
		Knoten k1 = e.getKnoten1();
		Knoten k2 = e.getKnoten2();
		if (!knoten.contains(k1)) {
			knoten.add(k1);
		}
		if (!knoten.contains(k2)) {
			knoten.add(k2);
		}
	}

	public int anzKanten() {
		return kanten.size();
	}

	public int anzKnoten() {
		return knoten.size();
	}

	public int knotenGrad(Knoten k) {
		int grad = 0;
		for (int i = 0; i < kanten.size(); i++) {
			if (kanten.get(i).getKnoten1() == k || kanten.get(i).getKnoten2() == k) {
				grad++;
			}
		}
		return grad;
	}

	public Knoten gibKnotenMitName(String name) {
		for (Knoten k : knoten) {
			if (k.getName().equals(name))
				return k;
		}
		return null;
	}

	public ArrayList<Knoten> eulerTour() {
		if (!eulerTourMoeglich()) {
			System.out.println("Die Eulertour ist mit der Figur nicht möglich!");
			// System.exit(1);
			return null;
		}
		// begin
		// tour :=(s),für einen beliebigen (Afangs-)Knoten s aus F
		// while es gibt einen Knoten u in der Tour, von dem noch eine Kante ausgeht
		// v:=u
		// repeat
		// nimm eine Kante v-w, die in v beginnt
		// f�ge den anderen Endknoten w hinter v in der Tour ein
		// v:=w
		// entferne die Kante aus F
		// until v=u {kreis geschlossen}
		// endwhile
		// return Tour
		// end
		ArrayList<Knoten> tour = new ArrayList<Knoten>();
		tour.add(knoten.get(0));
		ArrayList<Kante> k = (ArrayList<Kante>) kanten.clone();

		Knoten u = gibKnotenVonDemEineKanteAusgeht(tour, k);
		// System.out.println("Kantengroesse: "+kanten.size()+" und "+k.size());
		// System.exit(0);

		while (u != null) {
			Knoten v = u;
			do {
				for (int j = 0; j < k.size(); j++) {
					// System.out.println(k.get(j)+" - "+k.size()+ "Tour:"+tour);
					if (k.get(j).enthaeltKnoten(v)) {
						Knoten w = k.get(j).getGegenKnoten(v);
						int posv = tour.indexOf(v);
						tour.add(posv + 1, w);// hinter v einf�gen
						v = w;
						// System.out.println("Knoten v: "+v);
						k.remove(j);
						j--;
					}
				}
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} while (u != v);

			u = gibKnotenVonDemEineKanteAusgeht(tour, k);
		}
		return tour;
	}

	public boolean eulerTourMoeglich() {
		for (int i = 0; i < knoten.size(); i++) {
			if (knotenGrad(knoten.get(i)) % 2 != 0) {
				return false;
			}
		}
		return true;
	}

	public static Knoten gibKnotenVonDemEineKanteAusgeht(ArrayList<Knoten> vertices, ArrayList<Kante> edges) {
		// Gibt einen Knoten von ... zur�ck sonst null
		for (int i = 0; i < vertices.size(); i++) {
			for (int j = 0; j < edges.size(); j++) {
				if (edges.get(j).getKnoten1() == vertices.get(i) || edges.get(j).getKnoten2() == vertices.get(i)) {
					return vertices.get(i);
				}
			}
		}
		return null;
	}

	/**
	 * erh�lt einen Startknoten und notiert in allen Punkten den (k�rzesten) Abstand
	 * zu diesem Startknoten (setAbstand())
	 */
	public void dijkstra(String start) {
		// Alle Knoten warten, alle d[v] sind unendlich, nur d[Start]ist=0
		// while es wartende Knoten gibt do
		// v:= der wartende Knoten mit dem kleinsten d[v]
		// Mache v h�ngend
		// for all F�den von v zu einem Nachbarn u der L�nge l do
		// if d[v]+l<d[v], then d[v]:=d[v]+l//k�rtzester weg zu u f�hrt �ber v
		ArrayList<Knoten> wartend = new ArrayList<Knoten>();
		for (Knoten a : knoten) {
			wartend.add(a);
			if (a.getName().equals(start)) {
				a.setAbstand(0);
				System.out.println("Startknoten gesetzt:" + a);
			}
		}
		while (wartend.size() > 0) {
			Knoten v = wartend.get(0);
			for (int i = 1; i < wartend.size(); i++) {
				if (wartend.get(i).getAbstand() < v.getAbstand())
					v = wartend.get(i);
			}
			// Mache v h�ngend
			wartend.remove(v); // nicht mehr wartend
			// for all F�den von v zu einem Nachbarn u der L�nge l do
			for (Kante k : kanten) {
				if (k.getKnoten1().equals(v) || k.getKnoten2().equals(v)) {
					Knoten u = k.getGegenKnoten(v);
					// if d[v] + l < d[u], then d[u]:=d[v]+l
					if (v.getAbstand() + k.getGewicht() < u.getAbstand()) {
						// k�rzeren Weg hin zu u gefunden, f�hrt �ber v
						u.setAbstand(v.getAbstand() + k.getGewicht());
						u.setVonKnoten(v);
					}
				}
			}
		}
	}

	public int[] gibGrenzKoordinatenDerKnoten() {
		int minx = Integer.MAX_VALUE;
		int miny = Integer.MAX_VALUE;
		int maxx = Integer.MIN_VALUE;
		int maxy = Integer.MIN_VALUE;
		for (Knoten k : knoten) {
			minx = (k.getX() < minx ? k.getX() : minx);
			miny = (k.getY() < miny ? k.getY() : miny);
			maxx = (k.getX() > maxx ? k.getX() : maxx);
			maxy = (k.getY() > maxy ? k.getY() : maxy);
		}
		return new int[] { minx, miny, maxx, maxy };
	}

	// nicht von mir
	public void graphZeichnen() {
		// hier soll der Graph gezeichnet werden
		Turtle t = new Turtle(700, 300); // Turtle erzeugen - mit Graphikbildschirm (x-Länge x y-Länge)
		// System.out.println(t.getHeight()); //Zum Testen
		t.setColor(Turtle.green);
		int[] grenzen = this.gibGrenzKoordinatenDerKnoten();// Jeweils maximale Koordinaten bestimmen (Ecken des Bildes)
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
		for (Kante k : kanten) { // alle Kanten Zeichnen
			Knoten p1 = k.getKnoten1();
			Knoten p2 = k.getKnoten2();
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

	public void allePunkteMitKoordinatenBelegen() {
		// Um nicht jeden Punkt einzeln auf das Blatt zu setzen
		// werden alle Punkte an den Rand eines Kreises gesetzt
		int anzKnoten = knoten.size();
		// Der Abstand der Knoten auf dem Umfang des Kreises sollte
		// eine Mindestgröße haben, damit ganzzahlige Koordinaten möglich sind
		int mindestabstand = 5;
		int kreisumfang = 5 * anzKnoten;
		double radius = 1.0 * kreisumfang / (2 * Math.PI);
		System.out.println("Radius: " + radius);
		for (int i = 0; i < anzKnoten; i++) {
			int y = (int) (radius * Math.sin(i * 2 * Math.PI / anzKnoten) + radius);
			int x = (int) (radius * Math.cos(i * 2 * Math.PI / anzKnoten) + radius);
			knoten.get(i).setXY(x, y);
		}
	}

	@Override
	public String toString() {
		String out = "";
		for (Kante k : kanten) { // for each - Schleife
			out += k + "\n";
		}
		return out;
	}

	// ***** Methoden zum Anbinden an den View

	@Override
	public ArrayList<String[]> getKnotenPunkte() {
		ArrayList<String[]> ret = new ArrayList<String[]>();
		for (Knoten k : knoten) {
			ret.add(knotenToStringArray(k));
		}
		return ret;
	}

	private String[] knotenToStringArray(Knoten k) {
		String[] ret = graphen.HilfString
				.appendArray(new String[] { k.getName(), "(" + k.getX() + "," + k.getY() + ")" }, k.getInfo());
		return ret;
	}

	private void kInfoUpdate(Knoten k, String startsWith, String newValue) {
		k.setInfo(HilfString.updateArray(k.getInfo(), startsWith, newValue));
	}

	private void kInfoUpdate(Kante k, String startsWith, String newValue) {
		k.setInfo(HilfString.updateArray(k.getInfo(), startsWith, newValue));
	}

	@Override
	public ArrayList<String[]> getKnotenVerbindungen() {
		ArrayList<String[]> ret = new ArrayList<String[]>();
		for (Kante k : kanten) {
			ret.add(graphen.HilfString.appendArray(new String[] { k.getKnoten1().getName(), k.getKnoten2().getName() },
					k.getInfo()));
		}
		return ret;
	}

	@Override
	public boolean execute(int command, String[] args) {
		switch (command) {
		case VollstGraph:
		case BipartiterGraph:
		case LiesDatei:
			break;
		case SchreibeDatei:
			break;
		case UpdateKante:
			return false;
		case UpdateKnoten:
			if (args == null || args.length < 1)
				return false; // unsinnige Infos
			// Knoten finden
			for (Knoten k : knoten) {
				if (k.getName().equals(args[0])) { // Knoten gefunden
					// Koordinaten auslesen
					try {
						String koords = HilfString.stringArrayElement(args, "(");
						String[] koords2 = koords.replaceAll("[ ()]", "").split(",");
						k.setX(Integer.parseInt(koords2[0]));
						k.setY(Integer.parseInt(koords2[1]));
					} catch (Exception e) {
						System.err.println("Konnte Punktkoordinaten nicht parsen: " + Arrays.toString(args) + " - "
								+ e.getMessage());
					}
					k.setInfo(HilfString.removeElementsFromArray(HilfString.subArray(args, 1), "("));
					return true;
				}
			}
			return false;
		case KanteLoeschen:
			return false;
		case NeueKante:
			return false;
		case LoescheKnoten:
			break;
		case NeuerKnoten:
			break;
		case BefehleAnmelden:
			befehleAnmelden();
			break;
		case AngemeldeterBefehl:
			// Angemeldeten Befehl ausführen
			befehlAusfuehren(args);
			break;
		}
		return false;
	}

	private void befehlAusfuehren(String[] args) {
		System.out.println("Graph - befehlAusfuehren: "+Arrays.toString(args));
		ContInt.execute(ContInt.SetEnableActions, new String[] { "false" });
		if (args != null && args.length > 0) {
			if (args[0].equals("test")) { // Befehl Testinfo ausgeben
				ContInt.execute(ContInt.InfoAusgeben, new String[] { "TestInfo", "long" });
				ContInt.execute(ContInt.InfoAusgeben, new String[] { "gut gemacht :-)" });
			} else if (args[0].equals("euler")) { // Befehl minimal auspannenden Baum aufzeichnen
				eulerTourMitView();
			} else if (args[0].equals("dijkstra")) { // Befehl minimal auspannenden Baum aufzeichnen
				String start = knoten.get(0).getName();
				String name = HilfString.stringArrayElement(args, "-P");
				System.out.println("Name: "+name);
				if (name != null)
					start = name.substring(2);
				System.out.println("Dijkstra mit Startknoten: "+start);
				dijkstraMitView(start);
			}
		}
		ContInt.execute(ContInt.SetEnableActions, new String[] { "true" });
	}

	private void befehleAnmelden() {
		ContInt.execute(ContInt.BefehlAnmelden, new String[] { "test", "Testinfo ausgeben" });
		ContInt.execute(ContInt.BefehlAnmelden, new String[] { "euler", "Eulertour ermitteln" });
		ContInt.execute(ContInt.BefehlAnmelden, new String[] { "dijkstra", "Dijkstra Algorithmus", "punkt" });
	}

	public void eulerTourMitView() {
		if (!eulerTourMoeglich()) {
			ContInt.execute(ContInt.InfoAusgeben, new String[] { "Die Eulertour ist mit der Figur nicht möglich!" });
			return;
		}
		// begin
		// tour :=(s),für einen beliebigen (Afangs-)Knoten s aus F
		// while es gibt einen Knoten u in der Tour, von dem noch eine Kante ausgeht
		// v:=u
		// repeat
		// nimm eine Kante v-w, die in v beginnt
		// f�ge den anderen Endknoten w hinter v in der Tour ein
		// v:=w
		// entferne die Kante aus F
		// until v=u {kreis geschlossen}
		// endwhile
		// return Tour
		// end
		ArrayList<Knoten> tour = new ArrayList<Knoten>();
		tour.add(knoten.get(0));
		kInfoUpdate(knoten.get(0), "-f", "-fffff0000"); // Farbe auf rot setzen
		ContInt.execute(ContInt.UpdateGraph, null);
		ContInt.execute(ContInt.InfoAusgeben,
				new String[] { "Startknoten: " + knoten.get(0).getName(), "long", "-D0" });

		ArrayList<Kante> k = (ArrayList<Kante>) kanten.clone();

		Knoten u = gibKnotenVonDemEineKanteAusgeht(tour, k);
		// System.out.println("Kantengroesse: "+kanten.size()+" und "+k.size());
		// System.exit(0);

		while (u != null) {
			Knoten v = u;
			kInfoUpdate(v, "-f", "-fffff0000"); // v rot färben
			ContInt.execute(ContInt.UpdateGraph, null);
			ContInt.execute(ContInt.InfoAusgeben,
					new String[] { "aktueller Knoten: " + v.getName(), "long", "-D1000" });
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			do {
				for (int j = 0; j < k.size(); j++) {
					// System.out.println(k.get(j)+" - "+k.size()+ "Tour:"+tour);
					if (k.get(j).enthaeltKnoten(v)) {
						Knoten w = k.get(j).getGegenKnoten(v);
						int posv = tour.indexOf(v);
						tour.add(posv + 1, w);// hinter v einf�gen
						kInfoUpdate(v, "-f", "-fff00ff00"); // altes v grün färben
						v = w;
						kInfoUpdate(v, "-f", "-fffff0000"); // neues v rot färben
						kInfoUpdate(k.get(j), "-f", "-fffff0000"); // benutzte Kantek
						ContInt.execute(ContInt.UpdateGraph, null);
						ContInt.execute(ContInt.InfoAusgeben,
								new String[] { "nächster Knoten: " + w.getName(), "long", "-D1000" });
						try {
							Thread.sleep(400);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						// System.out.println("Knoten v: "+v);
						k.remove(j);
						j--;
					}
				}
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} while (u != v);

			u = gibKnotenVonDemEineKanteAusgeht(tour, k);
		}
	}

	public void dijkstraMitView(String start) {
		// Kantengewichte als Text anzeigen und kannten auf Schwarz schalten:
		for (Kante k : kanten) {
			kInfoUpdate(k, "-T", "-T" + k.getGewicht());
			kInfoUpdate(k, "-f", "-f0");
		}
		// Alle Knoten ohne Text und ohne Gewicht setzen
		for (Knoten k : knoten) {
			kInfoUpdate(k, "-T", "-T"); // leerer Text - ein Entfernen des Attributes wäre auch schön
			k.setAbstand(Integer.MAX_VALUE); // unendlich weit entfernt
			System.out.println("Knoten: " + k);
		}
		// Alle Knoten warten, alle d[v] sind unendlich, nur d[Start]ist=0
		// while es wartende Knoten gibt do
		// v:= der wartende Knoten mit dem kleinsten d[v]
		// Mache v h�ngend
		// for all F�den von v zu einem Nachbarn u der L�nge l do
		// if d[v]+l<d[v], then d[v]:=d[v]+l//k�rtzester weg zu u f�hrt �ber v
		ArrayList<Knoten> wartend = new ArrayList<Knoten>();
		for (Knoten a : knoten) {
			kInfoUpdate(a, "-f", "-fff0000ff"); // Farbe auf blau setzen

			wartend.add(a);
			if (a.getName().equals(start)) {
				kInfoUpdate(a, "-T", "-T0"); // Text auf Abstand 0 setzen
				a.setAbstand(0);
				System.out.println("Startknoten gesetzt:" + a);
			}
		}
		ContInt.execute(ContInt.UpdateGraph, null);
		ContInt.execute(ContInt.InfoAusgeben,
				new String[] { "alle Knoten wartend - Startknoten: " + start, "long", "-D0" });
		while (wartend.size() > 0) {
			Knoten v = wartend.get(0);
			for (int i = 1; i < wartend.size(); i++) {
				if (wartend.get(i).getAbstand() < v.getAbstand())
					v = wartend.get(i);
			}
			// Mache v h�ngend
			kInfoUpdate(v, "-f", "-fffff0000"); // Farbe auf rot setzen - hängend
			ContInt.execute(ContInt.UpdateGraph, null);
			ContInt.execute(ContInt.InfoAusgeben, new String[] { "Knoten: " + v + "jetzt hängend", "long", "-D0" });

			wartend.remove(v); // nicht mehr wartend
			// for all F�den von v zu einem Nachbarn u der L�nge l do
			for (Kante k : kanten) {
				if (k.getKnoten1().equals(v) || k.getKnoten2().equals(v)) {
					kInfoUpdate(k, "-f", "-fffaaaaff"); // Kante Hellblau - benutzt
					Knoten u = k.getGegenKnoten(v);
					// if d[v] + l < d[u], then d[u]:=d[v]+l
					if (v.getAbstand() + k.getGewicht() < u.getAbstand()) {
						// k�rzeren Weg hin zu u gefunden, f�hrt �ber v
						u.setAbstand(v.getAbstand() + k.getGewicht());
						kInfoUpdate(u, "-T", "-T" + u.getAbstand()); // Abstand als Text setzen
						u.setVonKnoten(v);
					}
				}
			}
			kInfoUpdate(v, "-f", "-fff00ff00"); // v -> Farbe grün - erleedigt
			ContInt.execute(ContInt.UpdateGraph, null);

			ContInt.execute(ContInt.InfoAusgeben,
					new String[] { "Alle Nachbarknoten von " + v + " erhalten neuen Abstand", "long", "-D0" });
		}
	}

}