package bwinf38Rd2Strom;

import java.awt.*;
//import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;

/**
 * @author scholl@unterrichtsportal.org
 * @version 0.1 (23.02.2020)
 * class is used to offer a simplified possibility to draw pictures and generate
 * graphical output
 * "Derived" from Turtle bei A. Hermes
 */
public class MyCanvas extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L; // can someone explain what serialVersionUID is for?
	private Color actColor = Color.black;  //The Color in which we will paint
	final int maxWidth = 1280, maxHeight = 1024;
	static int barHeight = 20; // height of the menubar
	static int currentWidth = 460, currentHeight = 460;
	static boolean firstCall = true;  // class variable to generate a singleton - only one occurence
	static JFrame jFrame;
	static Container container;
	public static Color[] colors = { Color.black, Color.green, Color.blue, Color.yellow, Color.red, Color.gray,
			Color.lightGray, Color.orange, Color.white };
	public static final int schwarz = 0, gruen = 1, blau = 2, gelb = 3, rot = 4, grau = 5, hellgrau = 6, orange = 7,
			weiss = 8;
	static Graphics g, gbackground; // one drawing region - drawing mostly on background - class based!
	static BufferedImage image; // Bufferd image - need for background
	static JMenuBar menueBar;
	static JButton eraseButton, restoreButton, closeButton;
	static boolean onlybackground = false; // if true drawing will only be done on gbackground and not copied to g

	// --------------constructors------------------------

	public MyCanvas() {
		this(currentWidth, currentHeight);
	}

	public MyCanvas(int b, int h) {
		if (firstCall | jFrame == null || !jFrame.isVisible()) {
			firstCall = false;
			jFrame = null;
			g = null;
			container = null;
			initComponents(b + 12, h + barHeight + 36);
			initGraphics();
			restoreView();
		}
		actColor = Color.black;
	}

//--------- administration -------------------------------------------------

	private void initGraphics() {
		if (g == null) { // if there is no current graphics
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

	/** restores view - means that background image will be copied to front graphics **/
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

	/**
	 * actionPerformed waits for Actions to the three buttons
	 * @param evt beside other, information about the pressed button 
	 */
	public void actionPerformed(ActionEvent evt) {  
		System.out.println("Action occured:" + evt);
		Object obj = evt.getSource();
		if (obj == eraseButton)
			clearScreen();
		else if (obj == restoreButton)
			restoreView();
		else if (obj == closeButton)
			hideFrame();
	}

	/**
	 * clears the Screen (everything white)
	 */
	public void clearScreen() {
		try {
			if (!jFrame.isVisible()) {
				jFrame.setVisible(true);
			}
			gbackground.setColor(Color.white);
			gbackground.fillRect(0, 0, currentWidth, currentHeight);
			gbackground.setColor(Color.black);
			if (!onlybackground) restoreView();
		} catch (Exception e) {
		}
		;
	}

	/**
	 * if set to true - drawing actions will only happen on the background image
	 * you have to call restoreView on your own.
	 * @param b true or false
	 */
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
		eraseButton = new JButton("erase all");
		menueBar.add(eraseButton);
		eraseButton.addActionListener(this);
		restoreButton = new JButton("reset window");
		menueBar.add(restoreButton);
		restoreButton.addActionListener(this);
		closeButton = new JButton("close");
		menueBar.add(closeButton);
		closeButton.addActionListener(this);
		jFrame.setSize(currentWidth, currentHeight); // +barHeight); ##
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		// jFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE );
	}// initComponents

	/**
	 * This method allows to add an ActionListener, which will report on all pressed
	 * alphabetical keys - you have to generate a class e.g. like follows in your code
	 * <pre>
	 * class MyKeyAction extends AbstractAction {
	 * 	public void actionPerformed(ActionEvent e) {
	 * 		switch (e.getActionCommand().charAt(0)) { 
	 * 		case 'a':
	 *			System.out.println("a pressed");
	 * 			... 
	 * 		}
	 * 	}
	 * }
	 * </pre>
	 * and add an Object ot this class as ActionListener to the MyCanvas-object
	 * mycanvas.addMyActionListener(new MyKeyAction);
		
	 * @param al Action Listener
	 */
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
	 * draw a simple line in chosen color
	 * from point (x1,y1) to (x2,y2) 
	 * (0,0) is in upper left corner
	 */
	public synchronized void drawLine(double x1, double y1, double x2, double y2) {
		// System.out.println("Linie von " + x1 + "-" + y1 + "-" + x2 + "-" + y2);
		try {
			gbackground.setColor(actColor);
			gbackground.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
			if (!onlybackground) restoreView();
		} catch (Exception ex) {
			System.out.println("error in drawing line!");
			System.exit(0);
		}
	}

	/**
	 * draw a grid / table
	 * @param x1 x of upper left corner
	 * @param y1 y of upper left corner
	 * @param x2 x lower right corner
	 * @param y2 y lower right corner
	 * @param zeilen number of rows
	 * @param spalten number of columns 
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
	 * simple way of choosing the color (e.g. type mycanvas.setColor(Color.gruen);
	 * 
	 * @param c schwarz, gruen, blau, gelb, rot, grau, hellgrau, orange, weiss;
	 */
	public synchronized void setColor(int c) {
		actColor = colors[c];
	}

	/**
	 * writing a textString with size 14
	 * @param x1 x-position of the lower left corner of the text
	 * @param y1 y-position of the lower left corner of the text
	 * @param s String to write
	 */
	public void writeText(int x1, int y1, String s) {
		writeText(x1, y1, s, 14);
	}

	/**
	 * writing a textString
	 * @param x1 x-position of the lower left corner of the text
	 * @param y1 y-position of the lower left corner of the text
	 * @param s String to write
	 * @param size textsize
	 */
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
	 * draw a circle
	 *  
	 * @param x x-coordinate of circle center
	 * @param y y-coordinate of circle center
	 * @param radius 
	 */
	public void drawCircle(int x, int y, double radius) {
		gbackground.setColor(actColor);
		gbackground.drawOval((int) (x - radius), (int) (y - radius), (int) (radius * 2), (int) (radius * 2));
		if (!onlybackground) restoreView();
	}

	/**
	 * drawing a filled circle
	 * 
	 * @param x x-coordinate of circle center
	 * @param y y-coordinate of circle center
	 * @param radius 
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
	 * drawing a single pixel
	 * @param x1 x-position of the pixel
	 * @param y1 y-position of the pixel
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
	 * wait for a short amount of time
	 * @param pause  time in milliseconds
	 */
	public void wait(int pause) {
		try {
			Thread.sleep(pause);
		} catch (InterruptedException ie) {
		}
	}

	/**
	 * copy a single pixel
	 * 
	 * @param x x-coordinate of the pixel to copy
	 * @param y y-coordinate of the pixel to copy
	 * @param xTarget x-coordinates of the destination pixel
	 * @param yTarget y-coordinates of the destination pixel
	 */
	public void copyPixel(int x, int y, int xTarget, int yTarget) {
		gbackground.copyArea(x, y, 1, 1, xTarget - x, yTarget - y);
		if (!onlybackground) restoreView();
	}

}
