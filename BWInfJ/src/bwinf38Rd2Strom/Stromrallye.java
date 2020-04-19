package bwinf38Rd2Strom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import javax.swing.AbstractAction;

public class Stromrallye {
	int size; // Groesse des Spielbretts
	int robx, roby; // Position des Roboters
	int robl; // Ladung des Roboters
	int[][] spielfeld; // Ladungen auf dem Spielfeld
	int gesamtl; // Gesamtladung
	String weg = "";
	MyCanvas mc = null;

	public Stromrallye(int size, int x, int y, int l) {
		this.size = size;
		this.robx = x;
		this.roby = y;
		this.robl = l;
		this.gesamtl = l;
		spielfeld = new int[size][size];
	}

	public void addBatterie(int x, int y, int l) {
		spielfeld[x][y] = l;
		gesamtl += l;
	}


	@Override
	public String toString() {
		String out = "";
		for (int y = size - 1; y >= 0; y--) { // Zeilen rueckwaerts durchlaufen
			out += "*";
			for (int x = 0; x < size; x++) { // Spalten durchlaufen
				out += spielfeld[x][y] + "*";
			}
			out += "\n";
		}
		return out;
	}

	class MyKeyAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			//System.out.println("KeyBinding: " + e);
			switch (e.getActionCommand().charAt(0)) {
			case 'n':
			case 'i':
				// go North (if possible) :-)
				move('N');
				//System.out.println("Go West!! Live is peaceful there");
				break;
			case 's':
			case 'k':
				// go South (if possible) :-)
				move('S');
				break;
			case 'e':
			case 'l':
				// east
				move('E');
				break;
			case 'w':
			case 'j':
				// west
				move('W');
				break;
			case 'b':
				// backwards
				moveBack();
				break;
			case 'q':
				System.exit(0);
				break;
			default:
				System.out.println("Unknown Char pressed:" + e.getActionCommand());
			}
			zeichne();

		}
	}

	public void setMyCanvas(MyCanvas mc) {
		this.mc = mc;
		mc.addMyActionListenerForKeys(new MyKeyAction());
	}

	public void zeichne() {
		if (this.mc == null)
			return;
		int x1 = 10; // obere linke Ecke
		int y1 = 10;
		int x2 = mc.getWidth()-10; // untere rechte Ecke
		int y2 = mc.getHeight()-10;
		double xl = (x2 - x1) / size; // Kantenlaenge in x
		double yl = (y2 - y1) / size; // Kantenlaenge in y
		mc.setOnlyBackground(true);
		mc.clearScreen();
		mc.drawGrid(x1, y1, x2, y2, size, size);
		for (int i = 0; i < size; i++) { // zeilen y
			for (int j = 0; j < size; j++) { // spalten x
				if (spielfeld[j][i] > 0) {
					mc.writeText((int) (x1 + (j + 0.5) * xl - yl / 4), (int) (y1 + (i + 0.75) * yl),
							"" + spielfeld[j][i], (int) yl / 2);
				}
			}
		}
		mc.setColor(MyCanvas.gruen);
		mc.fillCircle((int) ((robx + 0.5) * xl + x1), (int) ((roby + 0.5) * yl + y1), 1 * Math.min(xl, yl) / 3);
		mc.setColor(MyCanvas.schwarz);
		mc.writeText((int) ((robx + 0.5) * xl + x1 - yl / 4), (int) ((roby + 0.75) * yl + y1), "" + robl, (int) yl / 2);
		mc.restoreView();

	}

	/**
	 * geht einen Schritt zurück, wenn möglich
	 * 
	 * @return true wenn erfolgreich
	 */
	public boolean moveBack() {
		if (weg.length() <= 0)
			return false;
		if (weg.charAt(weg.length() - 1) == 'B') { // Batterie tauschen
			weg = weg.substring(0, weg.length() - 1);
			int t = robl;
			robl = spielfeld[robx][roby];
			spielfeld[robx][roby] = t;
		}
		int dx = 0, dy = 0;
		switch (weg.charAt(weg.length() - 1)) {
		case 'N':
			dy = 1;
			break;
		case 'S':
			dy = -1;
			break;
		case 'E':
			dx = -1;
			break;
		case 'W':
			dx = +1;
			break;
		default:
			System.out.println("Schwerer Fehler - ungültiger Zug zum zurücknehmen");
			return false;
		}
		if (!setpos(robx + dx, roby + dy)) {
			System.out.println("Schwerer Fehler - Zug nicht zurücknehmbar");
			return false;
		}
		weg = weg.substring(0, weg.length() - 1);
		robl++;
		gesamtl++;
		return true;
	}

	/**
	 * bewegt in Richtung, wenn zulässig und tauscht die Batterie, wenn auf neuem
	 * Feld vorhanden - reduziert die Ladung um 1
	 * 
	 * @param direction - Richtung "N","S","W","E"
	 * @return true, wenn erfolgreich
	 */
	public boolean move(char direction) {
		if (robl <= 0)
			return false;
		int dx = 0, dy = 0;
		switch (direction) {
		case 'N':
			dy = -1;
			break;
		case 'S':
			dy = 1;
			break;
		case 'E':
			dx = 1;
			break;
		case 'W':
			dx = -1;
			break;
		default:
			return false;
		}
		if (!setpos(robx + dx, roby + dy))
			return false;
		weg += direction;
		robl--;
		gesamtl--;
		if (spielfeld[robx][roby] > 0) { // Batterie tauschen
			int t = robl;
			robl = spielfeld[robx][roby];
			spielfeld[robx][roby] = t;
			weg += "B";
		}
		return true;
	}

	/**
	 * @param xpos - neue xposition
	 * @param ypos - neue yposition
	 * @return - erfolg
	 */
	public boolean setpos(int xpos, int ypos) {
		if (xpos < 0 || xpos >= size || ypos < 0 || ypos >= size)
			return false;
		robx = xpos;
		roby = ypos;
		return true;
	}
}
