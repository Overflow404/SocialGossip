import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.simple.JSONObject;

public class FileActionListener extends UtilsInterface implements ActionListener{
	/**
	 * @overview	Questo listener si occupa dell'operazione di invio di un file.
	 */
	private JPanel panel;
	private int result;
	private File selectedFile;
	private JTextField dest;
	private PrintWriter out;
	
	/**
	 * @constructor	Inizializza la dialog per scegliere il file, il nome dell'utente
	 * 				che richiede l'operazione, la textfield che contiene il nome
	 * 				del destinatario e l'outputstream
	 * @param		panel
	 * @param		loggedUser
	 * @param		dest
	 * @param		out
	 */
	public FileActionListener(JPanel panel, String loggedUser, JTextField dest, PrintWriter out) {
		this.panel = panel;
		result = 0;
		selectedFile = null;
		this.dest = dest;
		this.out = out;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	/**
	 * @effects	Se viene selezionato un file si invia una richiesta
	 * 			contenente fileop al server.
	 * @param	e
	 */
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		result = fileChooser.showOpenDialog(panel);
		
		//Check approve
		if (result == JFileChooser.APPROVE_OPTION) {
			//pick up selected file
			selectedFile = fileChooser.getSelectedFile();
			System.out.println(selectedFile.toString() + result + "");
			
			String to = dest.getText();
			if (checkTV(to)) {
				errorMessage("Not a valid username");
				return;
			}
			
			JSONObject json = new JSONObject();
			json.put("op", FILE_OP);
			json.put("dest", to);
			json.put("file", selectedFile.toString());
			SocialGui.textArea.append("Voglio inviare a " + to + " il file: " + selectedFile.toString() + "\n");
			writeJSONObjectFromPw(out, json);
		}
		else {
			System.out.println("no file selection" + result);
		}

	}
	
	/**
	 * @return	Il risultato sulla dialog di scelta file.
	 */
	public int getResult() {
		return result;
	}
	
	/**
	 * @return	Il file selezionato dall'utente.
	 */
	public File getFile() {
		return selectedFile;
	}
}