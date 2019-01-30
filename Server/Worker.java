import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Worker extends UtilsInterface implements Runnable {
	/**
	 * @overview	Ad ogni client connesso corrisponde un worker di questo tipo
	 * 				che soddisfa le richieste generiche di amicizia, info utente, lista
	 * 				amici etc.
	 */
	
	private Socket connectionSocket;
	private HashMap<String, UserEntry> users;
	private RuntimeSupport rt;
	/**
	 * @constructor	Inizializza il socket relativo all'utente ed il 
	 * 				runtime support.
	 * @param		connectionSocket
	 * @param		rt
	 */
	public Worker(Socket connectionSocket, RuntimeSupport rt) {
		this.connectionSocket = connectionSocket;
		this.rt = rt;
		users = rt.getSD();
	}

	/**
	 * @effects:	Resta in attesa fin quando non riceve un operazione o di register
	 * 				o di login. Se il login avviene con successo passa il controllo
	 * 				al loggedclientmanagment.
	 */
	public void run() {
		BufferedReader in;
		String textFromClient;

		try {
			in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			textFromClient = in.readLine();
		} catch (IOException e) {
			System.err.print("[WORKER]Error creating the input stream: " + e);
			try {
				connectionSocket.close();
			} catch (IOException e1) {
				System.err.print("[WORKER]Error closing socket: " + e1);
			}
			return;
		}
		JSONParser parser = new JSONParser();
		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) parser.parse(textFromClient);
		} catch (ParseException e) {
			System.err.print("[WORKER]Error in parsing request: " + e);
			try {
				connectionSocket.close();
			} catch (IOException e1) {
				System.err.print("[WORKER]Error closing socket: " + e1);
			}
			return;
		}

		String op = jsonObject.get("op").toString();
		String username = jsonObject.get("username").toString();
		String password = jsonObject.get("password").toString();
		switch (op) {
		case REGISTER_OP:
			String fullname = jsonObject.get("fullname").toString();
			String country = jsonObject.get("language").toString();
			rt.atomicRegister(username, fullname, password, country, connectionSocket);
			closeStreamAndSocket(connectionSocket);
			break;
		case LOGIN_OP:
			rt.getLock(username);
			if (users.containsKey(username)) {
				UserEntry tmp = users.get(username);
				if (tmp.getPassword().equals(password)) {
					//Caso password ok
					if (tmp.isOnline()) {
						//Caso utente gia' loggato
						rt.freeLock(username);
						sendResponse(ALREADY_OP);
						closeStreamAndSocket(connectionSocket);
					}
					else {
						//Caso ok
						tmp.setStatus(true);
						try {
							doStatusChangeCallback(username);
							rt.freeLock(username);
						} catch (RemoteException e1) {
							rt.freeLock(username);
							System.err.println("[WORKER]Error in statusChangeCallback: " + e1);
							return;
						}
						sendResponse(OK_OP);
						
						//Il socket e' ancora aperto, l'IO anche.
						try {
							new LoggedClientManagement(connectionSocket, rt, username);
						} catch (RemoteException e) {
							System.err.println("[WORKER]Error with callback register: ");
							e.printStackTrace();
							break;
						}
					}
				}
				else {
					//caso password errata
					rt.freeLock(username);
					sendResponse(WRONGPWD_OP);
					closeStreamAndSocket(connectionSocket);
				}
			}
			else {
				//caso utente non registrato
				rt.freeLock(username);
				sendResponse(NOTREG_OP);
				closeStreamAndSocket(connectionSocket);
			}
			break;	
		default:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * @effects	Invia un jsonobject con operazione uguale a code.
	 * @param	code
	 */
	private void sendResponse(String code) {
		JSONObject json = new JSONObject();
		json.put("op", code);

		try {
			PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);
			out.print(json.toJSONString() + "\n");
			out.flush();
		} catch (IOException e) {
			System.err.print("[WORKER]Error creating output stream " + e);
			try {
				connectionSocket.close();
			} catch (IOException e1) {
				System.err.print("[WORKER]Error closing socket: " + e1);
			}
			return;
		}
	}

	/**
	 * @effects	Chiude l'i/o sul socket ed il socket.
	 * @param	s
	 */
	private void closeStreamAndSocket(Socket s) {
		try {
			s.shutdownInput();
			s.shutdownOutput();
			s.close();
		} catch (IOException e) {
			System.err.print("[WORKER]Error in closeStreamAndSocket: " + e);
		}
	}
	
	/**
	 * @effects	Effettua la notifica di avvenuto cambiamento di stato agli amici.
	 * @param	username
	 * @throws	RemoteException
	 */
	private void doStatusChangeCallback(String username) throws RemoteException {
		UserEntry tmp = users.get(username);
		for(String myFriend : tmp.getAllReverseFriend()) {
			UserEntry myFriendEntry = users.get(myFriend);
			if (myFriendEntry.isOnline()) {
				myFriendEntry.getNEI().notifyEvent(username + " is online!");
			}
		}
	}
}