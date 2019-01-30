import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;

public class TextfieldMouseListener implements MouseListener{
	/**
	 * @overview	Questo listener ha semplicemente il compito
	 * 				di selezionare il testo al click su una textarea.
	 */
	private JTextField t1;
	
	public TextfieldMouseListener(JTextField t1) {
		this.t1 = t1;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		t1.selectAll();	
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
