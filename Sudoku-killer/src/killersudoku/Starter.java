package killersudoku;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Starter implements ActionListener, MouseListener { 
	// als Singleton ausführen - nur so zum Spaß!
	// Eine (versteckte) Klassenvariable vom Typ der eigenen Klasse
	private static Starter instance;
	private Sudoku s;
	private MyCanvas mc;

	// Verhindere die Erzeugung des Objektes über andere Methoden
	private Starter() {
	}

	// Eine Zugriffsmethode auf Klassenebene, welches dir '''einmal''' ein konkretes
	// Objekt erzeugt und dieses zurückliefert.
	public static Starter getInstance() {
		if (Starter.instance == null) {
			Starter.instance = new Starter();
		}
		return Starter.instance;
	}

	public static void main(String[] args) {
		// Jetzt wird das Singleton Object erzeugt und dessen mymain-Methode gestartet
		Starter.getInstance().mymain(args);
	}

	private void mymain(String[] args) {
		mc = new MyCanvas(800, 800);
		// Diese Klasse dient als ActionListener 
		mc.setExternalActionListener(this);
		mc.setExternalMouseListener(this);
		s = new Sudoku();
		s.readLimFile("sudoku3.txt");
		System.out.println(s);
		this.runtests();
		drawSudoku(s, mc);
	}

	private static void drawSudoku(Sudoku s, MyCanvas c) {
		c.setOnlyBackground(true);
		c.clearScreen();
		int width = 800;
		int height = 800;
		int breite = (width - 20) / 9;
		int hoehe = (height - 20) / 9;
		int dist = 3;
		for (FieldSum fs : s.getLimitations()) {
			for (SField field : fs.getFields()) {
				int x = field.getXpos();
				int y = field.getYpos();
				double xm = (x - 0.5) * breite;
				double ym = (y - 0.5) * hoehe;
				for (int i = -1; i <= 1; i += 2) {
					if (!fs.containsPos(x + i, y)) {
						c.drawLine(10 + xm + i * (breite / 2 - dist), 10 + ym - (hoehe / 2 - dist),
								10 + xm + i * (breite / 2 - dist), 10 + ym + (hoehe / 2 - dist));
					}
					if (!fs.containsPos(x, y + i)) {
						c.drawLine(10 + xm - (breite / 2 - dist), 10 + ym + i * (hoehe / 2 - dist),
								10 + xm + (breite / 2 - dist), 10 + ym + i * (hoehe / 2 - dist));
					}

				}
			}
			// Zahl in obere rechte Ecke schreiben
			SField top = fs.getUpperRightField();
			c.writeText(13 + (top.getXpos() - 1) * breite, 23 + (top.getYpos() - 1) * hoehe, "" + fs.sum, 10);
		}
		// Draw the Fields
		for (int i = 0; i < 9; i++) { // x-Koordinate
			for (int j = 0; j < 9; j++) { // y-Koordinate
				SField f = s.getFieldOn(i + 1, j + 1);
				if (f.getValue() > 0) { // eindeutiger Wert
					c.writeText(20 + i * breite, 2 + (j + 1) * hoehe, "" + f.getValue(), hoehe - 2);
				} else {
					int x_0 = 34 + i * breite;
					int y_0 = 34 + j * hoehe + 10;
					for (int p = 0; p < 9; p++) {
						if (f.getPossiblevalues().hasPossibility(p + 1)) {
							c.writeText(x_0 + p % 3 * breite / 5, y_0 + (p / 3) * hoehe / 5, "" + (p + 1), 10);
						}
					}
				}

			}
		}
		c.restoreView();
	}

	/**
	 * Diese Methode wird am Anfang aufgerufen und beinhaltet den ein oder anderen Test
	 */
	public void runtests() {
		System.out.println(SudokuStatic.possibilities(20, 3, new Possibilities(), new Possibilities(1 << 2)));

		System.out.println(SudokuStatic.isPossible(20, 3, new Possibilities()));
		System.out.println("Ist die Summe 20 mit 3 Zahlen aus " + new Possibilities(255 - 15) + " moeglich?:"
				+ SudokuStatic.isPossible(20, 3, new Possibilities(255 - 15)));
		SField t = new SField(4, 4);
		t.removePossibility(6);
		System.out.println(t.getPossiblevalues());
		System.out.println(SudokuStatic.possibilities(20, 3));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Action external performed" + e.getActionCommand());
		if (e.getActionCommand().equals("check possibilities")) {
			System.out.println("Check Standard Limitations");
			s.checkSolutions(s.stdlimitations);
			System.out.println("Check reg Limitations");
			s.checkSolutions();
			s.removeUnneededLimitations();
		} else if (e.getActionCommand().equals("test")) {
			/* s.checkLimitations();
			String[] t = {};
			try {
				String tempstring = (String) JOptionPane.showInputDialog(null,
						"Enter coma separated lists of squares(0-8) which should be combined:", "Customized Dialog",
						JOptionPane.PLAIN_MESSAGE, null, null, "0,1,2,3,4,5");

				t = tempstring.split(",");
			} catch (Exception ex) {
				System.out.println("Exception" + ex);
			}
			ArrayList<Integer> l = new ArrayList<Integer>();
			for (String t2 : t) {
				try {
					l.add(Integer.parseInt(t2));
				} catch (Exception ex) {
					System.out.println("String typing mismatch");
				}
			}
			s.checkSquareCoverage(l);
			*/
			s.checkSolutions2();
			s.reduceBigLimitations();
		} else if (e.getActionCommand().equals("column cover")) {
			s.checkColumnAndRowCoverage();
			s.removeFieldsWithValuesFromLimitations();
			for (int i=0;i<9;i++) {
				ArrayList<Integer> t = new ArrayList<Integer>();
				t.add(i);
				s.checkSquareCoverage(t);
				s.checkSquareCoverage(t,true); //inverse
			}
		} else if (e.getActionCommand().equals("Print Info")) {
			System.out.println("\n\nInformation about Sudoku");
			System.out.println("Number of calclimitations: " + s.calclimitations.size());
			for (FieldSum l : s.calclimitations) {
				System.out.println(" - " + l);
			}
		}
		drawSudoku(s, mc);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Mouse Clicked: " + e);

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
