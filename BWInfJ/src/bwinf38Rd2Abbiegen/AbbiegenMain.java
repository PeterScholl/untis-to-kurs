package bwinf38Rd2Abbiegen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

public class AbbiegenMain {

	public static void main(String[] args) throws NumberFormatException, IOException {
		System.out.println("Working Directory: " + System.getProperty("user.dir"));
		System.out.println("\n| Datei einlesen |\n");
		
        // JFileChooser-Objekt erstellen
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        // Dialog zum Oeffnen von Dateien anzeigen
        int rueckgabeWert = chooser.showOpenDialog(null);
        
        /* Abfrage, ob auf "Öffnen" geklickt wurde */
        if(rueckgabeWert == JFileChooser.APPROVE_OPTION)
        {
             // Ausgabe der ausgewaehlten Datei
            System.out.println("Die zu öffnende Datei ist: " +
                  chooser.getSelectedFile().getName());
        } else {
        	System.out.println("Programm beendet - keine Datei gewählt");
        	return;
        }

		//FileReader fr = new FileReader("src/bwinf38Rd2Abbiegen/"+args[0]);
        FileReader fr = new FileReader(chooser.getSelectedFile().getAbsolutePath());

        BufferedReader reader = new BufferedReader(fr);
		
		
		Graph g = new Graph();

		int anzKanten = Integer.parseInt(reader.readLine());
		Punkt start = new Punkt(reader.readLine());
		g.setQuelle(start);
		g.addEcke(start);
		Punkt stop = new Punkt(reader.readLine());
		g.setZiel(stop);
		g.addEcke(stop);
		for (int i = 0; i < anzKanten; i++) {
			g.addKante(new Kante(reader.readLine()));
		}
		
		
		reader.close();
		
		g.findShortestPath();
		System.out.println(g);
		System.out.println(g.gibMaxXKoord());
		
		// hier soll der Graph gezeichnet werden
		Turtle t = new Turtle(1200, 300); //Turtle erzeugen - mit Graphikbildschirm (x-Länge x y-Länge)
		// System.out.println(t.getHeight()); //Zum Testen 
		ArrayList<Kante> kanten = g.getKanten(); //Liste aller Kanten holen
		int xmax = g.gibMaxXKoord(); // Jeweils maximale Koordinaten bestimmen (Ecken des Bildes)
		int xmin = g.gibMinXKoord();
		int ymax = g.gibMaxYKoord();
		int ymin = g.gibMinYKoord();
		System.out.println("kleinster Punkt: ("+xmin+","+ymin+") - groesster Punkt: ("+xmax+","+ymax+")");
		double xstep = xmax != xmin ? (t.getMaxX()-20.0)/(xmax-xmin):0; //Schrittweite bestimmen Pixel pro x
		double ystep = ymax != ymin ? (t.getMaxY()-20.0)/(ymax-ymin):0;
		double radius = Math.min(5,Math.min(ystep/8, xstep/8)); //Radius eines Punktes
		for (Kante k : kanten) { //alle Kanten Zeichnen
			Punkt p1 = k.getStart();
			Punkt p2 = k.getStop();
			t.hebeStift();
			//Zum Startpunkt gehen
			t.geheNach(10+(p1.x-xmin)*xstep, t.getMaxY()-(10+(p1.y-ymin)*ystep));
			t.senkeStift();
			t.fillCircle(radius); //Dort einen Punkt zeichnen
			// Linie zum Zielpunkt ziehen
			t.geheNach(10+(p2.x-xmin)*xstep, t.getMaxY()-(10+(p2.y-ymin)*ystep));
			t.fillCircle(radius);
		}
		
		//den kürzesten Pfad zeichnen
		kanten = g.gibKuerzestenPfad();
		t.setColor(Turtle.green);
		for (Kante k : kanten) {
			Punkt p1 = k.getStart();
			Punkt p2 = k.getStop();
			t.hebeStift();
			t.geheNach(10+(p1.x-xmin)*xstep, t.getMaxY()-(10+(p1.y-ymin)*ystep));
			t.senkeStift();
			t.fillCircle(radius);
			t.geheNach(10+(p2.x-xmin)*xstep, t.getMaxY()-(10+(p2.y-ymin)*ystep));			
			t.fillCircle(radius);
		}
		
		// kürzesten Weg ausgeben
		System.out.println(g.getKuerzesterWeg());
		
		// optimalen Weg mit weniger Abbiegen
		Weg opt = g.findeWegMinimalAbbiegen(1.2);
		System.out.println(opt);
		// diesen Weg zewichen
		t.setColor(Turtle.red);
		ArrayList<Punkt> punkte = opt.getPunkte();
		for (int i=0; i<punkte.size()-1; i++) {
			Punkt p1 = punkte.get(i);
			Punkt p2 = punkte.get(i+1);
			t.hebeStift();
			t.geheNach(10+(p1.x-xmin)*xstep, t.getMaxY()-(10+(p1.y-ymin)*ystep));
			t.senkeStift();
			t.fillCircle(radius/2);
			t.geheNach(10+(p2.x-xmin)*xstep, t.getMaxY()-(10+(p2.y-ymin)*ystep));			
			t.fillCircle(radius/2);
		}
		
		
		
		
		
		
		//test();

	}
	
	public static void test() {
		Punkt p = new Punkt("(0,1)");
		System.out.println(p);
		Kante k = new Kante("(1,2) (4,5)");
		System.out.println(k+" hat length "+k.length());
		
	}

}
