import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotifyEventInterface extends Remote {
	/**
	 * @effects	Notifica l'evento di body update al client.
	 * @param	update
	 * @throws	RemoteException
	 */
	public void notifyEvent(String update) throws RemoteException;
}