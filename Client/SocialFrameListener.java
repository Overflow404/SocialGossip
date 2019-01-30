import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.Socket;
import java.rmi.RemoteException;

import org.json.simple.JSONObject;

public class SocialFrameListener extends UtilsInterface implements WindowListener{
	/**
	 * @overview	Questa classe effettua le operazioni per una chiusura
	 * 				consistente.
	 */
	private Socket s;
	private ServerInterface server;
	private NotifyEventInterface stub;
	private String username;
	
	/**
	 * @constructor	Inizializza il socket del server, l'username dell'utente loggato,
	 * 				lo stub del server per effettuare la deregistrazione della callback.
	 * @param		s
	 * @param		username
	 * @param		server
	 * @param		stub
	 */
	public SocialFrameListener(Socket s, String username, ServerInterface server, NotifyEventInterface stub) {
		this.s = s;
		this.username = username;
		this.server = server;
		this.stub = stub;
	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * @effects	Si effettua la deregistrazione alla callback di amicizia e si
	 * 			invia al server un jsonobject contenente come operazione closingop
	 * 			in modo che il server setti ad offline l'utente corrente ed altro.
	 */
	public void windowClosing(WindowEvent e) {
		JSONObject json = new JSONObject();
		try {
			server.unregisterToFriendshipCallback(username, stub);
			//Si dovrebbe fare la unregister per ogni gruppo a cui e' registrato l'utente

		} catch (RemoteException e1) {
			System.err.println("[SOCIALFRAMELISTENER]Error unregistering callback: " + e);
		}
		json.put("op", CLOSING_OP);
		System.out.println("[SOCIALFRAMELISTENER]Sending: " + json.toJSONString());
		writeJSONObject(s, json);
		e.getWindow().dispose();
		System.exit(0);
	}

	
	@Override
	public void windowOpened(WindowEvent e) {
	}
	
	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}