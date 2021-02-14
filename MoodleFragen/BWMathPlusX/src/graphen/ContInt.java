package graphen;

public class ContInt {
	private static Controller controller = null;
	public static final int SetEnableActions = 0;  //Interaktionen im View (de)aktivieren args={"true|false"}
	public static final int UpdateGraph = 1; //Graph neu Laden
	public static final int InfoAusgeben = 2; //args = {"Info",<"long">}  //ohne long - eine Zeile - long ist Text mit Wait

	public static void controllerAnmelden(Controller c) {
		ContInt.controller=c;
	}
	
	public static boolean execute(int command, String[] args) {
		if (controller!= null) {
			switch(command) {
			case SetEnableActions:
				controller.execute(Controller.SetEnableViewActions, args);
				break;
			default:
			}
		}
		return true;
	}
	
}
