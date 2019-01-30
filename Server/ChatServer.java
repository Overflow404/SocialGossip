import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.simple.JSONObject;

public class ChatServer extends UtilsInterface  implements Runnable{

	/**
	 * Overview:	Questo thread si occupa di accettare le connessioni tcp per le richieste
	 * 				di messaggi o richieste inerenti all'ambito chat. Dopo aver accettato
	 * 				una connessione passa il socket ad un thread che eseguira' le varie
	 * 				richieste.
	 */
	private RuntimeSupport rt;

	/**
	 * @Constructor:	Inizializza il runtime support.
	 */
	public ChatServer(RuntimeSupport rt) {
		this.rt = rt;
	}

	/**
	 * @Effects:	Porzione di codice eseguita dal thread.
	 * 				Il thread apre un socket sulla porta specificata ed
	 * 				accetta le varie connessioni provenienti dai client.
	 * 				Per ogni nuova connessione lancia un thread che si occupera'
	 * 				di gestire le richieste in ambito chat.
	 */
	public void run() {
		try (ServerSocket welcomeSocket = new ServerSocket(15000)) {
			ExecutorService executor = Executors.newCachedThreadPool();
			Socket connectionSocket;
			while (true) {
				try {
					connectionSocket = welcomeSocket.accept();
				} catch (IOException e) {
					System.err.println("[CHATSERVER]Error accepting new connection: " + e);
					continue;
				}
				JSONObject json = readJSONObjectNU(connectionSocket);
				if (json != null)
					executor.execute(new ChatWorker(connectionSocket, json.get("username").toString(), rt));
			}
		} catch (IOException e) {
			System.err.println("[CHATSERVER]Fatal error creating main socket: " + e);
			System.exit(1);
		}
	}
}