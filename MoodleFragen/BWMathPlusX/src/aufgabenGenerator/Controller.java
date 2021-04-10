package aufgabenGenerator;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Diese Klasse stellt den Controller zwischen dem Quiz-Generator und dem View
 * (grafische Oberfläche) dar
 * 
 * @author Peter Scholl (peter.scholl@aeg-online.de)
 *
 */
public class Controller {
	public static String curfilename = "";
	public static final int MC_lesen = 1; // Multiple-Choice-Textdatei lesen
	public static final int XML_speichern = 2; // XML-Datei speichern
	public static final int Quiz_loeschen = 3; // Quiz leeren bzw. loeschen
	public static final int XMLTemplate_lesen = 4; // Eine XML-Schablone (Textdatei) einlesen
	public static final int Datensatz_lesen = 5; // Eine csv-Datenbank lesen
	public static final int Testfunktion = 6; // Eine Testfunktion, die man ggf. Nutzen kann
	public static final int XMLToQuiz = 7; // XMLzuQuiz konvertieren
	public static final int XMLToQuizDS = 8; // XMLzuQuiz konvertieren
	public static final int Question_anzeigen = 9; // Frage anzeigen
	public static final int Delete_Questions = 10; // Fragen die als String[] übergeben wurden werden gelöscht
	public static final int MCToQuiz = 11; // MultipleChoice to Quiz konvertieren
	public static final int MCBeispielAusgeben = 12; // Der MultipleChoiceView wird mit einer Vorlage befüllt
	public static final int QuestionToXML = 13; // Frage in XML darstellen
	public static final int QuizToXML = 14; // quiz in XML darstellen
	public static final int QuizToMC = 15; // quiz als Multiple-Choice-Datei darstellen
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
			File file = Dateiaktionen.chooseFileToRead();
			curfilename = (file != null ? file.getAbsolutePath() : "");
			if (file != null) {
				String inhaltMC = Dateiaktionen.liesTextDatei(file);
				view.fillMCArea(inhaltMC);
				view.switchToPanel(View.PANEL_MultiChoice);
			}
			break;
		case MCBeispielAusgeben:
			view.fillMCArea(Hilfsfunktionen.gibBeispielMCText());
			view.switchToPanel(View.PANEL_MultiChoice);
			break;
		case MCToQuiz:
			q.append(Generator.gibQuizAusMultiChoiceString(args[0]));
			status = "Quiz (" + q.gibAnzQuestions() + " Fragen)";
			view.switchToPanel(View.PANEL_Questions);
			break;
		case QuizToMC:
			view.fillMCArea(q.toMCString());
			status = "Quiz in MultiChoice-Datei konvertiert";
			view.switchToPanel(View.PANEL_MultiChoice);
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
			String inhalt = Dateiaktionen.liesTextDatei(Dateiaktionen.chooseFileToRead());
			view.fillTextArea(inhalt);
			// Anzahl replacements feststellen
			break;
		case Datensatz_lesen:
			// Datensatz lesen - prüfen
			String inhaltdbfile = Dateiaktionen.liesTextDatei(Dateiaktionen.chooseFileToRead());
			view.fillDBArea(inhaltdbfile);
			view.switchToPanel(View.PANEL_Database);
			// zur Kontrolle des Inhalts
			datensatz = Dateiaktionen.liesDatensatz(inhaltdbfile);
			if (datensatz != null && datensatz.size() > 0) {
				status = "" + datensatz.size() + " Datensätze mit " + datensatz.get(0).length + " Einträgen gelesen";
				/*
				 * - Datensatz in String konvertieren String inhaltdb = ""; for (String[] s :
				 * datensatz) inhaltdb+=Arrays.toString(s)+"\n"; view.fillDBArea(inhaltdb);
				 */
			} else {
				status = "Keine Datensätze gelesen";
			}
			break;
		case XMLToQuiz:
			XMLObject x = ManageXML.documentToXML(ManageXML.parseString(args[0]));
			q.addQuestion(x);
			status = "Quiz (" + q.gibAnzQuestions() + " Fragen)";
			view.switchToPanel(View.PANEL_Questions);
			break;
		case XMLToQuizDS:
			datensatz = Dateiaktionen.liesDatensatz(args[1]);
			System.out.println("Datensatzgroesse: " + datensatz.size());
			if (datensatz != null && datensatz.size() > 0) {
				System.out.println(
						"" + datensatz.size() + " Datensätze mit " + datensatz.get(0).length + " Einträgen gelesen");
				for (String[] sa : datensatz) {
					XMLObject x2 = ManageXML
							.documentToXML(ManageXML.parseString(Generator.replaceWithStrings(args[0], sa)));
					q.addQuestion(x2);
				}
				status = "Quiz (" + q.gibAnzQuestions() + " Fragen)";
				view.switchToPanel(View.PANEL_Questions);
			} else {
				status = "Keine Datensätze gelesen";
			}
			break;
		case Delete_Questions:
			fragenAusListeLoeschen(args);
			status = "" + args.length + " Fragen geloescht!";
			break;
		case Testfunktion:
			Dateiaktionen.readTextInAllAvailableCharsetsAndPrint();
			break;
		case Question_anzeigen:
			if (args != null && args.length > 0) {
				Question q2 = this.q.getQuestion(Integer.parseInt(args[0]));
				if (q2 != null)
					new ViewQuestion(q2);
			}

			break;
		case QuestionToXML:
			int nr = Integer.parseInt(args[0]);
			Question q = this.q.getQuestion(nr);
			if (q != null) {
				view.fillTextArea(q.toString());
				view.switchToPanel(View.PANEL_XMLtemplate);
			}
			break;
		case QuizToXML:
			view.fillTextArea(this.q.toString());
			view.switchToPanel(View.PANEL_XMLtemplate);
			break;
		default:
			System.err.println("No valid command: " + command + " with args " + Arrays.deepToString(args));
		}
		updateView();
	}

	private void fragenAusListeLoeschen(String[] args) {
		ArrayList<Integer> indizes = new ArrayList<Integer>();
		for (int i : Hilfsfunktionen.stringArrayToIntArray(args))
			indizes.add(i);
		Collections.sort(indizes);
		for (int i = indizes.size(); i > 0; i--) {
			System.out.println("Delete item " + indizes.get(i - 1));
			q.deleteQuestion(indizes.get(i - 1));
		}

	}

	public void updateView() {
		writeStatus(status);
		view.writeList(q.questionsToStringArray());
	}

	public void writeStatus(String text) {
		view.setStatusLine(text);
	}

}
