import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotifyEventInterface extends Remote {
	public void notifyEvent(String update) throws RemoteException;
}