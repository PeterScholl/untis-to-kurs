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
	 * 
	 * @param in der zu erweiternde String
	 * @return der erweiterte String
	 */
	public static String addCData(String in) {
		return "<![CDATA[" + in + "]]>";
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
		//TODO fraction=0 ist auch falsch  Hilfsfunktion die String to double konvertiert
		if (ans.getAttribute("fraction") != null && ans.getAttribute("fraction").startsWith("-")) {
			out += "-";
		} else {
			out += "+";
		}
		if (ans.getChild("text") != null) {
			out += ans.getChild("text").getContent();
		}
		if (ans.getChild("feedback")!=null && ans.getChild("feedback").getChild("text")!=null && !ans.getChild("feedback").getChild("text").getContent().equals("")) {
		  out+="#"+ans.getChild("feedback").getChild("text").getContent();
		}
		return out;

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
