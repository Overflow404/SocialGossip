import javax.swing.JFrame;
import org.json.simple.JSONArray;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class GroupListGui {
	/**
	 * @overview	Crea il frame per mostrare la lista degli amici.
	 */
	private JSONArray list;
	
	public GroupListGui(JSONArray list) {
		this.list = list;
		run();
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		//Constants
		final short FONT_SIZE = 16;
		final Color BACK_COLOR = new Color(36, 47, 65);
		final Color WHITE = Color.WHITE;
		final Font BOLD_FONT = new Font("Century Gothic", Font.BOLD, FONT_SIZE);
		
		JFrame frame = new JFrame("Group List");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(300, 429);
		frame.setResizable(false);
			
		JPanel panel = new JPanel();
		panel.setBackground(BACK_COLOR);
		panel.setLayout(null);
		frame.getContentPane().add(panel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 264, 368);
		scrollPane.setOpaque(false);
		scrollPane.setBorder(null);
		scrollPane.getViewport().setBackground(BACK_COLOR);
		panel.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setOpaque(false);
		textArea.setForeground(WHITE);
		textArea.setFont(BOLD_FONT);
		textArea.setBorder(null);
		textArea.setEditable(false);
		list.forEach(friend -> textArea.append("-" + friend.toString() + "\n"));
		frame.setVisible(true);
	}
}