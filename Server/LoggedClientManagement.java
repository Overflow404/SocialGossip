import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LoggedClientManagement extends UtilsInterface  {

	/**
	 * Overview:	Questa classe gestisce un gruppo di operazioni quali richiesta amicizia, lista amici,
	 * 				info utente e chiusura.
	 */
	private Socket s;
	private String myUsername;
	private HashMap<String, UserEntry> users;
	private HashMap<String, ChatGroupEntry> chatId;
	private RuntimeSupport rt;
	private final String lastMulticastIp = "239.255.255.255";
	private static String firstMulticastIp = "224.0.0.255";
	/**
	 * @constructor		Inizializza il socket, il runtime support e l'username del
	 * 					client collegato.
	 * @param 			s
	 * @param 			rt
	 * @param 			myUsername
	 * @throws 			RemoteException
	 */
	public LoggedClientManagement(Socket s, RuntimeSupport rt, String myUsername) throws RemoteException {
		this.s = s;
		this.users = rt.getSD();
		this.myUsername = myUsername;
		this.rt = rt;
		chatId = rt.getChatSD();
		run();
	}

	/**
	 * @effects	Questa porzione di codice ascolta le richieste provenienti dal client
	 * 			e le soddisfa.
	 * @throws	RemoteException
	 */
	@SuppressWarnings("unchecked")
	public void run() throws RemoteException {
		String op = "";
		JSONObject jsonObject;
		
		
		//Al login del client eseguo delle operazioni di bootstrap, quali recupero gruppi e ip dei gruppi.
		JSONObject bootstrap = new JSONObject();
		JSONArray bootstrapArray = new JSONArray();
		JSONArray bootstrapIp = new JSONArray();
		bootstrap.put("op", CHATLIST_OP);
		rt.getLock(myUsername);
		Vector<String> groupList = users.get(myUsername).getGroupList();
		groupList.forEach(g -> {
			bootstrapArray.add(g);
			rt.getChatLock(g);
			String ip = chatId.get(g).getIp().toString().substring(1);
			if (!bootstrapIp.contains(ip))
				bootstrapIp.add(ip);
			rt.freeChatLock(g);
		});
		rt.freeLock(myUsername);
		
		bootstrap.put("ip_list", bootstrapIp);
		bootstrap.put("group_list", bootstrapArray);
		writeJSONObject(s, bootstrap);
		
		
		do {
			//Lettura richiesta
			jsonObject = readJSONObject(s, myUsername, rt);
			if (jsonObject == null) {
				continue;
			}
			JSONObject toClient = new JSONObject();
			op = jsonObject.get("op").toString();
			switch (op) {
			case SEARCHUSER_OP:
				String user = jsonObject.get("user").toString();
				rt.getLock(myUsername);
				if (users.containsKey(user)) {
					//Caso utente esistente, che sia offline o online.
					toClient.put("status", users.get(user).isOnline());
					rt.freeLock(myUsername);
					toClient.put("op", OK_OP_STATUS);
				}
				else {
					//Caso utente non esistente.
					rt.freeLock(myUsername);
					toClient.put("op", NOTREG_OP);
				}
				writeJSONObject(s, toClient);
				break;
			case FRIENDREQUEST_OP:
				String userToAdd = jsonObject.get("user").toString();
				rt.getLock(myUsername);
				if (userToAdd.equals(myUsername)) {
					//Caso richiesta di amicizia a se stessi.
					rt.freeLock(myUsername);
					toClient.put("op", FRIENDTOYOURSELF_OP);
				}
				else if (users.get(myUsername).checkFriend(userToAdd)) {
					//Caso di utenti gia' amici.
					rt.freeLock(myUsername);
					toClient.put("op", ALREADY_OP);
				}
				else {
					if (users.containsKey(userToAdd)) {
						//Caso ok.
						toClient.put("op", OK_OP_REG);
						users.get(myUsername).addFriend(userToAdd);
						users.get(userToAdd).addReverseFriend(myUsername);
						doFriendShipCallback(userToAdd);
						rt.freeLock(myUsername);
					}
					else {
						//Caso utente da aggiungere non registrato.
						rt.freeLock(myUsername);
						toClient.put("op", NOTREG_OP);
					}
				}
				writeJSONObject(s, toClient);
				break;
			case FRIENDLIST_OP:
				rt.getLock(myUsername);
				//Prelevo la lista degli amici
				Vector<String> frList = users.get(myUsername).getAllFriend();
				if (frList.isEmpty()) {
					//Caso utente senza amici
					rt.freeLock(myUsername);
					toClient.put("op", NOFRIEND_OP);
				}
				else {
					//Caso ok, creo un JSONArray lo inserisco nel JSONObject e lo mando.
					toClient.put("op", OK_OP_FRLIST);
					JSONArray tmp = new JSONArray();
					frList.forEach(friend -> tmp.add(friend));
					rt.freeLock(myUsername);
					toClient.put("friend_list", tmp);
				}
				writeJSONObject(s, toClient);
				break;
				
			case CREATEGROUP_OP:
				String groupId = (String) jsonObject.get("group_name");
				rt.getChatLock(groupId);
				if (chatId.containsKey(groupId)) {
					//Caso gruppo esistente
					rt.freeChatLock(groupId);
					toClient.put("op", ALREADYGROUP_OP);
					writeJSONObject(s, toClient);
				}
				else {
					//Caso gruppo ok
					firstMulticastIp = nextIpAddress(firstMulticastIp);
					if (firstMulticastIp.equals(lastMulticastIp)) {
						//Caso ip terminati
						firstMulticastIp = "224.0.0.255";
						rt.freeChatLock(groupId);
						System.err.println("Maximum ip available for multicast reached!");
						toClient.put("op", ERRCODE_OP);
						writeJSONObject(s, toClient);
						return;
					}

					InetAddress ia;
					try {
						ia = InetAddress.getByName(firstMulticastIp);
					} catch (UnknownHostException e) {
						rt.freeChatLock(groupId);
						System.err.println("Error getting multicast ip: " + e);
						toClient.put("op", ERRCODE_OP);
						writeJSONObject(s, toClient);
						return;
					}
					
					//Aggiunta owner al gruppo.
					System.out.println("aggiunta ok al gruppo");
					ChatGroupEntry n = new ChatGroupEntry(ia, myUsername);
					n.addMember(myUsername);
					chatId.put(groupId, n);
					rt.freeChatLock(groupId);
					
					toClient.put("op", OKGROUP_OP);
					toClient.put("mip", firstMulticastIp);
					writeJSONObject(s, toClient);
					rt.getLock(myUsername);
					users.get(myUsername).getGroupList().add(groupId);
					rt.freeLock(myUsername);
				}
				break;
			case ENTERGROUP_OP:
				groupId = (String) jsonObject.get("group_name");
				rt.getChatLock(groupId);
				if (!chatId.containsKey(groupId)) {
					//Caso gruppo non esistente
					rt.freeChatLock(groupId);
					toClient.put("op", GROUPNOTFOUND_OP);
					writeJSONObject(s, toClient);
				}
				else if (chatId.get(groupId).isSubscribed(jsonObject.get("mitt").toString())) {
					//Caso gruppo gia' registrato
					rt.freeChatLock(groupId);
					toClient.put("op", ALREADYINGROUP_OP);
					writeJSONObject(s, toClient);
				}
				else {
					//Caso ok
					toClient.put("op", OKENTERGROUP_OP);
					String mitt = jsonObject.get("mitt").toString();
					toClient.put("mip", chatId.get(groupId).getIp().getHostAddress());
					chatId.get(groupId).addMember(mitt);
					rt.freeChatLock(groupId);
					writeJSONObject(s, toClient);
					
					rt.getLock(myUsername);
					users.get(myUsername).addGroup(groupId);
					rt.freeLock(myUsername);
				}
				break;
			case CLOSEGROUP_OP:
				groupId = (String) jsonObject.get("group_name");
				rt.getChatLock(groupId);
				if (!chatId.containsKey(groupId)) {
					//Caso gruppo non esistente
					rt.freeChatLock(groupId);
					toClient.put("op", GROUPNOTFOUND_OP);
					writeJSONObject(s, toClient);
				}
				else {
					if (chatId.get(groupId).getOwner().equals(jsonObject.get("mitt"))) {
						//Caso close group ok
						try {
							//Avviso i client registrati al gruppo che sta chiudendo
							doGroupClosingCallback(groupId);
							chatId.get(groupId).getParts().forEach(m -> {
								rt.getLock(m);
								users.get(m).getGroupList().remove(groupId);
								rt.freeLock(m);
								});
							chatId.get(groupId).resetGroupNEI();
						} catch (@SuppressWarnings("unused") RemoteException e) {
							return;
						}
						chatId.remove(groupId);
						rt.freeChatLock(groupId);
						toClient.put("op", DELETEGROUPOK_OP);
						writeJSONObject(s, toClient);
					}
					else {
						//Caso utente non owner chiude gruppo
						rt.freeChatLock(groupId);
						toClient.put("op", NOTGROUPMEMBER_OP);
						writeJSONObject(s, toClient);
					}
				}
				break;
			case CHATLISTREQUEST_OP:
				JSONArray listOfGroup = new JSONArray();
				//Scorro i gruppi, per ogni gruppo vedo se l'utente che ha effettuato
				//richiesta appartiene o meno.
				chatId.forEach((k, v) -> {
					rt.getChatLock(k);
					rt.getLock(myUsername);
					if (users.get(myUsername).getGroupList().contains(k)) {
						listOfGroup.add(k + " -> you're member");
					}
					else {
						listOfGroup.add(k + " -> you aren't member");
					}
					rt.freeLock(myUsername);
					rt.freeChatLock(k);
				});
				
				toClient.put("op", CHATLISTREQUESTOK_OP);
				toClient.put("list", listOfGroup);
				writeJSONObject(s, toClient);
				break;
			case CLOSING_OP:
				//Il client si e' disconnesso.
				
				rt.getLock(myUsername);
				users.get(myUsername).setStatus(false);
			//	users.get(username).resetGroupNEI();
				doStatusChangeCallback();
				rt.freeLock(myUsername);
				try {
					s.shutdownInput();
					s.shutdownOutput();
					s.close();
				} catch (IOException e) {
					System.err.println("[LOGGEDCLIENTMANAGMENT]Error closing socket and streams: " + e);
					return;
				}
				break;
			}
		} while(!op.equals(CLOSING_OP));
	}

	/**
	 * @effects Effettua la notifica di amicizia attraverso RMI.
	 * @param	userToAdd
	 * @throws	RemoteException
	 */
	private void doFriendShipCallback(String userToAdd) throws RemoteException {
		UserEntry tmp = users.get(userToAdd);
		if (tmp.isOnline()) {
			if (tmp.getNEI() != null)
				tmp.getNEI().notifyEvent(myUsername + " ti ha aggiunto!");
		}
		else {
			System.out.println("[LOGGEDCLIENTMANAGMENT]User offline");
		}
	}
	
	/**
	 * @effects Effettua la notifica di cambiamento di stato attraverso RMI.
	 * @throws	RemoteException
	 */
	private void doStatusChangeCallback() throws RemoteException {
		UserEntry tmp = users.get(myUsername);
		for(String myFriend : tmp.getAllReverseFriend()) {
			UserEntry myFriendEntry = users.get(myFriend);
			if (myFriendEntry.isOnline()) {
				myFriendEntry.getNEI().notifyEvent(myUsername + " e' andato offline!");
			}
		}
	}
	
	/**
	 * @Effects:	Esegue la callback per avvisare tutti gli utenti	
	 * 				registrati ad un gruppo che il gruppo e' appena stato chiuso.
	 * @param 		groupId
	 * @throws 		RemoteException
	 */
	private void doGroupClosingCallback(String groupId) throws RemoteException {
		ChatGroupEntry tmp = chatId.get(groupId);
		Vector<String> member = tmp.getParts();
		for (String p : member) {
			rt.getLock(p);
			if (users.get(p).isOnline()) {
				NotifyEventInterface nei = users.get(p).getGroupNEI();
				if (nei != null) {
					nei.notifyEvent("The group " + groupId + " is now closed");
				}
			}
			rt.freeLock(p);
		}
	}
	
	/**
	 * @Effects:	Ritorna l'ip successivo ad input.
	 * @param 		input
	 * @return		Ip successivo(come stringa)
	 */
	private final String nextIpAddress(final String input) {
	    final String[] tokens = input.split("\\.");
	    if (tokens.length != 4)
	        throw new IllegalArgumentException();
	    for (int i = tokens.length - 1; i >= 0; i--) {
	        final int item = Integer.parseInt(tokens[i]);
	        if (item < 255) {
	            tokens[i] = String.valueOf(item + 1);
	            for (int j = i + 1; j < 4; j++) {
	                tokens[j] = "0";
	            }
	            break;
	        }
	    }
	    return new StringBuilder()
	    .append(tokens[0]).append('.')
	    .append(tokens[1]).append('.')
	    .append(tokens[2]).append('.')
	    .append(tokens[3])
	    .toString();
	}
}
