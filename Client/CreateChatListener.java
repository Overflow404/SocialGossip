import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import javax.swing.JTextField;
import org.json.simple.JSONObject;

public class CreateChatListener extends UtilsInterface implements ActionListener{

	/**
	 * @overview	Listener che si occupa di inviare la richiesta di
	 * 				creare un nuovo gruppo.
	 */
	private PrintWriter out;
	private JTextField groupId;
	
	/**
	 * @constructor	Inizializza l'output stream e la textfield che contiene
	 * 				il nome del gruppo da creare.
	 * @param		out
	 * @param		groupId
	 */
	public CreateChatListener(PrintWriter out, JTextField groupId) {
		this.out = out;
		this.groupId = groupId;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	/**
	 * @effects	Se il nome del gruppo è nullo o vuoto non fa nulla.
	 * 			Invia altrimenti al server un jsonobject con operazione
	 * 			contenente la creazione gruppo.
	 */
	public void actionPerformed(ActionEvent e) {
		String text = groupId.getText();
		if (text == null || text.trim().equals("")) {
			errorMessage("Not a valid chat id!");
			return;
		}
		
		JSONObject toServer = new JSONObject();
		toServer.put("op", CREATEGROUP_OP);
		toServer.put("group_name", text);
		
		writeJSONObjectFromPw(out, toServer);
		System.out.println("create in finished");
	}
}