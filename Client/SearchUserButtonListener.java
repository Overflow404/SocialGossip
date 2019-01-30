import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import javax.swing.JTextField;
import org.json.simple.JSONObject;

public class SearchUserButtonListener extends UtilsInterface implements ActionListener{
	/**
	 * @overview	Questo listener effettua la richiesta di informazioni su
	 * 				un utente al server.
	 */
	private Socket s;
	private String user;
	private JTextField txUser;

	/**
	 * @constructor	Inizializza il socket del server e la textfield
	 * 				che contiene il nome dell'utente su cui richiedere
	 * 				informazioni al server.
	 * @param		s
	 * @param		textUser
	 */
	public SearchUserButtonListener(Socket s, JTextField textUser) {
		this.s = s;
		txUser = textUser;
	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * @effects	Invia al server un jsonobject contenente come
	 * 			operazione searchuserop.
	 */
	public void actionPerformed(ActionEvent e) {
		user = txUser.getText();
		if (user == null || user.trim().equals("")) {
			errorMessage("Not a valid username!");
			return;
		}
		
		JSONObject json = new JSONObject();
		json.put("op", SEARCHUSER_OP);
		json.put("user", user);
		writeJSONObject(s, json);
	}
}