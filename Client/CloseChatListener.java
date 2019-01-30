import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import javax.swing.JTextField;
import org.json.simple.JSONObject;

public class CloseChatListener extends UtilsInterface implements ActionListener {

	/**
	 * @overview	Ogni volta che un utente chiude una chat
	 * 				va comunicata al server la chiusura.
	 */
	private PrintWriter out;
	private JTextField groupId;
	private String loggedUser;
	
	/**
	 * @constructor	Inizializza l'output stream sul socket, la textfield che contiene
	 * 				il nome del gruppo e il nome dell'utente che ha richiesto l'operazione.
	 * @param 		out
	 * @param		groupId
	 * @param		loggedUser
	 */
	public CloseChatListener(PrintWriter out, JTextField groupId, String loggedUser) {
		this.out = out;
		this.groupId = groupId;
		this.loggedUser = loggedUser;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	/**
	 * @effects	Se il nome del gruppo è nullo o vuoto non fa nulla,
	 * 			altrimenti invia al server un jsonobject contenente
	 * 			un operazione di chiusura gruppo.
	 */
	public void actionPerformed(ActionEvent e) {
		String group = groupId.getText();

		if (group == null) {
			errorMessage("Invalid group");
			return;
		}
		if (group.trim().equals("")) {
			errorMessage("Void group");
			return;
		}
		
		JSONObject toServer = new JSONObject();
		toServer.put("op", CLOSEGROUP_OP);
		toServer.put("group_name", group);
		toServer.put("mitt", loggedUser);
		System.out.println("[CLOSECHATLISTENER]Sending " + toServer);
		writeJSONObjectFromPw(out, toServer);
	}
}
