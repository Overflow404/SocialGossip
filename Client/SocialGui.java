import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JComboBox;

public class SocialGui {
	/**
	 * @overview	Creazione della ui after login.
	 */
	private Socket s;
	private JTextField txtNameToSearch;
	private JTextField txtNameToSend;
	private JTextField txtUsernameHere;
	private JTextField txtMessageHere;
	private JFrame frame;
	private PrintWriter cpw, scpw;
	private String loggedUser;
	public static JTextArea textArea;
	private JTextArea textArea_1;
	private JTextField txtChatId;
	private FileActionListener fal;
	private JButton btnSend_1;
	
	public SocialGui(Socket s, PrintWriter cpw, String loggedUser) throws IOException {
		this.s = s;
		this.cpw = cpw;
		scpw = new PrintWriter(s.getOutputStream(), true);
		this.loggedUser = loggedUser;
		runSocialGui();
	}

	public void runSocialGui() {
		//Constants
		final short FONT_SIZE = 16;
		final Color BACK_COLOR = new Color(36, 47, 65);
		final Color FORE_COLOR = new Color(204, 204, 204);
		final Color BUTTON_COLOR = new Color(255,127,80);
		final Color WHITE = Color.WHITE;
		final Font FONT = new Font("Century Gothic", Font.PLAIN, FONT_SIZE);
		final Font BOLD_FONT = new Font("Century Gothic", Font.BOLD, FONT_SIZE);

		frame = new JFrame("Social");
		frame.setSize(810, 628);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBackground(BACK_COLOR);
		frame.getContentPane().add(panel);
		
		txtNameToSearch = new JTextField();
		txtNameToSearch.setText("Name to search here");
		txtNameToSearch.setBounds(25, 20, 174, 35);
		txtNameToSearch.setFont(FONT);
		txtNameToSearch.setForeground(WHITE);
		txtNameToSearch.setBorder(null);
		txtNameToSearch.setOpaque(false);
		txtNameToSearch.addMouseListener(new TextfieldMouseListener(txtNameToSearch));
		panel.add(txtNameToSearch);
		
		JButton btnNewButton = new JButton("Search");
		btnNewButton.setBounds(209, 22, 94, 35);
		btnNewButton.setFont(FONT);
		btnNewButton.setForeground(WHITE);
		btnNewButton.setBackground(BUTTON_COLOR);
		btnNewButton.setFocusPainted(false);
		btnNewButton.addActionListener(new SearchUserButtonListener(s, txtNameToSearch));
		panel.add(btnNewButton);
		
		JLabel label = new JLabel("___________________");
		label.setBounds(25, 35, 183, 20);
		label.setFont(BOLD_FONT);
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setForeground(WHITE);
		panel.add(label);
		
		txtNameToSend = new JTextField();
		txtNameToSend.setText("Name to send request here");
		txtNameToSend.setOpaque(false);
		txtNameToSend.setForeground(WHITE);
		txtNameToSend.setFont(FONT);
		txtNameToSend.setBorder(null);
		txtNameToSend.setBounds(339, 18, 193, 35);
		txtNameToSend.addMouseListener(new TextfieldMouseListener(txtNameToSend));
		panel.add(txtNameToSend);
		
		JButton btnSend = new JButton("Send");
		btnSend.setForeground(WHITE);
		btnSend.setFont(FONT);
		btnSend.setFocusPainted(false);
		btnSend.setBackground(BUTTON_COLOR);
		btnSend.setBounds(542, 20, 94, 35);
		btnSend.addActionListener(new FriendRequestButtonListener(s, txtNameToSend));
		panel.add(btnSend);
		
		JLabel label_1 = new JLabel("_____________________");
		label_1.setHorizontalAlignment(SwingConstants.LEFT);
		label_1.setForeground(WHITE);
		label_1.setFont(BOLD_FONT);
		label_1.setBounds(339, 33, 193, 20);
		panel.add(label_1);
		
		JButton btnOnlineUsers = new JButton("Friendlist");
		btnOnlineUsers.setForeground(WHITE);
		btnOnlineUsers.setFont(FONT);
		btnOnlineUsers.setFocusPainted(false);
		btnOnlineUsers.setBackground(BUTTON_COLOR);
		btnOnlineUsers.setBounds(659, 20, 125, 35);
		btnOnlineUsers.addActionListener(new FriendListLButtonistener(s));
		panel.add(btnOnlineUsers);
		

		JLabel label_2 = new JLabel("____________________________________________________________________________________");
		label_2.setHorizontalAlignment(SwingConstants.LEFT);
		label_2.setForeground(WHITE);
		label_2.setFont(BOLD_FONT);
		label_2.setBounds(25, 234, 759, 20);
		panel.add(label_2);
		
		JLabel notifications = new JLabel("NOTIFICATIONS", SwingConstants.CENTER);
		notifications.setFont(FONT);
		notifications.setForeground(FORE_COLOR);
		notifications.setLocation(25, 78);
		notifications.setSize(130,20);
		notifications.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(notifications);
		
		JLabel label_3 = new JLabel("____________________________________________________________________________________");
		label_3.setHorizontalAlignment(SwingConstants.LEFT);
		label_3.setForeground(WHITE);
		label_3.setFont(BOLD_FONT);
		label_3.setBounds(25, 508, 759, 20);
		panel.add(label_3);
		
		JLabel label_4 = new JLabel("____________________________________________________________________________________");
		label_4.setHorizontalAlignment(SwingConstants.LEFT);
		label_4.setForeground(WHITE);
		label_4.setFont(BOLD_FONT);
		label_4.setBounds(25, 445, 759, 20);
		panel.add(label_4);
		
		txtUsernameHere = new JTextField();
		txtUsernameHere.setText("Username here");
		txtUsernameHere.setOpaque(false);
		txtUsernameHere.setForeground(WHITE);
		txtUsernameHere.setFont(FONT);
		txtUsernameHere.setBorder(null);
		txtUsernameHere.setBounds(180, 539, 145, 35);
		txtUsernameHere.addMouseListener(new TextfieldMouseListener(txtUsernameHere));
		panel.add(txtUsernameHere);
		
		txtMessageHere = new JTextField();
		txtMessageHere.setText("Message here");
		txtMessageHere.setOpaque(false);
		txtMessageHere.setForeground(WHITE);
		txtMessageHere.setFont(FONT);
		txtMessageHere.setBorder(null);
		txtMessageHere.setBounds(339, 539, 326, 35);
		txtMessageHere.addMouseListener(new TextfieldMouseListener(txtMessageHere));
		panel.add(txtMessageHere);
		
		JLabel label_6 = new JLabel("____________________________________");
		label_6.setHorizontalAlignment(SwingConstants.LEFT);
		label_6.setForeground(WHITE);
		label_6.setFont(BOLD_FONT);
		label_6.setBounds(339, 554, 326, 20);
		panel.add(label_6);
		
		JLabel label_5 = new JLabel("________________");
		label_5.setHorizontalAlignment(SwingConstants.LEFT);
		label_5.setForeground(WHITE);
		label_5.setFont(BOLD_FONT);
		label_5.setBounds(180, 554, 145, 20);
		panel.add(label_5);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(25, 109, 748, 133);
		scrollPane_1.setOpaque(false);
		scrollPane_1.setBorder(null);
		scrollPane_1.getViewport().setBackground(BACK_COLOR);
		panel.add(scrollPane_1);
		
		textArea = new JTextArea();
		textArea.setOpaque(false);
		textArea.setForeground(WHITE);
		textArea.setFont(FONT);
		textArea.setBorder(null);
		textArea.setEditable(false);
		scrollPane_1.setViewportView(textArea);
		
		JButton btnSelectFile = new JButton("Select file");
		btnSelectFile.setForeground(WHITE);
		btnSelectFile.setFont(FONT);
		btnSelectFile.setFocusPainted(false);
		btnSelectFile.setBackground(BUTTON_COLOR);
		btnSelectFile.setBounds(180, 539, 145, 35);
		btnSelectFile.setVisible(false);
		fal = new FileActionListener(panel, loggedUser, txtMessageHere, cpw);
		btnSelectFile.addActionListener(fal);
		panel.add(btnSelectFile);
		
		txtChatId = new JTextField();
		txtChatId.setText("Chat id here");
		txtChatId.setOpaque(false);
		txtChatId.setForeground(WHITE);
		txtChatId.setFont(FONT);
		txtChatId.setBorder(null);
		txtChatId.setBounds(490, 475, 137, 35);
		txtChatId.addMouseListener(new TextfieldMouseListener(txtChatId));
		panel.add(txtChatId);
		
		//TODO
		JButton btnCreateChat = new JButton("Create chat");
		btnCreateChat.setForeground(WHITE);
		btnCreateChat.setFont(FONT);
		btnCreateChat.setFocusPainted(false);
		btnCreateChat.setBackground(BUTTON_COLOR);
		btnCreateChat.setBounds(25, 476, 145, 35);
		btnCreateChat.addActionListener(new CreateChatListener(scpw,txtChatId));
		panel.add(btnCreateChat);
		
		//TODO
		JButton btnCloseChat = new JButton("Close chat");
		btnCloseChat.setForeground(WHITE);
		btnCloseChat.setFont(FONT);
		btnCloseChat.setFocusPainted(false);
		btnCloseChat.setBackground(BUTTON_COLOR);
		btnCloseChat.setBounds(180, 476, 145, 35);
		btnCloseChat.addActionListener(new CloseChatListener(scpw, txtChatId, loggedUser));
		panel.add(btnCloseChat);
		
		//TODO
		JButton btnEnterChat = new JButton("Enter chat");
		btnEnterChat.setForeground(WHITE);
		btnEnterChat.setFont(FONT);
		btnEnterChat.setFocusPainted(false);
		btnEnterChat.setBackground(BUTTON_COLOR);
		btnEnterChat.setBounds(335, 476, 145, 35);
		btnEnterChat.addActionListener(new EnterChatListener(scpw, txtChatId, loggedUser));
		panel.add(btnEnterChat);
		
		//TODO
		JButton btnChatList = new JButton("Chat list");
		btnChatList.setForeground(WHITE);
		btnChatList.setFont(FONT);
		btnChatList.setFocusPainted(false);
		btnChatList.setBackground(BUTTON_COLOR);
		btnChatList.setBounds(635, 476, 145, 35);
		btnChatList.addActionListener(new ChatListListener(scpw, loggedUser));
		panel.add(btnChatList);
		
		JLabel label_7 = new JLabel("_______________");
		label_7.setHorizontalAlignment(SwingConstants.LEFT);
		label_7.setForeground(WHITE);
		label_7.setFont(BOLD_FONT);
		label_7.setBounds(490, 491, 137, 20);
		panel.add(label_7);
		
		btnSend_1 = new JButton("Send");
		btnSend_1.setForeground(WHITE);
		btnSend_1.setFont(FONT);
		btnSend_1.setFocusPainted(false);
		btnSend_1.setBackground(BUTTON_COLOR);
		btnSend_1.setBounds(674, 539, 106, 35);
		
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setFont(FONT);
		comboBox.setForeground(BACK_COLOR);
		comboBox.addItem("Single message");
		comboBox.addItem("Group message");
		comboBox.addItem("File");
		comboBox.setOpaque(false);
		comboBox.setBorder(null);
		comboBox.setBounds(25, 539, 145, 35);
		comboBox.addItemListener(new ComboBoxListener(txtUsernameHere, label_5, btnSelectFile, txtMessageHere,btnSend_1));
		panel.add(comboBox);
		
		btnSend_1.addActionListener(new SendActionListener(s.getInetAddress(), loggedUser, cpw, txtUsernameHere, txtMessageHere, comboBox));
		panel.add(btnSend_1);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(25, 265, 748, 185);
		scrollPane.setOpaque(false);
		scrollPane.setBorder(null);
		scrollPane.getViewport().setBackground(BACK_COLOR);
		panel.add(scrollPane);
		
		textArea_1 = new JTextArea();
		textArea_1.setOpaque(false);
		textArea_1.setForeground(WHITE);
		textArea_1.setFont(FONT);
		textArea_1.setBorder(null);
		textArea_1.setEditable(false);
		scrollPane.setViewportView(textArea_1);
		frame.setVisible(true);
	}
	
	public JTextArea getTextArea() {
		return textArea;
	}
	
	public JButton getSendBtn() {
		return btnSend_1;
	}
	
	public JTextArea getMessageArea() {
		return textArea_1;
	}
	
	public JTextField getGroupIdTextfield() {
		return txtChatId;
	}
	
	public void setWindowListener(SocialFrameListener sfl) {
		frame.addWindowListener(sfl);
	}
	
	public FileActionListener getFileListener() {
		return fal;
	}
}