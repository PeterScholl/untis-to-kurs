package graphen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JFileChooser;

public class Graph implements GraphInt {
	private String name = "";
	private ArrayList<Knoten> knoten;
	private ArrayList<Kante> kanten;
	private HashMap<Knoten, Integer> knotengrade;
	private boolean debug = false;

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
			// debug("g2 hat "+g2.anzKanten()+"Kanten");
			emin = g2.gibKanteMinGewicht();
		}
		return baum;
	}

	public Graph gibMinimalAufspannendenBaumMitView() {
		// erst mal prüfen, ob der Graph zusammenhängend ist
		Graph g2 = this.clone(); // editierbare Version dieses Graphen
		//Alle Knoten in g2 werden auf Komponente 0 gesetzt
		for (Knoten k : g2.knoten) k.setArgs(HilfString.updateArray(k.getArgs(), "-K", "-K0"));
		//Alle Kanten auf Fabre grau Setzen
		for (Kante k: g2.kanten) k.setArgs(HilfString.updateArray(k.getArgs(), "-f", "-fffaaaaaa"));
		int nextk = 1; //Nummer der nächsten Komponente
		Graph baum = new Graph("min. aufspannender Baum von " + this.name);
		Kante emin = g2.gibKanteMinGewicht();
		while (emin != null) {
			//aktuelle Kante auf grün
			emin.setArgs(HilfString.updateArray(emin.getArgs(), "-f", "-fff00ff00"));
			ContInt.execute(ContInt.UpdateGraph, null);
			ContInt.execute(ContInt.InfoAusgeben, new String[] {"Betrachte Kante "+emin,"long"});
		
			// Prüfe ob ein Ende der Kante noch gar nicht oder beide Enden verschiedenen Komponenten angehören
			int kstart = Integer.parseInt(HilfString.stringArrayElement(emin.getStart().getArgs(),"-K").substring(2));
			int kziel = Integer.parseInt(HilfString.stringArrayElement(emin.getZiel().getArgs(),"-K").substring(2));
			if (kstart==0 || kziel==0 || kstart!=kziel) { // Neue Verbindung
				baum.kanteHinzufuegen(emin);
				emin.setArgs(HilfString.updateArray(emin.getArgs(), "-f", "-fffff0000"));
				ContInt.execute(ContInt.UpdateGraph, null);
				ContInt.execute(ContInt.InfoAusgeben, new String[] {"Hinzugefuegt "+emin,"long"});
				if (kstart==0 && kziel ==0) { // Neue Komponente
					//Beide Knoten bekommen die nächste Nummer
					emin.getStart().setArgs(HilfString.updateArray(emin.getStart().getArgs(), "-K", "-K"+nextk));
					emin.getZiel().setArgs(HilfString.updateArray(emin.getZiel().getArgs(), "-K", "-K"+nextk++));
				} else if (kstart==0) {
					//kstart die gleiche Komponente wie Ziel
					emin.getStart().setArgs(HilfString.updateArray(emin.getStart().getArgs(), "-K", "-K"+kziel));
				} else if (kziel==0) {
					//kziel die gleiche Kompoente wie Start
					emin.getZiel().setArgs(HilfString.updateArray(emin.getZiel().getArgs(), "-K", "-K"+kstart));
				} else { // alle Komponenten auf gleichen Wert setzen
					for (int i=0; i<knoten.size();i++) {
						int pos = HilfString.stringArrayEnthaelt(knoten.get(i).getArgs(), "-K"+kziel);
						if (pos>-1) {//gefunden
							knoten.get(i).setArgs(HilfString.updateArray(knoten.get(i).getArgs(), "-K", "-K"+kstart));
						}
					}
				}
			} else { //keine neue Verbindung
				emin.setArgs(HilfString.updateArray(emin.getArgs(), "-f", "-fffaaaaff")); //Kante blau
			}
			ContInt.execute(ContInt.UpdateGraph, null);
			g2.kanteEntfernen(emin);
			// debug("g2 hat "+g2.anzKanten()+"Kanten");
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
		Knoten[] knotenLi = new Knoten[n1]; // Knoten "links"
		Knoten[] knotenRe = new Knoten[n2]; // Knoten "rechts"
		for (int i = 0; i < n1; i++) {
			knotenLi[i] = new Knoten("A" + (i + 1));
		}
		for (int i = 0; i < n2; i++) {
			knotenRe[i] = new Knoten("B" + (i + 1));
		}

		Graph ret = new Graph("K_" + n1 + "," + n2);
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
		debug("Radius: " + radius);
		for (int i = 0; i < anzKnoten; i++) {
			int y = (int) (radius * Math.sin(i * 2 * Math.PI / anzKnoten) + radius);
			int x = (int) (radius * Math.cos(i * 2 * Math.PI / anzKnoten) + radius);
			debug("Knoten - Args: " + Arrays.toString(knoten.get(i).getArgs()));
			if (HilfString.stringArrayElementPos(knoten.get(i).getArgs(), "(") == -1) { // Keine Koordinaten vorhanden
				knoten.get(i).setArgs(HilfString.appendArray(new String[] { "(" + x + "," + y + ")", "-f" + i },
						knoten.get(i).getArgs()));
			}
			// TODO: wieder ändern Zu Testzwecken für die Textausgabe
			knoten.get(i).setArgs(
					HilfString.updateArray(knoten.get(i).getArgs(), "-T", "-T" + knotengrade.get(knoten.get(i))));
			ret.add(knoten.get(i).toStringArray());
		}
		return ret;
	}

	@Override
	public ArrayList<String[]> getKnotenVerbindungen() {
		ArrayList<String[]> ret = new ArrayList<String[]>();
		for (int i = 0; i < kanten.size(); i++) {
			Kante k = kanten.get(i);
			debug("Graph - getKnotenVerbindungen arbeitet mit Kante: " + k);
			// TODO: "-Thallo" zu Testzwecken
			k.setArgs(HilfString.appendIfNotExists(k.getArgs(), "-f", "-f" + i));
			//k.setArgs(HilfString.appendIfNotExists(k.getArgs(), "-T", "-Thallo"));
			ret.add(HilfString.appendArray(new String[] { k.getStart().getName(), k.getZiel().getName() },
					k.getArgs()));
		}
		return ret;
	}

	@Override
	public boolean execute(int command, String[] args) {
		debug("Graph-execute command: " + command + " args:" + Arrays.toString(args));
		switch (command) {
		case VollstGraph:
			try {
				Graph neu = vollstaendigenGraphK(Integer.parseInt(args[0]));
				this.kanten = neu.kanten;
				this.knoten = neu.knoten;
				this.knotengrade = neu.knotengrade;
				this.name = neu.name;
				kantenMitZufallsGewichtenBelegen();
				return true;
			} catch (Exception e) {
				System.err.println("Argumentfehler (Graph - vollst. Graph erzeugen): " + e.getMessage());
				return false;
			}
		case BipartiterGraph:
			try {
				String[] values = args[0].replaceAll("[ a-zA-Z]", "").split(",");
				Graph neu = biparitenGraphK(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
				this.kanten = neu.kanten;
				this.knoten = neu.knoten;
				this.knotengrade = neu.knotengrade;
				this.name = neu.name;
				return true;
			} catch (Exception e) {
				System.err.println("Argumentfehler (Graph - vollst. Graph erzeugen): " + e.getMessage());
				return false;
			}
			// break nicht nötig unreachable
		case LiesDatei:
			liesGraphAusString(liesTextDatei(args[0]));
			break;
		case SchreibeDatei:
			schreibeGraphInDatei(args[0]);
			break;
		case UpdateKante:
			if (args == null || args.length < 2)
				return false; // unsinnige Infos
			// Kante finden
			Kante neueKante = kanteAusStringArray(args);
			for (Kante k : kanten) {
				if (k.equals(neueKante)) {
					kanten.remove(k);
					kanten.add(neueKante);
					return true;
				}
			}
			return false;
		case UpdateKnoten:
			if (args == null || args.length < 1)
				return false; // unsinnige Infos
			// Knoten finden
			for (Knoten k : knoten) {
				if (k.getName().equals(args[0])) { // Knoten gefunden
					String[] newargs = new String[args.length - 1];
					for (int i = 0; i < newargs.length; i++)
						newargs[i] = args[i + 1];
					k.setArgs(newargs);
					return true;
				}
			}
			return false;
		case KanteLoeschen:
			Kante zuLoeschen = kanteAusStringArray(args);
			if (zuLoeschen != null) {
				kanteEntfernen(zuLoeschen);
				return true; //TODO wurde wirklich gelöscht?
			}
			return false;
		case NeueKante:
			Kante neueKante1 = kanteAusStringArray(args);
			if (neueKante1 != null) {
				// kanten.add(neueKante1);
				kanteHinzufuegen(neueKante1);
				return true;
			}
			return false;
		case LoescheKnoten:
			Knoten loeschKnoten = Knoten.gibKnotenMitName(args[0]);
			if (loeschKnoten == null)
				return false;
			// alle Kanten mit dem Knoten loeschen
			for (int i = kanten.size() - 1; i >= 0; i--) {
				if (kanten.get(i).hatKnoten(loeschKnoten)) {
					//kanten.remove(i);
					kanteEntfernen(kanten.get(i));
				}
			}
			knoten.remove(loeschKnoten);
			Knoten.entferneKnoten(loeschKnoten);
			break;
		case NeuerKnoten:
			Knoten neuerKnoten = new Knoten(args);
			knotenHinzufuegen(neuerKnoten);
			break;
		case BefehleAnmelden:
			befehleAnmelden();
			break;
		case AngemeldeterBefehl:
			//Angemeldeten Befehl ausführen
			befehlAusfuehren(args);
			break;
		default:
		}
		return false; // Keinen Befehl ausgeführt
	}

	private void kantenMitZufallsGewichtenBelegen() {
		for (Kante k: kanten) {
			k.setGewicht((new java.util.Random()).nextInt(100)+1);
			k.setArgs(HilfString.updateArray(k.getArgs(), "-T", "-T"+k.getGewicht()));
		}
	}

	private void befehlAusfuehren(String[] args) {
		ContInt.execute(ContInt.SetEnableActions, new String[] {"false"});
		if (args!=null && args.length>0) {
			if (args[0].equals("test")) {
				ContInt.execute(ContInt.InfoAusgeben, new String[] {"TestInfo","long"});
				ContInt.execute(ContInt.InfoAusgeben, new String[] {"gut gemacht :-)"});
			} else if (args[0].equals("minbaum")) {
				gibMinimalAufspannendenBaumMitView();				
			} else if (args[0].equals("kzufall")) {
				kantenMitZufallsGewichtenBelegen();
				ContInt.execute(ContInt.UpdateGraph, null);
			}
		}
		ContInt.execute(ContInt.SetEnableActions, new String[] {"true"});
	}

	private void befehleAnmelden() {
		ContInt.execute(ContInt.BefehlAnmelden, new String[] {"test","Testinfo ausgeben"});		
		ContInt.execute(ContInt.BefehlAnmelden, new String[] {"minbaum","Min. aufsp. Baum"});
		ContInt.execute(ContInt.BefehlAnmelden, new String[] {"kzufall","Kanten mit Zufallsgewichten"});
	}

	private Kante kanteAusStringArray(String[] args) {
		if (args == null || args.length < 2)
			return null;
		Knoten start = Knoten.gibKnotenMitName(args[0]);
		Knoten ziel = Knoten.gibKnotenMitName(args[1]);
		if (start == null || ziel == null)
			return null;
		Kante k = new Kante(start, ziel);
		String[] lastargs = HilfString.subArray(args, 2);
		k.setArgs(lastargs);
		return k;
	}

	// ************* Dateiaktionen ************************
	public static String liesTextDatei(String absolutePath) { // absolutePath = file.getAbsolutePath()
		try {
			FileReader fr = null;
			fr = new FileReader(absolutePath);

			BufferedReader reader = new BufferedReader(fr);

			// ArrayList<String> inhalt = new ArrayList<String>();
			StringBuffer inhalt = new StringBuffer();

			String line = reader.readLine();
			while (line != null) {
				inhalt.append(line + "\n");
				line = reader.readLine();
			}

			reader.close();
			// debug("Inhalt der Datei " + absolutePath + ": " + inhalt.toString());
			return inhalt.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void liesGraphAusString(String input) {
		try {
			// Graph wird neu Aufgesetzt
			InputStream targetStream = new ByteArrayInputStream(input.getBytes());
			InputStreamReader in = new InputStreamReader(targetStream);
			BufferedReader reader = new BufferedReader(in);

			// Variablen auf "0" setzen
			Knoten.leereAlleKnoten();
			name = "";
			knoten = new ArrayList<Knoten>();
			kanten = new ArrayList<Kante>();
			knotengrade = new HashMap<Knoten, Integer>();

			String line = reader.readLine();
			while (line != null) {
				// debug("> "+line);
				if (line.startsWith("N:")) {
					this.name = line.substring(2);
				} else if (line.startsWith("V:")) { // Knoten einlesen
					String[] liesKnoten = line.substring(2).split(";");
					debug("Neuer Knoten:" + Arrays.toString(liesKnoten));
					// knoten.add(new Knoten(liesKnoten));
					knotenHinzufuegen(new Knoten(liesKnoten));
				} else if (line.startsWith("E:")) { // Kante einlesen
					String[] liesKante = line.substring(2).split(";");
					debug("Neue Kante:" + Arrays.toString(liesKante));
					// kanten.add(kanteAusStringArray(liesKante));
					kanteHinzufuegen(kanteAusStringArray(liesKante));
				}
				line = reader.readLine();
			}

			reader.close();
			// Konsistenz prüfen Kanten in Knoten vorhanden
			// Knotengrade bestimmen
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static File chooseFileToRead() {
		// debug("Working Directory: " + System.getProperty("user.dir"));
		// debug("\n| Datei einlesen |\n");

		// JFileChooser-Objekt erstellen
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		// Dialog zum Oeffnen von Dateien anzeigen
		int rueckgabeWert = chooser.showOpenDialog(null);

		/* Abfrage, ob auf "Öffnen" geklickt wurde */
		if (rueckgabeWert == JFileChooser.APPROVE_OPTION) {
			// Ausgabe der ausgewaehlten Datei
			// debug("Die zu öffnende Datei ist: " +
			// chooser.getSelectedFile().getName());
		} else {
			System.out.println("Programm beendet - keine Datei gewählt");
			return null;
		}
		return chooser.getSelectedFile();
	}

	public void schreibeGraphInDatei(String absolutePath) {
		try {
			FileWriter fw = null;
			fw = new FileWriter(absolutePath);

			BufferedWriter writer = new BufferedWriter(fw);
			PrintWriter pwriter = new PrintWriter(writer);
			pwriter.println("N:" + this.name);
			for (Knoten k : knoten) {
				String line = "V:" + k.getName();
				for (String arg : k.getArgs()) {
					line += ";" + arg;
				}
				pwriter.println(line);
			}
			for (Kante k : kanten) {
				String line = "E:" + k.getStart().getName() + ";" + k.getZiel().getName();
				for (String arg : k.getArgs()) {
					line += ";" + arg;
				}
				pwriter.println(line);
			}
			pwriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void debug(String text) {
		if (debug)
			System.out.println("G:" + text);
	}

	private void debug(String text, boolean aktuell) {
		if (debug)
			System.out.println("G:" + text);
	}

	private void debuge(String text) {
		if (debug)
			System.err.println("G" + text);
	}
}
