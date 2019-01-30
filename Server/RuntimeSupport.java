import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class RuntimeSupport extends UtilsInterface {
	/**
	 * @overview:	Questa classe contiene le strutture dati che usa il server
	 * 				e i metodi per manipolarle.
	 */
	private HashMap<String, UserEntry> users;
	private HashMap<String, ChatGroupEntry> chatId;
	private final ReentrantLock[] entryLock;
	private final ReentrantLock[] chatIdLock;
	private final int group = 32;
	private final int initialCapacity = 2048;

	/**
	 * @constructor	Inizializza le hashtable che contengono gli utenti e i gruppi.
	 * 				Inizializza le lock sulle due hashtable(organizzate in gruppi da 32 entry).	
	 */
	public RuntimeSupport() {
		users = new HashMap<String, UserEntry>(initialCapacity);
		chatId = new HashMap<String, ChatGroupEntry>(initialCapacity);
		entryLock = new ReentrantLock[group];
		for (int i = 0; i < group; i++) {
			entryLock[i] = new ReentrantLock();
		}
		chatIdLock = new ReentrantLock[group];
		for (int i = 0; i < group; i++) {
			chatIdLock[i] = new ReentrantLock();
		}
	}

	/**
	 * @return La struttura dati per gli utenti
	 */
	public HashMap<String, UserEntry> getSD() {
		return users;
	}

	/**
	 * @return La struttura dati per i gruppi
	 */
	public HashMap<String, ChatGroupEntry> getChatSD() {
		return chatId;
	}

	/**
	 * @effects	Prende la lock sulla hashtable dei gruppi.
	 * @param	s
	 */
	public void getChatLock(String s) {
		int h = s.hashCode();
		if (h < 0) h*=-1;
		chatIdLock[h % group].lock();
	}

	/**
	 * @effects	Rilascia la lock sulla hashtable dei gruppi.
	 * @param	s
	 */
	public void freeChatLock(String s) {
		int h = s.hashCode();
		if (h < 0) h *= -1;
		chatIdLock[h % group].unlock();
	}

	/**
	 * @effects	Prende la lock sulla hashtable utenti.
	 * @param	s
	 */
	public void getLock(String s) {
		int h = s.hashCode();
		if (h < 0) h*=-1;
		entryLock[h % group].lock();
	}

	/**
	 * @effects	Rilascia la lock sulla hashtable utenti.
	 * @param	s
	 */
	public void freeLock(String s) {
		int h = s.hashCode();
		if (h < 0) h *= -1;
		entryLock[h % group].unlock();
	}

	/**
	 * @effects	Effettua la registrazione dell'utente username.
	 * @param	username
	 * @param	fullname
	 * @param	password
	 * @param	country
	 * @param	s
	 */
	public void atomicRegister(String username, String fullname, String password,
			String country, Socket s) {
		getLock(username);
		if (!users.containsKey(username)) {
			users.put(username, new UserEntry(fullname, password, country, false));
			freeLock(username);
			sendResponse(s, OK_OP);
		}
		else {
			freeLock(username);
			sendResponse(s, ALREADY_OP);

		}
	}
}