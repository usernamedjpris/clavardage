import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class VueChoixPseudo implements ActionListener{

	private JDialog frame;
	private JTextField textField;
	private ControleurApplication app;
	private boolean inApp;
	public VueChoixPseudo(ControleurApplication a,boolean inApp) {
		app=a;
		this.inApp=inApp;
		initialize();
	}

	private void initialize() {
		frame = new JDialog();
		frame.setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
		frame.setBounds(100, 100, 250, 175);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		frame.setTitle("Choisissez un pseudo ! :D ");
		frame.setIconImage(new ImageIcon("images/icon.png").getImage());
		//frame.getContentPane().setLayout(new GridLayout(6, 3));		
		
		
		JLabel lblPseudoDuJour = new JLabel(" Pseudo :");
		lblPseudoDuJour.setHorizontalAlignment(SwingConstants.CENTER);
		lblPseudoDuJour.setFont(new Font("Courier New", Font.BOLD, 20));
		frame.getContentPane().add(lblPseudoDuJour);


		textField = new JTextField();
		textField.setFont(new Font("Courier New", Font.BOLD | Font.ITALIC, 20));
		textField.setPreferredSize(new Dimension(200,25));
		frame.getContentPane().add(textField);


		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);



		JButton btnLogin = new JButton("s'identifier");
		btnLogin.setBackground(new Color(102, 205, 170));
		btnLogin.setForeground(Color.WHITE);
		btnLogin.setFont(new Font("Courier New", Font.BOLD, 20));
		btnLogin.setPreferredSize(new Dimension(200,25));
		panel.add(btnLogin);
		btnLogin.addActionListener(this);
		
		if(!inApp) {
			JLabel lblIp = new JLabel(" ip : "+app.getPersonne().getAddressAndPorts());
			lblIp.setHorizontalAlignment(SwingConstants.CENTER);
			lblIp.setFont(new Font("Courier New", Font.BOLD, 10));
			frame.getContentPane().add(lblIp);

			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					/*JLabel j=new JLabel("Vous devez entrer un pseudo valide pour vous connecter ou cliquer sur annuler ????? ");
					j.setFont(new Font("Courier New", Font.PLAIN, 12));
					JOptionPane.showMessageDialog(frame,j, "InfoBox: " + "??", JOptionPane.INFORMATION_MESSAGE);*/
					System.exit(0);
				}
			});
		} 

		frame.getRootPane().setDefaultButton(btnLogin); //permet de l'appuyer en appuyant sur entree
		frame.setVisible(true);
	}
	@SuppressWarnings("static-access")
	public void actionPerformed(ActionEvent ae){
	    String uname = textField.getText();
	    if(!uname.equals("") && app.checkUnicity(uname) ) //check unicity
	    {

			if(inApp)
			   app.setPseudoUserSwitch(uname);
			else
			   app.setPseudoUserConnexion(uname);
		    frame.setModalityType(JDialog.ModalityType.MODELESS);
		    // frame.setModalityType(JDialog.);
		    frame.dispose();
	    }
	    else
	    {
	    	 if(uname.equals(""))
	      JOptionPane.showMessageDialog(frame, "Ton pseudo ne peut pas être vide :'( ", "Dommage... " + "🙈", JOptionPane.INFORMATION_MESSAGE);
	    	 else
	      JOptionPane.showMessageDialog(frame, "Ton pseudo est déjà  pris désolé :'( ", "Dommage... " + "🙈", JOptionPane.INFORMATION_MESSAGE);
	    }
	 }
}
