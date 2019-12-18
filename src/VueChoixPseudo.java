import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;


import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;


public class VueChoixPseudo  implements ActionListener{

	private JDialog frame;
	private JTextField textField;
	private Application app;
	public VueChoixPseudo(Application a) {
		app=a;
		initialize();
	}

	private void initialize() {
		frame = new JDialog();
		frame.setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
		frame.setBounds(100, 100, 500, 100);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
		frame.setVisible(true);

		
	}
	public void actionPerformed(ActionEvent ae)
	 {
	   String uname = textField.getText();
	   if(!uname.equals("") && app.checkUnicity(uname) ) //check unicity
	   {
		   app.setPseudoUser(uname);
		   frame.setModalityType(JDialog.ModalityType.MODELESS);
		  // frame.setModalityType(JDialog.);
		   frame.dispose();
	   }
	    else
	   { 
	    	 if(uname.equals(""))
	      JOptionPane.showMessageDialog(frame, "Ton pseudo ne peut pas être vide :'( ", "Dommage... " + "📛", JOptionPane.INFORMATION_MESSAGE);
	    	 else
	      JOptionPane.showMessageDialog(frame, "Ton pseudo est déjà pris désolé :'( ", "Dommage... " + "📛", JOptionPane.INFORMATION_MESSAGE);
			
	   }
	 }
}
