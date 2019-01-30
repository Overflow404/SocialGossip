import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class UtilsInterface {
	final String OK_OP = "OK_OP";
	final String FILE_OP = "FILE_OP";
	final String LOGIN_OP = "LOGIN_OP";
	final String NOTREG_OP = "NOTREG_OP";
	final String OKCHAT_OP = "OKCHAT_OP";
	final String ALREADY_OP = "ALREADY_OP";
	final String CLOSING_OP = "CLOSING_OP";
	final String OKGROUP_OP = "OKGROUP_OP";
	final String DETAILFILE_OP = "DETAILFILE_OP";
	final String PEERFILEREQUEST_OP = "PEERFILEREQUEST_OP";
	final String OKFILEFROMSERVER_OP = "OKFILEFROMSERVER_OP";
	final String ERRCODE_OP = "ERRCODE_OP";
	final String REGISTER_OP = "REGISTER_OP";
	final String WRONGPWD_OP = "WRONGPWD_OP";
	final String NOFRIEND_OP = "NOFRIEND_OP";
	final String CHATLIST_OP = "CHATLIST_OP";
	final String MSG2GROUP_OP = "MSG2GROUP_OP";
	final String BOOTSTRAP_OP = "BOOTSTRAP_OP";
	final String SEARCHUSER_OP = "SEARCHUSER_OP";
	final String FRIENDLIST_OP = "FRIENDLIST_OP";
	final String MSG2FRIEND_OP = "MSG2FRIEND_OP";
	final String ENTERGROUP_OP = "ENTERGROUP_OP";
	final String CLOSEGROUP_OP = "CLOSEGROUP_OP";
	final String CREATEGROUP_OP = "CREATEGROUP_OP";
	final String OFFLINEUSER_OP = "OFFLINEUSER_OP";
	final String ALREADYGROUP_OP = "ALREADYGROUP_OP";
	final String OKGROUPMSG_OP = "NOTGROUPMEMBER_OP";
	final String OKENTERGROUP_OP = "OKENTERGROUP_OP";
	final String FRIENDREQUEST_OP = "FRIENDREQUEST_OP";
	final String NOTYOURFRIEND_OP = "NOTYOURFRIEND_OP";
	final String GROUPNOTFOUND_OP = "GROUPNOTFOUND_OP";
	final String NOTGROUPOWNER_OP = "NOTGROUPOWNER_OP";
	final String DELETEGROUPOK_OP = "DELETEGROUPOK_OP";
	final String NOTGROUPMEMBER_OP = "NOTGROUPMEMBER_OP";
	final String ALREADYINGROUP_OP = "ALREADYINGROUP_OP";
	final String CHATLISTREQUEST_OP = "CHATLISTREQUEST_OP";
	final String FRIENDTOYOURSELF_OP = "FRIENDTOYOURSELF_OP";
	final String NOONEONLINEGROUP_OP = "NOONEONLINEGROUP_OP";
	final String CHATLISTREQUESTOK_OP = "CHATLISTREQUESTOK_OP";
	final String OK_OP_STATUS = "OK_OP_STATUS";
	final String OK_OP_REG = "OK_OP_REG";
	final String OK_OP_FRLIST = "OK_OP_FRLIST";
	
	/**
	 * @effects	Mostra una messagebox di errore con un body
	 * @param	body
	 */
	void errorMessage(String body) {
		JOptionPane.showMessageDialog(null, body, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * @effects	Mostra una messagebox informativa con un body
	 * @param	body
	 */
	void infoMessage(String body) {
		JOptionPane.showMessageDialog(null, body, "Information", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * @effects	Scrive il jsonobject json sull'outputstream del socket s.
	 * @param	s
	 * @param 	json
	 */
	void writeJSONObject(Socket s, JSONObject json) {
		PrintWriter out;
		try {
			out = new PrintWriter(s.getOutputStream(), true);
			System.out.println("[UTILS]Sending: " + json);
			out.println(json);
			out.flush();
		} catch (IOException e) {
			System.err.println("[UTILS]Error writing jsonobject: " + e);
			return;
		}
	}

	/**
	 * @effects	Scrive il jsonobject json sull'outputstream out.
	 * @param	out
	 * @param	json
	 */
	void writeJSONObjectFromPw(PrintWriter out, JSONObject json) {
		out.println(json);
		out.flush();
	}
	
	/**
	 * @param	s
	 * @return	Il jsonobject letto dal socket s.
	 */
	JSONObject readJSONObject(Socket s) {
		BufferedReader in;
		JSONObject jsonObject = null;
		try {
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String textFromServer = in.readLine();
			if (textFromServer == null) {
				return null;
			}
			System.out.println("[UTILS]Received: " + textFromServer);
			JSONParser parser = new JSONParser();

			try {
				jsonObject = (JSONObject) parser.parse(textFromServer);
			} catch (ParseException e1) {
				System.err.println("[UTILS]Error in parsing request: " + e1);
				return null;
			}
		}
		catch (IOException e1) {
			System.err.print("[UTILS]Error opening input stream: " + e1);
			return null;
		}
		return jsonObject;
	}
	
	boolean checkTV(String s) {
		return s.trim().equals("");
	}
	
}