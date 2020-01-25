import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.clava.serializable.Interlocuteurs;

public class VueCreationGroupe {
	private List<Interlocuteurs> l=null;
	public VueCreationGroupe(DefaultListModel<Interlocuteurs> model, ControleurApplication app) {
		
     	JList<Interlocuteurs> list=new JList<Interlocuteurs>();
     	DefaultListModel<Interlocuteurs> m=new DefaultListModel<Interlocuteurs>();
     	for(Object i:model.toArray()) {
     		Interlocuteurs v=(Interlocuteurs)i;
     		if(v.getAddressAndPorts().size()==1 && v.getConnected() && app.getPersonne().getId() != v.getId() )
     	m.addElement((Interlocuteurs)i);
     	}
     	list.setModel(m);
     	///TODO EN PROD
     	/*if(m.getSize() <2) {
     		JOptionPane.showMessageDialog(null, "Il n'y a pas assez d'utilisateurs connecté pour créer un groupe :p ", "InfoBox " , JOptionPane.INFORMATION_MESSAGE);
     	}
     	else*/
     	{
     	JDialog frame = new JDialog();
		frame.setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
		frame.setBounds(100, 100, 200, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		frame.setTitle("Choisissez les participants ! :D ");
		frame.setIconImage(new ImageIcon("images/network.png").getImage());
		
 		list.setFont(new Font("Tahoma", Font.PLAIN, 28));
     	list.setCellRenderer(new DefaultListCellRenderer() {
 			private static final long serialVersionUID = 1L;
 			@Override
             public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                       boolean isSelected, boolean cellHasFocus) {
                  Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                  if (value instanceof Interlocuteurs) {
                 	 Interlocuteurs user = (Interlocuteurs) value;
                 	 setText(user.getPseudo());
                       this.setForeground(Color.BLACK);
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
 				 int[] selected = list.getSelectedIndices();
 				 if(selected != null) {
 					 l=list.getSelectedValuesList();
 				 }
 				}
 				
 			}};
 			JButton btnOK = new JButton("OK");
 			btnOK.setBackground(new Color(102, 205, 170));
 			btnOK.setForeground(Color.WHITE);
 			btnOK.setFont(new Font("Courier New", Font.BOLD, 20));
 			btnOK.setPreferredSize(new Dimension(75,50));
 	
 			btnOK.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ae)
 			 {		
 				///TODO 
 				if(l != null && l.size() >1) {
 				 	 l.add(app.getPersonne());
					 app.creationGroupe(new ArrayList<>(l));
					 frame.dispose();
 			 }else
 				JOptionPane.showMessageDialog(null, "Il faut sélectionner les personnes du groupe, au moins 2 :p (ctrl maintenu pour sélection multiple) ", "InfoBox " , JOptionPane.INFORMATION_MESSAGE);
 			 }});
 		    list.addListSelectionListener(listSelectionListener);
 		    list.setVisible(true);
 		   JSplitPane split=new JSplitPane(JSplitPane.VERTICAL_SPLIT,list, btnOK);
 		    frame.getContentPane().add(split);
 		   frame.getRootPane().setDefaultButton(btnOK);
 		   frame.setVisible(true);
     	}
     }

}
