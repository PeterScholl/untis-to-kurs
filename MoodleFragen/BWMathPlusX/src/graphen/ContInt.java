package graphen;

public class ContInt {
	private static Controller controller = null;
	public static final int SetEnableActions = 0;  //Interaktionen im View (de)aktivieren args={"true|false"}
	public static final int UpdateGraph = 1; //Graph neu Laden
	public static final int InfoAusgeben = 2; //args = {"Info",<"long">,<"wait">}  //ohne long - eine Zeile - long ist Text mit Wait - 
	public static final int BefehlAnmelden = 3; //args = {"commandstring", "HumanReadable", <"punkt">} - wenn ein Punkt benötigt wird
	public static final int StringErfragen = 4; //args = {"frage", "titel","vorgabe"}
	
	public static void controllerAnmelden(Controller c) {
		ContInt.controller=c;
	}
	
	public static boolean execute(int command, String[] args) {
		if (controller!= null) {
			switch(command) {
			case SetEnableActions:
				controller.execute(Controller.SetEnableViewActions, args);
				break;
			case UpdateGraph:
				controller.execute(Controller.GraphDatenNeuLaden, args);
				break;
			case InfoAusgeben:
				controller.execute(Controller.InfoAusgeben, args);
				break;
			case BefehlAnmelden:
				controller.execute(Controller.BefehlAnmelden, args);
				break;
			case StringErfragen:
				controller.execute(Controller.StringErfragen, args);
				break;
			default:
			}
		}
		return true;
	}
	
	public static String[] getResult() {
		if (controller!=null) {
			return controller.getResult();
		}
		return null;		
	}
	
}
