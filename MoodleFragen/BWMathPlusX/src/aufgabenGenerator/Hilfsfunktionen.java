package aufgabenGenerator;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class Hilfsfunktionen {
	public static void fensterZentrieren(JFrame fenster) {

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

		int x = dimension.width;
		int y = dimension.height;

		int fensterX = (x - fenster.getWidth()) / 2;
		int fensterY = (y - fenster.getHeight()) / 2;

		fenster.setLocation(fensterX, fensterY);
	}

	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		@SuppressWarnings("rawtypes")
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}
}
