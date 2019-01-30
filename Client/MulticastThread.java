import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import javax.swing.JTextArea;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MulticastThread implements Runnable{
	/**
	 * @overview	Questo thread riceve i messaggi di gruppo e li 
	 * 				stampa sulla message area.
	 */
	private MulticastSocket clientSocket;
	private JTextArea messageArea;
	
	/**
	 * @constructor	Inizializza il multicastsocket e la messagearea.
	 * @param		mcs
	 * @param		sg
	 */
	public MulticastThread(MulticastSocket mcs, SocialGui sg) {
		clientSocket = mcs;
		messageArea = sg.getMessageArea();
	}

	/**
	 * @effects	Si entra in un ciclo infinito in cui si attende la ricezione
	 * 			di un messaggio multicast codificato in json, si estrae il body
	 * 			e lo si mostra sulla message area.
	 */
	public void run() {
		try {
			while (true) {
				byte[] buf = new byte[1024];
				DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
				System.out.println("rcvbl");
				clientSocket.receive(msgPacket);
				System.out.println("rcvsbl");
				System.out.println(msgPacket.getAddress());
				String byteToString = new String(msgPacket.getData(),
						0, msgPacket.getLength(), "US-ASCII");
				System.out.println(byteToString);
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(byteToString);
				String mitt = json.get("mitt").toString();
				//Append on textarea
				messageArea.append("[" + json.get("group_id") + "]" + " from " + mitt + ": "+ json.get("body") +"\n");
			}
		} catch (IOException | ParseException ex) {
			ex.printStackTrace();
		}
	}
}