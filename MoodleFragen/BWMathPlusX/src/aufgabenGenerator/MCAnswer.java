/**
 * 
 */
package aufgabenGenerator;

import java.util.Locale;

/**
 * @author peter Antwort auf eine Multiplechoice-Frage
 */
public class MCAnswer {
	private double fraction = 0.0;
	private String format = "moodle auto format";
	private String text = "";
	private boolean richtig = true;
	private String feedback_format = "moodle auto format";
	private String feedback_text = "";

	/**
	 * @param text
	 * @param richtig
	 */
	public MCAnswer(String text, boolean richtig) {
		this.text = text;
		this.richtig = richtig;
	}

	public double getFraction() {
		return fraction;
	}

	public void setFraction(double fraction) {
		this.fraction = fraction;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isRichtig() {
		return richtig;
	}

	public void setRichtig(boolean richtig) {
		this.richtig = richtig;
	}

	public String getFeedback_format() {
		return feedback_format;
	}

	public void setFeedback_format(String feedback_format) {
		this.feedback_format = feedback_format;
	}

	public String getFeedback_text() {
		return feedback_text;
	}

	public void setFeedback_text(String feedback_text) {
		this.feedback_text = feedback_text;
	}

	public void checkHTMLFormat() {
		if (Generator.isStringHTML(this.text)) this.format="html";
		if (this.format.equals("html") && !this.text.startsWith("<![CDATA[")) { // CDATA hinzufügen
			this.text = "<![CDATA[" + this.text + "]]>";
		}
		if (Generator.isStringHTML(this.feedback_text)) this.feedback_format="html";
		if (this.feedback_format.equals("html") && !this.feedback_text.startsWith("<![CDATA[")) { // CDATA hinzufügen
			this.feedback_text = "<![CDATA[" + this.feedback_text + "]]>";
		}
	}

	@Override
	public String toString() {
		this.checkHTMLFormat();
		String out;
		out = "   <answer fraction=\"" + String.format(Locale.ENGLISH, "%.4f", fraction) + "\" format=\"" + this.format
				+ "\">\n" + "      <text>" + this.text + "</text>\n" + "      <feedback format=\""
				+ this.feedback_format + "\">\n" + "        <text>" + this.feedback_text + "</text>\n"
				+ "      </feedback>\n" + "   </answer>\n";
		return out;
	}
	
	public XMLObject toXMLObject() {
		this.checkHTMLFormat();
		XMLObject answer = new XMLObject("answer");
		answer.addAttribute("fraction", String.format(Locale.ENGLISH, "%.4f", fraction));
		answer.addAttribute("format", this.format);
		XMLObject xtext = new XMLObject("text",this.text);
		answer.addChild(xtext);
		XMLObject xfeedback = new XMLObject("feedback","");
		xfeedback.addAttribute("format", this.feedback_format);
		XMLObject xfeedbacktext = new XMLObject("text",this.feedback_text);
		xfeedback.addChild(xfeedbacktext);
		answer.addChild(xfeedback);
		return answer;
	}

}
