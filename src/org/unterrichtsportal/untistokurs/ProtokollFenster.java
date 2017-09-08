package org.unterrichtsportal.untistokurs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;


public class ProtokollFenster extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ProtokollFenster instance;
	private boolean adjustScrollbar = true;
	private DefaultListModel<String> jList1Model = new DefaultListModel<String>();
	private JList<String> jList1 = new JList<String>();
	private JScrollPane sp = new JScrollPane(jList1);
	
	
	private ProtokollFenster() {
		super("Protokoll");
		Container cp = this.getContentPane();
		
		cp.setLayout(new BorderLayout());
		
		JButton b1 = new JButton("Clear");
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				ProtokollFenster.clear();
			} 
		});
		cp.add(b1, BorderLayout.NORTH);
		
		
		jList1.setModel((DefaultListModel<String>) jList1Model);
		
		sp.setViewportView(jList1);
		sp.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (adjustScrollbar) {
					e.getAdjustable().setValue(e.getAdjustable().getMaximum());
					adjustScrollbar = false;
				}
			}
		});
		
		cp.add(sp, BorderLayout.CENTER);
		this.setMinimumSize(new Dimension(800,400));
		this.pack();
		this.setVisible(true);
	}

	/**
     * Methode um die einzige! Instanz zu erhalten und falls nicht vorhanden
     * sie zu instanzieren
     */
	private static ProtokollFenster getInstance () {
		if (ProtokollFenster.instance == null) { //es existiert noch keine Instanz
			ProtokollFenster.instance = new ProtokollFenster();
		}
		return ProtokollFenster.instance;
	}
	
	public static void textAusgeben(String text) {
		ProtokollFenster pf = ProtokollFenster.getInstance();
		pf.jList1Model.addElement(text);
		pf.sp.getVerticalScrollBar().setValue(pf.sp.getVerticalScrollBar().getMaximum());
		pf.adjustScrollbar=true;
		pf.repaint();
	}
	
	public static void clear() {
		ProtokollFenster pf = ProtokollFenster.getInstance();
		pf.jList1Model.clear();
	}

}
