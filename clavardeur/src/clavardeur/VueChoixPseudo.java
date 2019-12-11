import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JButton;
import java.awt.Font;
import net.miginfocom.swing.MigLayout;

public class vueChoixNom {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					vueChoixNom window = new vueChoixNom();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public vueChoixNom() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 710, 467);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[grow][grow][grow]", "[36px][36px][36px][36px][36px][36px][36px]"));
		
		JLabel label = new JLabel("");
		frame.getContentPane().add(label, "cell 0 0,grow");
		
		JLabel label_1 = new JLabel("");
		frame.getContentPane().add(label_1, "cell 1 0,grow");
		
		JLabel label_2 = new JLabel("");
		frame.getContentPane().add(label_2, "cell 0 1,grow");
		
		JLabel lblPseudoDuJour = new JLabel("pseudo du jour");
		lblPseudoDuJour.setFont(new Font("Courier New", Font.BOLD, 15));
		frame.getContentPane().add(lblPseudoDuJour, "cell 1 1,grow");
		
		JLabel label_3 = new JLabel("");
		frame.getContentPane().add(label_3, "cell 0 2,grow");
		
		JLabel label_4 = new JLabel("");
		frame.getContentPane().add(label_4, "cell 1 2,grow");
		
		JLabel label_5 = new JLabel("");
		frame.getContentPane().add(label_5, "cell 0 3,grow");
		
		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, "cell 1 3,grow");
		
		textField = new JTextField();
		splitPane.setRightComponent(textField);
		textField.setColumns(10);
		
		JLabel lblPseudo = new JLabel("pseudo");
		lblPseudo.setFont(new Font("Courier New", Font.PLAIN, 13));
		splitPane.setLeftComponent(lblPseudo);
		
		JLabel label_6 = new JLabel("");
		frame.getContentPane().add(label_6, "cell 0 4,grow");
		
		JLabel label_7 = new JLabel("");
		frame.getContentPane().add(label_7, "cell 1 4,grow");
		
		JLabel label_8 = new JLabel("");
		frame.getContentPane().add(label_8, "cell 0 5,grow");
		
		JLabel label_9 = new JLabel("");
		frame.getContentPane().add(label_9, "cell 1 5,grow");
		
		JLabel label_10 = new JLabel("");
		frame.getContentPane().add(label_10, "cell 0 6,grow");
		
		JButton btnLogin = new JButton("login");
		frame.getContentPane().add(btnLogin, "cell 1 6,grow");
	}

}
