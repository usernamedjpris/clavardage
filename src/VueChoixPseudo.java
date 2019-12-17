import java.awt.EventQueue;
import java.awt.FlowLayout;
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
import java.awt.Dimension;


public class VueChoixPseudo  implements ActionListener{

	private JFrame frame;
	private JTextField textField;

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
		frame.setBounds(100, 100, 500, 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		//frame.getContentPane().setLayout(new GridLayout(6, 3));

		JLabel lblPseudoDuJour = new JLabel("Surnom :");
		lblPseudoDuJour.setHorizontalAlignment(SwingConstants.CENTER);
		lblPseudoDuJour.setFont(new Font("Courier New", Font.BOLD, 15));
		frame.getContentPane().add(lblPseudoDuJour);

		
		textField = new JTextField();
		textField.setFont(new Font("Courier New", Font.BOLD | Font.ITALIC, 15));
		textField.setPreferredSize(new Dimension(150,20));
		frame.getContentPane().add(textField);
		
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		
		JButton btnLogin = new JButton("s'identifier");
		btnLogin.setBackground(new Color(102, 205, 170));
		btnLogin.setForeground(Color.WHITE);
		btnLogin.setFont(new Font("Calibri", Font.BOLD, 13));
		btnLogin.setPreferredSize(new Dimension(95,20));
		panel.add(btnLogin);
		btnLogin.addActionListener(this);
		frame.getRootPane().setDefaultButton(btnLogin); //permet de l'appuyer en appuyant sur entree

		
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
