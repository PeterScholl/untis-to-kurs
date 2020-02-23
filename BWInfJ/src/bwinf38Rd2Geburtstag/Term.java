/**
 * 
 */
package bwinf38Rd2Geburtstag;

/**
 * @author peter
 * Term is a static class, means its Attributes can't be changed after it was constructed
 */
public class Term {
	private Term operand1, operand2;
	int wert;
	int anzZiffern;
	char operator; //E ziffernfolge, +, -, *, /

	/**
	 * Konstruktor für eine simple Ziffernfolge
	 */
	public Term(int ziffer, int anzahl) {
		this.operator = 'E';
		this.wert = ziffer;
		for (int i=1; i<anzahl;i++) wert = 10*wert+ziffer;
		this.anzZiffern=anzahl;
	}

	/**
	 * @param operand1
	 * @param operand2
	 * @param operator
	 * @throws StringPartWithFloatValue 
	 * @throws IllegalOperand 
	 */
	public Term(Term operand1, Term operand2, char operator) throws StringPartWithFloatValue, IllegalOperand {
		this.operand1 = operand1;
		this.operand2 = operand2;
		this.operator = operator;
		this.anzZiffern=operand1.getAnzZiffern()+operand2.getAnzZiffern();
		switch(operator) {
		case '+':
			this.wert=operand1.getWert()+operand2.getWert();
			break;
		case '-':
			this.wert=operand1.getWert()-operand2.getWert();
			break;
		case '*':
			this.wert=operand1.getWert()*operand2.getWert();
			break;
		case '/':
			if (operand2.getWert() == 0 || operand1.getWert() % operand2.getWert() != 0)
				throw new StringPartWithFloatValue();
			this.wert=operand1.getWert()/operand2.getWert();
			break;
		default:
			throw new IllegalOperand();
			
		}
	}

	public Term getOperand1() {
		return operand1;
	}

	public Term getOperand2() {
		return operand2;
	}

	public int getWert() {
		return wert;
	}

	public char getOperand() {
		return operator;
	}

	public int getAnzZiffern() {
		return anzZiffern;
	}

	@Override
	public String toString() {
		if (this.operator=='E') return ""+wert;
		return "("+operand1.toString()+this.operator+operand2.toString()+")";
	}
	
	
	
	

	
}
