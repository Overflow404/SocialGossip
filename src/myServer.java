import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class myServer {
	/**
	 * @overview	Classe principale che avvia il server ed il servizio RMI.
	 * @param 		args
	 */
	public static void main(String[] args) {
		System.out.println("Starting server...");
		System.setProperty("java.net.preferIPv4Stack" , "true");
		final String NAME = "Server";
		final int PORT = 9998;
		final int RPORT = 5000;

		try {
			ExecutorService executor = Executors.newCachedThreadPool();
			RuntimeSupport rt = new RuntimeSupport();
			//Operazioni per RMI
			ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(new Server(rt), PORT);
			LocateRegistry.createRegistry(RPORT);
			LocateRegistry.getRegistry(RPORT).bind(NAME, stub);
			//Start del thread che si occupa di ascoltare le richieste udp(per i gruppi)
			executor.execute(new MulticastThread(rt));
		} catch (RemoteException e) {
			System.err.println("[MYSERVER]Error with export object: " + e);
			return;
		} catch (AlreadyBoundException e) {
			System.err.println("[MYSERVER]Error creating registry: " + e);
			return;
		}
	}
}