package killersudoku;

import java.util.ArrayList;

public class FieldSum {
	private String name = "";
	int sum;
	ArrayList<SField> fields = new ArrayList<SField>();
	boolean unconnected = false; // true if the fields do not depend on each other - could have same numbers
	Possibilities needed = new Possibilities(0);

	/**
	 * @param sum
	 */
	public FieldSum(int sum) {
		this.sum = sum;
	}

	public FieldSum(int sum, boolean unconnected) {
		this.sum = sum;
		this.unconnected = unconnected;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		FieldSum fs = new FieldSum(sum);
		fs.unconnected = this.unconnected;
		fs.fields = this.getFields();
		fs.needed = new Possibilities(needed);
		fs.name = this.name;
		return fs;
	}

	public void addField(SField f) {
		if (!fields.contains(f))
			fields.add(f);
	}

	public int numFields() {
		return fields.size();
	}

	public boolean containsPos(int x, int y) {
		for (SField sf : fields) {
			if (sf.getXpos() == x && sf.getYpos() == y)
				return true;
		}
		return false;
	}

	/**
	 * @return A copy(!) of the Array with all fields
	 */
	public ArrayList<SField> getFields() {
		ArrayList<SField> ret = new ArrayList<SField>(fields);
		return ret;
	}

	public boolean isEmpty() {
		return fields.size() == 0;
	}

	public SField getUpperRightField() {
		SField t = fields.get(0);
		for (SField i : fields) {
			if (i.getYpos() < t.getYpos()) {
				t = i;
			} else if (i.getYpos() == t.getYpos()) {
				if (i.getXpos() < t.getXpos()) {
					t = i;
				}
			}
		}
		return t;
	}

	/**
	 * @param temp
	 * @return true if this limitation is completely covered by limitation temp
	 */
	public boolean isCovered(FieldSum temp) {
		for (SField t : this.getFields()) {
			if (!temp.containsPos(t.getXpos(), t.getYpos()))
				return false;
		}
		return true;
	}

	/**
	 * If the limitation fstemp is completely covered by this limitation, this limitation is
	 * reduced by all the fields of fstemp and its sum
	 * @param fstemp
	 */
	public void minus(FieldSum fstemp) {
		if (fstemp.isCovered(this)) {
			for (SField s : fstemp.getFields()) {
				this.fields.remove(s);
			}
			this.sum -= fstemp.sum;
		}
	}

	/**
	 * checks if all Fields of this limitations (FieldSum) are connected - either in a row
	 * or column or a square or if they are all part of the same givenlimitation
	 * and sets Variable unconnected depending on this test 
	 * @return true if connected, false otherwise
	 */
	public boolean checkConnected() {
		for (int i = 0; i < fields.size() - 1; i++) {
			for (int j = i + 1; j < fields.size(); j++) {
				if (!(fields.get(i).getXpos() == fields.get(j).getXpos()
						|| fields.get(i).getYpos() == fields.get(j).getYpos()
						|| fields.get(i).getSquareNr() == fields.get(j).getSquareNr()
						|| fields.get(i).getGivenlimitationNr() == fields.get(j).getGivenlimitationNr())) {
					unconnected = true;
					return false;
				}
			}
		}
		unconnected = false;
		return true;
	}

	public void checkSolutions() {
		// check every possibility for the given Sum and number of fields
		needed = new Possibilities();
		if (unconnected)
			return; // no sense if unconnected
		Possibilities[] lsg = new Possibilities[fields.size()];
		for (int i = 0; i < lsg.length; i++)
			lsg[i] = new Possibilities(0);
		checkSolutions_rek(new int[lsg.length], 0, sum, lsg, new Possibilities());
		String out = "" + this + "\n";
		boolean updated = false;
		for (int i = 0; i < lsg.length; i++) {
			if ((fields.get(i).getPossiblevalues().possiblevalues & ~lsg[i].possiblevalues) > 0) {
				// Updated
				updated = true;
				out += "" + fields.get(i) + " updated! to: ";
				fields.get(i).setPossiblevalues(lsg[i]);
				out += "" + fields.get(i) + "\n";
			}
		}
		if (updated)
			System.out.println(out);
	}

	private boolean checkSolutions_rek(int[] aktwerte, int pos, int restsum, Possibilities[] lsg,
			Possibilities allowed) {
		if (pos == aktwerte.length - 1) { // we are at the last position - probably finished
			if (allowed.hasPossibility(restsum) && fields.get(pos).getPossiblevalues().hasPossibility(restsum)) { // yes
																													// it
																													// fits
				lsg[pos].addPossibility(restsum);
				// TODO: Die Kombination in aktwerte kann genutzt werden um notwendige Werte zu
				// ermitteln
				Possibilities temp = new Possibilities(0);
				for (int a : aktwerte) {
					temp.addPossibility(a);
				}
				needed.possiblevalues &= temp.possiblevalues;
				return true;
			} else {
				return false;
			}
		} else if (!SudokuStatic.isPossible(restsum, aktwerte.length - pos, allowed)) {
			return false;
		} else { // here the rekursion has to be done
			boolean possible = false;
			for (int i = 0; i < 9; i++) { // we are trying every possible number an this field
				if (fields.get(pos).getPossiblevalues().hasPossibility(i + 1) && allowed.hasPossibility(i + 1)) {
					aktwerte[pos] = i + 1;
					if (checkSolutions_rek(aktwerte, pos + 1, restsum - i - 1, lsg,
							allowed.removePossibilityC(i + 1))) {
						possible = true;
						lsg[pos].addPossibility(i + 1);
					}
				}
			}
			return possible; // true if at least one solution was found
		}
	}

	public void checkSolutions2(ArrayList<FieldSum> covering) {
		//TODO implement update of needed value if all fields are connected
		// check every possibility for the given Sum and number of fields
		Possibilities[] lsg = new Possibilities[fields.size()]; //Speicher für das Ergebnis
		for (int i = 0; i < lsg.length; i++)
			lsg[i] = new Possibilities(0); //auf 0 setzen
		//Rekursiv alle Kombinationen durchgehen und in lsg speichern
		checkSolutions2_rek(new int[lsg.length], 0, sum, lsg, covering);
		String out = "" + this + "\n";
		boolean updated = false;
		for (int i = 0; i < lsg.length; i++) {
			if ((fields.get(i).getPossiblevalues().possiblevalues & ~lsg[i].possiblevalues) > 0) {
				// Updated
				updated = true;
				out += "" + fields.get(i) + " updated! to: ";
				fields.get(i).setPossiblevalues(lsg[i]);
				out += "" + fields.get(i) + "\n";
			}
		}
		if (updated)
			System.out.println(out);
	}

	private boolean checkSolutions2_rek(int[] aktwerte, int pos, int restsum, Possibilities[] lsg, ArrayList<FieldSum> covering) {
		// calculate allowed possibilities - all minus those which are denied by peviosly choosen numbers
		Possibilities allowed = new Possibilities();
		for (int i=0;i<pos;i++) {
			if (this.fields.get(i).isConnected(this.fields.get(pos))) allowed.removePossibility(aktwerte[i]);
		}
		if (pos == aktwerte.length - 1) { // we are at the last position - probably finished
			if (allowed.hasPossibility(restsum) && fields.get(pos).getPossiblevalues().hasPossibility(restsum)) { 
				// yes it fits
				// lets check if there is a Fieldsum completely covered by this one, which is violated
				aktwerte[pos]=restsum; 
				boolean valid = true;
				for (FieldSum fs : covering) {
					int sum=0;
					for (SField f : fs.getFields()) {
						sum+=aktwerte[fields.indexOf(f)];
					}
					if (sum != fs.getSum()) valid = false;
				}
				if (valid) lsg[pos].addPossibility(restsum);
				return valid;
			} else {
				return false;
			}
		} else { // here the rekursion has to be done
			boolean possible = false;
			for (int i = 0; i < 9; i++) { // we are trying every possible number in this field
				if (fields.get(pos).getPossiblevalues().hasPossibility(i + 1) && allowed.hasPossibility(i + 1)) {
					aktwerte[pos] = i + 1;
					if (checkSolutions2_rek(aktwerte, pos + 1, restsum - i - 1, lsg, covering)) {
						possible = true;
						lsg[pos].addPossibility(i + 1);
					}
				}
			}
			return possible; // true if at least one solution was found
		}
	}
	
	public boolean removeFieldsContainingValue() {
		boolean found = false;
		for (SField f : this.getFields()) {
			if (f.getValue() > 0) {
				fields.remove(f);
				sum -= f.getValue();
				found = true;
			}
		}
		if (found)
			this.checkConnected();
		return found;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	@Override
	public String toString() {
		String out = name;
		for (SField f : getFields()) {
			if (unconnected) {
				out += "-" + f.getXpos() + "" + f.getYpos();
			} else {
				out += "+" + f.getXpos() + "" + f.getYpos();
			}
		}
		out += ":" + sum;
		return out;
	}

	@Override
	public boolean equals(Object obj) {
		/*
		 * Check if obj is an instance of FieldSum or not "null instanceof [type]" also
		 * returns false
		 */
		if (!(obj instanceof FieldSum)) {
			return false;
		}

		// typecast obj to Fieldsum so that we can compare data members
		FieldSum fs = (FieldSum) obj;
		if (fs.fields.size()!= this.fields.size()) return false;
		if (fs.sum != this.sum) return false;
		for (SField s : fs.fields) {
			if (!this.fields.contains(s)) return false;
		}
		return true;
	}

	/**
	 * @return false if every field that is part of this FieldSum is already known
	 */
	public boolean hasInformation() {
		for (SField f : fields) {
			if (f.getPossiblevalues().anz() > 1)
				return true;
		}
		return false;
	}

}