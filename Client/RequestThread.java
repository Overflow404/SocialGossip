import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import javax.swing.JTextField;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class RequestThread extends UtilsInterface implements Runnable{
	/**
	 * @overview	Questo thread si occupa della ricezione di richieste
	 * 				non messaggi e non file da parte del server.
	 */
	private Socket chatSocket;
	private String username;
	private ServerInterface server;
	private NotifyEventInterface stub;
	private MulticastSocket mcs;
	private JTextField groupTextField;
	
	/**
	 * @constructor	Inizializza il socket su cui ricevere le richieste,
	 * 				il nome dell'utente loggato, lo stub sul quale ricevere le
	 * 				notifiche rmi, il multicast socket per effettuare le join
	 * 				sui grupppi e la textfield contenente il nome del gruppo.
	 * @param		chatSocket
	 * @param		username
	 * @param		server
	 * @param		stub
	 * @param		mcs
	 * @param		sg
	 */
	public RequestThread(Socket chatSocket, String username, ServerInterface server, 
			NotifyEventInterface stub, MulticastSocket mcs, SocialGui sg) {
		groupTextField = sg.getGroupIdTextfield();
		this.chatSocket = chatSocket;
		this.username = username;
		this.server = server;
		this.stub = stub;
		this.mcs = mcs;
	}
	
	@Override
	/**
	 * @effects	Entra in un ciclo infinito nel quale aspetta l'arrivo di una
	 * 			risposta da parte del server e a seconda dell'operazione
	 * 			si mostra il risultato all'utente.
	 */
	public void run() {
		while (true) {
			JSONObject objFromServer = readJSONObject(chatSocket);
			if (objFromServer == null) {
				infoMessage("Server is off, disconnected");
				System.exit(0);
			}
			String op = objFromServer.get("op").toString();
			
			switch (op) {
			case OK_OP:
				System.out.println("rt qua");
				break;
			case OK_OP_STATUS:
				String status = objFromServer.get("status").toString();
				infoMessage("This user exist and his status is: " + (status.equals("true") ? "online" : "offline"));
				break;
			case NOTREG_OP:
				infoMessage("This user doesn't exist!");
				break;
			case OK_OP_REG:
				infoMessage("Added to friends!");
				break;
			case ALREADY_OP:
				infoMessage("You're already friend with this user");
				break;
			case FRIENDTOYOURSELF_OP:
				infoMessage("No friend request to yourself");
				break;
			case OK_OP_FRLIST:
				JSONArray tmp = (JSONArray) objFromServer.get("friend_list");
				new FriendListGui(tmp);
				break;
			case NOFRIEND_OP:
				infoMessage("You don't have friends");
				break;
			case ALREADYGROUP_OP:
				errorMessage("This group is already registered");
				break;
			case ALREADYINGROUP_OP:
				errorMessage("You are already member of this group");
				break;
			case OKGROUP_OP:
				infoMessage("Group registered successfully");
				String ip = (String) objFromServer.get("mip");
				try {
					InetAddress address = InetAddress.getByName(ip);
					mcs.joinGroup(address);
					server.registerToChatroomCloseCallback(
							username, groupTextField.getText(), stub);
				} catch (IOException e) {
				}
				break;
			case OKENTERGROUP_OP:
				infoMessage("Group entered successfully");
				ip = (String) objFromServer.get("mip");
				try {
					InetAddress address = InetAddress.getByName(ip);
					mcs.joinGroup(address);
					server.registerToChatroomCloseCallback(
							username, groupTextField.getText(), stub);
				} catch (IOException e) {
				}
				break;
			case GROUPNOTFOUND_OP:
				errorMessage("This group doesn't exist");
				break;
			case NOTGROUPOWNER_OP:
				errorMessage("You aren't group owner");
				break;
			case DELETEGROUPOK_OP:
				infoMessage("Group deleted");
				break;
			case NOTGROUPMEMBER_OP:
				errorMessage("You aren't member of this group");
				break;
			case ERRCODE_OP:
				errorMessage("Operation failed");
				break;
			case CHATLIST_OP:
				JSONArray tmp1 = (JSONArray) objFromServer.get("group_list");
				JSONArray tmp2 = (JSONArray) objFromServer.get("ip_list");
				for (Object o: tmp1) {
					try {
						server.registerToChatroomCloseCallback(
								username,o.toString(), stub);
						System.out.println(o.toString());
					} catch (RemoteException e) {
						errorMessage("Error with chat room callback register");
						break;
					}
				}
				for (Object o : tmp2) {
					try {
						InetAddress address = InetAddress.getByName(o.toString());
						System.out.println("join su ip " + address);
						mcs.joinGroup(address);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
			case CHATLISTREQUESTOK_OP:
				JSONArray tmpArray =(JSONArray) objFromServer.get("list");
				if (tmpArray != null)
					new GroupListGui(tmpArray);
				else 
					infoMessage("You aren't subcribed to any group");
				break;
			}
		}
	}
}