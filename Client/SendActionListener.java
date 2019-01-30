import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import org.json.simple.JSONObject;

public class SendActionListener extends UtilsInterface implements ActionListener{
	/**
	 * @overview	Questo listener invia, a seconda del caso, al server
	 * 				una richiesta di invio messaggio ad un singolo utente o 
	 * 				ad un gruppo.
	 */
	private JTextField username, message;
	private JComboBox<String> c;
	private String mitt;
	private PrintWriter cpw;
	private InetAddress addr;

	/**
	 * @constructor	Inizializza l'address del server(per l'invio di un file),
	 * 				il nome del mittente, l'outputstream sul quale scrivere,
	 * 				la textfield che contiene l'username del destinatario e la
	 * 				combobox che permette di switchare tra messaggio singolo, gruppo o file.
	 * @param		addr
	 * @param		mitt
	 * @param		cpw
	 * @param		username
	 * @param		message
	 * @param		c
	 */
	public SendActionListener(InetAddress addr, String mitt, PrintWriter cpw,
			JTextField username, JTextField message, JComboBox<String> c) {
		this.username = username;
		this.message = message;
		this.c = c;
		this.mitt = mitt;
		this.cpw = cpw;
		this.addr = addr;
	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * @effects	Se l'item selezionato nella combobox e'
	 * 			single message e l'username del destinatario e il messaggio non sono
	 * 			vuotu o nullu allora si invia al server un
	 * 			jsonobject contenente come operazione msg2friendop.
	 * 			Se l'item selezionato nella combobox e'
	 * 			group message e l'username del destinatario, il messaggio
	 * 			ed il nome del gruppo non sono
	 * 			vuoti o nulli allora si invia al server un
	 * 			jsonobject contenente come operazione msg2groupop.
	 */
	public void actionPerformed(ActionEvent e) {
		if (c.getSelectedIndex() == 0) {
			String user = username.getText();
			String body = message.getText();

			if (user == null || body == null) {
				errorMessage("Invalid username or message");
				return;
			}
			if (checkTV(user) || checkTV(body)) {
				errorMessage("Void username or message");
				return;
			}

			JSONObject json = new JSONObject();
			json.put("op", MSG2FRIEND_OP);
			json.put("dest", user);
			json.put("body", body);

			writeJSONObjectFromPw(cpw, json);
		}
		else if (c.getSelectedIndex() == 1) {
			String groupId = username.getText();
			String body = message.getText();

			if (groupId == null || body == null) {
				errorMessage("Invalid username or message");
				return;
			}
			if (checkTV(groupId) || checkTV(body)) {
				errorMessage("Void username or message");
				return;
			}
			if (body.length() > 800) {
				errorMessage("Message too long!");
				return;
			}

			JSONObject json = new JSONObject();
			json.put("op", MSG2GROUP_OP);
			json.put("mitt", mitt);
			json.put("group_id", groupId);
			json.put("body", body);
			System.out.println("invio groupmsg of " + json);
			byte[] buf;
			try {
				buf = json.toJSONString().getBytes("US-ASCII");
			} catch (UnsupportedEncodingException e2) {
				errorMessage("Operation failed due to encoding");
				return;
			} 

			try(DatagramSocket socket = new DatagramSocket()) {
				DatagramPacket dp = new DatagramPacket(buf, buf.length, addr, 12000);
				socket.send(dp);
			} catch (IOException e1) {
				errorMessage("Operation failed");
				return;
			}
		}
	}
}