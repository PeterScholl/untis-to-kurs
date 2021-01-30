package aufgabenGenerator;

import java.util.Arrays;

/**
 * Diese Klasse stellt den Controller zwischen dem Quiz-Generator und dem View (grafische Oberfläche) dar
 * 
 * @author Peter Scholl (peter.scholl@aeg-online.de)
 *
 */
public class Controller {
	public static final int MC_lesen = 1; //Multiple-Choice-Textdatei lesen
	public static final int XML_speichern = 2; //XML-Datei speichern
	public static final int Quiz_loeschen = 3; //Quiz leeren bzw. loeschen
	private Quiz q = new Quiz();
	private View view = null;

	public static void main(String[] args) {
		Controller c = new Controller();
		View v = new View(c, "XML-Questiongenerator for moodle V 0.1");
		c.view=v;
	}
	
	public void execute(int command, String[] args) {
		switch(command) {
		case MC_lesen:
			q.append(Generator.gibQuizAusMultiChoiceDatei());
			updateView();
			break;
		case XML_speichern:
			Generator.writeQuizToXMLFile(q);
			break;
		case Quiz_loeschen:
			q = new Quiz();
			updateView();
			break;
		default:
			System.err.println("No valid command: "+command+" with args "+Arrays.deepToString(args));				
		}
	}
	
	public void updateView() {
		String status = "Quiz ("+q.gibAnzQuestions()+" Fragen)";
		writeStatus(status);
		view.writeList(q.questionsToStringArray());
	}
	
	public void writeStatus(String text) {
		view.setStatusLine(text);
	}

}
