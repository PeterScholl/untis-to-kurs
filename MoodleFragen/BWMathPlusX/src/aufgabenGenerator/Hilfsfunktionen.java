package aufgabenGenerator;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Hilfsfunktionen {
	/**
	 * Zentriert das übergebene Fenster auf dem Bildschirm
	 * 
	 * @param fenster
	 */
	public static void fensterZentrieren(JFrame fenster) {

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

		int x = dimension.width;
		int y = dimension.height;

		int fensterX = (x - fenster.getWidth()) / 2;
		int fensterY = (y - fenster.getHeight()) / 2;

		fenster.setLocation(fensterX, fensterY);
	}

	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		@SuppressWarnings("rawtypes")
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}

	public static int sindSieSicher(JFrame root, String frage) {
		// Custom button text
		Object[] options = { "Ja", "Abbrechen" };
		int n = JOptionPane.showOptionDialog(root, frage, "Sind Sie sicher?", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		return n;
	}

	public static int[] stringArrayToIntArray(String[] args) {
		int[] ret = new int[args.length];
		for (int i = 0; i < args.length; i++) {
			try {
				ret[i] = Integer.parseInt(args[i]);
			} catch (Exception e) {
				System.err.println("Could not parse to Int " + args[i] + "\n" + e.getMessage());
			}
		}
		return ret;
	}

	/**
	 * Fügt dem übergebenen String zu Begin <![CDATA[ und am Ende ]]> hinzu
	 * @param in der zu erweiternde String
	 * @return der erweiterte String
	 */
	public static String addCData(String in) {
		return "<![CDATA[" + in + "]]>";
	}

	/**
	 * Entfernt ein möglicherweise vorhandenes CDATA-Tag aus dem übergebenen String
	 * @param in der möglicherweise zu bereinigende String
	 * @return der bereinigte String
	 */
	public static String removeCData(String in) {
		if (in.startsWith("<![CDATA[")) {
			return in.replaceAll("^<!\\[CDATA\\[","").replaceAll("\\]\\]>$", "");
		}
		return in;
	}
	
	
	/**
	 * Zu einem übergebenen Objekt vom Typ Answer, wird ein String erzeugt, wie er
	 * zu dieser Antwort in einer MultiplieChoice-Text-Datei stehen würde
	 * 
	 * @param ans das XML-Objekt vom Typ answer
	 * @return der zugehörige String
	 */
	public static String mcanswerToMcText(XMLObject ans) {
		if (ans == null || !ans.getBezeichnung().equals("answer"))
			return "";
		String out = "";
		if (Hilfsfunktionen.getFractionOfAnswerObject(ans)<=0) {
			out += "-";
		} else {
			out += "+";
		}
		if (ans.getChild("text") != null) {
			out += removeCData(ans.getChild("text").getContent());
		}
		if (ans.getChild("feedback") != null && ans.getChild("feedback").getChild("text") != null
				&& !ans.getChild("feedback").getChild("text").getContent().equals("")) {
			out += "#" + removeCData(ans.getChild("feedback").getChild("text").getContent());
		}
		return out;

	}

	/**
	 * liefert zu einem XML-Objekt vom Typ answer den double-Wert des eingetragenen
	 * fraction-Attributes zurück (bei Fehler oder fehlendem Objekt wird 0.0 zurückgeliefert) 
	 * @param ans - das XML-Objekt vom Typ answer
	 * @return der Wert des Attributes fraktion
	 */
	public static double getFractionOfAnswerObject(XMLObject ans) {
		if (ans == null || !ans.getBezeichnung().equals("answer"))
			return 0.0;
		String frac = ans.getAttribute("fraction");
		try {
			return Double.parseDouble(frac);
		} catch (Exception e) {
			System.err.println("Konnte fraction nicht in double umwandeln " + e.getMessage());
		}
		return 0.0;
	}

	public static String gibBeispielMCText() {
		String out = "# Format einer Multiple-Choice-Frageliste\n" + "# Kommentar!\n"
				+ "Hier steht die Frage bzw. die Aufgabe - Wähle die richtigen Antworten\n"
				+ "& (optional) Name der Frage (wenn diese Zeile Fehlt - wird die Frage als Name verwendet)\n"
				+ "+richtige Antwort\n"
				+ "+noch eine richtige Antwort#Feedback zu dieser Antwort (erscheint wenn sie ausgewählt wurde)\n"
				+ "-falsche Antwort#Feedback folgt wie oben nach der Raute (#)\n"
				+ "#generalFeedback (die letzte dieser mit # beginnenden Zeilen ist gültig)\n" + "	 \n"
				+ "# (Leerzeile beendet die Frage  - nächste Frage folgt)";
		return out;
	}
}
