import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;

import javax.swing.JTextArea;

public class NotifyEventImp extends RemoteObject implements NotifyEventInterface {
	/**
	 * @overview	Classe usata per effettuare le notifiche rmi.
	 */
	private static final long serialVersionUID = 1L;

	private JTextArea ta;
	
	/**
	 * @constructor	Inizializza la textarea per mostrare le notifiche.
	 * @param 		sg
	 * @throws 		RemoteException
	 */
	public NotifyEventImp(SocialGui sg) throws RemoteException {
		super(); 
		ta = sg.getTextArea();
	}

	/**
	 * @effects	Esegue l'append sulla textarea del parametro update.
	 */
	public void notifyEvent(String update) throws RemoteException {
		ta.append(update + "\n");
	}
}