package aufgabenGenerator;

import java.util.ArrayList;

import com.sun.xml.internal.fastinfoset.util.CharArrayString;

public class Question {
	public static final int multichoice = 1;
	public static final int truefalse = 2;
	public static final int shortanswer = 3;
	public static final int match = 4;
	public static final int cloze = 5;
	public static final int essay = 6;
	public static final int numerical = 7;
	public static final int description = 8;
	protected static String[] typenamen = new String[] { "", "multichoice", "truefalse", "shortanswer", "match",
			"cloze", "essay", "numerical", "description" };
	protected int type = 0;
	protected String question_format = "moodle_auto_format";
	protected String feedback_format = "";
	protected String name = "";
	protected String questiontext = "";
	protected String penalty = "0.3333333";
	private ArrayList<XMLObject> answers = new ArrayList<XMLObject>();
	private ArrayList<XMLObject> addXMLObjects = new ArrayList<XMLObject>();
	protected String generalfeedback = "";
	protected boolean hidden = false;
	protected double defaultgrade;

	public Question(int type) {
		if (type <= 0 || type > 8) {
			throw (new IllegalArgumentException("Frage mit diesem Typ gibt es nicht"));
		}
		this.type = type;
	}

	public Question(XMLObject xmlq) {
		if (!xmlq.getBezeichnung().equals("question"))
			throw (new IllegalArgumentException("Falsche Bezeichnung des XML-Objekts: " + xmlq.getBezeichnung()));
		//Type
		String stype = xmlq.getAttribute("type");
		this.type = 1;
		while (this.type < typenamen.length) {
			if (stype.equals(typenamen[type]))
				break;
			type++;
		}
		if (type == typenamen.length)
			throw (new IllegalArgumentException("Diesen Fragetyp gibt es nicht: " + stype));
		//Name
		XMLObject qname = xmlq.getChild("name");
		if (qname == null || qname.getChild("text") == null)
			throw (new IllegalArgumentException("Kein Name im XML"));
		this.name = qname.getChild("text").getContent();
		//Questiontext
		XMLObject qtext = xmlq.getChild("questiontext");
		if (qtext == null || qtext.getChild("text") == null)
			throw (new IllegalArgumentException("Kein Questiontext im XML"));
		this.questiontext = qtext.getChild("text").getContent();
		this.question_format = (qtext.getAttribute("format") != null ? qtext.getAttribute("format") : "");
		//Feedback
		XMLObject qftext = xmlq.getChild("generalfeedback");
		if (qftext == null || qftext.getChild("text") == null) {
			System.out.println("Kein Feedback im XML");
		} else {
			this.generalfeedback = qftext.getChild("text").getContent();
			this.feedback_format = (qftext.getAttribute("format") != null ? qftext.getAttribute("format") : "");
		}
		//penalty
		XMLObject qpenalty = xmlq.getChild("penalty");
		if (qpenalty == null) {
			System.out.println("Kein penalty-Wert im XML");
		} else {
			this.penalty=qpenalty.getContent();
		}
		//hidden
		XMLObject qhidden = xmlq.getChild("hidden");
		if (qpenalty == null) {
			System.out.println("Kein hidden-Wert im XML");
		} else {
			if (qhidden.getContent().equals("1")) this.hidden=true;
		}
		//All Other Objects
		xmlq.toFirstChild();
		while (xmlq.hasChildAccess()) {
			XMLObject c = xmlq.getCurrentChild();
			String bez = c.getBezeichnung();
			if (bez.equals("answer")) {
				answers.add(c.clone());
			} else if (bez.equals("name") || bez.equals("questiontext") || bez.equals("generalfeedback") || bez.equals("penalty") || bez.equals("hidden") || bez.equals("idnumber")) {
				//Nothing to be done
			} else {
				addXMLObjects.add(c.clone());
			}
			xmlq.toNextChild();
		}
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuestiontext() {
		return questiontext;
	}

	public void setQuestiontext(String questiontext) {
		this.questiontext = questiontext;
	}

	public String getGeneralfeedback() {
		return generalfeedback;
	}

	public void setGeneralfeedback(String generalfeedback) {
		this.generalfeedback = generalfeedback;
	}

	public int getType() {
		return type;
	}

	public String getQuestion_format() {
		return question_format;
	}

	public void setQuestion_format(String question_format) {
		this.question_format = question_format;
	}

	public void addAnswer(XMLObject ans) {
		if (ans.getBezeichnung().equals("answer"))
			answers.add(ans);
	}

	public void addXMLObject(XMLObject obj) {
		if (obj != null)
			addXMLObjects.add(obj);
	}

	public String getPenalty() {
		return penalty;
	}

	public void setPenalty(String penalty) {
		this.penalty = penalty;
	}

	public void checkHTMLFormat() {
		System.err.println("checkHTMLFormat");
		if (this.questiontext.indexOf('<') >= 0) { // Wenn ein < existiert -> html-Format
			this.question_format = "html";
		}
		if (this.generalfeedback.indexOf('<') >= 0) { // Wenn ein < existiert -> html-Format
			this.feedback_format = "html";
		}
		if (this.question_format.equals("html") && !this.questiontext.startsWith("<![CDATA[")) { // CDATA hinzufügen
			System.err.println("CDATA eingefügt!");
			this.questiontext = "<![CDATA[" + this.questiontext + "]]>";
		}
		if (this.feedback_format.equals("html") && !this.generalfeedback.startsWith("<![CDATA[")) { // CDATA hinzufügen
			System.err.println("CDATA eingefügt!");
			this.generalfeedback = "<![CDATA[" + this.generalfeedback + "]]>";
		}
		// alle name - Bestandteile von < und > befreien
		this.name = this.name.replaceAll("<[/a-z]*>", "");
	}

	@Override
	public String toString() {
		this.checkHTMLFormat();
		String out = "";
		out = "<question type=\"" + typenamen[this.type] + "\">\n" + "    <name>\n" + "      <text>" + this.name
				+ "</text>\n" + "    </name>\n" + "    <questiontext format=\"" + this.question_format + "\">\n"
				+ "        <text>" + this.questiontext + "</text>\n" + "    </questiontext>\n"
				+ "    <generalfeedback format=\"" + this.feedback_format + "\">\n" + "      <text>"
				+ this.generalfeedback + "</text>\n" + "    </generalfeedback>\n" + "    <penalty>" + this.penalty
				+ "</penalty>\n" + "    <hidden>" + (this.hidden ? "1" : "0") + "</hidden>\n"
				+ "    <idnumber></idnumber>\n";
		for (XMLObject obj : addXMLObjects) {
			out += obj.toString().replaceAll("^", "    ").replaceAll("\n", "\n    ").replaceAll("    $", "");
		}
		for (XMLObject ans : answers) {
			out += ans.toString().replaceAll("^", "    ").replaceAll("\n", "\n    ").replaceAll("    $", "");
		}
		out += "</question>\n\n";
		return out;
	}

}
