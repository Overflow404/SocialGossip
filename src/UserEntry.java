import java.net.Socket;
import java.util.Vector;

public class UserEntry {
	/**
	 * @overview	Ad ogni utente corrisponde una userentry con varie informazioni.
	 */
	private String fullname;
	private String password;
	private String country;
	private Vector<String> friendList;
	private NotifyEventInterface clientCall;
	private NotifyEventInterface groupCall;
	private Vector<String> reverseFriendList;
	private Vector<String> groupList;
	private boolean online;
	private Socket s;
	
	/**
	 * @constructor	Inizializza il fullname, password, country, status dell'utente ed inoltre
	 * 				crea tre vettori, uno per la lista di amici, uno per la lista di utenti che ha aggiunto
	 * 				agli amici l'utente in questione ed uno pe rla lista di gruppi a cui e' iscritto.
	 * 				Inizializza inoltre a null gli stub per le notifiche ed il socket.
	 * @param		fullname
	 * @param		password
	 * @param		country
	 * @param		online
	 */
	public UserEntry(String fullname, String password, String country, boolean online) {
		this.fullname = fullname;
		this.password = password;
		this.online = online;
		this.country = country;
		friendList = new Vector<String>();
		reverseFriendList = new Vector<String>();
		groupList = new Vector<String>();
		clientCall = null;
		groupCall = null;
		s = null;
	}
	
	/**
	 * @return Il paese dell'utente.
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * @effects	Aggiunge l'utente al gruppo s
	 * @param	s
	 */
	public void addGroup(String s) {
		groupList.add(s);
	}
	
	/**
	 * @effects	rimuove l'utente dal gruppo s
	 * @param	s
	 */
	public void removeGroup(String s) {
		groupList.remove(s);
	}
	
	/**
	 * @return La lista dei gruppi al quale l'utente e' iscritto.
	 */
	public Vector<String> getGroupList() {
		return groupList;
	}
	
	/**
	 * @return il nome completo dell'utente.
	 */
	public String getFullname() {
		return fullname;
	}
	
	/**
	 * @effects	Setta lo stub utente per i gruppi
	 * @param	e
	 */
	public void setGroupNEI(NotifyEventInterface e) {
		groupCall = e;
	}
	
	/**
	 * @return	Stub utente associato ai gruppi
	 */
	public NotifyEventInterface getGroupNEI() {
		return groupCall;
	}
	
	/**
	 * @effects	Effettua il reset dello stub utente per i gruppi
	 */
	public void resetGroupNEI() {
		groupCall = null;
	}
	
	/**
	 * @effects	Effettua il reset dello stub utente per le notifiche amicizia/status
	 */
	public void deleteNEI() {
		clientCall = null;
	}
	
	/**
	 * @effects	Associa il socket s all'utente corrente
	 * @param	s
	 */
	public void setSocket(Socket s) {
		this. s = s;
	}
	
	/**
	 * @return	Il socket associato all'utente corrente
	 */
	public Socket getSocket() {
		return s;
	}
	
	/**
	 * @return	Stub per le notifiche amicizia/status
	 */
	public NotifyEventInterface getNEI() {
		return clientCall;
	}
	
	/**
	 * @effects	Inizializza lo stub per le notifiche amicizia/status
	 * @param	nei
	 */
	public void setNEI(NotifyEventInterface nei) {
		clientCall = nei;
	}
	
	/**
	 * @return	La password dell'utente
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * @return	True se l'utente corrente e' online, false altrimenti.
	 */
	public boolean isOnline() {
		return online;
	}
	
	/**
	 * @effects	Aggiunge f alla lista degli amici dell'utente corrente.
	 * @param	f
	 */
	public void addFriend(String f) {
		friendList.add(f);
	}
	
	/**
	 * @effects	Aggiunge f alla lista delle amicizie inverse dell'utente corrente.
	 * @param	f
	 */
	public void addReverseFriend(String f) {
		reverseFriendList.add(f);
	}
	
	/**
	 * @param	f
	 * @return	True se f e' amico dell'utente corrente, false altrimenti.
	 */
	public boolean checkFriend(String f) {
		return friendList.contains(f);
	}
	
	/**
	 * @return	Lista degli amici.
	 */
	public Vector<String> getAllFriend() {
		return friendList;
	}
	
	/**
	 * @return	Lista inversa degli amici.
	 */
	public Vector<String> getAllReverseFriend() {
		return reverseFriendList;
	}
	
	/**
	 * @effects	Setta lo status dell'utente corrente ad arg
	 * @param	arg
	 */
	public void setStatus(boolean arg) {
		online = arg;
	}

	@Override
	public String toString() { 
	    return "Fullname: '" + fullname + "', Password: '" + password + "', Country: '" + country +"', Status: '" + online + "'";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((fullname == null) ? 0 : fullname.hashCode());
		result = prime * result + (online ? 1231 : 1237);
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserEntry other = (UserEntry) obj;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (fullname == null) {
			if (other.fullname != null)
				return false;
		} else if (!fullname.equals(other.fullname))
			return false;
		if (online != other.online)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	} 
}