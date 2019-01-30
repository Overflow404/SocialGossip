import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import javax.swing.JTextField;
import org.json.simple.JSONObject;

public class EnterChatListener extends UtilsInterface implements ActionListener{

	/**
	 * @overview	Questo listener gestisce l'invio della richiesta per entrare
	 * 				in un gruppo.
	 */
	private PrintWriter out;
	private JTextField groupId;
	private String loggedUser;
	
	/**
	 * @constructor	Inizializza l'outputstream, la textfield contenente il nome
	 * 				del gruppo in cui l'utente desidera entrare ed il nome
	 * 				dell'utente che esegue la richiesta.
	 * @param		out
	 * @param		groupId
	 * @param		loggedUser
	 */
	public EnterChatListener(PrintWriter out, JTextField groupId, String loggedUser) {
		this.out = out;
		this.groupId = groupId;
		this.loggedUser = loggedUser;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	/**
	 * @effects	Se il nome del gruppo e' vuoto o nullo non fa niente.
	 * 			Altrimenti invia al server un jsonobject contenente
	 * 			come operazione entergroup.
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
		toServer.put("op", ENTERGROUP_OP);
		toServer.put("group_name", group);
		toServer.put("mitt", loggedUser);
		System.out.println("[ENTERCHATLISTENER]Sending " + toServer);
		writeJSONObjectFromPw(out, toServer);
		
	}

}
