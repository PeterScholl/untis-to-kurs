package bwinf38Rd2Stromrallye;

import java.util.Arrays;

@SuppressWarnings("unused")
public class Labyrinth {
	// labyrinth is a string-Array where
	// # are Walls, [space] are ways, S is start, D is destination
	// n is used to signalize whether there is a nextline in case the labyrinth is
	// made by
	// a single string
	private String[] labyrinth = new String[1];
	private String backtrackpath = "";
	private boolean[][] pathmarks;

	public Labyrinth() {
		this("#D#n# #n#S#");
	}

	public Labyrinth(String single) {
		if (!checkstring(single)) { //The offered String is not valid
			labyrinth = single.split("#D#n# #n#S#");
		} else {
			labyrinth = single.split("n");
		}
	}

	/**
	 * checkstring evaluates if the ommitted string is valid
	 * @param s String with labyrinth
	 * @return true or false
	 */
	public static boolean checkstring(String s) {
		if (s==null || s.length()==0) return false;
		String[] lines = s.split("n");
		// check wether all lines are of same length
		int nrows = lines[0].length(); //Number of rows derived from first line
		for (int i=1; i<lines.length;i++) {
			if (lines[i].length() != nrows) return false;
		}
		//check whether S and D exist and exist only once!
		if (s.indexOf('S')==-1 || s.lastIndexOf('S')!=s.indexOf('S')) return false;
		if (s.indexOf('D')==-1 || s.lastIndexOf('D')!=s.indexOf('D')) return false;
		//seems to be valid
		return true;
	}
	/**
	 * solves the current Labyrinth by backtracking algorithm
	 * @return string with the way from S to D: N - north, S W E or empty string, if no possible solution
	 */
	public String solve() {
		//evaluate startposition - marked as S in the array
		int sposx = 0; 
		int sposy = 0;
		for (int i=0; i<labyrinth.length;i++) {//check all lines
			if (labyrinth[i].indexOf('S')!=-1) { // S was found in this line
				sposy = labyrinth.length-1-i;
				sposx = labyrinth[i].indexOf('S');
				break;
			}
		}
		//call backtrack algorithm and prepare attributes
		backtrackpath="";
		pathmarks = new boolean[labyrinth[0].length()][labyrinth.length];
		backtrack(sposx,sposy);
		return backtrackpath;		
	}
	
	private boolean backtrack(int xpos, int ypos) {
		// was destination reached?
		if (isChar(xpos,ypos,'D')) return true;
		// stepped on a wall?
		if (isChar(xpos,ypos,'#')) return false;
		// valid position?
		if (!isChar(xpos,ypos,' ') && !isChar(xpos,ypos,'S')  ) return false; 
		// have I been here before?
		if (pathmarks[xpos][ypos]==true) return false;
		//mark position!!
		pathmarks[xpos][ypos]=true;
		// check north
		if (backtrack(xpos,ypos+1)) { // destination was found!
			backtrackpath="N"+backtrackpath; // add an N at the beginning of the path
			return true;
		} 
		// check south
		if (backtrack(xpos,ypos-1)) { // destination was found!
			backtrackpath="S"+backtrackpath; // add an S at the beginning of the path
			return true;
		} 
		// check east
		if (backtrack(xpos+1,ypos)) { // destination was found!
			backtrackpath="E"+backtrackpath; // add an E at the beginning of the path
			return true;
		} 
		// check west
		if (backtrack(xpos-1,ypos)) { // destination was found!
			backtrackpath="W"+backtrackpath; // add an W at the beginning of the path
			return true;
		} 
		//demark position - leaving backwards !!
		pathmarks[xpos][ypos]=false;
		return false;
	}
	/**
	 * checks if position is valid and contains char c
	 * @param xpos
	 * @param ypos
	 * @param c
	 * @return
	 */
	public boolean isChar(int xpos, int ypos, char c) {
		if (xpos<0 || xpos >= labyrinth[0].length()) return false;
		if (ypos<0 || ypos >= labyrinth.length) return false;
		if (labyrinth[labyrinth.length-1-ypos].charAt(xpos)==c) return true;
		return false;	
	}
	
	@Override
	public String toString() {
		String out="";
		for (int i=0; i<labyrinth.length;i++) {
			out+=labyrinth[i]+"\n";
		}
		return out;
		//return "labyrinth=" + Arrays.toString(labyrinth);
	}
	
	

}
