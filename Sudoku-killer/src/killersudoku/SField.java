/**
 * 
 */
package killersudoku;

import java.util.HashSet;

/**
 * @author peter
 *
 */
public class SField {
	int xpos, ypos; // Position of this field x from 1 to 9, y the same
	private int value = 0; // 0 means not yet set - otherwise from 1 to 9
	private Possibilities possiblevalues = new Possibilities(); // All Values 987654321 are possible

	/**
	 * @param xpos
	 * @param ypos
	 */
	public SField(int xpos, int ypos) {
		this.xpos = xpos;
		this.ypos = ypos;
	}
	
	/**
	 * removes the Number n from the list of possibilities
	 * @param n which possibility has to be removed
	 * @return true if was removed, false if has been removed before
	 */
	public boolean removePossibility(int n) {
		return possiblevalues.removePossibility(n);
	}
	
	public int getXpos() {
		return xpos;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	public int getValue() {
		if (value==0 && possiblevalues.anz()==1) {
			value = possiblevalues.sum();
		}
		return value;
	}

	public void setValue(int value) {
		this.value = value;
		possiblevalues = new Possibilities(1<<(value-1));
	}

	public Possibilities getPossiblevalues() {
		if (value>0) return new Possibilities(1<<(value-1));
		return possiblevalues;
	}

	public void setPossiblevalues(Possibilities possiblevalues) {
		this.possiblevalues = possiblevalues;
	}

	@Override
	public String toString() {
		return "SField " + xpos + "" + ypos + "-" + value + " pos:" + possiblevalues;
	}

	/**
	 * @return the number of the square-Field in which this field is listed (from 0 to 8)
	 */
	public Object getSquareNr() {
		return 3*((ypos-1)/3)+((xpos-1)/3);
	}
	
	
	
	

}
