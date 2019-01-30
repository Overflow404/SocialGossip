import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends RemoteObject implements ServerInterface {
	/**
	 * @overview	Questa classe ha il compito di smistare le connessioni in arrivo
	 * 				dai client e lanciare due thread. Uno per ogni connessione effettuata(richieste normali)
	 * 				ed un thread che si occupa di creare thread per soddisfare le richieste chat.
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, UserEntry> users;
	private final short PORT;
	private RuntimeSupport rt;
	
	/**
	 * @constructor	Inizializza il runtime support, la porta, e le strutture dati
	 * 				relative agli utenti.
	 * @param		rt
	 */
	public Server(RuntimeSupport rt) {
		super();
		PORT = 9999;
		this.rt = rt;
		users = rt.getSD();
		startServer();
	}
	
	/**
	 * @effects	Start un thread che sta in accept ed aspetta connessioni sul socket
	 * 			per le richieste chat. Poi entra nel ciclo principale in cui aspetta
	 * 			connessioni sul main socket, per ogni nuova connessione lancia un worker
	 * 			che soddisfa le richieste.
	 */
	public void startServer() {
		new Thread(new Runnable() {
		    public void run() {
				ExecutorService executor = Executors.newCachedThreadPool();
				Socket connectionSocket;
				executor.execute(new ChatServer(rt));
				try (ServerSocket welcomeSocket = new ServerSocket(PORT)) {
					//Ciclo in cui accetto le connessioni e starto i thread che si occupano
					//delle varie richieste.
					while (true) {
						try {
							connectionSocket = welcomeSocket.accept();
						} catch (IOException e) {
							System.err.println("[SERVER]Error accepting new connection: " + e);
							continue;
						}
						System.out.println("[SERVER]New connection accepted: " + connectionSocket.toString());
						executor.execute(new Worker(connectionSocket, rt));
					}
				} catch (IOException e) {
					System.err.println("[SERVER]Error creating main socket: " + e);
					System.exit(1);
				}
		    }
		}).start();
	}

	@Override
	/**
	 * @effects Registrazione alla callback di amicizia.
	 */
	public void registerToFriendshipCallback(String username, NotifyEventInterface clientInterface) throws RemoteException {
		if (clientInterface == null || username == null) throw new NullPointerException();
		UserEntry tmp = users.get(username);
		if (tmp.getNEI() == null)
			tmp.setNEI(clientInterface);
	}

	@Override
	/**
	 * @effects Registrazione alla callback sulla chiusura dei gruppi.
	 */
	public void registerToChatroomCloseCallback(String username, String groupId, NotifyEventInterface clientInterface)
			throws RemoteException {
		if (clientInterface == null || groupId == null || username == null) throw new NullPointerException();

		users.get(username).setGroupNEI(clientInterface);
	}
	
	@Override
	/**
	 * @effects Deregistrazione alla callback sulla chiusura dei gruppi.
	 */
	public void unregisterToChatroomCloseCallback(String username, String groupId, NotifyEventInterface clientInterface) throws RemoteException {
		if (clientInterface == null || groupId == null) throw new NullPointerException();
		users.get(username).resetGroupNEI();
	}

	@Override
	/**
	 * @effects Deregistrazione alla callback di amicizia.
	 */
	public void unregisterToFriendshipCallback(String username, NotifyEventInterface clientInterface) throws RemoteException {
		if (clientInterface == null || username == null) throw new NullPointerException();
		UserEntry tmp = users.get(username);
		if (tmp.getNEI() != null) {
			tmp.deleteNEI();
		}
	}
}