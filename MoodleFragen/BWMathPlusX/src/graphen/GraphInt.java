package graphen;

import java.util.ArrayList;
import java.util.HashMap;

public interface GraphInt {
	public static final int VollstGraph = 0;  //Commando um einen vollständigen Graphen zu erzeugen im args wird eine Arraylist mit der Ordnung übergeben{"10"}
	public static final int BipartiterGraph = 1; //bipartiter Graph args = {"3,3"}
	public static final int LiesDatei = 2; //args = {"Dateiname full path"}
	public static final int SchreibeDatei = 3; //args = {"Dateiname full path"}
	public static final int UpdateKante = 6; //args = {kanteninformationen}
	public static final int UpdateKnoten = 7; //args ={knoteninformationen}
	public static final int KanteLoeschen = 8; //args = {Kanteninformationen}
	public static final int NeueKante = 9; //args = {Kanteninformationen}
	public static final int LoescheKnoten = 10; //args = {Knoteninformationen}
	public static final int NeuerKnoten = 11; //args = {Knoteninformationen}
	public static final int BefehleAnmelden = 12; // args=null
	public static final int AngemeldeterBefehl = 13; // args = {"commandstr", argumente - ggf Punkt};

	
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
	
	public boolean execute(int command, String[] args);

	
	
	
	
	
	

}
