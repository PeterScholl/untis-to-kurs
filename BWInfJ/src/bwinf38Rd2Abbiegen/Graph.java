package bwinf38Rd2Abbiegen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Stack;
import java.util.stream.Stream;

public class Graph {
	Double shortestPathLength = Double.NaN;
	ArrayList<Punkt> ecken;
	ArrayList<Kante> kanten;
	Punkt quelle, ziel;
	HashMap<Punkt, PunktInfo> haengend = new HashMap<Punkt, PunktInfo>(); // kürzester Pfad
	Weg kuerzesterWeg;

	class PunktInfo {
		Punkt von;
		Double wert = Double.NaN;

		public PunktInfo(Punkt von, Double wert) {
			this.von = von;
			this.wert = wert;
		}
	}

	public Graph() {
		ecken = new ArrayList<Punkt>();
		kanten = new ArrayList<Kante>();
	}

	public void addKante(Kante k) {
		kanten.add(k);
		addEcke(k.getStart());
		addEcke(k.getStop());
	}

	public void addEcke(Punkt p) {
		if (!ecken.contains(p))
			ecken.add(p);
	}

	public void findShortestPath() {
		Punkt minP = null;
		HashMap<Punkt, PunktInfo> alle = new HashMap<Punkt, PunktInfo>();
		ArrayList<Punkt> vorrat = new ArrayList<Punkt>();
		for (Punkt p : this.ecken) {
			if (p.equals(ziel)) {
				PunktInfo pi = new PunktInfo(null, Double.NaN);
				pi.wert = 0.0;
				alle.put(p, pi);
				minP = p;
			} else {
				alle.put(p, new PunktInfo(null, Double.NaN));
			}
			vorrat.add(p);
		}
		haengend = new HashMap<Punkt, PunktInfo>();
		while (minP != null) {
			// min aus vorat entfernen und in haengend aufnehmen
			Double wert = alle.get(minP).wert;
			haengend.put(minP, alle.get(minP));
			alle.remove(minP);
			vorrat.remove(minP);
			// update Infos der Punkte in alle
			for (Punkt p : vorrat) {
				if (kanten.contains(new Kante(minP, p))) {
					if (alle.get(p).wert.isNaN() || (wert + new Kante(minP, p).length() < alle.get(p).wert)) {
						alle.put(p, new PunktInfo(minP, wert + new Kante(minP, p).length()));
					}
				}
			}
			// neues Minimum finden
			minP = null;
			Double minW = Double.MAX_VALUE;
			for (Punkt p : vorrat) {
				if (!alle.get(p).wert.isNaN() && alle.get(p).wert < minW) {
					minP = p;
					minW = alle.get(p).wert;
				}
			}
		}
		if (haengend.get(quelle) != null) {
			shortestPathLength = haengend.get(quelle).wert;
			Punkt aktpkt = quelle;
			kuerzesterWeg = new Weg();
			kuerzesterWeg.addPunkt(aktpkt);
			while (haengend.get(aktpkt).von != null) {
				aktpkt = haengend.get(aktpkt).von;
				kuerzesterWeg.addPunkt(aktpkt);
			}
		}

	}

	public Weg findeWegMinimalAbbiegen(double faktor) {
		Weg result = kuerzesterWeg.clone();
		Stack<Weg> s = new Stack<Weg>();
		Weg w = new Weg();
		w.addPunkt(quelle);
		s.push(w);
		while (!s.isEmpty()) {
			Weg aWeg = s.pop();
			if (aWeg.hatPunkt(ziel)) { // Möglicher Kandidat für optimalen Weg
				if (aWeg.anzAbbiegen() < result.anzAbbiegen() && aWeg.laenge() <= kuerzesterWeg.laenge() * faktor) {
					// neues Optimum gefunden
					result = aWeg.clone();
				} else if (aWeg.anzAbbiegen() == result.anzAbbiegen() && aWeg.laenge() < result.laenge()) {
					// Weglaenge verbessert
					result = aWeg.clone();
				}
			} else {
				// Ziel noch nicht erreicht
				// Prüfen ob dieser Weg noch sinnvoll ist
				Punkt pos = aWeg.gibStandpunkt();
				if (aWeg.laenge() <= kuerzesterWeg.laenge() * faktor
						&& aWeg.anzAbbiegen() <= result.anzAbbiegen()) {
					for (Kante k : kanten) { // alle Kanten durchlaufen
						Punkt npos = k.gibZiel(pos); // wenn von pos ein anderer Punkt erreicht werden kann
						if (npos != null) {
							if (!aWeg.hatPunkt(npos)) {
								Weg t = aWeg.clone();
								t.addPunkt(npos);
								s.push(t);
							}
						}
					}
				}
			}
		}
		return result;
	}

	public int gibMinXKoord() {
		Stream<Kante> kantenStream = kanten.stream();
		return kantenStream.map(x -> x.getMinXKoord()).min(Comparator.naturalOrder()).get();
	}

	public int gibMaxXKoord() {
		Stream<Kante> kantenStream = kanten.stream();
		return kantenStream.map(x -> x.getMaxXKoord()).max(Comparator.naturalOrder()).get();
	}

	public int gibMinYKoord() {
		Stream<Kante> kantenStream = kanten.stream();
		return kantenStream.map(x -> x.getMinYKoord()).min(Comparator.naturalOrder()).get();
	}

	public int gibMaxYKoord() {
		Stream<Kante> kantenStream = kanten.stream();
		return kantenStream.map(x -> x.getMaxYKoord()).max(Comparator.naturalOrder()).get();
	}

	public ArrayList<Kante> gibKuerzestenPfad() {
		// TODO: Fehler abfangen, z.B. haengend ist leer oder ähnlich
		ArrayList<Kante> erg = new ArrayList<Kante>();
		Punkt pos = quelle;
		while (!pos.equals(ziel)) {
			Punkt npos = haengend.get(pos).von;
			erg.add(new Kante(pos, npos));
			pos = npos;
		}
		return erg;
	}

	/**
	 * @param quelle the quelle to set
	 */
	public void setQuelle(Punkt quelle) {
		this.quelle = quelle;
	}

	/**
	 * @param ziel the ziel to set
	 */
	public void setZiel(Punkt ziel) {
		this.ziel = ziel;
	}

	public ArrayList<Kante> getKanten() {
		return kanten;
	}

	public Weg getKuerzesterWeg() {
		return kuerzesterWeg;
	}

	@Override
	public String toString() {
		return "Start: " + quelle + " Ziel:" + ziel + "\nGraph\n ecken:\n  " + ecken + "\n kanten:\n  " + kanten
				+ "\nshorestPathlength: " + shortestPathLength;
	}

}
