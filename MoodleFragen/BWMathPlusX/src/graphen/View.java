package graphen;

import java.awt.*;

import javax.print.attribute.standard.JobMessageFromOperator;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * In der Klasse GUI wird das Logical dargestellt
 * 
 * @author scholl@unterrichtsportal.org
 * @version 16.05.2016
 */
public class View implements MouseListener, MouseMotionListener, KeyListener {

	private JFrame hauptfenster;
	private GraphCanvas center; // JPanel
	private JLabel topInfoLabel, statusLabel;
	private Controller controller = null;
	private Font generalfont = new Font("Dialog", Font.BOLD, 16);
	private boolean aktionenEnabled = true;
	private JMenuItem oeffnenEintrag, speichernEintrag;
	private JMenu dateimenue, ansichtmenue, generatormenue, hilfemenue;

	/**
	 * Constructor for objects of class GUI
	 */
	public View(Controller c, String title) {
		this.controller = c;
		fensterErzeugen(title);
	}

	public void fensterErzeugen(String title) {
		// Hilfsfunktionen.setUIFont(new javax.swing.plaf.FontUIResource(generalfont));
		if (title == null)
			title = "Fenster";
		hauptfenster = new JFrame(title);
		hauptfenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		hauptfenster.addKeyListener(this);

		// Menü erzeugen
		JMenuBar menuezeile = new JMenuBar();
		hauptfenster.setJMenuBar(menuezeile);

		dateimenue = new JMenu("Datei"); // Datei-Menue
		menuezeile.add(dateimenue);
		oeffnenEintrag = new JMenuItem("Graph einlesen");
		oeffnenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.Graph_einlesen, null);
			}
		});
		dateimenue.add(oeffnenEintrag);

		speichernEintrag = new JMenuItem("Graph speichern");
		speichernEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.Graph_speichern, null);
			}
		});
		dateimenue.add(speichernEintrag);

		JMenuItem beendenEintrag = new JMenuItem("Beenden");
		beendenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				beenden();
			}
		});
		dateimenue.add(beendenEintrag);

		ansichtmenue = new JMenu("Ansicht"); // Datei-Menue
		menuezeile.add(ansichtmenue);
		JMenuItem zoomInEintrag = new JMenuItem("Zoom in (Strg-+)");
		zoomInEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.ZOOM, new String[] { "90" }); // Zoom in
			}
		});
		ansichtmenue.add(zoomInEintrag);

		JMenuItem zoomOutEintrag = new JMenuItem("Zoom out (Strg+-)");
		zoomOutEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.ZOOM, new String[] { "110" }); // Zoom out
			}
		});
		ansichtmenue.add(zoomOutEintrag);

		JMenuItem linienDickeEintrag = new JMenuItem("Liniendicke ändern");
		linienDickeEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.LinienDickeAendern, null);
			}
		});
		ansichtmenue.add(linienDickeEintrag);

		JCheckBoxMenuItem linienGitterEintrag = new JCheckBoxMenuItem("Gitter zeichnen");
		linienGitterEintrag.setSelected(true);
		linienGitterEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.GitterZeichnen, null); // Zoom out
			}
		});
		ansichtmenue.add(linienGitterEintrag);

		JMenuItem fensterAnpassenEintrag = new JMenuItem("Fenster einstellen");
		fensterAnpassenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.ZOOM_Einstellen, null); // Fenster anpassen
			}
		});
		ansichtmenue.add(fensterAnpassenEintrag);

		generatormenue = new JMenu("Generatoren"); // Menue um Graphen zu generieren
		menuezeile.add(generatormenue);
		JMenuItem vollstGraphErzEintrag = new JMenuItem("vollst. Graph");
		vollstGraphErzEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.VollstGraph, null);
			}
		});
		generatormenue.add(vollstGraphErzEintrag);

		JMenuItem bipartitGraphErzEintrag = new JMenuItem("bipartiter Graph");
		bipartitGraphErzEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(Controller.BipartiterGraph, null);
			}
		});
		generatormenue.add(bipartitGraphErzEintrag);

		hilfemenue = new JMenu("Hilfe"); // Datei-Menue
		menuezeile.add(hilfemenue);
		JMenuItem infoEintrag = new JMenuItem("Info");
		infoEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoAusgeben();
			}
		});
		hilfemenue.add(infoEintrag);

		JMenuItem testEintrag = new JMenuItem("Testfunktion");
		testEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				testfunktion();
			}
		});
		hilfemenue.add(testEintrag);

		Container contentPane = hauptfenster.getContentPane();

		contentPane.setLayout(new BorderLayout());

		topInfoLabel = new JLabel("Infozeile oben");
		contentPane.add(topInfoLabel, BorderLayout.NORTH);

		// Kern des Fensters ist das Canvas auf das gezeichnet werden soll
		center = new GraphCanvas(controller);

		center.addMouseListener(this);
		center.addMouseMotionListener(this);
		center.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				// System.out.println("Resized" + center.getWidth() + "-" + center.getHeight());
				controller.graphZeichnen();
			}
		});

		contentPane.add(center, BorderLayout.CENTER);

		statusLabel = new JLabel("Ich bin das Status-Label");
		contentPane.add(statusLabel, BorderLayout.SOUTH);

		hauptfenster.setLocation(200, 200);
		hauptfenster.setPreferredSize(new Dimension(1200, 600));
		// increaseFontSize(hauptfenster, 0); // Alle Komponenten auf den gleichen Font
		// setzen
		hauptfenster.pack();
		hauptfenster.setVisible(true);
		center.clearBGScreen();
		center.update();
	}

	private void beenden() {
		// Abzuarbeitender Code, wenn auf beenden geclickt wurde
		System.out.println("Beenden!");
		System.exit(0);
	}

	private void infoAusgeben() {
		// Abzuarbeitender Code, wenn auf Info geclickt wurde

		System.out.println("Info!");
	}

	private void testfunktion() {
		this.setEnableAlleMenueAktionen(!aktionenEnabled);
		Color newColor = JColorChooser.showDialog(null, "Choose a color", Color.RED);
		System.out.println(Integer.toHexString(newColor.getRGB()));
	}

	public BufferedImage getBufferedImage() {
		return center.getBufferedImage();
	}

	public void updateCanvas() {
		center.update();
		center.repaint();
	}

	// ******** Von außen aufzurufende Methoden ***********//

	public void setStatusLine(String text) {
		statusLabel.setText(text);
		statusLabel.repaint(); // ist das nötig?
	}

	public void increaseFontSize(Container parent, int inc) {
		generalfont = generalfont.deriveFont((float) (1.0 * generalfont.getSize() + (1.0 * inc)));
		increaseFontSizeRek(parent, inc);
	}

	public void increaseFontSizeRek(Container parent, int inc) {
		if (parent instanceof JMenu) {
			int icount = ((JMenu) parent).getItemCount();
			// System.out.println("JMenu found - Anz Component: "+icount);
			for (int i = 0; i < icount; i++)
				if (((JMenu) parent).getItem(i) != null)
					((JMenu) parent).getItem(i).setFont(generalfont);
		} else {
			for (Component c : parent.getComponents()) {
				// System.out.println(c.toString());
				Font font = c.getFont();
				// System.out.println("Font: " + font);
				if (font != null) {
					c.setFont(generalfont);
				}

				if (c instanceof Container)
					increaseFontSizeRek((Container) c, inc);
			}
		}
	}

	public void setEnableAlleMenueAktionen(boolean bool) {
		aktionenEnabled = bool;
		ansichtmenue.setEnabled(bool);
		generatormenue.setEnabled(bool);
		// System.out.println("Components in dateimenue: "+dateimenue.getItemCount());
		for (int i = 0; i < dateimenue.getItemCount(); i++) {
			JMenuItem c = dateimenue.getItem(i);
			// System.out.println(c.getText());
			if (!c.getText().equals("Beenden"))
				c.setEnabled(bool);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("Mouse Clicked:" + e);
		controller.execute(Controller.CanvasClicked, new String[] { "" + e.getX(), "" + e.getY() });
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (aktionenEnabled) {
			// System.out.println("Pressed: " + e);
			if (e.isPopupTrigger() && e.getSource().equals(center)) {
				// System.out.println("Pop-UP-Menu der Fragenliste öffnen! - Mouse pressed");
				this.doPopMenu(e);
			}
			// System.out.println(e);
			controller.grabPos(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (aktionenEnabled) {
			// System.out.println("Released: " + e);
			if (e.isPopupTrigger() && e.getSource().equals(center)) {
				// System.out.println("Pop-UP-Menu der Fragenliste öffnen! - Mouse released");
				this.doPopMenu(e);
			}
			// System.out.println(e);
			controller.released(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// System.out.println("MouseDragged: "+e.getX()+" - "+e.getY());
		if (aktionenEnabled) {
			controller.dragged(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// System.out.println("MouseMoved: "+e.getX()+" - "+e.getY());
	}

	@Override
	public void keyTyped(KeyEvent e) {
		System.out.println("key Typed: " + e);

	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("key pressed: " + e);
		if (aktionenEnabled) {
			if (e.getKeyCode() == KeyEvent.VK_PLUS && (e.getModifiers() & KeyEvent.CTRL_MASK) > 0) {
				System.out.println("CTRL + + pressed");
				controller.execute(Controller.ZOOM, new String[] { "90" }); // Zoom in
			} else if (e.getKeyCode() == KeyEvent.VK_MINUS && (e.getModifiers() & KeyEvent.CTRL_MASK) > 0) {
				System.out.println("CTRL + - pressed");
				controller.execute(Controller.ZOOM, new String[] { "110" }); // Zoom out
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	private void doPopMenu(MouseEvent e) {
		if (!ansichtmenue.isEnabled())
			return;
		JPopupMenu menu = new JPopupMenu();

		JMenuItem neuerPunktEintrag = new JMenuItem("Neuer Punkt");
		neuerPunktEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// TODO neuen Punkt anlegen und zeichnen
				controller.execute(Controller.NeuerPunkt, new String[] { "" + e.getX(), "" + e.getY() });
			}
		});
		menu.add(neuerPunktEintrag);

		JMenuItem neueKanteEintrag = new JMenuItem("Neue Kante");
		neueKanteEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				controller.execute(Controller.NeueKante, new String[] { "" + e.getX(), "" + e.getY() });
			}
		});
		menu.add(neueKanteEintrag);

		JMenuItem kantenLoeschenEintrag = new JMenuItem("eine Kante löschen");
		kantenLoeschenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				controller.execute(Controller.KantenLoeschHotspots,
						new String[] { "" + e.getX(), "" + e.getY(), "single" });
			}
		});
		menu.add(kantenLoeschenEintrag);

		JMenuItem kantenLoeschenMultiEintrag = new JMenuItem("mehrere Kanten löschen");
		kantenLoeschenMultiEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				controller.execute(Controller.KantenLoeschHotspots,
						new String[] { "" + e.getX(), "" + e.getY(), "multi" });
			}
		});
		menu.add(kantenLoeschenMultiEintrag);

		JMenuItem kantenFaerbenEintrag = new JMenuItem("Kanten färben");
		kantenFaerbenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				controller.execute(Controller.KantenFaerbeHotspots, new String[] { "" + e.getX(), "" + e.getY() });
			}
		});
		menu.add(kantenFaerbenEintrag);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	public int getMaxX() {
		return center.getWidth();
	}

	public int getMaxY() {
		return center.getHeight();
	}

	public JFrame getHauptfenster() {
		return hauptfenster;
	}

}
