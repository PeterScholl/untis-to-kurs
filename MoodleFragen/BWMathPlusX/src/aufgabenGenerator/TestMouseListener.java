package aufgabenGenerator;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;

/**
 * Diese Klasse dient nur dazu probehalber auf Mouseclicks zu reagieren und die
 * Inhalte der Information auszugeben
 * 
 * @author peter.scholl@aeg-online.de
 *
 */
public class TestMouseListener implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent e) {
		//
		System.out.println("MouseEvent\nSource: " + e.getSource());
		System.out.println("Source-Klasse: " + e.getSource().getClass());
		System.out.println("Click-Count: " + e.getClickCount());
		if (e.getClickCount() == 2 && e.getSource() instanceof JList) {
			@SuppressWarnings("unchecked")
			JList<String> theList = (JList<String>) e.getSource();
			int index = theList.locationToIndex(e.getPoint());
			if (index >= 0) {
				Object o = theList.getModel().getElementAt(index);
				System.out.println("Double-clicked on: " + o.toString());
			}
		}
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
