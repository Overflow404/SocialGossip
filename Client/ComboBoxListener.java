import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ComboBoxListener implements ItemListener{
	/**
	 * @overview	Questa classe gestisce il cambiamento grafico della gui quando
	 * 				si seleziona dalla combobox un messaggio singolo, di gruppo
	 * 				o un file.
	 */
	private JTextField tx1, tx2;
	private JLabel label_1;
	private JButton file, send;
	
	/**
	 * @constructor	Inizializza le componenti grafiche che vanno messe in hide
	 * 				al click di un item della combobox.
	 * @param		tx1
	 * @param		label_1
	 * @param		file
	 * @param		tx2
	 * @param		send
	 */
	public ComboBoxListener(JTextField tx1, JLabel label_1, JButton file, JTextField tx2, JButton send) {
		this.tx1 = tx1;
		this.tx2 = tx2;
		this.label_1 = label_1;
		this.send = send;
		this.file = file;
	}
	
	@Override
	/**
	 * @effects	Setta a true o false la proprieta' visible delle
	 * 			componenti grafiche da nascondere al click di un item
	 * 			della combobox.
	 */
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
	          Object item = e.getItem();
	          if (item.equals("Single message")) {
	        	  tx1.setVisible(true);
	        	  label_1.setVisible(true);
	        	  file.setVisible(false);
	        	  tx1.setText("Username here");
	        	  tx2.setText("Message here");
	        	  send.setVisible(true);
	          }
	          else if (item.equals("Group message")) {
	        	  tx1.setVisible(true);
	        	  label_1.setVisible(true);
	        	  file.setVisible(false);
	        	  tx1.setText("Group id here");
	        	  tx2.setText("Message here");
	        	  send.setVisible(true);
	          }
	          else {
	        	  tx1.setVisible(false);
	        	  label_1.setVisible(false);
	        	  file.setVisible(true);;
	        	  tx2.setText("Username here");
	        	  send.setVisible(false);
	          }
	   
	       }	
	}
}