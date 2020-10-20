package org.unterrichtsportal.schroedinger.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.stream.*;

public class LambdaSchuhTest {

	public static void main(String[] args) {
		List<SchuhPaar> schuhPaare = Arrays.asList(
				new SchuhPaar("schwarz", 38, true),
				new SchuhPaar("rot", 38, true),
				new SchuhPaar("rot", 39, true),
				new SchuhPaar("schwarz", 38, false),
				new SchuhPaar("weiß", 39, false)
		);
		
		Stream<SchuhPaar> gr = schuhPaare.stream()
				.filter(name -> (name.getGroesse()==39 && name.mitStoeckeln ) );
		gr.forEach(name -> System.out.println(name));	
		
		//Oder alles in "einer Zeile"
		System.out.println("Einzeilenversion:");
		schuhPaare.stream()
		  .filter(sp -> (sp.getGroesse()==39 && sp.mitStoeckeln))
		  .forEach(sp -> System.out.println(sp));
		
		//Zu jedem Schuh - einen eigenen Schuh
		System.out.println("eigene Schuhe mit map erstellen");
		schuhPaare.stream()
				.map(sp -> new SchuhPaar(sp.getFarbe(), 45, false))
				.forEach(sp -> System.out.println(sp));

	}

}
