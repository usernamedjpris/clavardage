
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class VuePrincipale {

	private JFrame frame;
	private JTextArea textField;
	HashMap<String,ArrayList<Message>> conv=new HashMap<>();//pseudo, liste de message
	JList<Entry<String, Personne>> list = new JList<Entry<String, Personne>>();
	Entry<String, Personne> activePseudo;
	Application app;
	DefaultListModel<Entry<String, Personne>> model;
	JButton btnSend;
	JButton btnDeco;

	public VuePrincipale(Application application,DefaultListModel<Entry<String, Personne>> m) {
		app=application;
		model=m;
		initialize();
		this.frame.setVisible(true);
	}
	private void initializeMenu() {
		JMenuBar bar=new JMenuBar();
		//Build the File Menu.
        JMenu menu = new JMenu("Fichier");
        menu.setFont(new Font("Tahoma", Font.BOLD, 16));
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("Gestion des fichiers");
// create menu item and add it to the menu
        JMenuItem fr = new JMenuItem("Ouvrir le dossier des fichiers reçus",
                new ImageIcon("images/icon_open.png"));
        fr.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_D, ActionEvent.ALT_MASK));
        fr.setMnemonic(KeyEvent.VK_F);
        menu.add(fr);
        JMenuItem tele = new JMenuItem("Changer le dossier de téléchargement",
                new ImageIcon("images/icon_wheel.png"));
        tele.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_D, ActionEvent.ALT_MASK));
        tele.setMnemonic(KeyEvent.VK_F);
        //tele.addActionListener(new ActionListener() {});
        menu.add(tele);
        JMenuItem apropos = new JMenuItem("à propos 🕴",new ImageIcon("images/icon22.png"));
        apropos.setMnemonic(KeyEvent.VK_A);
        menu.add(apropos);
        JMenuItem swip = new JMenuItem("Pseudo");
        swip.setFont(new Font("Tahoma", Font.BOLD, 16));
        swip.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.ALT_MASK));
        swip.setMnemonic(KeyEvent.VK_P);
        swip.getAccessibleContext().setAccessibleDescription("Gestion des pseudos");
        swip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO
				System.out.print("\n OK !");
				
			}});
        bar.add(menu);
        bar.add(swip);
        bar.setVisible(true);
		frame.setJMenuBar(bar);
	}
	private void initializeHtmlView() {
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
		frame.getContentPane().add(message_zone, BorderLayout.CENTER);
	}
	private void initializeList() {
		list.setBorder(new LineBorder(new Color(0, 0, 0), 4, true));
		list.setFont(new Font("Tahoma", Font.ITALIC, 18));
		list.setModel(model);
		list.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("unchecked")
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
				 activePseudo = list.getSelectedValue();
				 loadConversation(activePseudo.getKey());
				 System.out.print(activePseudo);
				 }
				}
				
			}};
		    list.addListSelectionListener(listSelectionListener);
			list.setSelectedIndex(0);
			activePseudo = list.getSelectedValue();
			conv.put(activePseudo.getKey(), new ArrayList<>());
	}
	private void initializeButtons() {
		btnSend = new JButton("Send ! 😎");
		btnSend.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
				String tosend = null;
						tosend = textField.getText();
						if(!tosend.equals("")) {
						try {
							Message m =new Message(tosend.getBytes(), app.getPersonne(), activePseudo.getValue());
							Reseau.getReseau().sendData(m);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(frame, "Erreur réseau... :'( ", "ErrorBox: " + "📛", JOptionPane.ERROR_MESSAGE);	
						}
						}
						else
							JOptionPane.showMessageDialog(frame, "Vous ne pouvez pas envoyer un message vide désolé :p ", "InfoBox: " + "🙄", JOptionPane.INFORMATION_MESSAGE);
					
						}});
		btnSend.setFont(new Font("Arial Unicode MS", Font.BOLD, 18));
		
		btnDeco= new JButton("Déconnexion 😥");
		btnDeco.setFont(new Font("Arial Unicode MS", Font.BOLD, 18));
		btnDeco.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(frame, "Au revoir ! ", "👋", JOptionPane.INFORMATION_MESSAGE);
					//System.exit(0); //too crude
					//app.close();
				            Container frame = btnDeco.getParent();
				            do 
				              frame = frame.getParent(); 
				            while (!(frame instanceof JFrame));                                      
				            ((JFrame) frame).dispose();
				    		System.exit(0);
				        }
				    });

	}
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 723, 324);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setTitle("Super clavardeur ! 🧙‍♂️ ‍🐱");
		frame.setIconImage(new ImageIcon("images/icon.png").getImage());
		initializeHtmlView();
		initializeList();

		frame.getContentPane().add(list, BorderLayout.WEST);
		JPanel panel = new JPanel(new BorderLayout());
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		
		textField = new JTextArea();
		textField.setLineWrap(true);
		textField.setWrapStyleWord(true);
		/*textField.setRows(4);
		textField.setColumns(20);*/
		
		JScrollPane js=new  JScrollPane(textField);
		js.setVisible(true);
		js.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

		panel.add(js, BorderLayout.CENTER);
		//textField.setColumns(10);
		
		initializeButtons();
		JSplitPane jsp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, btnSend, btnDeco);
		jsp.setDividerSize(0);
		panel.add(jsp,BorderLayout.EAST);
		initializeMenu();
	}
	protected void loadConversation(String pseudo) {
		// TODO Auto-generated method stub
		
	}

	public void createConversation(Personne toPersonne) {
		ArrayList<Message> hist=BD.getBD().getHistorique(BD.getBD().getIdPersonne(toPersonne.getPseudo()));
		/*String html="";
		for(Message m:hist) //max de chargement historiques messages possible
			html+=m.toHtml();*/
		conv.put(toPersonne.getPseudo(), hist);
	}
	public void update(Personne emetteur, Message message) {
		//R: save des messsages dans la BD à l'envoie et à la réception par AA
		
		//RQ: en vrai: sur CONNECTION  model add user
		//sur clic pseudo liste de gauche, maj conv !
		ArrayList<Message> l=conv.get(emetteur.getPseudo());
		if(l != null)
		l.add(message);
		else {
			l= new ArrayList<>();
			l.add(message);
			conv.put(emetteur.getPseudo(),l);
		}
		System.out.print(message.toHtml());
		//if list active user 
		//maj Jpanel en add le message
		//sinon change la jList en gras/rouge 
		//quand switch de conv => aff conv.get(Pseudo)
		
	}

	public void deconnection(Personne emetteur) {
		// TODO Auto-generated method stub
		
	}
}