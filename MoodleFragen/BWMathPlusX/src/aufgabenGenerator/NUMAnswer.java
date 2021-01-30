package aufgabenGenerator;

import java.util.Locale;

public class NUMAnswer {
	private double fraction = 0.0; // Wert der Antwort in %
	private String format = "moodle auto format";
	private String text = "";
	private String feedback_format = "moodle auto format";
	private String feedback_text = "";
	private double tolerance = 0;

	/**
	 * @param text
	 * @param richtig
	 */
	public NUMAnswer(String text, double fraction) {
		this.fraction=fraction;
		this.text = text;
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

	public double getTolerance() {
		return tolerance;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
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
		String out;
		out = "   <answer fraction=\"" + String.format(Locale.ENGLISH, "%.4f", fraction) + "\" format=\"" + this.format
				+ "\">\n" + "      <text>" + this.text + "</text>\n" + "      <feedback format=\""
				+ this.feedback_format + "\">\n" + "        <text>" + this.feedback_text + "</text>\n"
				+ "      </feedback>\n" 
				+ "      <tolerance>"+ String.format(Locale.ENGLISH, "%.4f", this.tolerance) + "</tolerance>\n"
				+ "   </answer>\n";
		return out;
	}
}
