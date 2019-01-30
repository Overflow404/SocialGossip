import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import org.json.simple.JSONObject;

public class FriendListLButtonistener extends UtilsInterface implements ActionListener{
	/**
	 * @overview	Questo listener si occupa di inviare una richiesta di
	 * 				retrieve lista amici al server.
	 */
	private Socket s;

	/**
	 * @constructor	Inizializza il socket del server.
	 * @param s
	 */
	public FriendListLButtonistener(Socket s) {
		this.s = s;
	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * @effects	Invia al server un jsonobject contenente
	 * 			come operazione friendlistop.
	 */
	public void actionPerformed(ActionEvent e) {
		JSONObject json = new JSONObject();
		json.put("op", FRIENDLIST_OP);
		writeJSONObject(s, json);
	}
}