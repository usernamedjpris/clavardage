import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;


import javax.swing.JButton;
import javax.swing.JFrame;

import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;


public class VueChoixPseudo  implements ActionListener{

	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VueChoixPseudo window = new VueChoixPseudo();
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
	public VueChoixPseudo() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 683, 321);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(6, 3));
		
		JLabel lblvide1 = new JLabel("");
		JLabel lblvide2 = new JLabel("");
		JLabel lblvide3 = new JLabel("");
		JLabel lblvide4 = new JLabel("");
		JLabel lblvide5 = new JLabel("");
		JLabel lblvide6 = new JLabel("");
		JLabel lblvide7 = new JLabel("");
		JLabel lblvide9 = new JLabel("");
		JLabel lblvide10 = new JLabel("");
		JLabel lblvide11 = new JLabel("");
		JLabel lblvide12 = new JLabel("");
		JLabel lblvide13 = new JLabel("");


		frame.getContentPane().add(lblvide1);
		frame.getContentPane().add(lblvide2);		
		frame.getContentPane().add(lblvide3);
		frame.getContentPane().add(lblvide4);
		JLabel lblPseudoDuJour = new JLabel("Pseudo du jour :");
		lblPseudoDuJour.setHorizontalAlignment(SwingConstants.CENTER);
		lblPseudoDuJour.setFont(new Font("Courier New", Font.BOLD, 20));
		frame.getContentPane().add(lblPseudoDuJour);
		frame.getContentPane().add(lblvide5);
		frame.getContentPane().add(lblvide6);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1);
		
		JSplitPane splitPane = new JSplitPane();
		panel_1.add(splitPane);
		
		JLabel lblPseudo = new JLabel("pseudo :");
		lblPseudo.setBackground(new Color(240, 240, 240));
		lblPseudo.setFont(new Font("Courier New", Font.PLAIN, 17));
		splitPane.setLeftComponent(lblPseudo);
		
		textField = new JTextField();
		textField.setFont(new Font("Courier New", Font.BOLD | Font.ITALIC, 13));
		splitPane.setRightComponent(textField);
		textField.setColumns(10);
		frame.getContentPane().add(lblvide7);
		frame.getContentPane().add(lblvide9);
		frame.getContentPane().add(lblvide10);
		frame.getContentPane().add(lblvide11);
		frame.getContentPane().add(lblvide12);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		
		JButton btnLogin = new JButton("login");
		btnLogin.setBackground(new Color(102, 205, 170));
		btnLogin.setForeground(Color.WHITE);
		btnLogin.setFont(new Font("Calibri", Font.BOLD, 13));
		btnLogin.setSize(20,50);
		panel.add(btnLogin);
		btnLogin.addActionListener(this);
		frame.getContentPane().add(lblvide13);

		
	}
	public void actionPerformed(ActionEvent ae)
	 {
	   String uname = textField.getText();
	   //if(uname.checkunicity() && uname != "") //check unicity
	   //{
	   System.out.println("ok : "+uname);

	    //}
	    //else
	    //{
	    //  JOptionPane.showMessageDialog(this,"Incorrect login or password",
	    //  "Error",JOptionPane.ERROR_MESSAGE);  
	    //}
	  //}
	 }
}
