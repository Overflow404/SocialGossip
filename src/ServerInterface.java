import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	public void registerToFriendshipCallback (String username, NotifyEventInterface clientInterface) throws RemoteException;
	public void unregisterToFriendshipCallback (String username, NotifyEventInterface clientInterface) throws RemoteException;
	public void registerToChatroomCloseCallback (String username, String groupId, NotifyEventInterface clientInterface) throws RemoteException;
	public void unregisterToChatroomCloseCallback (String username,String groupId, NotifyEventInterface clientInterface) throws RemoteException;
}