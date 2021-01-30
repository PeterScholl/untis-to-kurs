/**
 * 
 */
package aufgabenGenerator;

import java.util.ArrayList;

/**
 * @author peter Multichoice-Question
 */
public class QuestionMC extends Question {
	private ArrayList<MCAnswer> answers = new ArrayList<MCAnswer>();
	private boolean single = true; // nur eine Antwort richtig
	private boolean shuffleanswers = true; // antworten Mischen
	private String answernumbering = "none"; //possible values none, abc, ...
	private String correctfeedback_format = "moodle auto format";
	private String correctfeedback_text ="";
	private String partiallycorrectfeedback_format = "moodle auto format";
	private String partiallycorrectfeedback_text ="";
	private String incorrectfeedback_format = "moodle auto format";
	private String incorrectfeedback_text ="";
	private boolean autoCalcFraction = true;
	

	public QuestionMC() {
		super(Question.multichoice);
		this.question_format="html";
	}

	/**
	 * @param a Antwortstring, wenn richtig beginnt mit +, ansonsten mit -
	 */
	public void addAnswer(String a) {
		if (a == null || a.length() < 1)
			return;
		MCAnswer ans;
		switch (a.charAt(0)) {
		case '+':
		case '-':
			String[] parts = a.substring(1).split("#");
			if (!(parts.length<3)) {
				System.err.println("Zu viele Kommentarzeichen");
			}
			ans = new MCAnswer(parts[0],(a.charAt(0)=='+'?true:false));
			if (parts.length >1) ans.setFeedback_text(parts[1]);
			break;
		default:
			return;
		}
		addAnswer(ans);
	}
	
	public void addAnswer(MCAnswer ma) {
		answers.add(ma);		
		if (single) {
			int count=0;
			for (MCAnswer i: answers) {
				if (i.isRichtig()) count++;
			}
			if (count>1) single=false;
		}
	}
	
	public void checkHTMLFormat() {
		super.checkHTMLFormat();
		//this.correctfeedback_text
		if (this.correctfeedback_text.indexOf('<')>=0) { // Wenn ein < existiert -> html-Format
			this.correctfeedback_format="html";
		}
		if (this.correctfeedback_format.equals("html") && !this.correctfeedback_text.startsWith("<![CDATA[")) { // CDATA hinzufügen
			System.err.println("correctfeedback CDATA eingefügt!");
			this.correctfeedback_text="<![CDATA["+this.correctfeedback_text+"]]>";
		}
		//this.incorrectfeedback_text
		if (this.incorrectfeedback_text.indexOf('<')>=0) { // Wenn ein < existiert -> html-Format
			this.incorrectfeedback_format="html";
		}
		if (this.incorrectfeedback_format.equals("html") && !this.incorrectfeedback_text.startsWith("<![CDATA[")) { // CDATA hinzufügen
			System.err.println("incorrectfeedback CDATA eingefügt!");
			this.incorrectfeedback_text="<![CDATA["+this.incorrectfeedback_text+"]]>";
		}
		//this.partiallycorrectfeedback_format
		if (this.partiallycorrectfeedback_text.indexOf('<')>=0) { // Wenn ein < existiert -> html-Format
			this.partiallycorrectfeedback_format="html";
		}
		if (this.partiallycorrectfeedback_format.equals("html") && !this.partiallycorrectfeedback_text.startsWith("<![CDATA[")) { // CDATA hinzufügen
			System.err.println("paritallycorrect feedback CDATA eingefügt!");
			this.partiallycorrectfeedback_text="<![CDATA["+this.partiallycorrectfeedback_text+"]]>";
		}

	}

	@Override
	public String toString() {
		this.checkHTMLFormat();
		String out;
		out = "<question type=\"" + typenamen[this.type] + "\">\n" + "    <name>\n" + "      <text>" + this.name
				+ "</text>\n" + "    </name>\n" + "    <questiontext format=\"" + this.question_format + "\">\n"
				+ "        <text>" + this.questiontext + "</text>\n" + "    </questiontext>\n"
				+ "    <generalfeedback format=\"" + this.feedback_format + "\">\n" + "      <text>"
				+ this.generalfeedback + "</text>\n" + "    </generalfeedback>\n" + "    <penalty>" + this.penalty
				+ "</penalty>\n" + "    <hidden>" + (this.hidden ? "1" : "0") + "</hidden>\n"
				+ "    <idnumber></idnumber>\n" + "    <single>" + (this.single?"true":"false") + "</single>\n" + "    <shuffleanswers>"
				+ (this.shuffleanswers?"true":"false") + "</shuffleanswers>\n" 
				+ "    <answernumbering>"+this.answernumbering+"</answernumbering>\n"
				+ "    <correctfeedback format=\""+this.correctfeedback_format+"\">\n"
				+ "      <text>"+this.correctfeedback_text+"</text>\n" 
				+ "    </correctfeedback>\n"
				+ "    <partiallycorrectfeedback format=\""+this.partiallycorrectfeedback_format+"\">\n"
				+ "      <text>"+this.partiallycorrectfeedback_text+"</text>\n"
				+ "    </partiallycorrectfeedback>\n"
				+ "    <incorrectfeedback format=\""+this.incorrectfeedback_format+"\">\n"
				+ "      <text>"+this.incorrectfeedback_text+"</text>\n"
				+ "    </incorrectfeedback>\n";
		if (autoCalcFraction) {
			int anzq=answers.size();
			int anzr=0;
			int anzw=0;
			for (MCAnswer m : answers) {
				if (m.isRichtig()) anzr++;
				else anzw++;
			}
			for (MCAnswer m : answers) {
				if (m.isRichtig()) {
					m.setFraction(100.0/anzr);
				} else if (anzr==1) { // Nur eine richtige Frage, dann sollten keine negativen Werte auftreten
					m.setFraction(0);
				} else { //ansonsten sind negative Werte wichtig, da sonst der Prüfling alle Antworten ankreuzt
					m.setFraction(-100.0/anzw);
				}
			}
		}
		for (MCAnswer m : answers) {
			out+=m.toString();
		}
		out += "</question>\n\n";
		return out;
	}

	public boolean isSingle() {
		return single;
	}

	public void setSingle(boolean single) {
		this.single = single;
	}

	public boolean isShuffleanswers() {
		return shuffleanswers;
	}

	public void setShuffleanswers(boolean shuffleanswers) {
		this.shuffleanswers = shuffleanswers;
	}
	
	public boolean isEmpty() {
		return answers.size()==0;
	}

	public void addGenralFeedback(String substring) {
		this.generalfeedback=substring;
	}
	
	

}
