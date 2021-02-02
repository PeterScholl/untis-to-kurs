package aufgabenGenerator;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Diese Klasse stellt den Controller zwischen dem Quiz-Generator und dem View
 * (grafische Oberfläche) dar
 * 
 * @author Peter Scholl (peter.scholl@aeg-online.de)
 *
 */
public class Controller {
	public static final int MC_lesen = 1; // Multiple-Choice-Textdatei lesen
	public static final int XML_speichern = 2; // XML-Datei speichern
	public static final int Quiz_loeschen = 3; // Quiz leeren bzw. loeschen
	public static final int XMLTemplate_lesen = 4; // Eine XML-Schablone (Textdatei) einlesen
	public static final int Datensatz_lesen = 5; // Eine XML-Schablone (Textdatei) einlesen
	public static final int Testfunktion = 6; // Eine Testfunktion, die man ggf. Nutzen kann
	public static final int XMLToQuiz = 7; // XMLzuQuiz konvertieren
	public static final int XMLToQuizDS = 8; // XMLzuQuiz konvertieren
	public static final int Question_anzeigen = 9; // Frage anzeigen
	private String status = "Programm gestartet...";

	private ArrayList<String[]> datensatz = null;
	private Quiz q = new Quiz();
	private View view = null;

	public static void main(String[] args) {
		Controller c = new Controller();
		View v = new View(c, "XML-Questiongenerator for moodle V 0.1");
		c.view = v;
	}

	public void execute(int command, String[] args) {
		switch (command) {
		case MC_lesen:
			q.append(Generator.gibQuizAusMultiChoiceDatei());
			status = "Quiz (" + q.gibAnzQuestions() + " Fragen)";
			view.switchToPanel(View.PANEL_Questions);
			break;
		case XML_speichern:
			Generator.writeQuizToXMLFile(q);
			break;
		case Quiz_loeschen:
			q = new Quiz();
			status = "Quiz (" + q.gibAnzQuestions() + " Fragen)";
			break;
		case XMLTemplate_lesen:
			// Datei lesen und darstellen
			String inhalt = Dateiaktionen.liesTextDatei();
			view.fillTextArea(inhalt);
			// Anzahl replacements feststellen
			break;
		case Datensatz_lesen:
			// Datensatz lesen - prüfen
			datensatz = Dateiaktionen.liesDatensatz();
			if (datensatz != null && datensatz.size() > 0) {
				status = "" + datensatz.size() + " Datensätze mit " + datensatz.get(0).length + " Einträgen gelesen";
				//TODO Datensatz in das Textfeld schreiben
				String inhaltdb = "";
				for (String[] s : datensatz) inhaltdb+=Arrays.toString(s)+"\n";
				view.fillDBArea(inhaltdb);
			} else {
				status = "Keine Datensätze gelesen";
			}
			break;
		case XMLToQuiz:
			XMLObject x = ManageXML.documentToXML(ManageXML.parseString(args[0]));
			q.addQuestion(x);
			status = "Quiz (" + q.gibAnzQuestions() + " Fragen)";
			break;
		case XMLToQuizDS:
			for (String[] sa : datensatz) {
				XMLObject x2 = ManageXML.documentToXML(ManageXML.parseString(Generator.replaceWithStrings(args[0], sa)));
				q.addQuestion(x2);
			}
			status = "Quiz (" + q.gibAnzQuestions() + " Fragen)";
			break;
		case Testfunktion:
			XMLObject x1 = ManageXML.documentToXML(ManageXML.parseString(args[0]));
			Question q = new Question(x1.getChild("question"));
			System.out.println(q.toString());
			break;
		case Question_anzeigen:
			new ViewQuestion(this.q.getQuestion(Integer.parseInt(args[0])));
			break;
		default:
			System.err.println("No valid command: " + command + " with args " + Arrays.deepToString(args));
		}
		updateView();
	}

	public void updateView() {
		writeStatus(status);
		view.writeList(q.questionsToStringArray());
	}

	public void writeStatus(String text) {
		view.setStatusLine(text);
	}

}
