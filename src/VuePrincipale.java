
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
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
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class VuePrincipale {

	private JFrame frame;
	private JTextArea textField;
	HashMap<Long,ArrayList<Message>> conv=new HashMap<>();//id, liste de message
	HashMap<Long,Boolean> unread=new HashMap<>();//id, lu ou pas
	JList<Personne> list = new JList<Personne>();
	Personne activeUser;
	Application app;
	DefaultListModel<Personne> model;
	JButton btnSend;
	JButton btnDeco;
	JEditorPane message_zone;
	String defaultTitle=new String("Super clavardeur !  :D");
	
	public VuePrincipale(Application application,DefaultListModel<Personne> m) {
		app=application;
		model=m;
		initialize();
		this.frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				app.sendDisconnected();
				e.getWindow().dispose();
			}
		});
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
                KeyEvent.VK_O, ActionEvent.ALT_MASK));
        fr.setMnemonic(KeyEvent.VK_O);
        fr.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(app.getDownloadPath());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}});
        menu.add(fr);
        JMenuItem tele = new JMenuItem("Changer le dossier de téléchargement",
                new ImageIcon("images/icon_wheel.png"));
        tele.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_C, ActionEvent.ALT_MASK));
        tele.setMnemonic(KeyEvent.VK_C);
        tele.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               JFileChooser dirChooser = new JFileChooser(app.getDownloadPath());
               dirChooser.setMultiSelectionEnabled(true);
               dirChooser.setDialogTitle("Choisir le dossier de téléchargement");
               dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            // disable the "All files" option.
               dirChooser.setAcceptAllFileFilterUsed(false);
               dirChooser.setFileHidingEnabled(true);
               int option = dirChooser.showOpenDialog(frame);
               if(option == JFileChooser.APPROVE_OPTION){
                 // File[] files = dirChooser.getSelectedFiles();
            	   /* for(File file: files){
                     fileNames += file.getName() + " ";
                  }*/	   
                  File dir = dirChooser.getSelectedFile();
                  System.out.print("Directory  Selected: " + dir.getAbsolutePath());
                  app.setDownloadPath(dir);
               }else{
            	   System.out.print("cancelled");
               }
            }
         });
        menu.add(tele);
        JMenuItem apropos = new JMenuItem("A propos",new ImageIcon("images/icon22.png"));
        apropos.setMnemonic(KeyEvent.VK_A);
        apropos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	JOptionPane.showMessageDialog(frame, "Programmeurs: Rémi Fache et Jeremie Gantet V1.0 2020", "A propos", JOptionPane.INFORMATION_MESSAGE);	
            }
            });
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
				new VueChoixPseudo(app,true);
				
			}});
        bar.add(menu);
        bar.add(swip);
        bar.setVisible(true);
		frame.setJMenuBar(bar);
	}
	private void initializeHtmlView() {
		message_zone = new JEditorPane();	
		message_zone.setContentType("text/html");
	    message_zone.setBorder( BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    HTMLEditorKit kit = new HTMLEditorKit();
	    message_zone.setEditorKit(kit);
	    StyleSheet styleSheet = kit.getStyleSheet();
	    styleSheet.addRule(".title{font-family: 'Courier New', monospace; margin-bottom: 20px}");
	    styleSheet.addRule(".alignleft{margin-right:200px; text-align: left;font-family: Calibri, sans-serif; padding: 2px 7px 5px 7px; font-size:17pt; font-weight:bold; background-color:rgb(240,250,240);}");
	    styleSheet.addRule(".date{font-family: 'Courier New', monospace; padding: 0px 7px; color:black; font-weight: none; font-size:12pt;}");
	    styleSheet.addRule(".alignright{margin-left:200px;text-align: right;font-family: Calibri, sans-serif; padding:2px 7px 5px 7px; font-size:17pt; font-weight:bold; background-color:rgb(240,240,250);}");
	      /*message_zone.setText("<html>" +
	            "<center><b><font size=6>Important Information</font></b></center>" +
	            "<div id=textbox><p class='alignleft'>left</p><p class='alignright'>right</p></div>" +
	            "</html>");*/
	    message_zone.setEditable(false);
	}
	public void setHtmlView(ArrayList<Message> m) {
		Long to=activeUser.getId();
		String new_message_text = "<html>";//Embetant à switch : +"<center class='title'><b><font size=6>"+activeUser.getPseudo()+"</font></b></center><div id=textbox>";
		for (Message i: m) {
			if (to==i.getEmetteur().getId()){ 
				new_message_text += "<div class='alignleft'>"+i.toHtml()+"</div>";
			} else {
				new_message_text += "<div class='alignright'>"+i.toHtml()+"</div>";
			}
		}
		//System.out.println(new_message_text+"</div></html>");
		this.message_zone.setText(new_message_text);

	}
	private void initializeList() {
		//list.setBorder(new LineBorder(new Color(0, 0, 0), 4, true));
		list.setFont(new Font("Tahoma", Font.ITALIC, 18));
		list.setModel(model);
		list.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                      boolean isSelected, boolean cellHasFocus) {
                 Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                 if (value instanceof Personne) {
                	 Personne user = (Personne) value;
                      setText(user.getPseudo());
                      if(unread.get(user.getId())!=null)
                      this.setFont(new Font(Font.SANS_SERIF,  Font.BOLD, 15));
                      else
                      this.setFont(new Font(Font.SANS_SERIF,  Font.PLAIN, 15));
                      if (user.getConnected()) {
                           setBackground(Color.GREEN);
                      } else {
                           setBackground(Color.RED);
                      }
                      this.setBorder(new EmptyBorder(0, 0, 0, 10));
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
				 activeUser = list.getSelectedValue();
				 loadConversation(activeUser.getId());
				 System.out.print(activeUser);
				 }
				}
				
			}};
		    list.addListSelectionListener(listSelectionListener);
			list.setSelectedIndex(0);
			activeUser = list.getSelectedValue();
			loadConversation(activeUser.getId());
			//conv.put(activeUser.getId(), new ArrayList<>());
	}
	private void initializeButtons() {
		btnSend = new JButton("Send ! ");
		btnSend.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
				String tosend = null;
						tosend = textField.getText();
						if(!tosend.equals("")) {
							Message m =new Message(tosend.getBytes(), app.getPersonne(), activeUser);
							Reseau.getReseau().sendTCP(m);
												}
						else
							JOptionPane.showMessageDialog(frame, "Vous ne pouvez pas envoyer un message vide désolé :p ", "InfoBox " , JOptionPane.INFORMATION_MESSAGE);
					
						}});
		btnSend.setFont(new Font("Arial Unicode MS", Font.BOLD, 18));
		btnDeco= new JButton("Déconnexion");
		btnDeco.setFont(new Font("Arial Unicode MS", Font.BOLD, 18));
		btnDeco.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(frame, "Au revoir ! ", "See you ! ", JOptionPane.INFORMATION_MESSAGE);
					app.sendDisconnected();
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
		changePseudo(app.getPseudo());
		frame.setIconImage(new ImageIcon("images/icon.png").getImage());
		initializeHtmlView();
		initializeList();
		

	    
		JPanel panel = new JPanel(new BorderLayout());
			
		JScrollPane ljs=new  JScrollPane(list);
		ljs.setVisible(true);
		//ljs.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		
	    JScrollPane editorScrollPane = new JScrollPane(message_zone); 
	    editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    
		JSplitPane split_conv=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,ljs, editorScrollPane);
		split_conv.setDividerSize(5);
		
		JSplitPane split_final=new JSplitPane(JSplitPane.VERTICAL_SPLIT,split_conv, panel);
		split_final.setDividerSize(5);
		//frame.getContentPane().add(list, BorderLayout.WEST);
		frame.getContentPane().add(split_final, BorderLayout.CENTER);
		
		
		//frame.getContentPane().add(panel, BorderLayout.SOUTH);
		
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
	protected void loadConversation(Long id) {
		ArrayList<Message> c=conv.get(id);
		if(c != null) {
			setHtmlView(c);
		}else {
		ArrayList<Message> hist=BD.getBD().getHistorique(app.getPersonne(),activeUser);
		conv.put(activeUser.getId(), hist);
		}
	}
	public void update(Personne emetteur, Message message) {
		//R: save des messsages dans la BD à  l'envoie et à  la réception par app
		ArrayList<Message> l=conv.get(emetteur.getId());
		if(l != null)
		l.add(message);
		else {
			l= new ArrayList<>();
			l.add(message);
			conv.put(emetteur.getId(),l);
		}
		//si on à la conversation affichée à l'écran:
		if(emetteur.getId()==activeUser.getId())
		{
		message_zone.setText(message_zone.getText()+"<div class='alignleft'>"+message.toHtml()+"</div>");
		}
		else
		{
			unread.put(emetteur.getId(), true);
			updateList();
		}
		
	}

	public void changePseudo(String uname) {
		frame.setTitle(defaultTitle+" ["+uname+"]");
	}
	public void updateList() {
		list.repaint();
		
	}
}
