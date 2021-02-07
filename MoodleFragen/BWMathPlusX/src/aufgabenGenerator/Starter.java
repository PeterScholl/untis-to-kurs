package aufgabenGenerator;

public class Starter {

	public static void main(String[] args) {
		testQuestionXML();
		System.exit(0);
		
		Quiz q = new Quiz();
		for (int i=18; i<256; i+=1) {
			q.addQuestion(Generator.erzKlammerAufl1(Generator.zufallszahl(2, 10, 1), Generator.zufallszahl(1, 9, 10), Generator.zufallszahl(2, 8, 1)));
			//q.addQuestion(Generator.erzKlammerAufl2(Generator.zufallszahl(2, 10, 1), Generator.zufallszahl(1, 9, 10), Generator.zufallszahl(2, 8, 1)));
			//q.addQuestion(Generator.erzKlammerAufl3(Generator.zufallszahl(2, 10, 1), Generator.zufallszahl(1, 9, 10), Generator.zufallszahl(2, 8, 1)));
			//q.addQuestion(Generator.erzKlammereAus1(Generator.zufallszahl(2, 10, 1), Generator.zufallszahl(3, 10, 10), Generator.zufallszahl(11, 19, 1)));
			//q.addQuestion(Generator.erzKlammereAus2(Generator.zufallszahl(2, 10, 1), Generator.zufallszahl(3, 10, 10), Generator.zufallszahl(2, 19, 1)));
			//q.addQuestion(Generator.erzKlammereAus3(Generator.zufallszahl(2, 10, 1), Generator.zufallszahl(3, 10, 10), Generator.zufallszahl(2, 19, 1)));
			//q.addQuestion(Generator.erzKlammereAus4(Generator.zufallszahl(2, 10, 1), Generator.zufallszahl(3, 10, 10), Generator.zufallszahl(2, 19, 1)));
			//q.addQuestion(Generator.berechneGeschickt1(Generator.zufallszahl(3, 9, 1), Generator.zufallszahl(3, 10, 10)));
			//q.addQuestion(Generator.erzPotenzieren1wrap(2,7,2,6,1000));
			//q.addQuestion(Generator.erzPotenzieren2wrap(2,10,2,6,1000));
			//************ACHTUNG************** durch den Faktor in Generator.zufallszahl, kann man einstellen,
			//dass die Aussagen wahr sind
			//q.addQuestion(Generator.erzIstTeilbar(Generator.zufallszahl(201, 5000, 2), 2));
			//q.addQuestion(Generator.erzIstTeilbar(Generator.zufallszahl(201, 3000, 3), 3));
			//q.addQuestion(Generator.erzIstTeilbar(Generator.zufallszahl(201, 4000, 3), 3));
			//q.addQuestion(Generator.erzIstTeilbar(Generator.zufallszahl(201, 2000, 5), 5));
			//q.addQuestion(Generator.erzIstTeilbar(Generator.zufallszahl(201, 1000, 10), 10));
			//q.addQuestion(Generator.erzIstTeilbar(Generator.zufallszahl(201, 3000, 4), 4));
			//q.addQuestion(Generator.erzIstTeilbar(Generator.zufallszahl(201, 3000, 4), 4));
			//q.addQuestion(Generator.erzIstXPrimzahl(i));
			//q.addQuestion(Generator.erzQDualzahlZuHexzahlAngeben(i));
			//q.addQuestion(Generator.erzQHexzahlZuDualzahlAngeben(i));
		}
		
		/*
		String mctest="Für welchen Wert steht x in x+5=7\n+2\n--2\n-5";
		q.addQuestion(Generator.gibMultiChoiceAusString(mctest));
		String mctest2="Für welchen Wert steht x in x²=4\n+2\n+-2\n-4\n-1";
		q.addQuestion(Generator.gibMultiChoiceAusString(mctest2));
		*/
		// Multiplechoice - Generator 
		//q.append(Generator.gibQuizAusMultiChoiceDatei());
		
		
		System.out.println(q);
		//Generator.writeQuizToXMLFile(q);
		
		
	}
	
	public static void testQuestionXML() {
		Question q = new Question(Question.multichoice);
		System.out.println(q);
	}

}
