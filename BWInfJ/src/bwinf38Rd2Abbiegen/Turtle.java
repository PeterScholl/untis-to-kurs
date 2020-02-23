package bwinf38Rd2Abbiegen;

/**
 * deutsch
 * @author (Alfred Hermes)
 * @version (5a, 6.12.2003)
 */
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;

public class Turtle extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int delay = 1, lastX = 0, lastY = 0, colorValue = 0;
	private double x = 0, startX = 0, y = 0, startY = 0, direction = 0;
	private boolean down = true, isVisible; // the turtle is visible as triangle;
	private boolean activ; // created, but deactivated by clearScreen
	private Color tColor = Color.black;
	final int maxWidth = 1280, maxHeight = 1024;
	static int tNumber;
	static int barHeight = 20; // height of the menubar
	static int currentWidth = 460, currentHeight = 460;
	static boolean firstCall = true;
	static final Vector tList = new Vector();
	static JFrame jFrame;
	static Container container;
	public static Color[] colors = { Color.black, Color.green, Color.blue, Color.yellow, Color.red, Color.gray,
			Color.lightGray, Color.orange, Color.white };
	public static final int schwarz = 0, gruen = 1, blau = 2, gelb = 3, rot = 4, grau = 5, hellgrau = 6, orange = 7,
			weiss = 8;
	public static int[] rgbColor = { -16777216, -16711936, -16776961, -256, -65536, -8355712, -4144960, -14336, -1 };
	static Graphics g, gWithTurtle, gWithoutTurtle;
	static BufferedImage iWithTurtle, iWithoutTurtle;
	static JMenuBar menueBar;
	static JButton eraseButton, restoreButton, closeButton;

	// ----------------- Konstruktoren --------------------------

	// --------------constructors------------------------

	public Turtle() {
		this(currentWidth, currentHeight);
	}

	public Turtle(int b, int h) {
		if (firstCall) {
			firstCall = false;
			jFrame = null;
			g = null;
			container = null;
			initComponents(b + 12, h + barHeight + 36);
			initGraphics();
		}
		if (jFrame == null || !jFrame.isVisible()) {
			jFrame = null;
			g = null;
			container = null;
			removeTurtles();
			if (h == currentHeight)
				initComponents(b, h);
			else
				initComponents(currentWidth, currentHeight);
			initGraphics();
		}
		double xx = currentWidth / 2;
		double yy = (currentHeight - barHeight) / 2;
		x = xx;
		y = yy;
		startX = x;
		startY = y;
		lastX = (int) x;
		lastY = (int) y;
		direction = 0;
		tColor = Color.black;
		isVisible = false;
		activ = true;
		tList.add(this);
	}

//--------- administration -------------------------------------------------

	private void initGraphics() {
		if (g == null) {
			try {
				g = container.getGraphics();
				iWithTurtle = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
				iWithoutTurtle = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
				gWithTurtle = iWithTurtle.getGraphics();
				gWithoutTurtle = iWithoutTurtle.getGraphics();
				g.setColor(Color.white);
				g.fillRect(0, 0, maxWidth, maxHeight);
				g.setColor(Color.black);
				gWithoutTurtle.setColor(Color.white);
				gWithoutTurtle.fillRect(0, 0, maxWidth, maxHeight);
				gWithoutTurtle.setColor(Color.black);
				gWithTurtle.setColor(Color.white);
				gWithTurtle.fillRect(0, 0, maxWidth, maxHeight);
				gWithTurtle.setColor(Color.black);
			} catch (Exception e) {
				System.out.println("Fehler bei initGraphics");
				initComponents(currentWidth, currentHeight);
				g = container.getGraphics();
			}
		}
	}

	/** Löscht die Turtle-Liste */
	private void removeTurtles() {
		if (tList != null) {
			tList.removeAllElements();
		}
	}

	/** malt die Turtle */
	private synchronized void restoreView() {
		if (!jFrame.isVisible()) {
			jFrame.setVisible(true);
		}
		if (g == null)
			initGraphics();
		activ = true;
		if (!jFrame.isVisible()) {
			initComponents(currentWidth, currentHeight);
			jFrame.setSize(currentWidth, currentHeight + barHeight); // ##
			jFrame.setVisible(true);
			container = this.getContentPane();
			g = container.getGraphics();
			g.setClip(0, 0, currentWidth, currentHeight);
			g.drawImage(iWithoutTurtle, 0, 0, container);
			menueBar.setVisible(true);
			// Zeige die Turtles
			for (int k = 0; k < tList.size(); k++) {
				Turtle turtle = (Turtle) tList.elementAt(k);
				if (turtle.isVisible && turtle.activ)
					zeichneTurtle(g, turtle);
			} // for
		}
		if (isVisible) {
			if (delay < 2)
				delay = 2;
			int x0 = Math.min(lastX, (int) x);
			int y0 = Math.min(lastY, (int) y);
			int b0 = Math.abs((int) (x - lastX));
			int h0 = Math.abs((int) (y - lastY));
			gWithTurtle.setClip(x0 - 21, y0 - 21, b0 + 42, h0 + 42);
			gWithTurtle.drawImage(iWithoutTurtle, 0, 0, container);
			tNumber = tList.size();
			for (int k = 0; k < tNumber; k++) {
				Turtle tu = (Turtle) tList.elementAt(k);
				if (tu.isVisible && tu.activ)
					zeichneTurtle(gWithTurtle, tu);
			} // for
			g.setClip(gWithTurtle.getClip());
			g.drawImage(iWithTurtle, 0, 0, container);
			g.setClip(0, 0, currentWidth, currentHeight); // neu
		} // if
		if (delay > 0) {
			try {
				Thread.sleep(delay);
			} catch (Exception e) {
			}
		}
	}

	private void zeichneTurtle(Graphics grafics, Turtle turtle) {
		int[] fx = new int[3];
		int[] fy = new int[3];
		double td = turtle.direction;
		double x = turtle.x;
		double y = turtle.y;
		Color tColor = turtle.tColor;
		fx[0] = (int) (x + Math.cos(bogenmass(td)) * 20);
		fy[0] = (int) (y + Math.sin(bogenmass(td)) * 20);
		td += 90;
		fx[1] = (int) (x + Math.cos(bogenmass(td)) * 5);
		fy[1] = (int) (y + Math.sin(bogenmass(td)) * 5);
		td += 180;
		fx[2] = (int) (x + Math.cos(bogenmass(td)) * 5);
		fy[2] = (int) (y + Math.sin(bogenmass(td)) * 5);
		grafics.setColor(tColor);
		grafics.fillPolygon(fx, fy, 3);
	}

	private void examineVisibility() {
		try {
			if (!jFrame.isVisible()) {
				jFrame.setVisible(true);
			}
			if (g == null || gWithoutTurtle == null) {
				g = null;
				initGraphics();
			}
		} catch (Exception e) {
			System.out.println("Error showing window!");
		}
	}

	public void actionPerformed(ActionEvent evt) {
		Object obj = evt.getSource();
		if (obj == eraseButton)
			clearScreen();
		else if (obj == restoreButton)
			restauriere();
		else if (obj == closeButton)
			hideFrame();
	}

	/**
	 * clears the Screen, not the informations about the turtles
	 */
	public void clearScreen() {
		try {
			if (!jFrame.isVisible()) {
				jFrame.setVisible(true);
			}
			g.setColor(Color.white);
			g.fillRect(0, 0, currentWidth, currentHeight);
			g.setColor(Color.black);
			gWithoutTurtle.setClip(0, 0, maxWidth, maxHeight);
			gWithoutTurtle.setColor(Color.white);
			gWithoutTurtle.fillRect(0, 0, maxWidth, maxHeight);
			gWithoutTurtle.setColor(Color.black);
			gWithTurtle.setClip(0, 0, maxWidth, maxHeight);
			gWithTurtle.setColor(Color.white);
			gWithTurtle.fillRect(0, 0, maxWidth, maxHeight);
			gWithTurtle.setColor(Color.black);
			// Deaktivate the turtles
			for (int k = 0; k < tList.size(); k++) {
				Turtle tu = (Turtle) tList.elementAt(k);
				if (tu.isVisible)
					tu.activ = false;
			} // for
		} catch (Exception e) {
		}
		;
	}

	protected void hideFrame() {
		jFrame.setVisible(false);
	}

	public void restauriere() {
		try {
			gWithTurtle.setClip(0, 0, currentWidth, currentHeight);
			gWithoutTurtle.setClip(0, 0, currentWidth, currentHeight);
			g.setClip(0, 0, currentWidth, currentHeight); // may be not necessary
			g.drawImage(iWithoutTurtle, 0, 0, container);
			// show the turtles -------------
			tNumber = tList.size();
			for (int k = 0; k < tNumber; k++) {
				Turtle turtle = (Turtle) tList.elementAt(k);
				if (turtle.isVisible && turtle.activ)
					zeichneTurtle(g, turtle);
			} // for
		} catch (Exception e) {
		}
	}

	private void initComponents(int b, int h) {
		if (container == null)
			container = this.getContentPane();
		container.setBackground(Color.white);
		currentWidth = b;
		currentHeight = h;
		jFrame = this;
		jFrame.setTitle("Turtle Version 5a (c) Alfred Hermes");
		menueBar = new JMenuBar();
		jFrame.setJMenuBar(menueBar);
		eraseButton = new JButton("Alles löschen");
		menueBar.add(eraseButton);
		eraseButton.addActionListener(this);
		restoreButton = new JButton("Fenster restaurieren");
		menueBar.add(restoreButton);
		restoreButton.addActionListener(this);
		closeButton = new JButton("Fenster schließen");
		menueBar.add(closeButton);
		closeButton.addActionListener(this);
		jFrame.setSize(currentWidth, currentHeight); // +barHeight); ##
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		// jFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE );
	}// initComponents

	public void paint(Graphics gr) {
		try {
			currentWidth = this.getWidth();
			currentHeight = this.getHeight();
			container = this.getContentPane();
			g = container.getGraphics();
			paintComponents(gr);
			restauriere();
			if (iWithoutTurtle == null) {
				iWithTurtle = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
				iWithoutTurtle = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
				gWithTurtle = iWithTurtle.getGraphics();
				gWithoutTurtle = iWithoutTurtle.getGraphics();
				gWithoutTurtle.setColor(Color.white);
				gWithoutTurtle.fillRect(0, 0, maxWidth, maxHeight);
				gWithoutTurtle.setColor(Color.black);
				gWithTurtle.setColor(Color.white);
				gWithTurtle.fillRect(0, 0, maxWidth, maxHeight);
				gWithTurtle.setColor(Color.black);
			} // end of if
		} // end of try
		catch (Exception e) {
			System.out.println("Restaurieren gescheitert");
			System.out.println("iOhne = null: " + (iWithoutTurtle == null));
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

	private int convertColor(int rgb) {
		for (int k = 0; k < 9; k++) {
			if (rgbColor[k] == rgb)
				return k;
		}
		return -1;
	}
//----------------- export --------------------------

	/**
	 * Die Turtle bewegt sich um die Anzahl von Einheiten in aktueller Richtung
	 */
	public synchronized void vor(double l) {
		examineVisibility();
		try {
			// if(g==null || gWithoutTurtle==null) initGraphics();
			double nextX = x + Math.cos(bogenmass(direction)) * l;
			double nextY = y + Math.sin(bogenmass(direction)) * l;
			g.setColor(tColor);
			gWithoutTurtle.setColor(tColor);
			if (down) {
				g.drawLine((int) x, (int) y, (int) nextX, (int) nextY);
				gWithoutTurtle.drawLine((int) x, (int) y, (int) nextX, (int) nextY);
			}
			lastX = (int) x;
			lastY = (int) y;
			x = nextX;
			y = nextY;
			restoreView();
		} catch (Exception ex) {
			System.out.println("error in moving!");
			System.out.println("gWithoutTurtle == null: " + (gWithoutTurtle == null));
			System.out.println("Frame is Visible: " + jFrame.isVisible());
			System.exit(0);
		}
	}

	/**
	 * Die Turtle zieht keine Linien, der Stift ist hochgezogen
	 */
	public void hebeStift() {
		down = false;
	}

	/**
	 * Der Stift ist in Schreibstellung
	 */
	public void senkeStift() {
		down = true;
	}

	/**
	 * Festlegung der Richtung im Gradmass.
	 */
	public void setzeRichtung(double gradmass) {
		direction = gradmass;
		restoreView();
	}

	/**
	 * Rueckgabe der Richtung im Gradmass.
	 */
	public double liesRichtung() {
		return direction;
	}

	/**
	 * Rueckgabe der absoluten x-Koordinate der Turtle.
	 */
	public double liesX() {
		return x;
	}

	/**
	 * Rueckgabe der absoluten y-Koordinate der Turtle.
	 */
	public double liesY() {
		return y;
	}

	/**
	 * Rueckgabe der maximalen Breite der Landschaft.
	 */
	public double liesMaxX() {
		return container.getBounds().width;
	}

	/**
	 * Rueckgabe der maximalen Laenge der Landschaft.
	 */
	public double liesMaxY() {
		return container.getBounds().height;
	}

	/**
	 * Drehung um einen Winkel in Gradmass (0 Grad = rechts, 90 Grad = unten).
	 * 
	 * @param angle Gradmass
	 */
	public synchronized void drehe(double angle) {
		direction += angle;
		restoreView();
	}

	/**
	 * Festlegung der Turtlefarbe.
	 * 
	 * @param c schwarz, gruen, blau, gelb, rot, grau, hellgrau, orange, weiss;
	 */
	public synchronized void setzeFarbe(int c) {
		tColor = colors[c];
		colorValue = c;
		restoreView();
	}

	/**
	 * Lesen der aktuellen Stiftfarbe. Farben: schwarz=0, grün=1, blau=2, gelb=3,
	 * rot=4, grau=5, hellgrau=6,orange=7,weiss=8;
	 * 
	 * @return colorValue als Zahl zwischen 0 und 8
	 */
	public int liesFarbe() {
		return colorValue;
	}

	/**
	 * Blick in Richtung der absoluten Koordinaten x und y. Links oben ist (0,0).
	 * 
	 * @param x x-Koordinate (absolut)
	 * @param y y-Koordinate (absolut auf der Landschaft)
	 */
	public synchronized void schaueNach(double x, double y) {
		setzeRichtung(x - liesX(), y - liesY());
		restoreView();
	}

	/**
	 * Ausgabe einer Zeichenkette an aktueller Stelle (horizontal). Die Turtle kehrt
	 * zum Anfang der Zeichenkette zurueck
	 * 
	 * @param s eine Zeichenkette
	 */
	public synchronized void schreibe(String s) {
		examineVisibility();
		gWithoutTurtle.setColor(tColor);
		gWithoutTurtle.setFont(new Font("Helvetica", Font.BOLD, 14));
		gWithoutTurtle.drawString(s, (int) x, (int) y);
		g.setColor(tColor);
		g.setFont(new Font("Helvetica", Font.BOLD, 14));
		g.drawString(s, (int) x, (int) y);
		restoreView();
	}

	/**
	 * Festlegung einer Zeichengeschwindigkeit
	 * 
	 * @param s 0 <= s <= 10
	 */
	public void setzeTempo(int s) {
		if (s > 10)
			s = 10;
		if (s < 0)
			s = 0;
		delay = 10 - s;
	}

	/**
	 * Die Turtle wird unsichtbar (wie versteckeTurtle())
	 */
	public void verstecke() {
		isVisible = false;
		if (g == null)
			initGraphics();
	}

	public void versteckeTurtle() {
		verstecke();
	}

	/**
	 * Die Turtle wird als Dreieck sichtbar. (wie zeigeTurtle())
	 */
	public void zeige() {
		isVisible = true;
		activ = true;
		if (g == null)
			initGraphics();
	}

	public void zeigeTurtle() {
		zeige();
	}

//----------------- useful methods ------------------------
	/**
	 * Die Turtle geht ohne zu zeichnen zur angebenen Position.
	 * 
	 * @param sx x-Wert der Zielposition
	 * @param sy y-Wert der Zielposition
	 */
	public synchronized void zumStart(double sx, double sy) {
		if (!jFrame.isVisible()) {
			jFrame.setVisible(true);
		}
		x = sx;
		y = sy;
		startX = x;
		startY = y;
		lastX = (int) x;
		lastY = (int) y;
		initGraphics();
		restoreView();
	}

	/**
	 * Die Turtle geht auf geradem Wege zur angebenen Position.
	 * 
	 * @param nextX neue x-Positionen
	 * @param nexty neue y-Positionen
	 */
	public synchronized void geheNach(double nextX, double nextY) {
		examineVisibility();
		g.setColor(tColor);
		gWithoutTurtle.setColor(tColor);
		if (down) {
			g.drawLine((int) x, (int) y, (int) nextX, (int) nextY);
			gWithoutTurtle.drawLine((int) x, (int) y, (int) nextX, (int) nextY);
		}
		lastX = (int) x;
		lastY = (int) y;
		x = nextX;
		y = nextY;
		restoreView();
	}

	/**
	 * Die Turtle geht auf dem Bogen eines Kreises mit vorgegebenem Radius. Die
	 * Gradzahl ist die Drehung im Winkelmaß.
	 * 
	 * @param angle  Winkel der Kreisbahn(Orbit)
	 * @param radius Radius der Kreisbahn
	 */
	public void geheImBogen(double angle, double radius) {
		// examineVisibility();
		double preDirection = direction;
		double step = (Math.PI * radius) / 180.0;
		double alpha = angle / Math.abs(angle);
		double rotation = alpha;
		while (Math.abs(rotation) <= Math.abs(angle)) {
			drehe(alpha);
			rotation += alpha;
			vor(step);
		}
		direction = preDirection + angle;
	}

	/**
	 * legt die Richtung als Steigungsdreieck mit DeltaX und DeltaY fest
	 * 
	 * @param deltaX nach rechts oder nach links (+ oder -)
	 * @param deltaY nach unten oder nach oben (+ oder -)
	 */
	public synchronized void setzeRichtung(double deltaX, double deltaY) {
		if (deltaX == 0 && deltaY == 0)
			direction = 0;
		else if (deltaX == 0 && deltaY < 0)
			direction = -90;
		else if (deltaX == 0 && deltaY > 0)
			direction = 90;
		else if (deltaX > 0 && deltaY == 0)
			direction = 0;
		else if (deltaX < 0 && deltaY == 0)
			direction = 180;
		else {
			double w = gradmass(Math.atan((deltaY) / (deltaX)));
			if (deltaX > 0)
				direction = w;
			else
				direction = 180 + w;
		}
		restoreView();
	}

	/**
	 * Zeichnung eines Kreises in der Turtlefarbe
	 * 
	 * @param radius Kreisradius
	 */
	public void zeichneKreis(double radius) {
		examineVisibility();
		gWithoutTurtle.setColor(tColor);
		gWithoutTurtle.drawOval((int) (x - radius), (int) (y - radius), (int) (radius * 2), (int) (radius * 2));
		g.setColor(tColor);
		g.drawOval((int) (x - radius), (int) (y - radius), (int) (radius * 2), (int) (radius * 2));
		activ = true;
	}

	/**
	 * Zeichnung einer Kreisscheibe in der Turtlefarbe
	 * 
	 * @param radius Kreisradius
	 */
	public void fuelleKreis(double radius) {
		examineVisibility();
		gWithoutTurtle.setColor(tColor);
		int x1 = (int) (x - radius);
		int y1 = (int) (y - radius);
		int d = (int) (radius * 2);
		gWithoutTurtle.fillOval(x1, y1, d, d);
		gWithoutTurtle.setClip(x1, y1, d, d);
		g.setClip(gWithoutTurtle.getClip());
		g.drawImage(iWithoutTurtle, 0, 0, container);
		gWithoutTurtle.setClip(0, 0, currentWidth, currentHeight);
		g.setClip(gWithoutTurtle.getClip());
		activ = true;
	}

	/**
	 * Zeichung eines Pixels in der aktuellen Turtlefarbe
	 */
	public void plotPixel() {
		if (!jFrame.isVisible()) {
			jFrame.setVisible(true);
		}
		int x1 = (int) (x);
		int y1 = (int) (y);
		gWithoutTurtle.setColor(tColor);
		gWithoutTurtle.fillRect(x1, y1, 1, 1);
		g.setColor(tColor);
		g.fillRect(x1, y1, 1, 1);
		activ = true;
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
	 * Kopie von Pixeln Die Turtleposition ist anschließend xTarget, xTarget
	 * 
	 * @param x       x-Koordinate des zu transportierenden Punktes
	 * @param y       y-Koordinate des zu transportierenden Punktes
	 * @param xTarget x-Ziel
	 * @param yTarget y-Ziel
	 */
	public void copyPixel(int x, int y, int xTarget, int yTarget) {
		boolean status = down;
		down = false;
		if (isVisible)
			geheNach(x, y);
		gWithoutTurtle.copyArea(x, y, 1, 1, xTarget - x, yTarget - y);
		gWithoutTurtle.setClip(xTarget, yTarget, 1, 1);
		g.setClip(gWithoutTurtle.getClip());
		g.drawImage(iWithoutTurtle, 0, 0, container);
		g.setClip(0, 0, currentWidth, currentHeight);
		gWithoutTurtle.setClip(0, 0, currentWidth, currentHeight);
		if (isVisible)
			geheNach(xTarget, yTarget);
		else {
			x = xTarget;
			y = yTarget;
		}
		down = status;
	}

	/**
	 * Bestimmung einer Pixelfarbe an einer aktuellen Position
	 * 
	 * @return int Farbe zwischen 0 und 8. Fehler, wenn -1
	 */
	public int liesPixelFarbe() {
		WritableRaster raster = iWithoutTurtle.getRaster();
		DataBufferInt buffer = (DataBufferInt) raster.getDataBuffer();
		int[] values = buffer.getData();
		int c = values[(int) (y * iWithoutTurtle.getWidth() + x)];
		int pixelColor = convertColor(c);
		return pixelColor;
	}
//----------------englisch ------------------------

	public static final int black = 0, green = 1, blue = 2, yellow = 3, red = 4, gray = 5, lightGrau = 6, white = 8;

	public synchronized void move(double l) {
		vor(l);
	}

	public void penUp() {
		hebeStift();
	}

	public void penDown() {
		senkeStift();
	}

	public void setDirection(double degree) {
		setzeRichtung(degree);
	}

	public synchronized void setDirection(double deltaX, double deltaY) {
		setzeRichtung(deltaX, deltaY);
	}

	public double getDirection() {
		return liesRichtung();
	}

	public double getXPos() {
		return liesX();
	}

	public double getYPos() {
		return liesY();
	}

	public double getMaxX() {
		return liesMaxX();
	}

	public double getMaxY() {
		return liesMaxY();
	}

	public synchronized void turn(double degree) {
		drehe(degree);
	}

	public synchronized void setColor(int c) {
		setzeFarbe(c);
	}

	public int getColor() {
		return liesFarbe();
	}

	public synchronized void turnTo(double x, double y) {
		schaueNach(x, y);
	}

	public synchronized void write(String s) {
		schreibe(s);
	}

	public void setSpeed(int s) {
		setzeTempo(s);
	}

	public void hideTurtle() {
		verstecke();
	}

	public void showTurtle() {
		zeige();
	}

	public synchronized void toStartingPoint(double sx, double sy) {
		zumStart(sx, sy);
	}

	public synchronized void moveTo(double newX, double newY) {
		geheNach(newX, newY);
	}

	public void moveArc(double degree, double r) {
		geheImBogen(degree, r);
	}

	public void drawCircle(double r) {
		zeichneKreis(r);
	}

	public void fillCircle(double r) {
		fuelleKreis(r);
	}

	public void sleep(int time) {
		ruhe(time);
	}

	public int getPixelColor() {
		return liesPixelFarbe();
	}

}
