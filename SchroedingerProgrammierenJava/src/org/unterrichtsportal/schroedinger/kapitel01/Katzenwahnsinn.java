package org.unterrichtsportal.schroedinger.kapitel01;

import java.util.ArrayList;
import java.util.List;
/**
 * Diese Klasse dient dazu das Verhaltens des Heaps zu beobachten,
 * also die Speichernuztung. Dies ist sehr anschaulich erklärt in 
 * dem Buch "Schrödinger programmiert Java" S. 215 ff
 * 
 * Wichtig: Unter Run->RunConfigurations muss im Reiter Main ganz 
 * unten als Launcher ide Java Visual VM gewählt werden.
 * 
 * Einfach die Java VisualVM starten und beobachten, was passiert
 * Also das entsprechende Paket auswählen und auf Monitor schauen.
 * 
 * Interessant sind dann die VirtualMachine Arguments
 * -Xmx200m (mit dem man den maximalen Heap auf 200MB festlegen kann)
 * oder
 * -Xms75m (mit dem man den minimalen Heap auf 75m festlegen kann)
 * @author peter
 *
 */
public class Katzenwahnsinn {
	private static List<Katze> tierschuetzer = new ArrayList<>();

	public static void main(String[] args) {
		for (int i = 0; i<20;i++) {
			machKatzen(100000);
			warten(3000);
		}
	}
	
	private static void machKatzen(int anzahl) {
		Katze last = new Katze("last");
		for(int i=0; i<anzahl; i++) {
			Katze katze = new Katze("Katze "+i, last);
			tierschuetzer.add(katze);
			last = katze;
		}
	}
	
	private static void warten(long millisekunden) {
		try {
			Thread.sleep(millisekunden);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
