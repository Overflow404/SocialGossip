import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MulticastSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SignListener extends UtilsInterface implements ActionListener {
	/**
	 * @overview	Questo classe ha il compito di inviare le richieste per
	 * 				il login o la registrazione al server.
	 */
	private ServerInterface server;
	private String loggedUsername;
	private NotifyEventInterface stub;
	private String IP = "localhost";
	private SignGui signGui;

	/**
	 * @constructor	Inizializza il riferimento alla ui.
	 * @param		signGui
	 */
	public SignListener(SignGui signGui) {
		this.signGui = signGui;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		JCheckBox c1 = signGui.getTermsCheckbox();
		if (!c1.isSelected()) {
			errorMessage("You have to agree terms!");
			return;
		}
		
		JButton b1 = signGui.getButton();

		JTextField nameField = signGui.getFullnameTextField();
		String nameFieldText = nameField.getText();

		JTextField usernameField = signGui.getUsernameTextField();
		loggedUsername = usernameField.getText();
		
		JTextField countryField = signGui.getCountryTextField();
		String countryFieldText = countryField.getText().toUpperCase();

		JPasswordField passwordField = signGui.getPasswordTextField();
		char[] tmp = passwordField.getPassword();
		String passwordFieldText = new String(tmp);

		if (b1.getText().equals("Sign up")) {
			//Caso in cui si registra l'utente.
			if (nameFieldText == null || loggedUsername == null || passwordFieldText == null || countryFieldText == null) {
				errorMessage("Null fields!");
			}
			else if (checkTV(nameFieldText) || checkTV(loggedUsername) || checkTV(passwordFieldText) || checkTV(countryFieldText)) {
				errorMessage("Void fields!");
			}
			else {
				if (!IsoUtil.isValidISOCountry(countryFieldText.toLowerCase())) {
					errorMessage("Not a valid country according to ISO 639!");
					return;
				}

				JSONObject json = new JSONObject();

				try(Socket clientSocket = new Socket(IP, 9999)) {
					BufferedReader in;
					try {
						in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					} catch (IOException e1) {
						System.err.println("[SIGNLISTENER]Opening input stream: " + e1);
						return;
					}

					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

					json.put("op", REGISTER_OP);
					json.put("fullname", nameFieldText);
					json.put("username", loggedUsername);
					json.put("password", passwordFieldText);
					json.put("language", countryFieldText);

					System.out.println("[SIGNLISTENER]Sending: " + json.toJSONString());
					out.println(json.toJSONString());
					out.flush();

					String textFromServer = in.readLine();
					System.out.println("[SIGNLISTENER]Received: " + textFromServer);

					JSONParser parser = new JSONParser();
					out.close();

					try {
						in.close();
					} catch (IOException e1) {
						System.err.println("[SIGNLISTENER]Error closing the input stream " + e1);
						return;
					}

					JSONObject jsonObject;
					try {
						jsonObject = (JSONObject) parser.parse(textFromServer);
					} catch (ParseException e1) {
						System.err.println("[SIGNLISTENER]Error in parsing request: " + e1);
						return;
					}

					String op = jsonObject.get("op").toString();
					switch (op) {
					case OK_OP:
						infoMessage("Registration successfull!");
						break;
					case ALREADY_OP:
						errorMessage("You are already registered!");
						break;
					default:
						break;
					}
				} catch (IOException e1) {
					errorMessage("Server offline or read error!");
					return;
				}
			}
		}
		else {
			//Caso in cui si logga l'utente
			if (loggedUsername == null || passwordFieldText == null) {
				errorMessage("Null fields!");
			}
			else if (checkTV(loggedUsername) || checkTV(passwordFieldText)) {
				errorMessage("Void fields!");
			}
			else {
				JSONObject json = new JSONObject();
				try {
					Socket clientSocket = new Socket(IP, 9999);
					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

					BufferedReader in;
					try {
						in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					} catch (IOException e1) {
						System.err.println("[SIGNLISTENER]Opening input stream: " + e1);
						closeStreamsAndSocket(clientSocket);
						return;
					}

					json.put("op", LOGIN_OP);
					json.put("username", loggedUsername);
					json.put("password", passwordFieldText);

					System.out.println("[SIGNLISTENER]Sending: " + json.toJSONString());
					out.println(json.toJSONString());
					out.flush();

					String textFromServer = in.readLine();
					System.out.println("[SIGNLISTENER]Received: " + textFromServer);
					
					JSONParser parser = new JSONParser();
					JSONObject jsonObject;
					
					try {
						jsonObject = (JSONObject) parser.parse(textFromServer);
					} catch (ParseException e1) {
						System.err.println("[SIGNLISTENER]Error in parsing request: " + e1);
						closeStreamsAndSocket(clientSocket);
						return;
					}
					
					String op = jsonObject.get("op").toString();
					switch (op) {
					case OK_OP:
						ExecutorService executor = Executors.newCachedThreadPool();
						Socket chatSocket = new Socket(IP, 15000);
						PrintWriter pw = new PrintWriter(chatSocket.getOutputStream(), true);
						MulticastSocket mcs = new MulticastSocket(11000);
						mcs.setReuseAddress(true);
						signGui.getSignFrame().dispose();
						SocialGui sg = new SocialGui(clientSocket, pw, loggedUsername);	
						final String NAME = "Server";
						final int RPORT = 5000;
						Registry registry = LocateRegistry.getRegistry(IP, RPORT);
						NotifyEventInterface callbackObj;
						try {
							server = (ServerInterface) registry.lookup(NAME);
							callbackObj = new NotifyEventImp(sg);
							stub = (NotifyEventInterface) UnicastRemoteObject.exportObject(callbackObj, 0);
							
						} catch (NotBoundException e1) {
							System.err.println("[SIGNLISTENER]Error setting up registry: " + e1);
							break;
						}
						executor.execute(new MulticastThread(mcs, sg));
						executor.execute(new ChatThread(chatSocket, loggedUsername, sg));
						executor.execute(new RequestThread(clientSocket, loggedUsername, server, stub, mcs, sg));
						server.registerToFriendshipCallback(loggedUsername, stub);
						SocialFrameListener sfl = new SocialFrameListener(clientSocket,loggedUsername, server, stub);
						sg.setWindowListener(sfl);
						break;
					case WRONGPWD_OP:
						errorMessage("Wrong password!");
						closeStreamsAndSocket(clientSocket);
						break;
					case ALREADY_OP:
						errorMessage("Already logged in!");
						closeStreamsAndSocket(clientSocket);
						break;	
					case NOTREG_OP:
						errorMessage("You aren't registered!");
						closeStreamsAndSocket(clientSocket);
						break;
					default:
						closeStreamsAndSocket(clientSocket);
						break;
					}
				} catch (IOException e1) {
					errorMessage("Server offline or read error");
					return;
				}
			}
		}
	}

	private void closeStreamsAndSocket(Socket s) {
		try {
			s.shutdownInput();
			s.shutdownOutput();
			s.close();
		} catch (IOException e) {
			System.err.println("[SIGNLISTENER]Error in closeStreamAndSocket: " + e);
		}
	}
}