import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

public class ChatListListener extends UtilsInterface implements ActionListener {

	/**
	 *	@overview	Invia una richiesta per ottenere la lista delle
	 *				chatroom presenti sul server.
	 */
	private PrintWriter out;
	private String loggedUser;
	
	/**
	 * @constructor	Inizializza l'outputstream e il nome dell'utente
	 * 				che richiede l'operazione.
	 * @param		out
	 * @param		loggedUser
	 */
	public ChatListListener(PrintWriter out, String loggedUser) {
		this.out = out;
		this.loggedUser = loggedUser;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	/**
	 * @effects	Invia un jsonobject al server contenente
	 * 			un'operazione di recupero lista chatroom.
	 */
	public void actionPerformed(ActionEvent e) {
		JSONObject toServer = new JSONObject();
		toServer.put("op", CHATLISTREQUEST_OP);
		toServer.put("username", loggedUser);
		writeJSONObjectFromPw(out, toServer);
	}
}