import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ChatWorker extends UtilsInterface  implements Runnable {
	/**
	 * @Overview:	Ad ogni client connesso corrisponde un chatworker che si occupa
	 * 				di eseguire le richieste inerenti all'ambito chat.
	 */
	private final String translator = "https://api.mymemory.translated.net/get?q=";
	private HashMap<String, UserEntry> users;
	private RuntimeSupport rt;
	private String username;
	private Socket s;
	
	/**
	 * @Constructor:	Inizializza il socket relativo al client, l'username, il
	 * 					runtime support e le due strutture che contengono gli utenti
	 * 					e i gruppi.
	 */
	public ChatWorker(Socket s, String username, RuntimeSupport rt) {
		this.s = s;
		this.username = username;
		this.rt = rt;
		users = rt.getSD();
	}
	
	/**
	 * @Effects:	Dopo aver settato il socket nella hashtable
	 * 				ed aver effettuato delle operazioni di bootstrap
	 * 				si entra in un ciclo in cui si attendono le varie
	 * 				richieste in ambito chat.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		String op = null;
		JSONObject jsonObject;
		
		//Recupero socket dalla hashtable
		rt.getLock(username);
		users.get(username).setSocket(s);
		rt.freeLock(username);
		
		//Ciclo principale in cui si soddisfano le richieste del client
		boolean stop = false;
		do {
			//Lettura richiesta client
			System.out.println("blocc");
			jsonObject = readJSONObject(s, username, rt);
			System.out.println("sblocc");
			if (jsonObject == null) {
				stop = true;
				continue;
			}
			JSONObject toClient = new JSONObject();
			op = jsonObject.get("op").toString();

			//Switch sull'operazione richiesta
			switch (op) {
			case MSG2FRIEND_OP:
				String dest = jsonObject.get("dest").toString();
				String body = jsonObject.get("body").toString();

				rt.getLock(dest);
				if (!users.containsKey(dest)) {
					//Caso utente non esistente
					rt.freeLock(dest);
					toClient.put("op", NOTREG_OP);
					writeJSONObject(s, toClient);
					break;
				}
				rt.freeLock(dest);
				rt.getLock(username);
				if(!users.get(username).checkFriend(dest)) {
					//Caso utente non amico del destinatario
					rt.freeLock(username);
					toClient.put("op", NOTYOURFRIEND_OP);
					writeJSONObject(s, toClient);
				}
				else {
					//Caso ok
					String mittLang = users.get(username).getCountry();
					rt.freeLock(username);
					rt.getLock(dest);
					UserEntry tmp = users.get(dest);
					if (!tmp.checkFriend(username)) {
						//Caso destinatario non amico del mittente
						rt.freeLock(dest);
						toClient.put("op", NOTYOURFRIEND_OP);
						writeJSONObject(s, toClient);
					}
					else if (tmp.isOnline()) {
						//Caso destinatario online
						String destLang = users.get(dest).getCountry();
						rt.freeLock(dest);
						toClient.put("op", OKCHAT_OP);
						toClient.put("resp", username + ": " + body + "\n");
						writeJSONObject(s, toClient);
						if (mittLang.equals(destLang)) {
							//Caso mittente e destinatario stessa lingua
							toClient.put("resp", username + ": " + body + " \n");
							writeJSONObject(tmp.getSocket(), toClient);
						}
						else {
							//Caso mittente e destinatario con lingua divesa
							String toRest;
							String toLang = "&langpair=" + mittLang + "|" + destLang;
							String[] splittedWords = body.split("\\s+");
							StringBuilder sb = new StringBuilder();
							for (String s : splittedWords) {
								sb.append(s + "%");
							}
							
							toRest = translator + sb.toString() + toLang;
							try {
								JSONParser parser = new JSONParser();
								JSONObject obj = (JSONObject) parser.parse(getHTML(toRest));
								JSONObject obj2 = (JSONObject) obj.get("responseData");
								toClient.put("resp", username + ": " + obj2.get("translatedText").toString().replace("%", "") + " \n");
								writeJSONObject(tmp.getSocket(), toClient);
							} catch (@SuppressWarnings("unused") Exception e) {
								toClient.put("resp", username + ": " + "[ORIGINAL LANG]" + body + " \n");
								writeJSONObject(tmp.getSocket(), toClient);
								break;
							}
						}
					}
					else {
						//Caso destinatario offline
						rt.freeLock(dest);
						toClient.put("op", OFFLINEUSER_OP);
						writeJSONObject(s, toClient);
					}
				}
				break;
			case FILE_OP:
				//Inizio operazione trasferimento file
				dest = jsonObject.get("dest").toString();
				rt.getLock(dest);
				UserEntry tmp = users.get(dest);
				if (!users.containsKey(dest)) {
					//Caso destinatario non esistente
					toClient.put("op", NOTREG_OP);
					writeJSONObject(s, toClient);
					break;
				}
				if (!tmp.checkFriend(username)) {
					//Caso destinatario non amico del mittente
					toClient.put("op", NOTYOURFRIEND_OP);
					writeJSONObject(s, toClient);
					rt.freeLock(dest);
					break;
				}
				rt.freeLock(dest);
				rt.getLock(username);

				
				if(!users.get(username).checkFriend(dest)) {
					//Caso destinatario non amico dell'utente mittente
					rt.freeLock(username);
					toClient.put("op", NOTYOURFRIEND_OP);
					writeJSONObject(s, toClient);
				}
				else {
					//Caso ok
					rt.freeLock(username);
					rt.getLock(dest);
					if (tmp.isOnline()) {
						//Destinatario online
						String file = (String) jsonObject.get("file");
						toClient.put("op", PEERFILEREQUEST_OP);
						toClient.put("mitt", username);
						toClient.put("file", file);
						//Invio al destinatario una richiesta per farmi comunicare
						//ip e porta
						writeJSONObject(users.get(dest).getSocket(), toClient);
						rt.freeLock(dest);
					}
					else {
						//Destinatario offline
						rt.freeLock(dest);
						toClient.put("op", OFFLINEUSER_OP);
						writeJSONObject(s, toClient);
					}
				}
				break;
			case DETAILFILE_OP:
				//Ho ricevuto ip e porta dal server, li estraggo e comunico al mittente ip e porta
				//Da ora in poi faranno tutto in p2p.
				dest = jsonObject.get("to").toString();
				String ip = extractIPV4(jsonObject.get("ip").toString());
				String port = (String) jsonObject.get("port");
				
				JSONObject toClient1 = new JSONObject();
				toClient1.put("op", OKFILEFROMSERVER_OP);
				toClient1.put("port", port);
				toClient1.put("ip", ip);
				toClient1.put("file", jsonObject.get("file"));
				rt.getLock(dest);
				writeJSONObject(users.get(dest).getSocket(), toClient1);
				rt.freeLock(dest);
				break;	
			}
		} while(!stop);
		try {
			s.shutdownInput();
			s.shutdownOutput();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}