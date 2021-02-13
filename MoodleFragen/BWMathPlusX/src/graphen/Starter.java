package graphen;

import java.util.Arrays;

public class Starter {

	public static void main(String[] args) {
		
		//Basic tests
		//System.out.println(Arrays.toString(HilfString.subArray(new String[] {"a","b","c"},1,3)));
		//System.out.println(Arrays.toString(HilfString.duplikateLoeschen(new String[] {"a","b","c","c","b","a","d","a"})));
		//System.exit(0);
		Graph g = new Graph("testgraph");
		Knoten v1 = new Knoten("start");
		Knoten v2 = new Knoten("ziel");
		Knoten v3 = null;
		try {
			v3 = new Knoten("start");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		Kante e1 = new Kante(v1, v2);
		System.out.println(e1);
		System.out.println(v3);
		g.kanteHinzufuegen(e1);
		System.out.println(g);
		Graph g2 = hausVomNikolaus();
		System.out.println(g2 + "\n mit "+g2.anzKanten() +" Kanten und "+g2.anzKnoten()+" Knoten");
		Graph g3 = g2.gibMinimalAufspannendenBaum();
		System.out.println(g3 + "\n mit "+g3.anzKanten() +" Kanten und "+g3.anzKnoten()+" Knoten");
		
		// Test auf Zusammenhaengend
		Knoten v4 = new Knoten("Extra");
		g2.knotenHinzufuegen(v4);
		System.out.println(g2+ "ist zusammenhaengend: "+g2.istZusammenhaengend());
		
		//vollst. Graph
		Graph g4 = Graph.vollstaendigenGraphK(4);
		System.out.println(g4);
		
		
		//bipartiter Graph
		Graph g5 = g2.clone();
		Kante k_t = new Kante(Knoten.gibKnotenMitName("1"),Knoten.gibKnotenMitName("2"));
		System.out.println("Testkante: "+k_t);
		g5.kanteEntfernen(k_t);	
		g5.kanteEntfernen(new Kante(Knoten.gibKnotenMitName("3"),Knoten.gibKnotenMitName("4")));
		g5.unverbundeneEinzelknotenEntfernen();
		System.out.println(g5+"\n ist bipartit: "+g5.istBipartit());
		
		//jetzt mal einen Graphen zeichnen
		Controller cont = new Controller(g5);
		cont.graphZeichnen();
		
		
	}
	
	public static Graph hausVomNikolaus() {
		Graph g = new Graph("Haus vom Nikolaus");
		Knoten k1 = new Knoten("1"); // unten Links
		Knoten k2 = new Knoten("2"); // unten Rechts
		Knoten k3 = new Knoten("3"); // mitte Links
		Knoten k4 = new Knoten("4"); // mitte Rechts
		Knoten k5 = new Knoten("5"); // oben Mitte
		g.kanteHinzufuegen(new Kante(k1,k2));
		g.kanteHinzufuegen(new Kante(k2,k4));
		g.kanteHinzufuegen(new Kante(k4,k5));
		g.kanteHinzufuegen(new Kante(k5,k3));
		g.kanteHinzufuegen(new Kante(k3,k1));
		g.kanteHinzufuegen(new Kante(k1,k4));
		g.kanteHinzufuegen(new Kante(k4,k3));
		g.kanteHinzufuegen(new Kante(k3,k2));
		return g;
	}

}
