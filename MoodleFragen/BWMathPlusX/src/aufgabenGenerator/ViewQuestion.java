package aufgabenGenerator;

import javax.swing.*;
import java.awt.*;

/**
 * Klasse um zu einer Frage (Question) eine Darstellung anzuzeigen
 * 
 * @author peter
 *
 */
public class ViewQuestion {
	private Question q;
	private JFrame frame;
	private JTextField nameTextField;
	private JLabel nameLabel;
	private JTextArea questionTextField;
	private JLabel questionLabel;

	public ViewQuestion(Question q) {
		this.q = q;
		initComponents();

	}

	static void addComponent(Container cont, GridBagLayout gbl, Component c, int x, int y, int width, int height,
			double weightx, double weighty, int gridbagconstraints) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = gridbagconstraints;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbl.setConstraints(c, gbc);
		cont.add(c);
	}

	static void addComponent(Container cont, GridBagLayout gbl, Component c, int x, int y, int width, int height,
			double weightx, double weighty) {
		addComponent(cont, gbl, c, x, y, width, height, weightx, weighty, GridBagConstraints.BOTH);
	}

	private void initComponents() {
		frame = new JFrame("Test");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = frame.getContentPane();

		GridBagLayout gbl = new GridBagLayout();
		c.setLayout(gbl);

		nameTextField = new JTextField(30);
		nameTextField.setText(this.q.getName());
		nameLabel = new JLabel("Name: ");
		questionTextField = new JTextArea(3, 0);
		questionTextField.setText(q.getQuestiontext());
		questionLabel = new JLabel("Frage: ");

		//                                      x  y  w  h  wx   wy

		addComponent(c, gbl, nameLabel, 0, 0, 1, 1, 0, 0);
		addComponent(c, gbl, nameTextField, 1, 0, 2, 1, 1.0, 0);
		addComponent(c, gbl, questionLabel, 0, 1, 1, 1, 0, 0);
		addComponent(c, gbl, questionTextField, 1, 1, 2, 1, 1.0, 0);
		//addComponent(c, gbl, new JButton("1"), 0, 0, 2, 2, 1.0, 1.0);
		//addComponent(c, gbl, new JButton("2"), 2, 0, 1, 1, 0, 1.0);
		//addComponent(c, gbl, new JButton("3"), 2, 1, 1, 1, 0, 0);
		addComponent(c, gbl, new JButton("4"), 0, 2, 3, 1, 0, 1.0);
		addComponent(c, gbl, new JButton("5"), 0, 3, 2, 1, 0, 0);
		addComponent(c, gbl, new JButton("6"), 0, 4, 2, 1, 0, 0);
		addComponent(c, gbl, new JButton("7"), 2, 3, 1, 2, 0, 0);

		frame.setSize(300, 200);
		frame.setVisible(true);
	}
}
