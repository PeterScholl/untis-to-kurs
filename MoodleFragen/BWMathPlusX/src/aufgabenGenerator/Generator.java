package aufgabenGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Generator {
	private static java.util.Random rand = new java.util.Random();
	private static String filename="";


	/**
	 * Erzeugt ein XMLObject vom Typ answer, dass als Antwort bei verschiedenen Fragetypen verwendet werden kann
	 * @param text Antworttext
	 * @param fraction Wert der Antwort als double (100 entspricht richtig)
	 * @param format format der Antwort, z.B. "moodle auto format" oder "html"
	 * @param feedback Feedbacktext zu dieser Antwort (in html)
	 * @return XMLObject vom Typ answer
	 */
	public static XMLObject generateAnswer(String text, double fraction, String format, String feedback) {
		XMLObject answer = new XMLObject("answer");
		if (format==null || format.equals("")) format="moodle auto format";
		if (fraction >= 0) answer.addAttribute("fraction", String.format(Locale.ENGLISH, "%.4f", fraction));
		answer.addAttribute("format", format);
		answer.addChild(new XMLObject("text", text));
		XMLObject xfeedback = new XMLObject("feedback");
		xfeedback.addAttribute("format", "html");
		xfeedback.addChild(new XMLObject("text",feedback));
		answer.addChild(xfeedback);
		return answer;
	}
	
	/**
	 * Erzeugt eine Short-Answer-Frage - weitere Antworten können mit addAnswer ergänzt werden
	 * @param text Text der Frage
	 * @param name Name der Frage (wenn null oder "" wird dafür auch der Fragetext verwendet)
	 * @param answer eine richtige Antwort (Mit * als wildcard)
	 * @return eine ShortAnswerQuestion
	 */
	public static Question generateShortAnswer(String text, String name, String answer) {
		Question q = new Question(Question.shortanswer);
		q.setQuestiontext(text);
		if (name==null || name.equals("")) name=text;
		q.setName(name);
		q.addXMLObject(new XMLObject("usecase", "0")); //Case-in-sensitive with value 0
		XMLObject xanswer = generateAnswer(answer, 100, "moodle auto format", "");
		q.addAnswer(xanswer);
		return q;
	}
	
	/**
	 * Erzeugt eine shortanswer-Frage vom Typ: Gib die Dualzahl zur Hexadezimalzahl wert an.
	 * @param wert
	 * @return die Frage
	 */
	public static Question erzQDualzahlZuHexzahlAngeben(int wert) {
		String hex = Integer.toHexString(wert).toUpperCase();
		String dual = Integer.toBinaryString(wert);
		while (dual.length()<8) dual = "0"+dual;
		Question q= generateShortAnswer("Gib die Dualzahl zur Hexadezimalzahl ("+hex+")<sub>16</sub> an.", "Gib die Dualzahl zur Hexadezimalzahl "+hex+" an.", dual);
		q.setGeneralfeedback("Die erste Hex-Ziffer entspricht den ersten 4 Ziffern der Dualzahl usw.");
		return q;
	}

	/**
	 * Erzeugt eine shortanswer-Frage vom Typ: Gib die Hexadezimalzahl zur Dualzahl wert an. 
	 * @param wert
	 * @return die Frage
	 */
	public static Question erzQHexzahlZuDualzahlAngeben(int wert) {
		String hex = Integer.toHexString(wert).toUpperCase();
		String dual = Integer.toBinaryString(wert);
		while (dual.length()<8) dual = "0"+dual;
		Question q= generateShortAnswer("Gib die Hexadezimalzahl zur Dualzahl ("+dual+")<sub>2</sub> an.", "Gib die Hexzahl zur Dualzahl "+dual+" an.", hex);
		q.setGeneralfeedback("Die erste Hex-Ziffer entspricht den ersten 4 Ziffern der Dualzahl usw.");
		return q;
	}

	/**
	 * Erzeugt eine truefalse-Frage vom Typ: Ist zahl durch divisor teilbar?
	 * @param zahl
	 * @param divisor
	 * @return die Frage
	 */
	public static Question erzIstTeilbar(int zahl, int divisor) {
		Question ques = new Question(Question.truefalse);
		ques.setName("Ist "+zahl+" durch "+divisor+" teilbar?");
		ques.setQuestion_format("html");
		ques.setQuestiontext("Ist "+zahl+" durch "+divisor+" teilbar?");
		boolean teilbar = false;
		if (zahl%divisor==0) teilbar = true;
		XMLObject answer = new XMLObject("answer");
		XMLObject atext = new XMLObject("text", "true");
		answer.addChild(atext);
		answer.addAttribute("fraction", (teilbar?"100":"0"));
		answer.addAttribute("format", "moodle auto format");
		ques.addAnswer(answer);
		answer = new XMLObject("answer");
		atext = new XMLObject("text", "false");
		answer.addChild(atext);
		answer.addAttribute("fraction", (teilbar?"0":"100"));
		answer.addAttribute("format", "moodle auto format");
		ques.addAnswer(answer);
		ques.setPenalty("1.0000000");
		return ques;
	}

	/**
	 * Erzeugt eine truefalse-Frage vom Typ: Ist zahl eine Primzahl?
	 * @param zahl
	 * @return die Frage
	 */
	public static Question erzIstXPrimzahl(int zahl) {
		Question ques = new Question(Question.truefalse);
		ques.setName("Ist "+zahl+" eine Primzahl?");
		ques.setQuestion_format("html");
		ques.setQuestiontext("Ist "+zahl+" eine Primzahl?");
		int primteiler = kleinsterPrimTeiler(zahl);
		boolean teilbar = false;
		if (primteiler>0) teilbar = true;
		XMLObject answer = new XMLObject("answer");
		XMLObject atext = new XMLObject("text", "true");
		answer.addChild(atext);
		answer.addAttribute("fraction", (teilbar?"0":"100"));
		answer.addAttribute("format", "moodle auto format");
		//feedback einfügen
		if (primteiler>0) {
			XMLObject afeedback = new XMLObject("feedback");
			afeedback.addAttribute("format", "moodle auto format");
			afeedback.addChild(new XMLObject("text",""+zahl+" ist durch "+primteiler+" teilbar"));
			answer.addChild(afeedback);
		}
		ques.addAnswer(answer);
		answer = new XMLObject("answer");
		atext = new XMLObject("text", "false");
		answer.addChild(atext);
		answer.addAttribute("fraction", (teilbar?"100":"0"));
		answer.addAttribute("format", "moodle auto format");
		ques.addAnswer(answer);
		ques.setPenalty("1.0000000");
		return ques;
	}
	
	/**
	 * liefert den kleinsten Primteiler von zahl, sonst -1, falls die Zahl eine Primzahl ist.
	 * @param zahl
	 * @return kleinster Primteiler oder -1
	 */
	public static int kleinsterPrimTeiler(int zahl) {
		for (int i=2;i<=Math.sqrt(zahl);i++) {
			if (zahl % i == 0) return i;
		}
		return -1;
	}

	/**
	 * Erzeugt eine numerische Frage vom Typ Basis^x = Potenzwert (dabei ist x gesucht und muss eingegeben werden)
	 * @param basis
	 * @param exponent
	 * @return Eine numerische Aufgabe mit Basis^x = potenzwert - x muss eingegeben werden
	 * Mit △ oder ☐
	 */
	public static Question erzPotenzieren1(int basis, int exponent) {
		Question ques = new Question(Question.numerical);
		int potenzwert = (int) Math.round(Math.pow(basis, exponent));
		ques.setName("Exponent angeben: "+basis+"^x="+potenzwert);
		ques.setQuestion_format("html");
		ques.setQuestiontext(Generator.replaceWithNumber("<p>Für welche Zahl steht ☐?</p><p>#1<sup>☐</sup> = #2 <br></p>", new Integer[] {basis,potenzwert})); 
		XMLObject answer = new XMLObject("answer");
		XMLObject atext = new XMLObject("text", ""+exponent);
		XMLObject tolerance = new XMLObject("tolerance","0");
		answer.addChild(atext);
		answer.addChild(tolerance);
		answer.addAttribute("fraction", "100");
		answer.addAttribute("format", "moodle auto format");
		ques.addAnswer(answer);
		return ques;
	}
	
	public static Question erzPotenzieren1wrap(int bmin, int bmax, int emin, int emax, int potmax) {
		int basis = Generator.zufallszahl(emin, emax, 1);
		int exp = Generator.zufallszahl(emin, emax, 1);
		if (Math.pow(basis, exp)>potmax) return erzPotenzieren1wrap(bmin, bmax, emin, emax, potmax);
		return erzPotenzieren1(basis, exp);
	}

	/**
	 * Erzeugt eine numerische Frage vom Typ x^exponent = potenzwert (x muss eingegeben werden)
	 * @param basis
	 * @param exponent
	 * @return Eine numerische Aufgabe mit x^exponent = potenzwert - x muss eingegeben werden
	 * Mit △ oder ☐
	 */
	public static Question erzPotenzieren2(int basis, int exponent) {
		Question ques = new Question(Question.numerical);
		int potenzwert = (int) Math.round(Math.pow(basis, exponent));
		ques.setName("Basis angeben: x^"+exponent+"="+potenzwert);
		ques.setQuestion_format("html");
		ques.setQuestiontext(Generator.replaceWithNumber("<p>Für welche Zahl steht △?</p><p>△<sup>#1</sup> = #2 <br></p>", new Integer[] {exponent,potenzwert})); 
		XMLObject answer = new XMLObject("answer");
		XMLObject atext = new XMLObject("text", ""+basis);
		XMLObject tolerance = new XMLObject("tolerance","0");
		answer.addChild(atext);
		answer.addChild(tolerance);
		answer.addAttribute("fraction", "100");
		answer.addAttribute("format", "m)oodle auto format");
		ques.addAnswer(answer);
		return ques;
	}
	
	public static Question erzPotenzieren2wrap(int bmin, int bmax, int emin, int emax, int potmax) {
		int basis = Generator.zufallszahl(emin, emax, 1);
		int exp = Generator.zufallszahl(emin, emax, 1);
		if (Math.pow(basis, exp)>potmax) return erzPotenzieren2wrap(bmin, bmax, emin, emax, potmax);
		return erzPotenzieren2(basis, exp);
	}
	
	
	/**
	 * @param a
	 * @param b
	 * @param c
	 * @return Aufgabe a*(b+c) = a*b+a*c = ...
	 */
	public static Question erzKlammerAufl1(int a, int b, int c) {
		Question q = new Question(Question.cloze);
		q.setQuestion_format("html");
		q.setName("Klammer auflösen - S95 Nr. 1: " + a + "·(" + b + "+" + c + ")");
		q.setQuestiontext("<![CDATA[<p dir=\"ltr\" style=\"text-align: left;\">" + a + " · (" + b + " + " + c + ") <br>"
				+ "</p><p dir=\"ltr\" style=\"text-align: left;\">= {1:NUMERICAL:~%100%" + a
				+ ":0} · {1:NUMERICAL:~%100%" + b + ":0} +" + "{1:NUMERICAL:~%100%" + a + ":0} · {1:NUMERICAL:~%100%"
				+ c + ":0} <br></p><p dir=\"ltr\" style=\"text-align: left;\">=" + "{1:NUMERICAL:~%100%" + (a * b)
				+ ":0} + {1:NUMERICAL:~%100%" + (a * c) + ":0} <br></p><p dir=\"ltr\" style=\"text-align: left;\">="
				+ "{1:NUMERICAL:~%100%" + (a * (b + c)) + ":0}<br></p>]]>");
		return q;
	}

	/**
	 * @param a
	 * @param b
	 * @param c
	 * @return Aufgabe a*(b-c) = a*b-a*c = ...
	 */
	public static Question erzKlammerAufl2(int a, int b, int c) {
		Question q = new Question(Question.cloze);
		q.setQuestion_format("html");
		q.setName("Klammer auflösen - S95 Nr. 1: " + a + "·(" + b + "-" + c + ")");
		q.setQuestiontext("<![CDATA[<p dir=\"ltr\" style=\"text-align: left;\">" + a + " · (" + b + " - " + c + ") <br>"
				+ "</p><p dir=\"ltr\" style=\"text-align: left;\">= {1:NUMERICAL:~%100%" + a
				+ ":0} · {1:NUMERICAL:~%100%" + b + ":0} -" + "{1:NUMERICAL:~%100%" + a + ":0} · {1:NUMERICAL:~%100%"
				+ c + ":0} <br></p><p dir=\"ltr\" style=\"text-align: left;\">=" + "{1:NUMERICAL:~%100%" + (a * b)
				+ ":0} - {1:NUMERICAL:~%100%" + (a * c) + ":0} <br></p><p dir=\"ltr\" style=\"text-align: left;\">="
				+ "{1:NUMERICAL:~%100%" + (a * (b - c)) + ":0}<br></p>]]>");
		return q;
	}

	/**
	 * @param a
	 * @param b
	 * @param c
	 * @return Aufgabe (b+c)*a = b*a+c*a = ...
	 */
	public static Question erzKlammerAufl3(int a, int b, int c) {
		Question q = new Question(Question.cloze);
		q.setQuestion_format("html");
		q.setName("Klammer auflösen - S95 Nr. 1: (" + b + "+" + c + ")·" + a);
		q.setQuestiontext("<![CDATA[<p dir=\"ltr\" style=\"text-align: left;\">(" + b + " + " + c + ") · " + a + " <br>"
				+ "</p><p dir=\"ltr\" style=\"text-align: left;\">= {1:NUMERICAL:~%100%" + b
				+ ":0} · {1:NUMERICAL:~%100%" + a + ":0} +" + "{1:NUMERICAL:~%100%" + c + ":0} · {1:NUMERICAL:~%100%"
				+ a + ":0} <br></p><p dir=\"ltr\" style=\"text-align: left;\">=" + "{1:NUMERICAL:~%100%" + (a * b)
				+ ":0} + {1:NUMERICAL:~%100%" + (a * c) + ":0} <br></p><p dir=\"ltr\" style=\"text-align: left;\">="
				+ "{1:NUMERICAL:~%100%" + (a * (b + c)) + ":0}<br></p>]]>");
		return q;
	}

	/**
	 * @param a (kleine Zahl)
	 * @param b (Zahl mit Faktor 10)
	 * @param c (Zahl zwischen 1-9)
	 * @return a * c + a * (b-c)
	 */
	public static Question erzKlammereAus1(int a, int b, int c) {
		Question q = new Question(Question.cloze);
		if (rand.nextInt(10) < 4)
			c = b - c;
		q.setName("Ausklammern - S95 Nr. 2: " + a + "·" + c + "+" + a + "·" + (b - c));
		q.setQuestiontext("" + a + "·" + c + "+" + a + "·" + (b - c) + "\n= " + "{1:NUMERICAL:~%100%" + a
				+ ":0} · ( {1:NUMERICAL:~%100%" + c + ":0} +" + "{1:NUMERICAL:~%100%" + (b - c)
				+ ":0}) \n= {1:NUMERICAL:~%100%" + a + ":0} · " + "{1:NUMERICAL:~%100%" + b + ":0} \n= "
				+ "{1:NUMERICAL:~%100%" + (a * b) + ":0}");
		return q;
	}

	/**
	 * @param a (kleine Zahl)
	 * @param b (Zahl mit Faktor 10)
	 * @param c (Zahl zwischen 1-9)
	 * @return a * (b+c) - a * c
	 */
	public static Question erzKlammereAus2(int a, int b, int c) {
		Question q = new Question(Question.cloze);
		q.setName("Ausklammern - S95 Nr. 2: " + a + "·" + (b + c) + "-" + a + "·" + c);
		q.setQuestiontext("" + a + "·" + (b + c) + "-" + a + "·" + (c) + "\n= " + "{1:NUMERICAL:~%100%" + a
				+ ":0} · ( {1:NUMERICAL:~%100%" + (b + c) + ":0} -" + "{1:NUMERICAL:~%100%" + (c)
				+ ":0}) \n= {1:NUMERICAL:~%100%" + a + ":0} · " + "{1:NUMERICAL:~%100%" + b + ":0} \n= "
				+ "{1:NUMERICAL:~%100%" + (a * b) + ":0}");
		return q;
	}

	/**
	 * @param a (kleine Zahl)
	 * @param b (Zahl mit Faktor 10)
	 * @param c (Zahl zwischen 1-9)
	 * @return c*a + (b-c)*a
	 */
	public static Question erzKlammereAus3(int a, int b, int c) {
		Question q = new Question(Question.cloze);
		if (rand.nextInt(10) < 4)
			c = b - c;
		q.setName("Ausklammern - S95 Nr. 2: " + c + "·" + a + "+" + (b - c) + "·" + a);
		q.setQuestiontext("" + c + "·" + a + "+" + (b - c) + "·" + a + "\n= " + "( {1:NUMERICAL:~%100%" + c + ":0} +"
				+ "{1:NUMERICAL:~%100%" + (b - c) + ":0}) · {1:NUMERICAL:~%100%" + a + ":0} \n= {1:NUMERICAL:~%100%" + b
				+ ":0} · " + "{1:NUMERICAL:~%100%" + a + ":0} \n= " + "{1:NUMERICAL:~%100%" + (a * b) + ":0}");
		return q;
	}

	/**
	 * Erzeugt eine Aufabe vom Typ (b+c)·a-c·a , z.B. 63·5-3·5
	 * @param a (kleine Zahl)
	 * @param b (Zahl mit Faktor 10)
	 * @param c (Zahl zwischen 1-9)
	 * @return (b+c)* a - c*a
	 */
	public static Question erzKlammereAus4(int a, int b, int c) {
		Question q = new Question(Question.cloze);
		q.setName("Ausklammern - S95 Nr. 2: " + (b + c) + "·" + (a) + "-" + c + "·" + a);
		q.setQuestiontext("" + (b + c) + "·" + (a) + "-" + c + "·" + (a) + "\n= " + "( {1:NUMERICAL:~%100%" + (b + c)
				+ ":0} -" + "{1:NUMERICAL:~%100%" + (c) + ":0}) · {1:NUMERICAL:~%100%" + a
				+ ":0} \n= {1:NUMERICAL:~%100%" + b + ":0} · " + "{1:NUMERICAL:~%100%" + a + ":0} \n= "
				+ "{1:NUMERICAL:~%100%" + (a * b) + ":0}");
		return q;
	}

	/**
	 * Erzeugt eine Aufabe vom Typ a * (b+-{1,2}), also z.B. 6 ·  62
	 * @param a (kleine Zahl)
	 * @param b (Zahl mit Faktor 10)
	 * @return a * (b+-{1,2}) oder umgekehrte Reihenfolge
	 */
	public static Question berechneGeschickt1(int a, int b) {
		Question q = new Question(Question.cloze);
		q.setQuestion_format("html");
		boolean positiv = rand.nextBoolean();
		boolean umgekehrt = rand.nextBoolean();
		int wert = (rand.nextBoolean() ? 1 : 2);
		if (umgekehrt) {
			q.setName("Ausklammern  - S95 Nr. 4/5: " + (positiv ? (b + wert) : (b - wert)) + " · " + a);
			q.setQuestiontext("<![CDATA[<p>Berechne Geschickt mit dem Distributivgesetz<br><small>zerlege den Wert von "+b+" dazu in Zehner und Einer, z.B. 39 = 40-1</p><p>"
					+ (positiv ? (b + wert) : (b - wert)) + " · " + a + "<br></p><p>= " + "( {1:NUMERICAL:~%100%" + b
					+ ":0} &nbsp;&nbsp;{1:MULTICHOICE_S:~%100%" + (positiv ? "+~-" : "-~+")
					+ "} &nbsp;&nbsp;{1:NUMERICAL:~%100%" + wert + ":0}&nbsp;)&nbsp;" + "{1:NUMERICAL:~%100%" + a
					+ ":0}<br></p><p>=" + "{1:NUMERICAL:~%100%" + b + ":0} · {1:NUMERICAL:~%100%" + a
					+ ":0} &nbsp;&nbsp;{1:MULTICHOICE_S:~%100%" + (positiv ? "+~-" : "-~+")
					+ "} &nbsp;&nbsp;{1:NUMERICAL:~%100%" + wert + ":0} · " + "{1:NUMERICAL:~%100%" + a
					+ ":0}<br></p><p>= " + "{1:NUMERICAL:~%100%" + (a * b) + ":0} &nbsp;&nbsp;{1:MULTICHOICE_S:~%100%"
					+ (positiv ? "+~-" : "-~+") + "} &nbsp;&nbsp;{1:NUMERICAL:~%100%" + (a * wert) + ":0}<br></p><p>= "
					+ "{1:NUMERICAL:~%100%" + (a * (positiv ? (b + wert) : (b - wert))) + ":0}</p>]]>");
		} else {
			q.setName("Ausklammern - S95 Nr. 4/5: " + (a) + "·" + (positiv ? (b + wert) : (b - wert)));
			q.setQuestiontext("<![CDATA[<p>Berechne Geschickt mit dem Distributivgesetz<br></p><p>" + a + "·"
					+ (positiv ? (b + wert) : (b - wert)) + "<br></p><p>= " + "{1:NUMERICAL:~%100%" + a
					+ ":0} · &nbsp;(&nbsp; " + "{1:NUMERICAL:~%100%" + b + ":0} &nbsp;&nbsp;{1:MULTICHOICE_S:~%100%"
					+ (positiv ? "+~-" : "-~+") + "} &nbsp;&nbsp;{1:NUMERICAL:~%100%" + wert
					+ ":0}) <br></p><p>= {1:NUMERICAL:~%100%" + a + ":0} · " + "{1:NUMERICAL:~%100%" + b
					+ ":0}&nbsp;&nbsp; {1:MULTICHOICE_S:~%100%" + (positiv ? "+~-" : "-~+")
					+ "} &nbsp;&nbsp;{1:NUMERICAL:~%100%" + a + ":0} · {1:NUMERICAL:~%100%" + wert + ":0}<br></p><p>= "
					+ "{1:NUMERICAL:~%100%" + (a * b) + ":0} &nbsp;&nbsp;{1:MULTICHOICE_S:~%100%"
					+ (positiv ? "+~-" : "-~+") + "} &nbsp;&nbsp;{1:NUMERICAL:~%100%" + (a * wert) + ":0}<br></p><p>= "
					+ "{1:NUMERICAL:~%100%" + (a * (positiv ? (b + wert) : (b - wert))) + ":0}</p>]]>");
		}
		return q;
	}

	/**
	 * Ersetzt in einem String mit Platzhalten #1,#2 usw. diese durch Zahlen eines Arrays
	 * @param s String mit #1,#2, ... für die Stellen an denen die entsprechende Zahl einzutragen ist
	 * @param z int[] mit den Zahlen
	 * @return String bei dem die Ersetzung stattgefunden hat
	 */
	public static String replaceWithNumber(String s, Integer[] z) {
		String out = s;
		for (int i=0; i<z.length; i++) {
			out = out.replaceAll("#"+(i+1), ""+z[i]);
		}
		return out;
	}

	/**
	 * Ersetzt in einem String mit Platzhalten #1,#2 usw. diese durch Zahlen eines Arrays
	 * @param s String mit #1,#2, ... für die Stellen an denen die entsprechende Zahl einzutragen ist
	 * @param z String[] mit den Ersetzungen
	 * @return String bei dem die Ersetzung stattgefunden hat
	 */
	public static String replaceWithStrings(String s, String[] z) {
		String out = s;
		for (int i=0; i<z.length; i++) {
			out = out.replaceAll("#"+(i+1), z[i]);
		}
		return out;
	}

	
	
	public static Question gibMultiChoiceAusString(String text) {
		if (text == null)
			return null;
		String[] inhalt = text.split("\n");
		return gibMultiChoiceAusStrings(inhalt);
	}

	public static Question gibMultiChoiceAusStrings(String[] inhalt) {
		if (inhalt.length < 3)
			return null; // Nur 2 Zeilen?!
		Question ret = new Question(Question.multichoice);
		ret.setQuestiontext(inhalt[0]);
		ret.setName(inhalt[0]);
		for (int i = 1; i < inhalt.length; i++) {
			ret.addAnswerFromString(inhalt[i]);
		}
		return ret;
	}

	/**
	 * Erzeugt aus einer Datei ein Quiz mit MulitpleChoice-Frage<br>
	 * Dateiformat<br>
	 * # Kommentar oder auch mehrere<br>
	 * Welche der Folgenden&lt;br&gt;Antworten wählst du?<br>
	 * &Name der Frage (wenn diese Zeile Fehlt - wird die Frage als Name verwendet)<br>
	 * +richtige Antwort<br>
	 * +noch eine Richige Antwort#Feedback zu dieser Antwort (erscheint wenn sie ausgewählt wurde)<br>
	 * -falsche Antwort#Feedback<br>
	 * #generalFeedback (die letzte dieser Zeilen ist gültig)<br>
	 * (Leerzeile beendet die Frage  - nächste Frage folgt)<br>
	 * 
	 * @return ein Quiz mit disen Multiple-Choice-Fragen
	 */
	public static Quiz gibQuizAusMultiChoiceString(String input) {
		try {
			System.err.println("In der an QuestionXML angepassten Variante");
			InputStream targetStream = new ByteArrayInputStream(input.getBytes());
			InputStreamReader in = new InputStreamReader(targetStream);
			BufferedReader reader = new BufferedReader(in);

			Quiz quiz = new Quiz();
			//QuestionMC q = new QuestionMC();
			Question q = new Question(Question.multichoice);
			boolean answer = false;

			String line = reader.readLine();
			while (line != null) {
				//System.out.println("> "+line);
				if (!answer && line.startsWith("#")) {// Kommentar - nichts tun
					System.err.println("Kommentar: "+line);
				} else if (line.trim().equals("")) { // Leerzeile - neue Frage
					//System.out.println("Frage "+q+"beendet und angefuegt - wenn möglich");
					if (!q.hasNoAnswers()) { // Frage an das Quiz anhängen und neue Starten
						quiz.addQuestion(q);
						//q = new QuestionMC();
						q = new Question(Question.multichoice);
						answer = false;
					}
				} else { // Zeile, die zur Frage gehört
					if (!answer) {
						q.setName(line);
						q.setQuestiontext(line);
						answer=true;
					} else {
						char t = line.charAt(0);
						switch(t) {
						case '+':
						case '-':
							q.addAnswerFromString(line); //war addAnswer
							break;
						case '#':
							q.setGeneralfeedback(line.substring(1));
							break;
						case '&':
							q.setName(line.substring(1));
							break;
							
						}
					}
				}
				line = reader.readLine();
			}
			if (!q.hasNoAnswers()) { // Frage an das Quiz anhängen und neue Starten - falls nicht mit leerer Zeile beendet wurde
				quiz.addQuestion(q);
			}
	
			reader.close();
			return quiz;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Quiz gibQuizAusMultiChoiceDatei() {
		File file = Dateiaktionen.chooseFileToRead();
		if (file != null) {
			Controller.curfilename=file.getAbsolutePath();
			System.out.println("Neuer filename: "+filename);
		}
		String input = Dateiaktionen.liesTextDatei(file);
		return gibQuizAusMultiChoiceString(input);
	}

	public static void writeQuizToXMLFile(Quiz q) {
		System.out.println("Working Directory: " + System.getProperty("user.dir"));
		
		// JFileChooser-Objekt erstellen
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		System.out.println("Current filename: "+Controller.curfilename);
		String fname = Controller.curfilename.replaceAll(".[a-z]*$", "");
		chooser.setSelectedFile(new File("./"+fname+".xml"));
		chooser.setFileFilter(new FileNameExtensionFilter("moodle-xml-Files (.xml)","xml"));
		// Dialog zum Oeffnen von Dateien anzeigen
		JFrame jf = new JFrame( "Dialog" ); // added
        jf.setAlwaysOnTop( true ); // added
		int rueckgabeWert = chooser.showSaveDialog(jf);
		jf.dispose();
		/* Abfrage, ob auf "Speichern" geklickt wurde */
		if (rueckgabeWert != JFileChooser.APPROVE_OPTION) {
			System.out.println("Auswahl beendet - keine Datei gewählt");
			return;
		}
		//Wenn Datei schon existiert
		if (chooser.getSelectedFile().exists()) {
		    int response = JOptionPane.showConfirmDialog(null, //
		            "Do you want to replace the existing file?", //
		            "Confirm", JOptionPane.YES_NO_OPTION, //
		            JOptionPane.QUESTION_MESSAGE);
		    if (response != JOptionPane.YES_OPTION) {
		        return;
		    } 
		}
		try {

			FileWriter fw = null;
			fw = new FileWriter(chooser.getSelectedFile().getAbsolutePath());

			BufferedWriter writer = new BufferedWriter(fw);
			PrintWriter pwriter = new PrintWriter(writer);
			
			pwriter.println(q.toString());

	
			pwriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public static boolean isStringHTML(String s) {
		return s.indexOf('<')>=0;
	}

	/**
	 * @param min
	 * @param max
	 * @param faktor
	 * @return eine Zufallszahl die zwischen min und max (jeweils einschliesslich
	 *         liegt) und mit faktor multipliziert wird
	 */
	public static int zufallszahl(int min, int max, int faktor) {
		return (rand.nextInt(max - min + 1) + min) * faktor;
	}
}
