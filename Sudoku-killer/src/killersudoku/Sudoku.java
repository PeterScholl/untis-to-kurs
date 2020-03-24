package killersudoku;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Sudoku {
	private SField[][] board = new SField[9][9];
	// TODO: change to private
	public ArrayList<FieldSum> stdlimitations = new ArrayList<FieldSum>();
	public ArrayList<FieldSum> givenlimitations = new ArrayList<FieldSum>();
	public ArrayList<FieldSum> calclimitations = new ArrayList<FieldSum>();

	/**
	 * 
	 */
	public Sudoku() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				board[i][j] = new SField(i + 1, j + 1);
			}
		}
		// Limitations for rows and columns
		for (int i = 0; i < 9; i++) {
			FieldSum limcol = new FieldSum(45);
			FieldSum limrow = new FieldSum(45);
			for (int j = 0; j < 9; j++) {
				limcol.addField(board[i][j]);
				limrow.addField(board[j][i]);
			}
			stdlimitations.add(limcol);
			stdlimitations.add(limrow);
		}
		// Limitations for the 9 3x3squares
		for (int i = 0; i < 9; i++) {
			FieldSum limsquare = new FieldSum(45);
			for (int j = 0; j < 9; j++) {
				int y = (i / 3) * 3 + j / 3;
				int x = (i % 3) * 3 + j % 3;
				limsquare.addField(board[x][y]);
			}
			stdlimitations.add(limsquare);
		}
	}

	/**
	 * Bestimmt für alle Limitations (FieldSum s) die für diese Summe und Anzahl verwendbaren
	 * Zahlen und löscht andere Zahlen aus allen Feldern die in der jeweiligen FieldSum enthalten sind
	 */
	public void checkLimitations() {
		// in the ideal case for every limitations all possibilities would be calculated
		// and
		// every consequence for the possibilities should be updated.
		for (FieldSum fs : givenlimitations) {
			ArrayList<Possibilities> pos = SudokuStatic.possibilities(fs.sum, fs.numFields());
			int union = 0;
			for (Possibilities i : pos) {
				union |= i.possiblevalues;
			}
			for (SField sf : fs.getFields()) {
				if ((sf.getPossiblevalues().possiblevalues & ~union) > 0) { // update
					sf.setPossiblevalues(new Possibilities(union));
					System.out.println("Feld " + sf.getXpos() + "," + sf.getYpos() + " aktualisiert!");
				}
			}
		}
	}
    public void checkSquareCoverage(ArrayList<Integer> list) {
    	checkSquareCoverage(list, false);
    }
	
	public void checkSquareCoverage(ArrayList<Integer> list, boolean inverse) {
		// The given Combination of Squares is checked, which limitation it completely
		// covers -> generates
		// new Limitation
		FieldSum temp = new FieldSum(list.size() * 45, true); // Mark as unconnected - fields could have identical
																// values
		if (inverse) temp.setSum((9-list.size())*45);

		for (int j = 0; j < 9; j++) {
			for (int j2 = 0; j2 < 9; j2++) {
				if (inverse ^ list.contains(board[j][j2].getSquareNr())) //XOR Opperator, when inverse true result of contains is inverted 
					temp.addField(board[j2][j]);
			}
		}
		// search all limitations which are coverd completely by Fieldsum temp and
		// substract them from temp
		for (FieldSum fstemp : calclimitations) {
			if (fstemp.isCovered(temp)) {
				temp.minus(fstemp);
			}
		}
		// Check if resulting Limitation leads to new results - store into
		// calclimitations
		if (temp.numFields() < 3 || temp.sum < 45) {
			temp.checkConnected();
			//if (temp.checkConnected() && temp.numFields()>0)
			if (temp.numFields()>0)
				calclimitations.add(temp);
			System.out.println("Limitation with less than 3 Fields or sum < 45  " + temp);
		}
	}

	public void checkColumnAndRowCoverage() {
		// Every Column is checked which limitation it completely covers -> generates
		// new Limitation
		for (int il = 0; il < 9; il++) { // all columns as start
			for (int iu = il; iu < 9; iu++) { // rest columns as end

				FieldSum tempC = new FieldSum((iu - il + 1) * 45, true); // Mark as unconnected - fields could have
																			// identical values
				FieldSum tempR = new FieldSum((iu - il + 1) * 45, true); // Mark as unconnected - fields could have
																			// identical values
				for (int j = 0; j < 9; j++) {
					for (int j2 = il; j2 <= iu; j2++) {
						tempC.addField(board[j2][j]);
						tempR.addField(board[j][j2]);
					}
				}
				// search all limitations which are coverd completely by Fieldsum temp and
				// substract them from temp
				for (FieldSum fstemp : calclimitations) {
					if (fstemp.isCovered(tempC)) {
						tempC.minus(fstemp);
					}
					if (fstemp.isCovered(tempR)) {
						tempR.minus(fstemp);
					}
				}
				// Check if resulting Limitation leads to new results - store into
				// calclimitations
				if (tempC.sum < 45 && tempC.numFields()>0) {
					tempC.checkConnected();
					//if (tempC.checkConnected() && tempC.numFields()>0)
					if (tempC.numFields()>0)
						calclimitations.add(tempC);
					System.out.println("Limitation with less than 3 Fields or sum < 45  " + tempC);
				}
				if (tempR.numFields() > 0 && tempR.sum < 45) {
					tempR.checkConnected();
					//if (tempR.checkConnected() && tempR.numFields()>0)
					if (tempR.numFields()>0)
						calclimitations.add(tempR);
					System.out.println("Limitation with less than 3 Fields or sum < 45  " + tempR);
				}
			}

		}
	}

	public void readLimFile(String file) {
		try {
			System.out.println("Working Directory: " + System.getProperty("user.dir"));
			System.out.println("\n| Datei einlesen |\n");

			FileReader fr = new FileReader("src/killersudoku/" + file);
			BufferedReader reader = new BufferedReader(fr);

			boolean[][] check = new boolean[9][9];
			int sumcheck = 0;

			String t = reader.readLine();
			int n = Integer.parseInt(t); // Number of Limitations
			for (int i = 0; i < n; i++) {
				t = reader.readLine();
				String[] limtemp = t.split(",");
				int limsum = Integer.parseInt(limtemp[limtemp.length - 1]);
				sumcheck += limsum;
				FieldSum limit = new FieldSum(limsum);
				for (int j = 0; j < limtemp.length - 1; j++) {
					int x = Integer.parseInt(limtemp[j].substring(0, 1)) - 1;
					int y = Integer.parseInt(limtemp[j].substring(1)) - 1;
					if (check[x][y])
						System.out.println("FEHLER: Feld wird doppelt benutzt!!");
					limit.addField(board[x][y]);
					check[x][y] = true;
				}
				limit.setName("(" + i + ")");
				givenlimitations.add(limit);
				calclimitations.add((FieldSum) limit.clone());
			}
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					System.out.print(check[j][i] == true ? "1" : "0");
				}
				System.out.println("");
			}
			if (sumcheck != 9 * 45)
				System.out.println("FEHLER: Summe aller Limitations stimmt nicht!");

			reader.close();
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		// add for all Fields the number of the givenlimitation they are included
		for (int i=0; i<givenlimitations.size();i++) {
			for (SField f : givenlimitations.get(i).getFields()) f.setGivenlimitationNr(i);
		}
	}

	public ArrayList<FieldSum> getLimitations() {
		return givenlimitations;
	}

	public SField getFieldOn(int x, int y) {
		return board[x - 1][y - 1];
	}

	@Override
	public String toString() {
		return "Sudoku [board=" + Arrays.toString(board) + ", limitations=" + givenlimitations + "]";
	}

	public void checkSolutions() {
		// for (FieldSum fs : givenlimitations) fs.checkSolutions();
		for (FieldSum fs : calclimitations)
			fs.checkSolutions();
	}
	public void checkSolutions2() {
		// for (FieldSum fs : givenlimitations) fs.checkSolutions();
		for (FieldSum fs : calclimitations)
			fs.checkSolutions2(this.giveAllFieldSumsCoveredBy(fs));
		for (FieldSum fs : stdlimitations) fs.checkSolutions2(this.giveAllFieldSumsCoveredBy(fs));
	}

	
	public void checkSolutions(ArrayList<FieldSum> limits) {
		for (FieldSum fs : limits)
			fs.checkSolutions();
	}

	public void removeFieldsWithValuesFromLimitations() {
		// for (FieldSum fs: givenlimitations) fs.removeFieldsContainingValue();
		for (FieldSum fs : calclimitations)
			fs.removeFieldsContainingValue();
		// now empty Limitations have to be removed
		// givenlimitations.removeIf(n -> n.isEmpty());
		calclimitations.removeIf(n -> n.isEmpty());
	}

	/**
	 * entfernt Limitations (FieldSums), die keine Information mehr bringen, also z.B. doppelt vorliegen
	 * oder bei denen alle Felder schon bekannt sind.
	 */
	public void removeUnneededLimitations() {
		// TODO probably reduce Limitations before? Unnötige Felder rauswerfen
		System.out.print("Removing duplicate Limitations from: " + calclimitations.size());
		calclimitations = SudokuStatic.removeDuplicates(calclimitations);
		System.out.println(" to " + calclimitations.size());
		System.out.print("Removing unneeded Limitations from: " + calclimitations.size());
		calclimitations.removeIf(n -> !n.hasInformation());
		System.out.println(" to " + calclimitations.size());
	}
	
	public void reduceBigLimitations() {
		ArrayList<FieldSum> temp = new ArrayList<FieldSum>();
		for (FieldSum fs : calclimitations) {
			temp.add(fs);
		}
		for (FieldSum fs1 : calclimitations) {
			for (FieldSum fs2 : temp) {
				if (!fs1.equals(fs2) && fs1.isCovered(fs2)) {
					System.out.println(""+fs2+" wird um "+fs1+" reduziert");
					fs2.minus(fs1);
				}
			}
		}
	}
	
	public ArrayList<FieldSum> giveAllFieldSumsCoveredBy(FieldSum fs) {
		ArrayList<FieldSum> ret = new ArrayList<FieldSum>();
		for (FieldSum l : calclimitations) {
			if (!l.equals(fs) && l.isCovered(fs)) ret.add(l);
		}
		return ret;
	}

}
