package graphen;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph implements AbstrGraph {
	public static final int VollstGraph = 0;
	public static final int BipartiterGraph = 1;
	private String name = "";
	private ArrayList<Knoten> knoten;
	private ArrayList<Kante> kanten;
	private HashMap<Knoten, Integer> knotengrade;

	/**
	 * @param name
	 */
	public Graph(String name) {
		this.name = name;
		this.knoten = new ArrayList<Knoten>();
		this.kanten = new ArrayList<Kante>();
		this.knotengrade = new HashMap<Knoten, Integer>();
	}

	public Graph() {
		this("");
	}

	public void knotenHinzufuegen(Knoten v) {
		if (knoten.contains(v))
			return; // Knoten schon vorhanden
		knoten.add(v);
		knotengrade.put(v, 0);
	}

	public void kanteHinzufuegen(Kante e) {
		if (kanten.contains(e))
			return; // Kante schon vorhanden
		// Knoten hinzufügen
		Knoten start = e.getStart();
		Knoten ziel = e.getZiel();
		knotengrade.computeIfPresent(start, (k, x) -> x + 1);
		knotengrade.computeIfPresent(ziel, (k, x) -> x + 1);
		if (!knoten.contains(start)) {
			knoten.add(start);
			knotengrade.put(start, 1);
		}
		if (!knoten.contains(ziel)) {
			knoten.add(ziel);
			knotengrade.put(ziel, 1);
		}
		kanten.add(e);
	}

	public int anzKanten() {
		return kanten.size();
	}

	public int anzKnoten() {
		return knoten.size();
	}

	public boolean istBaum() { // nach einem Satz aus der Graphentheorie ist diese Aussage äquivalent
		return this.anzKnoten() - this.anzKanten() == 1;
	}

	public boolean istKreis() { // Wenn alle Knotengrade = 2 sind und zusammenhängend
		if (this.istZusammenhaengend()) {
			for (Integer i : knotengrade.values()) {
				if (i != 2)
					return false;
			}
			return true;
		}
		return false;
	}

	public boolean istZusammenhaengend() {
		if (knoten.isEmpty())
			return true; // Leerer Graph
		this.knotenMarkierungenLoeschen();
		ArrayList<Knoten> todo = new ArrayList<Knoten>();
		todo.add(knoten.get(0));
		knoten.get(0).setMarked(true);
		while (!todo.isEmpty()) {
			Knoten start = todo.get(0);
			todo.remove(0);
			for (Kante e : kanten) {
				Knoten v = e.gibAnderenKnoten(start);
				if (v != null && !v.isMarked()) {
					todo.add(v);
					v.setMarked(true);
				}
			}
		}
		return this.alleKnotenMarkiert();
	}

	public boolean istBipartit() {
		if (!istZusammenhaengend())
			return false;
		if (knoten.isEmpty())
			return false;
		for (Knoten v : knoten)
			v.setFarbe(0); // alle Farben zurücksetzen
		ArrayList<Kante> todo = new ArrayList<Kante>();
		for (Kante k : kanten)
			todo.add(k);
		knoten.get(0).setFarbe(1); // erste Knoten erhält Farbe
		while (!todo.isEmpty()) {
			Kante a = todo.get(0);
			todo.remove(0);
			if (a.getStart().getFarbe() > 0) { // Startknoten ist gefärbt
				if (a.getZiel().getFarbe() == a.getStart().getFarbe())
					return false; // gleiche Farbe benachbarter Knoten - nicht bipartit
				a.getZiel().setFarbe(3 - a.getStart().getFarbe()); // Knoten in der anderen Farbe färben
			} else if (a.getZiel().getFarbe() > 0) { // Zielknoten ist gefärbt und Startknoten ist nicht gefärbt
				a.getStart().setFarbe(3 - a.getZiel().getFarbe()); // Startknoten in der Gegenfarbe färben
			} else { // Kein Knoten ist gefärbt - Kante muss wieder betrachtet werden
				todo.add(a);
			}
		}
		return true;
	}

	private boolean alleKnotenMarkiert() {
		return (this.anzKnotenMarkiert() == this.anzKnoten());
	}

	private int anzKnotenMarkiert() {
		int count = 0;
		for (Knoten k : knoten) {
			if (k.isMarked())
				count++;
		}
		return count;
	}

	public boolean hatKnoten(Knoten k) {
		return knoten.contains(k);
	}

	public boolean hatKante(Kante k) {
		return kanten.contains(k);
	}

	public void knotenMarkierungenLoeschen() {
		knoten.forEach((k) -> k.setMarked(false));
	}

	@SuppressWarnings("unchecked")
	public Graph clone() {
		Graph neu = new Graph(this.name + " (Kopie)");
		neu.knoten = (ArrayList<Knoten>) this.knoten.clone();
		neu.kanten = (ArrayList<Kante>) this.kanten.clone();
		neu.knotengrade = (HashMap<Knoten, Integer>) this.knotengrade.clone();
		return neu;
	}

	public void kanteEntfernen(Kante k) {
		if (k == null)
			return;
		Knoten start = k.getStart();
		Knoten ziel = k.getZiel();
		kanten.remove(k);
		knotengrade.computeIfPresent(start, (key, x) -> x - 1);
		knotengrade.computeIfPresent(ziel, (key, x) -> x - 1);
	}

	public void unverbundeneEinzelknotenEntfernen() {
		for (Knoten s : knoten) {
			if (knotengrade.get(s) <= 0) {
				knotengrade.remove(s);
				// knoten.remove(s);
			}
		}
		knoten.removeIf((v) -> (knotengrade.get(v) == null));
	}

	public Kante gibKanteMinGewicht() {
		int w = Integer.MAX_VALUE;
		Kante ret = null;
		for (Kante k : kanten) {
			if (k.getGewicht() < w) {
				w = k.getGewicht();
				ret = k;
			}
		}
		return ret;
	}

	public Graph gibMinimalAufspannendenBaum() {
		// erst mal prüfen, ob der Graph zusammenhängend ist
		Graph g2 = this.clone(); // editierbare Version dieses Graphen
		Graph baum = new Graph("min. aufspannender Baum von " + this.name);
		Kante emin = g2.gibKanteMinGewicht();
		while (emin != null) {
			if (!(baum.hatKnoten(emin.getStart()) && baum.hatKnoten(emin.getZiel()))) { // NEUE Verbindung
				baum.kanteHinzufuegen(emin);
			}
			g2.kanteEntfernen(emin);
			// System.out.println("g2 hat "+g2.anzKanten()+"Kanten");
			emin = g2.gibKanteMinGewicht();
		}
		return baum;
	}

	public static Graph vollstaendigenGraphK(int n) {
		Knoten[] alleKnoten = new Knoten[n];
		for (int i = 0; i < n; i++) {
			alleKnoten[i] = new Knoten("K" + n + "_" + (i + 1));
		}
		Graph ret = new Graph("K_" + n);
		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				ret.kanteHinzufuegen(new Kante(alleKnoten[i], alleKnoten[j]));
			}
		}
		return ret;
	}
	
	private Graph biparitenGraphK(int n1, int n2) {
		Knoten[] knotenLi = new Knoten[n1]; //Knoten "links"
		Knoten[] knotenRe = new Knoten[n2]; //Knoten "rechts"
		for (int i = 0; i < n1; i++) {
			knotenLi[i] = new Knoten("A" + (i + 1));
		}
		for (int i = 0; i < n2; i++) {
			knotenRe[i] = new Knoten("B" + (i + 1));
		}
		
		Graph ret = new Graph("K_" + n1 +","+n2);
		for (int i = 0; i < n1; i++) {
			for (int j = 0; j < n2; j++) {
				ret.kanteHinzufuegen(new Kante(knotenLi[i], knotenRe[j]));
			}
		}
		return ret;
	}


	@Override
	public String toString() {
		return "Graph: " + name + "\nkanten=" + kanten + "\nknotengrade=" + knotengrade;
	}

	@Override
	public ArrayList<String[]> getKnotenPunkte() {
		ArrayList<String[]> ret = new ArrayList<String[]>();
		// die Knoten haben hier (noch) keie Koordinaten - also Kreiskoordinaten
		// Um nicht jeden Punkt einzeln auf das Blatt zu setzen
		// werden alle Punkte an den Rand eines Kreises gesetzt
		int anzKnoten = knoten.size();
		// Der Abstand der Knoten auf dem Umfang des Kreises sollte
		// eine Mindestgröße haben, damit ganzzahlige Koordinaten möglich sind
		int mindestabstand = 5;
		int kreisumfang = mindestabstand * anzKnoten;
		double radius = 1.0 * kreisumfang / (2 * Math.PI);
		System.out.println("Radius: " + radius);
		for (int i = 0; i < anzKnoten; i++) {
			int y = (int) (radius * Math.sin(i * 2 * Math.PI / anzKnoten) + radius);
			int x = (int) (radius * Math.cos(i * 2 * Math.PI / anzKnoten) + radius);
			ret.add(new String[] { knoten.get(i).getName(), "(" + x + "," + y + ")", "-f" + i });
		}
		return ret;
	}

	@Override
	public ArrayList<String[]> getKnotenVerbindungen() {
		ArrayList<String[]> ret = new ArrayList<String[]>();
		for (int i = 0; i < kanten.size(); i++) {
			Kante k = kanten.get(i);
			ret.add(new String[] { k.getStart().getName(), k.getZiel().getName(), "-f" + i });
		}
		return ret;
	}

	@Override
	public boolean execute(int command, String[] args) {
		switch (command) {
		case VollstGraph:
			try {
				Graph neu = vollstaendigenGraphK(Integer.parseInt(args[0]));
				this.kanten = neu.kanten;
				this.knoten = neu.knoten;
				this.knotengrade = neu.knotengrade;
				this.name = neu.name;
				return true;
			} catch (Exception e) {
				System.err.println("Argumentfehler (Graph - vollst. Graph erzeugen): " + e.getMessage());
				return false;
			}
		case BipartiterGraph:
			try {
				String[] values = args[0].replaceAll("[ a-zA-Z]", "").split(",");
				Graph neu = biparitenGraphK(Integer.parseInt(values[0]),Integer.parseInt(values[1]));
				this.kanten = neu.kanten;
				this.knoten = neu.knoten;
				this.knotengrade = neu.knotengrade;
				this.name = neu.name;
				return true;
			} catch (Exception e) {
				System.err.println("Argumentfehler (Graph - vollst. Graph erzeugen): " + e.getMessage());
				return false;
			}
		default:
		}
		return false; // Keinen Befehl ausgeführt
	}
}
