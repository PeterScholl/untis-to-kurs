package bwinf38Rd2Abbiegen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AbbiegenMain {

	public static void main(String[] args) throws NumberFormatException, IOException {
		System.out.println("Working Directory: " + System.getProperty("user.dir"));
		System.out.println("\n| Datei einlesen |\n");

		FileReader fr = new FileReader("src/bwinf38Rd2Abbiegen/"+args[0]);
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
		
		// ab hier soll der Graph gezeichnet werden
		Turtle t = new Turtle(1200, 300);
		System.out.println(t.getHeight());
		ArrayList<Kante> kanten = g.getKanten();
		int xmax = g.gibMaxXKoord();
		int xmin = g.gibMinXKoord();
		int ymax = g.gibMaxYKoord();
		int ymin = g.gibMinYKoord();
		System.out.println("kleinster Punkt: ("+xmin+","+ymin+") - groesster Punkt: ("+xmax+","+ymax+")");
		double xstep = xmax != xmin ? (t.getMaxX()-20.0)/(xmax-xmin):0;
		double ystep = ymax != ymin ? (t.getMaxY()-20.0)/(ymax-ymin):0;
		double radius = Math.min(5,Math.min(ystep/8, xstep/8));
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
		
		
		
		
		
		//test();

	}
	
	public static void test() {
		Punkt p = new Punkt("(0,1)");
		System.out.println(p);
		Kante k = new Kante("(1,2) (4,5)");
		System.out.println(k+" hat length "+k.length());
		
	}

}
