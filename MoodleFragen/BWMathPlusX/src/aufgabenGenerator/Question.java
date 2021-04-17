package aufgabenGenerator;

import java.util.Locale;

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
	public static final String format_moodle = "moodle_auto_format";
	public static final String format_html = "html";
	public static final String format_plain = "plain_text";
	public static final String format_markdown = "markdown";
	
	private XMLObject xmldata = new XMLObject("question");
	private boolean calculateFractions = true; //Sollen bei einer Multiple-Choice-Frage die Fractions berechnet werden

	public Question(int type) {
		if (type <= 0 || type > 8) {
			throw (new IllegalArgumentException("Frage mit diesem Typ gibt es nicht"));
		}
		xmldata.addAttribute("type", typenamen[type]);
		checkFieldsNeededForType();
	}

	public Question(XMLObject xmlq) {
		if (!xmlq.getBezeichnung().equals("question"))
			throw (new IllegalArgumentException("Falsche Bezeichnung des XML-Objekts: " + xmlq.getBezeichnung()));
		//Type
		String stype = xmlq.getAttribute("type");
		//check if type is in array		
		int type = 1;
		while (type < typenamen.length) {
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
		//Questiontext
		XMLObject qtext = xmlq.getChild("questiontext");
		if (qtext == null || qtext.getChild("text") == null)
			throw (new IllegalArgumentException("Kein Questiontext im XML"));
		xmldata = xmlq.clone();
		checkFieldsNeededForType();
	}

	public String getName() {
		return xmldata.getContent(new String[] {"name","text"});
	}

	public void setName(String name) {
		xmldata.setContent(new String[] {"name","text",name});		
	}

	public String getQuestiontext() {
		return xmldata.getContent(new String[] {"questiontext","text"});
	}

	public void setQuestiontext(String questiontext) {
		xmldata.setContent(new String[] {"questiontext","text",questiontext});
	}

	public String getGeneralfeedback() {
		return xmldata.getContent(new String[] {"generalfeedback","text"});
	}

	public void setGeneralfeedback(String generalfeedback) {
		xmldata.setContent(new String[] {"generalfeedback","text",generalfeedback});
	}

	public int getType() {
		String typename = xmldata.getAttribute("type");
		if (typename == null) return 0;
		for (int i=0; i<typenamen.length; i++) {
			if (typenamen[i].equals(typename)) return i;
		}
		return 0;
	}

	public boolean isCalculateFractions() {
		return calculateFractions;
	}

	public void setCalculateFractions(boolean calculateFractions) {
		this.calculateFractions = calculateFractions;
	}

	public String getQuestion_format() {
		return xmldata.getAttribute(new String[] {"questiontext","format"});
	}

	public void setQuestion_format(String question_format) {
		xmldata.addAttribute(new String[] {"questiontext","format"}, question_format);
	}

	public String getGeneralfeedback_format() {
		return xmldata.getAttribute(new String[] {"generalfeedback","format"});
	}

	public void setGeneralfeedback_format(String generalfeedback_format) {
		xmldata.addAttribute(new String[] {"generalfeedback","format"}, generalfeedback_format);
	}

	public void addAnswer(XMLObject ans) {
		if (ans.getBezeichnung().equals("answer"))
			xmldata.addChild(ans);
	}

	public void addXMLObject(XMLObject obj) {
		if (obj != null)
			xmldata.addChild(obj);
	}
	
	public void addAnswerFromString(String a) {
		if (a == null || a.length() < 1)
			return;
		XMLObject ans = new XMLObject("answer");
		switch (a.charAt(0)) {
		case '+':
		case '-':
			String[] parts = a.substring(1).split("#",2);
			ans.setContent(new String[] {"text",parts[0]});
			if (a.charAt(0)=='+') {
				ans.addAttribute("fraction", "100.0");
			} else {
				ans.addAttribute("fraction", "0.0");
			}
			if (parts.length >1) ans.setContent(new String[] {"feedback","text",parts[1]});
			break;
		default:
			return;
		}
		addAnswer(ans);
	}
	
	public String getPenalty() {
		return xmldata.getContent(new String[] {"penalty"});
	}

	public void setPenalty(String penalty) {
		xmldata.setContent(new String[] {"penalty",penalty});
	}
	
	public boolean isSingle() {
		return xmldata.getContent(new String[] {"single"}).equals("true");
	}

	public void setSingle(boolean single) {
		xmldata.setContent(new String[] {"single",""+single});
	}

	public boolean isShuffleanswers() {
		return xmldata.getContent(new String[] {"shuffleanswers"}).equals("1");
	}

	public void setShuffleanswers(boolean shuffleanswers) {
		xmldata.setContent(new String[] {"shuffleanswers",(shuffleanswers?"1":"0")});
	}
	
	public boolean hasNoAnswers() {
		return xmldata.getAllChildren("answer").size()==0;
	}

	public void checkHTMLFormat() {
		//this.checkFieldsNeededForType();
		//System.err.println("checkHTMLFormat");
		if (this.getQuestiontext().indexOf('<') >= 0) { // Wenn ein < existiert -> html-Format
			this.setQuestion_format("html");
		}
		if (this.getGeneralfeedback().indexOf('<') >= 0) { // Wenn ein < existiert -> html-Format
			this.setGeneralfeedback_format("html");
		}
		if (this.getQuestion_format().equals("html") && !this.getQuestiontext().startsWith("<![CDATA[")) { // CDATA hinzufügen
			System.err.println("CDATA eingefügt!");
			this.setQuestiontext("<![CDATA[" + this.getQuestiontext() + "]]>");
		}
		if (this.getGeneralfeedback_format().equals("html") && !this.getGeneralfeedback().startsWith("<![CDATA[")) { // CDATA hinzufügen
			System.err.println("CDATA eingefügt!");
			this.setGeneralfeedback("<![CDATA[" + this.getGeneralfeedback() + "]]>");
		}
		// alle name - Bestandteile von < und > befreien
		this.setName(this.getName().replaceAll("<[/a-z]*>", "").replaceAll("\\\\", ""));
		//TODO für jede Antwort das Format checken
		for (XMLObject ans : xmldata.getAllChildren("answer")) {
			if (ans.getChild("text").getContent().indexOf('<') >= 0 || ans.getChild("text").getContent().indexOf('&') >= 0 ) { // Wenn ein < oder & existiert -> html-Format
				ans.addAttribute("format", format_html);
			}
			//format html mit CDATA einpacken
			if (ans.getAttribute("format") != null && ans.getAttribute("format").equals("html") && !ans.getChild("text").getContent().startsWith("<![CDATA[")) { // CDATA hinzufügen
				System.err.println("CDATA eingefügt!");
				ans.getChild("text").setContent("<![CDATA[" + ans.getChild("text").getContent() + "]]>");
			}
		}
	}

	/** prüft die vorliegende Frage auf zulässigkeit und berichtigt gegebenenfalls falsche Werte
	 * bzw. berechnet Werte wenn notwendig
	 */
	private void checkFieldsNeededForType() {
		int type = this.getType();
		//Feld Name prüfen ggf. anlegen
		deepCheckField(new String[] {"name","text"},"Frage vom Typ "+typenamen[type]);
		//Questiontext prüfen ggf. anlegen
		if (this.getQuestiontext()==null) this.setQuestiontext("The question is ...");
		if (this.getQuestion_format()==null || this.getQuestion_format().equals("")) this.setQuestion_format(format_html);
		//Generalfeedback prüfen
		if (this.getGeneralfeedback()==null) this.setGeneralfeedback("");
		if (this.getGeneralfeedback_format()==null || this.getGeneralfeedback_format().equals("")) this.setGeneralfeedback_format(format_html);
		//penalty - vorhanden? - sonst defaultwert 0.3333333
		checkField("penalty", "0.3333333");
		//hidden - vorhanden? - default 0 - mögliche Werte 0,1
		checkField("hidden", "0", new String[] {"0","1"});
		//idnumber
		checkField("idnumber", "");
		
		switch(type) {
		case multichoice:
			//single
			checkField("single","true", new String[] {"true","false"});
			//shuffleanswers
			checkField("shuffleanswers","1", new String[] {"0","1"});
			//correctfeedback
			deepCheckField(new String[] {"correctfeedback", "text"},"");
			deepCheckAttribute(new String[] {"correctfeedback","format"}, format_moodle);
			//partiallycorrectfeedback
			deepCheckField(new String[] {"partiallycorrectfeedback", "text"},"");
			deepCheckAttribute(new String[] {"partiallycorrectfeedback","format"}, format_moodle);
			//incorrectfeedback
			deepCheckField(new String[] {"incorrectfeedback", "text"},"");
			deepCheckAttribute(new String[] {"incorrectfeedback","format"}, format_moodle);
			//answernumbering
			checkField("answernumbering","none", new String[] {"none","abc","ABC","123"});
			if (calculateFractions) { //Für diese Frage sollen die Werte berechnet werden
				//ist es eine single-Frage - dann nur eine Antwort mit 100% - Rest 0
				boolean issingle = xmldata.getContent(new String[] {"single"}).contentEquals("true");
				//ist es eine Frage mit multiple-Antwort - dann 100% auf richtige Fragen verteilen
				//bei falschen Fragen gibt es zwei Optionen 1. -100% auf die Falschen Fragen verteilen oder 2. negativen Wert von richtigen Fragen nehmen
				int cntr=0; //anzRichtige
				int cntw=0; //anzFalsche
				for (XMLObject ans : xmldata.getAllChildren("answer")) if (Hilfsfunktionen.getFractionOfAnswerObject(ans)>0) cntr++; else cntw++;
				double fracr = 100.0/cntr;
				double fracw = -100.0/cntw;
				System.out.println("richtig: "+cntr+" falsch: "+cntw+" fracr: "+fracr+" fracw: "+fracw);
				if (issingle && cntr>1) { // doch keine single-Frage
					xmldata.setContent(new String[] {"single","false"});
					issingle=false;
				}
				if (issingle && cntr==1) { //Die eine richtige Antwort muss jetzt eine Fraction von 100% erhalten, die anderen 0
					for (XMLObject ans : xmldata.getAllChildren("answer")) {
						if (Hilfsfunktionen.getFractionOfAnswerObject(ans)>0) { //richtige Antwort
							ans.addAttribute("fraction", "100.0"); 
						} else { //falsche Antwort
							ans.addAttribute("fraction", "0.0");
						}
					}
				} else { // mehrere Antworten erlaubt
					for (XMLObject ans : xmldata.getAllChildren("answer")) {
						if (Hilfsfunktionen.getFractionOfAnswerObject(ans)>0) { //richtige Antwort
							ans.addAttribute("fraction", String.format(Locale.ENGLISH, "%.4f", fracr)); 
						} else { //falsche Antwort
							ans.addAttribute("fraction", String.format(Locale.ENGLISH, "%.4f", fracw)); 
						}
					}
				}
			}
			break;
		case numerical: //Numerische Antwort
			System.err.println("In numerical check");
			//tolerance und unit testen //tolerance ist in der Answer
			for (XMLObject ans : xmldata.getAllChildren("answer")) {
				deepCheckField(ans, new String[] {"tolerance"}, "0.0", null);
			}
			
			/*
			 * Beispiel
			 * <units>
			 *   <unit>
			 *     <multiplier>1</multiplier>
			 *     <unit_name>T</unit_name>
			 *   </unit>
			 *   <unit>
			 *     <multiplier>1000</multiplier>
			 *     <unit_name>mT</unit_name>
			 *   </unit>
			 * </units>
			 * <unitgradingtype>0</unitgradingtype>
			 * <unitpenalty>0.1000000</unitpenalty>
			 * <showunits>0</showunits>
			 * <unitsleft>0</unitsleft>
			 */
			break;
		default:
		}
	}
	
	private void checkField(String fieldname, String defaultvalue) {
		checkField(xmldata, fieldname, defaultvalue);
	}
	private void checkField(String fieldname, String defaultvalue, String[] allowedvalues) {
		checkField(xmldata, fieldname, defaultvalue,allowedvalues);
	}

	/**
	 * überprüft ob das Feld <fieldname> in einem XML-Objekt enthalten ist, wenn nicht wird es erzeugt und mit
	 * dem Default-wert gefüllt
	 * @param xmld das zu prüfende XML-Objekt
	 * @param fieldname das zu suchende Kind-Objekt
	 * @param defaultvalue der Default-Wert dieses Kind-Objektes
	 */
	private static void checkField(XMLObject xmld, String fieldname, String defaultvalue) {
		if (xmld.getContent(new String[] {fieldname})==null) xmld.setContent(new String[] {fieldname, defaultvalue});
	}
	
	/**
	 * überprüft ob das Feld <fieldname> in einem XML-Objekt enthalten ist und nur erlaubte Werte enthält.
	 * Wenn nicht wird es erzeugt und mit dem Default-wert gefüllt
	 * @param xmld das zu prüfende XML-Objekt
	 * @param fieldname das zu prüfende Kind-Objekt
	 * @param defaultvalue der Default-Wert dieses Kind-Objektes
	 * @param allowedvalues ein String-Array mit erlaubten Werten für das Kind-Objekt
	 */
	private static void checkField(XMLObject xmld, String fieldname, String defaultvalue, String[] allowedvalues) {
		String content = xmld.getContent(new String[] {fieldname});
		if (content == null | (!isStringInArray(content,allowedvalues) && allowedvalues != null && allowedvalues.length>0)) {
			xmld.setContent(new String[] {fieldname, defaultvalue});
		}
	}
	
	private static boolean isStringInArray(String content, String[] array) {
		if (array==null || content == null) return false;
		for (String i : array) if (i.equals(content)) return true;
		return false;
	}

	private void deepCheckField(String[] fieldsequence, String defaultvalue) {
		deepCheckField(xmldata, fieldsequence, defaultvalue, new String[] {});
	}

	private void deepCheckField(String[] fieldsequence, String defaultvalue, String[] allowedvalues) {
		deepCheckField(xmldata, fieldsequence, defaultvalue, allowedvalues);
	}
	
	private void deepCheckField(XMLObject xmld, String[] fieldsequence, String defaultvalue, String[] allowedvalues) {
		if (fieldsequence == null || defaultvalue == null || fieldsequence.length==0) return;
		if (fieldsequence.length==1) checkField(xmld,fieldsequence[0],defaultvalue,allowedvalues);
		else {
			XMLObject child = xmld.getChild(fieldsequence[0]);
			if (child == null) {
				child = new XMLObject(fieldsequence[0]);
				xmld.addChild(child);
			}
			String[] nseq = new String[fieldsequence.length-1];
			for (int i=0; i<nseq.length;i++) nseq[i]=fieldsequence[i+1];
			deepCheckField(child, nseq, defaultvalue,allowedvalues);
		}
	}
	
	private void deepCheckAttribute(String[] fieldsequence, String defaultvalue) {
		String attr =xmldata.getAttribute(fieldsequence); 
		if (attr==null || attr.equals("")) xmldata.addAttribute(fieldsequence, defaultvalue);
	}
	

	
	/**
	 * wenn diese Frage eine Multiple-Choice-Frage ist, wird eine String-Darstellung, wie sie in einer
	 * Multiple-Choice-Datei stehen würde erzeugt.
	 * Bilder werden entfernt!
	 * Wenn es keine MultipleChoice-Frage ist wird null zurückgegeben
	 * @return MCString oder null
	 */
	public String getStringRepresentationOfMCQuestion() {
		if (this.getType()!=multichoice) return null;
		String out= Hilfsfunktionen.removeCData(this.getQuestiontext())+"\n";
		out+="&"+Hilfsfunktionen.removeCData(this.getName())+"\n";
		//TODO Möglichkeit finden nicht nur richtige und falsche +/- Antworten sondern auch mit individuellen fractions auszugeben
		for (XMLObject ans : xmldata.getAllChildren("answer")) {
			out+=Hilfsfunktionen.mcanswerToMcText(ans)+"\n";
		}
		if (this.getGeneralfeedback()!=null && !Hilfsfunktionen.removeCData(this.getGeneralfeedback()).equals("")) {
			out+="#"+Hilfsfunktionen.removeCData(this.getGeneralfeedback())+"\n";
		}
		return out;
	}
	
	
	
	@Override
	public String toString() {
		this.checkFieldsNeededForType();
		this.checkHTMLFormat();
		return xmldata.toString();
	}
	
	public XMLObject toXML() {
		this.checkFieldsNeededForType();
		return xmldata.clone();
	}

}
