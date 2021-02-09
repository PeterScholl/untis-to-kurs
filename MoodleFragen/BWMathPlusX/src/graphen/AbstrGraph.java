package graphen;

import java.util.ArrayList;
import java.util.HashMap;

public interface AbstrGraph {

	/**
	 * Liefert die Kntoenliste in der Form name, punkt als String, z.B. Berlin,(1,1)
	 * @return HashMap mit Knotennamen und Punkten
	 */
	abstract HashMap<String, String> getKnotenPunkte();

	/**
	 * Liefert die Kanten eines Graphen in Form knotenname,knotenname
	 * @return Hashmap mit den Verbundenen Knoten
	 */
	abstract ArrayList<String[]> getKnotenVerbindungen();
	
	
	
	
	
	

}
