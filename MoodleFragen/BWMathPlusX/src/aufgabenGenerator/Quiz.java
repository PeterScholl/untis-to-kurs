package aufgabenGenerator;

import java.util.ArrayList;

public class Quiz {
	private ArrayList<Question> fragen = new ArrayList<Question>();
	
	public void addQuestion(Question q) {
		if (q!= null )	fragen.add(q);
	}
	
	
	public void addQuestion(XMLObject x) {
		if (x==null) return;
		if (x.getBezeichnung().equals("quiz")) {
			x.toFirstChild();
			while (x.hasChildAccess()) {
				this.addQuestion(new Question(x.getCurrentChild()));
				x.toNextChild();
			}
		} else {
			this.addQuestion(new Question(x));
		}
	}
	
	public void append(Quiz q2) {
		if (q2==null) return;
		for (Question q : q2.fragen) {
			this.fragen.add(q);
		}
	}
	
	public int gibAnzQuestions() {
		return fragen.size();
	}

	/**
	 * gibt eine String-Darstellung im XML-Format
	 */
	@Override
	public String toString() {
		String out="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<quiz>\n";
		for (Question q : fragen) {
			out+=q.toString();
		}
		out+="\n</quiz>\n";
		return out;
	}
	
	public String toMCString() {
		String out="";
		for (Question q : fragen) {
			String frage = q.getStringRepresentationOfMCQuestion();
			if (frage != null) {
				out += frage+"\n";
			}
		}
		return out;
	}

	/**
	 * gibt ein String-Array aller Namen von Fragen in diesem Quiz
	 * zurück
	 * @return String-Array aller Namen
	 */
	public String[] questionsToStringArray() {
		String[] questionsNames = new String[fragen.size()];
		for (int i=0; i<fragen.size();i++) {
			questionsNames[i]=fragen.get(i).getName();
		}
		return questionsNames;
	}


	public Question getQuestion(int nr) {
		//System.out.println("Fehler finden: "+fragen);
		if (nr>=0 && nr<fragen.size()) return fragen.get(nr);
		return null;
	}


	public void deleteQuestion(int i) {
		if (i<fragen.size()) fragen.remove(i);		
	}
	
	

}
