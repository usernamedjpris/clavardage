
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class VuePrincipale {

	private JFrame frame;
	private JTextField textField;
	HashMap<String,String> conv;
	JList<Entry<String, Personne>> list = new JList<Entry<String, Personne>>();
	String activePseudo;
	Application app;
	DefaultListModel<Entry<String, Personne>> model;
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VuePrincipale window = new VuePrincipale();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	public VuePrincipale(Application application,DefaultListModel<Entry<String, Personne>> m) {
		app=application;
		model=m;
		initialize();
		this.frame.setVisible(true);
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 723, 324);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setTitle("Super clavardeur ! 🧙‍♂️ ‍🐱");
		//css choice between : (discontinued) 
		/*JFXPanel jfxPanel = new JFXPanel(); // Scrollable JCompenent
		Platform.runLater( () -> { // FX components need to be managed by JavaFX
		   WebView webView = new WebView();
		   webView.getEngine().loadContent( "<html> Hello World!" );
		   webView.getEngine().load( "http://www.stackoverflow.com/" );
		   jfxPanel.setScene( new Scene( webView ) );
		});
		*/
		/*
		 * (very old )

JEditorPane ep = new JEditorPane();
ep.setContentType("text/html");
ep.setText("html code");

*/
		JEditorPane message_zone = new JEditorPane();
		message_zone.setContentType("text/html");
	    message_zone.setBorder( BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    HTMLEditorKit kit = new HTMLEditorKit();
	    message_zone.setEditorKit(kit);
	    StyleSheet styleSheet = kit.getStyleSheet();
	    styleSheet.addRule(".alignleft{color : rgb(0,128,25); font-weight: bold;text-align: left;}");
	    styleSheet.addRule(".alignright{color : rgb(0,0,25); font-weight: bold;text-align: right;}");
	    message_zone.setText("<html>" +
	            "<center><b><font size=6>Important Information</font></b></center>" +
	            "<div id=textbox><p class='alignleft'>left</p><p class='alignright'>right</p></div>" +
	            "</html>");
	    message_zone.setEditable(false);
		JTextArea txtrText = new JTextArea();
		txtrText.setText("\r\nAs it currently stands, this question is not a good fit for our Q&A format. We expect answers to be supported by facts, references, or expertise, but this question will likely solicit debate, arguments, polling, or extended discussion. If you feel that this question can be improved and possibly reopened, visit the help center for guidance.\r\nClosed 8 years ago.\r\nLocked. This question and its answers are locked because the question is off-topic but has historical significance. It is not currently accepting new answers or interactions.\r\n\r\nI'm looking for a good GUI designer for swing in eclipse. My preference is for a free/open-source plugin.\r\njava eclipse swing gui-designer\r\nshare\r\nasked Aug 27 '08 at 3:06\r\njumar\r\n5,09577 gold badges3939 silver badges4242 bronze badges\r\n\r\n    11\r\n    The reason this question was closed is that it is effectively a poll. There rarely is one answer to the \"Best XXX for YYY\" style of question, so these don't fit well with the Stack Overflow question system. There are plenty of other sites to go to in order to find lists of products and subjective discussions about them, but we've found that they don't work here. \u2013 Brad Larson\u2666 Jul 2 '12 at 16:05\r\n    126 voted for the question. 70 voted for the chosen answer. Not constructive enough for you? Your comment received 7 votes. \u2013 Agnel Kurian Jul 2 '12 at 17:06 \r\n\r\n4\r\n@Agnel Kurian: \"asked Aug 27 '08\" \"answered Sep 28 '10\" Considering that the comment was posted just an hour ago... \u0CA0_\u0CA0 \u0CA0_\u0CA0 \u0CA0_\u0CA0 \u2013 BoltClock\u2666 Jul 2 '12 at 17:20\r\n2\r\nWhy not keep it as a CW? It's useful and the votes prove it, that's what's most important \u2013 Kos Jul 3 '12 at 8:54\r\n");
		frame.getContentPane().add(message_zone, BorderLayout.CENTER);
		
		
		list.setBorder(new LineBorder(new Color(0, 0, 0), 4, true));
		list.setFont(new Font("Tahoma", Font.ITALIC, 18));
		list.setModel(model);
		list.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                      boolean isSelected, boolean cellHasFocus) {
                 Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                 if (value instanceof Entry<?,?>) {
                	 Entry<String, Personne> user = (Entry<String, Personne>) value;
                      setText(user.getKey());
                      if (user.getValue().getConnected()) {
                           setBackground(Color.GREEN);
                      } else {
                           setBackground(Color.RED);
                      }
                      if (isSelected) {
                           setBackground(getBackground().darker());
                      }
                 } else {
                      setText("whodat?");
                 }
                 return c;
            }

       });

		ListSelectionListener listSelectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) {
				 int selected = list.getSelectedIndex();
				 if(selected != -1) {
				 activePseudo = (String) list.getSelectedValue().getKey();
				 loadConversation(activePseudo);
				 System.out.print(activePseudo);
				 }
				}
				
			}};
		    list.addListSelectionListener(listSelectionListener);
			list.setSelectedIndex(0);
			activePseudo = (String) list.getSelectedValue().getKey();
		
		frame.getContentPane().add(list, BorderLayout.WEST);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		
		textField = new JTextField();
		textField.setPreferredSize(new Dimension(200,35));
		panel.add(textField);
		//textField.setColumns(10);
		
		JButton btnSend = new JButton("Send ! 😎");
		btnSend.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
				String tosend = null;
						tosend = textField.getText();
						if(!tosend.equals("")) {
						try {
							Message m =new Message(tosend.getBytes(), app.getPersonne(), app.getPersonneOfPseudo(activePseudo));
							Reseau.getReseau().sendData(m);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(frame, "Erreur réseau... :'( ", "ErrorBox: " + "📛", JOptionPane.ERROR_MESSAGE);	
						}
						}
						else
							JOptionPane.showMessageDialog(frame, "Vous ne pouvez pas envoyer un message vide désolé :p ", "InfoBox: " + "🙄", JOptionPane.INFORMATION_MESSAGE);
					
						}});
		btnSend.setFont(new Font("Arial Unicode MS", Font.BOLD, 18));
		panel.add(btnSend);
		
		JButton btnNewButton_1;
			btnNewButton_1 = new JButton("Déconnexion 😥");
			btnNewButton_1.setFont(new Font("Arial Unicode MS", Font.BOLD, 18));
			panel.add(btnNewButton_1);
	}
	protected void loadConversation(String pseudo) {
		// TODO Auto-generated method stub
		
	}

	public void createConversation(Personne toPersonne) {
		ArrayList<Message> hist=BD.getBD().getHistorique(BD.getBD().getIdPersonne(toPersonne.getPseudo()));
		String html="";
		for(Message m:hist) //max de chargement historiques messages possible
			html+=m.toHtml();
		conv.put(toPersonne.getPseudo(), html);
	}
	public void update(Personne emetteur, Message message) {
		//R: save des messsages dans la BD à l'envoie et à la réception par AA
		conv.put(emetteur.getPseudo(), message.toHtml());
		//if list active user 
		//maj Jpanel en add le message
		//sinon change la jList en gras/rouge 
		//quand switch de conv => aff conv.get(Pseudo)
		
	}

	public void deconnection(Personne emetteur) {
		// TODO Auto-generated method stub
		
	}
}
