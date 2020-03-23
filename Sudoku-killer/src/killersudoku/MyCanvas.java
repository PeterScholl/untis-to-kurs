package killersudoku;

import java.awt.*;
//import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;

public class MyCanvas extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L; // what is this for?
	private Color actColor = Color.black;
	final int maxWidth = 1280, maxHeight = 1024;
	static int barHeight = 20; // height of the menubar
	static int currentWidth = 460, currentHeight = 460;
	static boolean firstCall = true;
	static JFrame jFrame;
	static Container container;
	public static Color[] colors = { Color.black, Color.green, Color.blue, Color.yellow, Color.red, Color.gray,
			Color.lightGray, Color.orange, Color.white };
	public static final int schwarz = 0, gruen = 1, blau = 2, gelb = 3, rot = 4, grau = 5, hellgrau = 6, orange = 7,
			weiss = 8;
	static Graphics g, gbackground; // at least one drawing region
	static BufferedImage image; // Bufferd image - need for background
	static JMenuBar menueBar;
	static JButton eraseButton, restoreButton, closeButton,infoButton;
	static boolean onlybackground = false; // nur im Hintergrund malen - um später zu zeigen
	static ActionListener externalActionListener = null;

	// ----------------- Konstruktoren --------------------------

	// --------------constructors------------------------

	public MyCanvas() {
		this(currentWidth, currentHeight);
	}

	public MyCanvas(int b, int h) {
		if (firstCall) {
			firstCall = false;
			jFrame = null;
			g = null;
			container = null;
			initComponents(b + 12, h + barHeight + 36);
			initGraphics();
			restoreView();
		}
		if (jFrame == null || !jFrame.isVisible()) {
			jFrame = null;
			g = null;
			container = null;
			if (h == currentHeight)
				initComponents(b, h);
			else
				initComponents(currentWidth, currentHeight);
			initGraphics();
		}
		actColor = Color.black;
	}

//--------- administration -------------------------------------------------

	private void initGraphics() {
		if (g == null) {
			try {
				g = container.getGraphics();
				image = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
				gbackground = image.getGraphics();
				g.setColor(Color.white);
				g.fillRect(0, 0, maxWidth, maxHeight);
				g.setColor(Color.black);
				gbackground.setColor(Color.white);
				gbackground.fillRect(0, 0, maxWidth, maxHeight);
				gbackground.setColor(Color.black);

			} catch (Exception e) {
				System.out.println("Fehler bei initGraphics");
				initComponents(currentWidth, currentHeight);
				g = container.getGraphics();
			}
		}
	}

	/** setzt Bildschirm zurück - malt alles neu **/
	public synchronized void restoreView() {
		if (!jFrame.isVisible()) {
			jFrame.setVisible(true);
		}
		if (g == null)
			initGraphics();
		if (!jFrame.isVisible()) {
			initComponents(currentWidth, currentHeight);
			jFrame.setSize(currentWidth, currentHeight + barHeight); // ##
			jFrame.setVisible(true);
			container = this.getContentPane();
			menueBar.setVisible(true);
		}
		g = container.getGraphics();
		g.setClip(0, 0, currentWidth, currentHeight);
		g.drawImage(image, 0, 0, container);

	}

	public void actionPerformed(ActionEvent evt) {
		if (externalActionListener != null) externalActionListener.actionPerformed(evt);
		else {
		System.out.println("Action occured:" + evt);
		Object obj = evt.getSource();
		if (obj == eraseButton)
			clearScreen();
		else if (obj == restoreButton)
			restoreView();
		else if (obj == closeButton)
			hideFrame();
		}
	}
	
	public void setExternalActionListener(ActionListener act) {
		MyCanvas.externalActionListener=act;
	}
	
	public void setExternalMouseListener(MouseListener ml) {
		jFrame.addMouseListener(ml);
	}

	/**
	 * clears the Screen
	 */
	public void clearScreen() {
		try {
			if (!jFrame.isVisible()) {
				jFrame.setVisible(true);
			}
			if (!onlybackground) {
				g.setColor(Color.white);
				g.fillRect(0, 0, currentWidth, currentHeight);
				g.setColor(Color.black);
			}
			gbackground.setColor(Color.white);
			gbackground.fillRect(0, 0, currentWidth, currentHeight);
			gbackground.setColor(Color.black);
		} catch (Exception e) {
		}
		;
	}

	public void setOnlyBackground(boolean b) {
		onlybackground=b;
	}

	protected void hideFrame() {
		jFrame.setVisible(false);
	}

	private void initComponents(int b, int h) {
		if (container == null)
			container = this.getContentPane();
		container.setBackground(Color.white);
		currentWidth = b;
		currentHeight = h;
		jFrame = this;
		jFrame.setTitle("MyCanvas by Peter Scholl 2019 - based on Turtle by A. Hermes");
		menueBar = new JMenuBar();
		jFrame.setJMenuBar(menueBar);
		eraseButton = new JButton("column cover");
		menueBar.add(eraseButton);
		eraseButton.addActionListener(this);
		restoreButton = new JButton("check possibilities");
		menueBar.add(restoreButton);
		restoreButton.addActionListener(this);
		closeButton = new JButton("test");
		menueBar.add(closeButton);
		closeButton.addActionListener(this);
		infoButton = new JButton("Print Info");
		menueBar.add(infoButton);
		infoButton.addActionListener(this);
		jFrame.setSize(currentWidth, currentHeight); // +barHeight); ##
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		// jFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE );
	}// initComponents

	public void addMyActionListenerForKeys(ActionListener al) {
		for (char c = 'a'; c <= 'z'; c++) {
			// WHEN_IN_FOCUSED_WINDOW is really important, because otherwise I don't get the
			// inputs if a button was pressed!
			this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(c),
					"gotAlphaKeyStroke");
			this.rootPane.getInputMap().put(KeyStroke.getKeyStroke(c), "gotAlphaKeyStroke");
		}
		this.rootPane.getActionMap().put("gotAlphaKeyStroke", (Action) al);
	}

	public void paint(Graphics gr) {
		try {
			currentWidth = this.getWidth();
			currentHeight = this.getHeight();
			container = this.getContentPane();
			g = container.getGraphics();
			paintComponents(gr);
			if (image == null) {
				image = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
				gbackground = image.getGraphics();
				gbackground.setColor(Color.white);
				gbackground.fillRect(0, 0, maxWidth, maxHeight);
				gbackground.setColor(Color.black);
			}
			restoreView();
		} // end of try
		catch (Exception e) {
			System.out.println("Restaurieren gescheitert");
			paintComponents(gr);
		}
	}

//----------------- private: help functions ------------------------------

	private static double bogenmass(double winkel) {
		return winkel * Math.PI / 180;
	}

	private static double gradmass(double bogenmass) {
		return bogenmass * 180 / Math.PI;
	}

//----------------- export --------------------------

	/**
	 * Linie Zeichnen
	 */
	public synchronized void drawLine(double x1, double y1, double x2, double y2) {
		// System.out.println("Linie von " + x1 + "-" + y1 + "-" + x2 + "-" + y2);
		try {
			// g.setColor(actColor);
			gbackground.setColor(actColor);
			// g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
			gbackground.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
			if (!onlybackground) restoreView();
		} catch (Exception ex) {
			System.out.println("error in drawing line!");
			System.exit(0);
		}
	}

	/**
	 * Grid zeichnen / Tablle
	 */
	public void drawGrid(double x1, double y1, double x2, double y2, int zeilen, int spalten) {
		double zeilenhoehe = (y2 - y1) / zeilen;
		double spaltenbreite = (x2 - x1) / spalten;
		for (int i = 0; i <= spalten; i++) {
			drawLine(x1 + i * spaltenbreite, y1, x1 + i * spaltenbreite, y2);
		}
		for (int i = 0; i <= zeilen; i++) {
			drawLine(x1, y1 + i * zeilenhoehe, x2, y1 + i * zeilenhoehe);
		}
	}

	/**
	 * Festlegung der Zeichenfarbe.
	 * 
	 * @param c schwarz, gruen, blau, gelb, rot, grau, hellgrau, orange, weiss;
	 */
	public synchronized void setColor(int c) {
		actColor = colors[c];
	}

	/**
	 * Ausgabe einer Zeichenkette an aktueller Stelle (horizontal). Die Turtle kehrt
	 * zum Anfang der Zeichenkette zurueck
	 * 
	 * @param s eine Zeichenkette
	 */
	public void writeText(int x1, int y1, String s) {
		writeText(x1, y1, s, 14);
	}

	public synchronized void writeText(int x1, int y1, String s, int size) {
		// g.setColor(actColor);
		gbackground.setColor(actColor);
		// g.setFont(new Font("Helvetica", Font.BOLD, size));
		// g.drawString(s, x1, y1);
		gbackground.setFont(new Font("Helvetica", Font.BOLD, size));
		gbackground.drawString(s, x1, y1);
		if (!onlybackground) restoreView();
	}

//----------------- useful methods ------------------------

	/**
	 * Zeichnung eines Kreises
	 * 
	 * @param x      - xKoordinate des Mittelpunktes
	 * @param y      - yKoordinate des Mittelpunktes
	 * @param radius Kreisradius
	 */
	public void drawCircle(int x, int y, double radius) {
		// g.setColor(actColor);
		gbackground.setColor(actColor);
		// g.drawOval((int) (x - radius), (int) (y - radius), (int) (radius * 2), (int)
		// (radius * 2));
		gbackground.drawOval((int) (x - radius), (int) (y - radius), (int) (radius * 2), (int) (radius * 2));
		if (!onlybackground) restoreView();
	}

	/**
	 * Zeichnung einer Kreisscheibe
	 * 
	 * @param x      - xKoordinate des Mittelpunktes
	 * @param y      - yKoordinate des Mittelpunktes
	 * @param radius Kreisradius
	 */
	public void fillCircle(int x, int y, double radius) {
		int x1 = (int) (x - radius);
		int y1 = (int) (y - radius);
		int d = (int) (radius * 2);
		// g.setColor(actColor);
		gbackground.setColor(actColor);
		// g.fillOval(x1, y1, d, d);
		gbackground.fillOval(x1, y1, d, d);
		if (!onlybackground) restoreView();
	}

	/**
	 * Zeichung eines Pixels in der aktuellen Turtlefarbe
	 */
	public void plotPixel(int x1, int y1) {
		if (!jFrame.isVisible()) {
			jFrame.setVisible(true);
		}
		gbackground.setColor(actColor);
		gbackground.fillRect(x1, y1, 1, 1);
		if (!onlybackground) restoreView();
	}

	/**
	 * Eine Pause in Millisekunden
	 */
	public void ruhe(int pause) {
		try {
			Thread.sleep(pause);
		} catch (InterruptedException ie) {
		}
	}

	/**
	 * Kopie von Pixeln
	 * 
	 * @param x       x-Koordinate des zu transportierenden Punktes
	 * @param y       y-Koordinate des zu transportierenden Punktes
	 * @param xTarget x-Ziel
	 * @param yTarget y-Ziel
	 */
	public void copyPixel(int x, int y, int xTarget, int yTarget) {
		gbackground.copyArea(x, y, 1, 1, xTarget - x, yTarget - y);
		if (!onlybackground) restoreView();
	}

}
