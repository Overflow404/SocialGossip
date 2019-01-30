import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import javax.swing.JTextField;
import org.json.simple.JSONObject;

public class FriendRequestButtonListener extends UtilsInterface implements ActionListener{

	/**
	 * @overview	Questo listener si occupa di inviare la richiesta di
	 * 				amicizia verso un utente al server.
	 */
	private Socket s;
	private String user;
	private JTextField txUser;

	/**
	 * @constructor	Inizializza il socket del server e la textfield che contiene
	 * 				il nome dell'utente al quale inviare la richiesta di amicizia.
	 * @param		s
	 * @param		textUser
	 */
	public FriendRequestButtonListener(Socket s, JTextField textUser) {
		this.s = s;
		txUser = textUser;

	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * @effects	Se il nome dell'utente da aggiungere non e' nullo o
	 * 			vuoto si invia al server un jsonobject contenente come
	 * 			operazione friendrequestop.
	 */
	public void actionPerformed(ActionEvent e) {
		user = txUser.getText();
		if (user == null || user.trim().equals("")) {
			errorMessage("Not a valid username!");
			return;
		}

		JSONObject json = new JSONObject();
		json.put("op", FRIENDREQUEST_OP);
		json.put("user", user);
		writeJSONObject(s, json);
	}
}