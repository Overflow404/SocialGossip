import java.net.InetAddress;
import java.util.Vector;

public class ChatGroupEntry {

	/**
	 * @Overview:	Ogni gruppo ha un entry nella hashtable dei gruppi formata
	 * 				da un indirizzo ip associato al gruppo, nome dell'owner, lista di membri, e lista
	 * 				di stub a cui notificare i cambiamenti.
	 */
	private InetAddress ip;
	private String ownerName;
	private Vector<String> member;
	private Vector<NotifyEventInterface> groupCall;
	
	/**
	 * @Constructor:	Il costruttore inizializza l'ip del gruppo,
	 * 					il nome dell'owner e due vettori che conterranno
	 * 					rispettivamente la lista dei membri appartenenti al gruppo
	 * 					e la lista di stub per notificare ai partecipanti la 
	 * 					chiusura del gruppo attraverso RMI.
	 */
	public ChatGroupEntry(InetAddress ip, String ownerName) {
		this.ip = ip;
		this.ownerName = ownerName;
		member = new Vector<String>();
		groupCall = new Vector<NotifyEventInterface>();
	}

	/**
	 * @Return:	Il nome dell'owner del gruppo.
	 */
	public String getOwner() {
		return ownerName;
	}
	
	/**
	 * @Return:	L'ip associato al gruppo.
	 */
	public InetAddress getIp() {
		return ip;
	}
	
	/**
	 * @Return:	La lista dei partecipanti al gruppo.
	 */
	public Vector<String> getParts() {
		return member;
	}
	
	/**
	 * @Effects:	Aggiunge l'utente m al gruppo.
	 */
	public void addMember(String m) {
		member.add(m);
	}
	
	/**
	 * @Effects:	Inizializza a null la lista di stub del gruppo.
	 */
	public void resetGroupNEI() {
		groupCall = null;
	}
	
	/**
	 * @Return:	True se l'utente s e' membro del gruppo, false altrimenti.
	 */
	public boolean isSubscribed(String s) {
		return s.equals(ownerName) || member.contains(s);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupCall == null) ? 0 : groupCall.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((member == null) ? 0 : member.hashCode());
		result = prime * result + ((ownerName == null) ? 0 : ownerName.hashCode());
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
		ChatGroupEntry other = (ChatGroupEntry) obj;
		if (groupCall == null) {
			if (other.groupCall != null)
				return false;
		} else if (!groupCall.equals(other.groupCall))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (member == null) {
			if (other.member != null)
				return false;
		} else if (!member.equals(other.member))
			return false;
		if (ownerName == null) {
			if (other.ownerName != null)
				return false;
		} else if (!ownerName.equals(other.ownerName))
			return false;
		return true;
	}
}