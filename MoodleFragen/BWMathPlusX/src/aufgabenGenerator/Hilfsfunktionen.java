package aufgabenGenerator;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class Hilfsfunktionen {
	public static void fensterZentrieren(JFrame fenster) {

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

		int x = dimension.width;
		int y = dimension.height;

		int fensterX = (x - fenster.getWidth()) / 2;
		int fensterY = (y - fenster.getHeight()) / 2;

		fenster.setLocation(fensterX, fensterY);
	}

}
