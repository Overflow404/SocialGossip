import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class UtilsInterface {
	//Un po' di stringhe per codificare i messaggi.
	final String OK_OP = "OK_OP";
	final String FILE_OP = "FILE_OP";
	final String LOGIN_OP = "LOGIN_OP";
	final String NOTREG_OP = "NOTREG_OP";
	final String OKCHAT_OP = "OKCHAT_OP";
	final String ALREADY_OP = "ALREADY_OP";
	final String CLOSING_OP = "CLOSING_OP";
	final String OKGROUP_OP = "OKGROUP_OP";
	final String ERRCODE_OP = "ERRCODE_OP";
	final String REGISTER_OP = "REGISTER_OP";
	final String WRONGPWD_OP = "WRONGPWD_OP";
	final String NOFRIEND_OP = "NOFRIEND_OP";
	final String CHATLIST_OP = "CHATLIST_OP";
	final String DETAILFILE_OP = "DETAILFILE_OP";
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
	final String PEERFILEREQUEST_OP = "PEERFILEREQUEST_OP";
	final String CHATLISTREQUEST_OP = "CHATLISTREQUEST_OP";
	final String FRIENDTOYOURSELF_OP = "FRIENDTOYOURSELF_OP";
	final String NOONEONLINEGROUP_OP = "NOONEONLINEGROUP_OP";
	final String OKFILEFROMSERVER_OP = "OKFILEFROMSERVER_OP";
	final String CHATLISTREQUESTOK_OP = "CHATLISTREQUESTOK_OP";
	final String OK_OP_STATUS = "OK_OP_STATUS";
	final String OK_OP_REG = "OK_OP_REG";
	final String OK_OP_FRLIST = "OK_OP_FRLIST";

	@SuppressWarnings("unchecked")
	/**
	 * @effects	Invia una risposta sul socket s contenente come operazione code.
	 * @param	s
	 * @param	code
	 */
	void sendResponse(Socket s, String code) {
		JSONObject json = new JSONObject();
		json.put("op", code);

		try {
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			out.print(json.toJSONString() + "\n");
			out.flush();
		} catch (IOException e) {
			System.err.print("[WORKER]Error creating output stream " + e);
			try {
				s.close();
			} catch (IOException e1) {
				System.err.print("[WORKER]Error closing socket: " + e1);
			}
			return;
		}
	}
	
	/**
	 * @effects	Scrive il jsonobject json sul socket s.
	 * @param	s
	 * @param	json
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
	 * @effects	Scrive il jsonobject json sul printwriter out.
	 * @param	out
	 * @param	json
	 */
	void writeJSONObjectFromPw(PrintWriter out, JSONObject json) {
		out.println(json);
		out.flush();
	}

	/**
	 * @param	s
	 * @param	user
	 * @param	rt
	 * @return	Il jsonobject letto dal socket s.
	 */
	JSONObject readJSONObject(Socket s, String user, RuntimeSupport rt) {
		BufferedReader in;
		JSONObject jsonObject = null;
		try {
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String textFromServer = in.readLine();
			System.out.println("[CODE]Received: " + textFromServer);
			JSONParser parser = new JSONParser();

			try {
				jsonObject = (JSONObject) parser.parse(textFromServer);
			} catch (@SuppressWarnings("unused") ParseException e1) {
				return null;
			}
		}
		catch (@SuppressWarnings("unused") IOException e1) {
			rt.getLock(user);
			rt.getSD().get(user).setStatus(false);
			rt.freeLock(user);
			return null;
		}
		return jsonObject;
	}
	
	/**
	 * @param	s
	 * @param	user
	 * @param	rt
	 * @return	Il jsonobject letto dal socket s.
	 */
	JSONObject readJSONObjectNU(Socket s) {
		BufferedReader in;
		JSONObject jsonObject = null;
		try {
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String textFromServer = in.readLine();
			System.out.println("[CODE]Received: " + textFromServer);
			JSONParser parser = new JSONParser();

			try {
				jsonObject = (JSONObject) parser.parse(textFromServer);
				System.out.println("parse");
			} catch (@SuppressWarnings("unused") ParseException e1) {
				return null;
			}
		}
		catch (@SuppressWarnings("unused") IOException e1) {
			System.out.println("ioexcep");
			return null;
		}
		return jsonObject;
	}
	
	/**
	 * @param s
	 * @return	True se s non e' vuota, altrimenti false.
	 */
	boolean checkTV(String s) {
		return s.trim().equals("");
	}
	
	/**
	 * 
	 * @param	urlToRead
	 * @return	Una stringa contente l'html relativo alla richiesta get effettuata.
	 * @throws	Exception
	 */
	String getHTML(String urlToRead) throws Exception {
	      StringBuilder result = new StringBuilder();
	      URL url = new URL(urlToRead);
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      conn.setRequestMethod("GET");
	      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	      String line;
	      while ((line = rd.readLine()) != null) {
	         result.append(line);
	      }
	      rd.close();
	      return result.toString();
	   }
	
	/**
	 * @param	ip
	 * @return	Una stringa contenente l'indirizzo ipv4 estratto da ip.
	 */
	String extractIPV4(String ip) {
		String IPADDRESS_PATTERN = 
		        "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		Matcher matcher = pattern.matcher(ip);
		if (matcher.find()) {
		    return matcher.group();
		}
		return "0.0.0.0";
	}
}