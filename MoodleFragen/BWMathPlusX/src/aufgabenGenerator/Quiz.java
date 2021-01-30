package aufgabenGenerator;

import java.util.ArrayList;

public class Quiz {
	private ArrayList<Question> fragen = new ArrayList<Question>();
	
	public void addQuestion(Question q) {
		fragen.add(q);
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

	@Override
	public String toString() {
		String out="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<quiz>\n";
		for (Question q : fragen) {
			out+=q.toString();
		}
		out+="\n</quiz>\n";
		return out;
	}
	
	

}
