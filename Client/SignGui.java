import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class SignGui {
	/**
	 * @overview	Questa classe si occupa della creazione della ui
	 * 				iniziale(login e register).
	 */
	private JLabel l1, l2, l5, l8, l9, l10, l11;
	private JButton b1;
	private JFrame frame;
	private JTextField t1, t2, t3;
	JPasswordField pf;
	JCheckBox c1;

	public SignGui() {
		runGui();
	}

	private void runGui() {
		//Constants
		final String SEPARATOR = "__________________________________________________";
		final String MINI_SEPARATOR = "_______________";
		final int LEFT_MARGIN = 80;
		final short FONT_SIZE = 16;
		final Color BACK_COLOR = new Color(36, 47, 65);
		final Color FORE_COLOR = new Color(204, 204, 204);
		final Color BUTTON_COLOR = new Color(255,127,80);
		final Color WHITE = Color.WHITE;
		final Font FONT = new Font("Century Gothic", Font.PLAIN, FONT_SIZE);
		final Font BOLD_FONT = new Font("Century Gothic", Font.BOLD, FONT_SIZE);

		frame = new JFrame("Register or login");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 800);
		frame.setResizable(false);

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBackground(BACK_COLOR);
		frame.add(panel);
		
		t3 = new JTextField();
		t3.setText("it");
		t3.setLocation(LEFT_MARGIN, 250);
		t3.setSize(450, 35);
		t3.setFont(FONT);
		t3.setForeground(WHITE);
		t3.setBorder(null);
		t3.setOpaque(false);
		t3.addMouseListener(new TextfieldMouseListener(t3));
		panel.add(t3);
		
		l1 = new JLabel("Sign in or Sign up", SwingConstants.CENTER);
		l1.setFont(FONT);
		l1.setForeground(FORE_COLOR);
		l1.setSize(130,20);
		l1.setLocation(frame.getWidth() / 2 - l1.getWidth() / 2, 130);
		l1.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(l1);

		l10 = new JLabel("LANGUAGE", SwingConstants.CENTER);
		l10.setFont(FONT);
		l10.setForeground(FORE_COLOR);
		l10.setLocation(LEFT_MARGIN, 230);
		l10.setSize(130,20);
		l10.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(l10);

		l11 = new JLabel(SEPARATOR, SwingConstants.CENTER);
		l11.setFont(BOLD_FONT);
		l11.setHorizontalAlignment(SwingConstants.LEFT);
		l11.setForeground(WHITE);
		l11.setLocation(LEFT_MARGIN, 275);
		l11.setSize(450,20);
		panel.add(l11);

		l2 = new JLabel("FULL NAME", SwingConstants.CENTER);
		l2.setFont(FONT);
		l2.setForeground(FORE_COLOR);
		l2.setLocation(LEFT_MARGIN, 310);
		l2.setSize(130,20);
		l2.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(l2);

		JLabel l4 = new JLabel("USERNAME", SwingConstants.CENTER);
		l4.setFont(FONT);
		l4.setHorizontalAlignment(SwingConstants.LEFT);
		l4.setForeground(FORE_COLOR);
		l4.setLocation(LEFT_MARGIN, 390);
		l4.setSize(130,20);
		panel.add(l4);

		JLabel l3 = new JLabel("PASSWORD", SwingConstants.CENTER);
		l3.setFont(FONT);
		l3.setForeground(FORE_COLOR);
		l3.setLocation(LEFT_MARGIN, 470);
		l3.setHorizontalAlignment(SwingConstants.LEFT);
		l3.setSize(130,20);
		panel.add(l3);

		l5 = new JLabel(SEPARATOR, SwingConstants.CENTER);
		l5.setFont(BOLD_FONT);
		l5.setHorizontalAlignment(SwingConstants.LEFT);
		l5.setForeground(WHITE);
		l5.setLocation(LEFT_MARGIN, 355);
		l5.setSize(450,20);
		panel.add(l5);

		JLabel l6 = new JLabel(SEPARATOR, SwingConstants.CENTER);
		l6.setFont(BOLD_FONT);
		l6.setHorizontalAlignment(SwingConstants.LEFT);
		l6.setForeground(WHITE);
		l6.setLocation(LEFT_MARGIN, 435);
		l6.setSize(450,20);
		panel.add(l6);

		JLabel l7 = new JLabel(SEPARATOR, SwingConstants.CENTER);
		l7.setFont(BOLD_FONT);
		l7.setHorizontalAlignment(SwingConstants.LEFT);
		l7.setForeground(WHITE);
		l7.setLocation(LEFT_MARGIN, 515);
		l7.setSize(450,20);
		panel.add(l7);

		pf = new JPasswordField();
		pf.setLocation(LEFT_MARGIN,490);
		pf.setSize(450, 35);
		pf.setFont(FONT);
		pf.setForeground(WHITE);
		pf.setBorder(null);
		pf.setOpaque(false);
		pf.addMouseListener(new TextfieldMouseListener(pf));
		panel.add(pf);

		t1 = new JTextField();
		t1.setText("Your name here");
		t1.setLocation(LEFT_MARGIN, 330);
		t1.setSize(450, 35);
		t1.setFont(FONT);
		t1.setForeground(WHITE);
		t1.setBorder(null);
		t1.setOpaque(false);
		t1.addMouseListener(new TextfieldMouseListener(t1));
		panel.add(t1);

		t2 = new JTextField();
		t2.setText("Your username here");
		t2.setLocation(LEFT_MARGIN, 410);
		t2.setSize(450, 35);
		t2.setFont(FONT);
		t2.setForeground(WHITE);
		t2.setBorder(null);
		t2.setOpaque(false);
		t2.addMouseListener(new TextfieldMouseListener(t2));
		panel.add(t2);

		c1 = new JCheckBox("I agree all terms of service");
		c1.setLocation(LEFT_MARGIN, 560);
		c1.setSize(222, 30);
		c1.setFont(FONT);
		c1.setForeground(FORE_COLOR);
		c1.setOpaque(false);
		c1.setFocusPainted(false);
		panel.add(c1);

		b1 = new JButton("Sign up");
		b1.setLocation(LEFT_MARGIN, 610);
		b1.setSize(222, 50);
		b1.setFont(FONT);
		b1.setForeground(WHITE);
		b1.setBackground(BUTTON_COLOR);
		b1.setFocusPainted(false);
		panel.add(b1);

		l8 = new JLabel("Already a member", SwingConstants.CENTER);
		l8.setFont(FONT);
		l8.setHorizontalAlignment(SwingConstants.LEFT);
		l8.setForeground(FORE_COLOR);
		l8.setLocation(340, 620);
		l8.setSize(128,20);
		panel.add(l8);

		l9 = new JLabel(MINI_SEPARATOR, SwingConstants.CENTER);
		l9.setFont(BOLD_FONT);
		l9.setHorizontalAlignment(SwingConstants.LEFT);
		l9.setForeground(WHITE);
		l9.setLocation(340, 625);
		l9.setSize(l8.getWidth() + 7, 20);
		panel.add(l9);

		frame.setVisible(true);
		l8.addMouseListener(new SignSwitchListener(this));
	}

	//Getters
	public JButton getButton() {
		return b1;
	}

	public JLabel getSwitchLabel() {
		return l8;
	}

	public JLabel getSignLabel() {
		return l1;
	}

	public JLabel getFullnameLabel() {
		return l2;
	}

	public JLabel getSeparatorLabel() {
		return l5;
	}
	
	public JLabel getSeparatorCountryLabel() {
		return l11;
	}
	
	public JLabel getCountryLabel() {
		return l10;
	}

	public JTextField getFullnameTextField() {
		return t1;
	}

	public JTextField getUsernameTextField() {
		return t2;
	}
	
	public JTextField getCountryTextField() {
		return t3;
	}

	public JPasswordField getPasswordTextField() {
		return pf;
	}

	public JLabel getSeparatorSwitchLabel() {
		return l9;
	}

	public JFrame getSignFrame() {
		return frame;
	}
	
	public JCheckBox getTermsCheckbox() {
		return c1;
	}
}
