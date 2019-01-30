import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MulticastThread extends UtilsInterface implements Runnable{
	/**
	 * @overview	Questa classe viene instanziata una sola volta ed
	 * 				ha il compito di ascoltare le richieste(UDP) su una specifica
	 * 				porta e soddisfarle.
	 */
	private HashMap<String, ChatGroupEntry> chatId;
	private HashMap<String, UserEntry> users;
	private RuntimeSupport rt;

	/**
	 * @constructor	Inizializza il runtime support e le strutture dati per gli
	 * 				utenti e per i gruppi.
	 * @param		rt
	 */
	public MulticastThread(RuntimeSupport rt) {
		this.rt = rt;
		chatId = rt.getChatSD();
		users = rt.getSD();
	}

	/**
	 * @effects	Inizializza un nuovo datagram socket, ascolta le richiesta
	 * 			ed a seconda della richiesta si comporta di conseguenza.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try(DatagramSocket socket = new DatagramSocket(12000)) {
			while (true) {
				try {
					byte[] buf = new byte[1024];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					//Ricezione pacchetto udp
					System.out.println("Bloccato su receive");
					socket.receive(packet);
					System.out.println("SBloccato su receive");
					String byteToString = new String(packet.getData(),
							0, packet.getLength(), "US-ASCII");
					JSONParser parser = new JSONParser();

					//Parsing del pacchetto in un jsonobject
					JSONObject json = (JSONObject) parser.parse(byteToString);

					switch(json.get("op").toString()) {
					case MSG2GROUP_OP:
						String groupId = json.get("group_id").toString();
						String mitt = json.get("mitt").toString();

						//Recupero il socket del mittente.
						rt.getLock(mitt);
						Socket s = users.get(mitt).getSocket();
						rt.freeLock(mitt);

						JSONObject obj = new JSONObject();
						rt.getChatLock(groupId);
						if (!chatId.containsKey(groupId)) {
							//Caso gruppo non trovasto
							rt.freeChatLock(groupId);
							obj.put("op", GROUPNOTFOUND_OP);
							writeJSONObject(s, obj);
						}
						else if(!noOneOnline(groupId)) {
							//Caso nessun utente online nel gruppo
							rt.freeChatLock(groupId);
							obj.put("op", NOONEONLINEGROUP_OP);
							writeJSONObject(s, obj);
						}
						else {
							if (chatId.get(groupId).isSubscribed(mitt)) {
								//Caso ok, invio in multicast a tutti
								System.out.println("invio un opok");
								rt.freeChatLock(groupId);
								String body = json.get("body").toString();
								obj.put("op", OK_OP);
								obj.put("body", body);
								obj.put("mitt", json.get("mitt"));
								obj.put("group_id", groupId);
								System.out.println(chatId.get(groupId).getIp());
								//Preparazione del pacchetto udp
								DatagramPacket msgPacket2 = new DatagramPacket(obj.toString().getBytes(),
										obj.toString().getBytes().length, chatId.get(groupId).getIp(), 11000);
								socket.send(msgPacket2);
								System.out.println(msgPacket2);
							}
							else {
								//utente non membro del gruppo
								rt.freeChatLock(groupId);
								obj.put("op", NOTGROUPMEMBER_OP);
								writeJSONObject(s, obj);
							}
						}
						break;
					}

				} catch (IOException | ParseException e) {
					System.err.println("[MULTICASTTHREAD]Error receiving packet or casting json object: " + e);
					continue;
				}
			}
		} catch (SocketException e1) {
			System.err.println("[MULTICASTTHREAD]Error creating udp socket: " + e1);
			System.exit(1);
		}
	}

	/**
	 * @param 	groupId
	 * @return	True se qualcuno nel groupId e' online, false altrimenti.
	 */
	private boolean noOneOnline(String groupId) {
		rt.getChatLock(groupId);
		String owner = chatId.get(groupId).getOwner();
		rt.freeChatLock(groupId);

		Vector<String> tmp = chatId.get(groupId).getParts();
		for(String s : tmp) {
			rt.getLock(s);
			if (users.get(s).isOnline() && !owner.equals(s)) {
				rt.freeLock(s);
				return true;
			}
			rt.freeLock(s);
		}
		return false;
	}
}