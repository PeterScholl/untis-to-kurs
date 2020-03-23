package killersudoku;

import java.util.ArrayList;

/**
 * @author peter
 *
 */
/**
 * @author peter
 *
 */
public class Possibilities {
	int possiblevalues = 0b111111111; // All Values 987654321 are possible

	/**
	 * 
	 */
	public Possibilities() {
	}

	/**
	 * @param possiblevalues
	 */
	public Possibilities(int possiblevalues) {
		this.possiblevalues = possiblevalues;
	}
	/**
	 * @param Possibilities to clone
	 */
	public Possibilities(Possibilities pos) {
		this.possiblevalues = pos.possiblevalues;
	}

	/**
	 * @return ArrayList of Possibilities as Integer
	 */
	public ArrayList<Integer> possibilitiesArray() {
		ArrayList<Integer>  result = new ArrayList<Integer>();
		for (int i=1;i<10;i++) {
			if (hasPossibility(i)) result.add(i);
		}
		return result;
	}
	
	/**
	 * removes the Number n from the list of possibilities
	 * @param n which possibility has to be removed
	 * @return true if was removed, false if has been removed before
	 */
	public boolean removePossibility(int n) {
		int a = 1<<(n-1);
		boolean retvalue = ((possiblevalues & a) == a);
		possiblevalues = possiblevalues & ~a;
		return retvalue;
	}
	/**
	 * removes the Number n from the list of possibilities
	 * @param n which possibility has to be removed
	 * @return Possibilities with new values
	 */
	public Possibilities removePossibilityC(int n) {
		Possibilities ret = new Possibilities(this.possiblevalues);
		ret.removePossibility(n);
		return ret;
	}

	
	
	/**
	 * adds the Number n to the list of possibilities
	 * @param n which possibility has to be added
	 * @return true if was added, false if has been added before
	 */
	public boolean addPossibility(int n) {
		int a = 1<<(n-1);
		boolean retvalue = !((possiblevalues & a) == a);
		possiblevalues = possiblevalues | a;
		return retvalue;
	}

	/**
	 * adds the Number n to the list of possibilities
	 * @param n which possibility has to be added
	 * @return Possibilites with new values
	 */
	public Possibilities addPossibilityC(int n) {
		Possibilities ret = new Possibilities(this.possiblevalues);
		ret.addPossibility(n);
		return ret;
	}
	
	
	/**
	 * @param n 
	 * @return true if n is a possibility
	 */
	public boolean hasPossibility(int n) {
		if (n<1 || n>9) return false;
		int a = 1<<(n-1);
		return ((possiblevalues & a) == a);
	}

	/**
	 * @return the sum of all elements of this possibility
	 */
	public int sum() {
		int sum=0;
		for (int i=0;i<9;i++) {
			if ((possiblevalues & 1<<i) == 1<<i) sum+=i+1;
		}
		return sum;
	}

	/**
	 * @return amount of used numbers in this possibility
	 */
	public int anz() {
		int anz=0;
		for (int i=0;i<9;i++) {
			if ((possiblevalues & 1<<i) == 1<<i) anz+=1;
		}
		return anz;
	}

	@Override
	public String toString() {
		String out="";
		for (int i=1;i<10;i++) {
			if ((possiblevalues & (1<<(i-1)))== (1<<(i-1))) out+=" "+i;
		}
		return out;
	}

	/**
	 * @param i
	 * @return
	 */
	public Possibilities removePossibilitiesUpToC(Integer i) {
		Possibilities ret = new Possibilities(possiblevalues);
		for (int j=1;j<=i;j++) {
			ret.removePossibility(j);
		}
		return ret;
	}


}
