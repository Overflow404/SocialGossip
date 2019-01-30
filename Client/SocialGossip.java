import javax.swing.JButton;

public class SocialGossip {
	/**
	 * @overview	Main class, da qui parte tutto.
	 * @param 		args
	 */
	public static void main(String[] args) {
		System.setProperty("java.net.preferIPv4Stack" , "true");
		SignGui sg = new SignGui();
		JButton b1 = sg.getButton();
		b1.addActionListener(new SignListener(sg));
	}
}