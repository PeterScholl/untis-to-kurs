package graphen;

import java.util.ArrayList;
import java.util.HashMap;

public interface AbstrGraph {

	/**
	 * Liefert die Kntoenliste in der Form name, punkt als String[], z.B. {"Berlin","(1,1)"}
	 * weitere Informationen können folgen
	 * @return Liste mit Knotennamen und Punkten (mindestens)
	 */
	abstract ArrayList<String[]> getKnotenPunkte();

	/**
	 * Liefert die Kanten eines Graphen in Form knotenname,knotenname
	 * @return Liste mit den Verbundenen Knoten
	 */
	abstract ArrayList<String[]> getKnotenVerbindungen();
	
	
	
	
	
	

}
