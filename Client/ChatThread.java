import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JTextArea;
import org.json.simple.JSONObject;

public class ChatThread extends UtilsInterface implements Runnable{

	/**
	 * @overview	Questo thread riceve le richieste inerenti ai messaggi inviati
	 * 				ai singoli utenti ed ai file.
	 */
	private Socket chatSocket;
	private String username;
	private JTextArea messageArea, notificationsArea;
	private SocialGui sg;
	
	/**
	 * @constructor	Inizializza il socket sul quale ricevere le richieste,
	 * 				l'username dell'utente loggato, e due riferimenti alle
	 * 				textarea della gui per poter stampare i messaggi/notifiche.
	 * @param		chatSocket
	 * @param		username
	 * @param		sg
	 */
	public ChatThread(Socket chatSocket, String username, SocialGui sg) {
		this.chatSocket = chatSocket;
		this.username = username;
		this.sg = sg;
		messageArea = sg.getMessageArea();
		notificationsArea = sg.getTextArea();

	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * @effects	In attesa fin quando non riceve una richiesta, una volta
	 * 			ricevuta, a seconda dell'operazione si mostra il risultato
	 * 			all'utente. Se viene ricevuta una peerfilerequest si attiva
	 * 			un thread che fa da server per ricevere il file. Se viene
	 * 			ricevuta una okfilefromserver si attiva un thread che fa da client
	 * 			per inviare il file.
	 */
	public void run() {
		ExecutorService executor = Executors.newCachedThreadPool();
		String op;
		JSONObject bootstrapObj = new JSONObject();
		bootstrapObj.put("op", BOOTSTRAP_OP);
		bootstrapObj.put("username", username);
		writeJSONObject(chatSocket, bootstrapObj);

		while (true) {
			JSONObject objFromServer = readJSONObject(chatSocket);
			if (objFromServer == null) {
				infoMessage("Server is off, disconnected");
				System.exit(0);
			}
			op = objFromServer.get("op").toString();

			switch (op) {
			case OK_OP:
				System.out.println("rt qua");
				break;
			case OKCHAT_OP:
				String toAppend = (String) objFromServer.get("resp");
				messageArea.append(toAppend);
				break;
			case OFFLINEUSER_OP:
				infoMessage("This user is offline");
				break;
			case NOTYOURFRIEND_OP:
				errorMessage("You aren't friend with this user");
				break;
			case NOTREG_OP:
				errorMessage("This user doesn't exist");
				break;
			case PEERFILEREQUEST_OP:
				System.out.println("sono qui");
				String mitt = (String) objFromServer.get("mitt");
				SocialGui.textArea.append(mitt + " want to send me " + objFromServer.get("file") + "\n");
				JSONObject toServer = new JSONObject();
				try {
					ServerSocket ss = new ServerSocket(0);
					int port = ss.getLocalPort();
					toServer.put("op", "DETAILFILE_OP");
					toServer.put("port", port + "");
					toServer.put("ip", InetAddress.getLocalHost().toString());
					toServer.put("to", mitt);
					toServer.put("from", username);
					toServer.put("file", objFromServer.get("file"));
					ss.close();
					notificationsArea.append("Senind ip: " + InetAddress.getLocalHost().toString() + " and port: " + port + "\n");
					writeJSONObject(chatSocket, toServer);
					executor.execute(new FileTransferServer(port, notificationsArea));
				} catch (IOException e) {
					errorMessage("Error receiving file!");
				}
				break;
			case OKFILEFROMSERVER_OP:
				//CLIENT
				String peerIp = (String) objFromServer.get("ip");
				Integer peerPort = Integer.parseInt(objFromServer.get("port").toString());
				SocialGui.textArea.append("Received details from server ");
				SocialGui.textArea.append("ip: " + peerIp + " and port: " + peerPort  + "\n");
				File toSend = sg.getFileListener().getFile();
				String file = objFromServer.get("file").toString();
				if (toSend.getAbsolutePath().equals(file))
					executor.execute(new FileTransferClient(peerIp, peerPort, toSend, notificationsArea));
				else {
					executor.execute(new FileTransferClient(peerIp, peerPort, new File(file), notificationsArea));
				}
				break;
			case GROUPNOTFOUND_OP:
				errorMessage("This group doesn't exist");
				break;
			case NOONEONLINEGROUP_OP:
				infoMessage("Nobody is online in this group");
				break;

			}
		}
	}
}