import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;

public class SignSwitchListener implements MouseListener {
	/**
	 * @overview	Questo listener si occupa di switchare la ui
	 * 				fra la modalita' register e quella login.
	 */
	private SignGui signGui;
	private final String SIGNIN_SEPARATOR = "____________";
	private final String SIGNUP_SEPARATOR = "_______________";
	
	public SignSwitchListener(SignGui signGui) {
		this.signGui = signGui;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//Lookup components with getters
		JLabel l8 = signGui.getSwitchLabel();
		JLabel signLabel = signGui.getSignLabel();
		final int CENTER_X = 300 - signLabel.getWidth() / 2;
		
		//Mode switch between signin and signup
		if (l8.getText().equals("Already a member")) {
			try {
			signGui.getCountryTextField().setVisible(false);
		
			signGui.getFullnameLabel().setVisible(false);
			signGui.getFullnameTextField().setVisible(false);
			signGui.getSeparatorLabel().setVisible(false);
			signGui.getSeparatorCountryLabel().setVisible(false);
			signGui.getCountryLabel().setVisible(false);
			signGui.getButton().setText("Sign in");
			signGui.getSeparatorSwitchLabel().setText(SIGNIN_SEPARATOR);
			signLabel.setLocation(CENTER_X, 340);
			l8.setText("Not a member");
			} catch (Exception e1) {}
		}
		else {
			signGui.getFullnameLabel().setVisible(true);
			signGui.getFullnameTextField().setVisible(true);
			signGui.getSeparatorLabel().setVisible(true);
			signGui.getSeparatorCountryLabel().setVisible(true);
			signGui.getCountryTextField().setVisible(true);
			signGui.getCountryLabel().setVisible(true);
			signGui.getButton().setText("Sign up");
			signGui.getSeparatorSwitchLabel().setText(SIGNUP_SEPARATOR);
			signLabel.setLocation(CENTER_X, 130);
			l8.setText("Already a member");
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}