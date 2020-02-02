package com.clava.model.bd;

import java.net.InetAddress;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;

import com.clava.serializable.Group;
import com.clava.serializable.Interlocuteurs;
import com.clava.serializable.Message;
import com.clava.serializable.Personne;



//http://www.hsqldb.org/doc/guide/guide.pdf
//embded BD mode
public class BD {
	
	static BD instance = null;
	String mp = "";
	String url = "jdbc:hsqldb:file:SuperDB;shutdown=true";
	String login = "SA";
	Connection c = null;
    /**
     * Constructeur BD
     * <p>
     * classe private car singleton
     * </p>
     * 
     * @see BD#getBD()
     */
	private BD() {
		try {
			this.connexion();
			// creation de la bdd si pas deja cree
			PreparedStatement stmt;
			//cached useful if data >= 10Mo ( chargement partiel de la BD )
			
			String sql1 = "CREATE CACHED TABLE IF NOT EXISTS message (idEmet INTEGER NOT NULL, idDest INTEGER NOT NULL, "
					+ "sentDate VARCHAR(60) NOT NULL, type VARCHAR(60) NOT NULL, texte BLOB NOT NULL);";
			stmt = c.prepareStatement(sql1);
			stmt.executeUpdate();

			String sql2 = "CREATE TABLE IF NOT EXISTS identification (idUtilisateur INTEGER NOT NULL, pseudo VARCHAR(120) NOT NULL, PRIMARY KEY(idUtilisateur));";
			stmt = c.prepareStatement(sql2);
			stmt.executeUpdate();
			
			String sql3 = "CREATE TABLE IF NOT EXISTS groupe (idGroup INTEGER NOT NULL, idUtilisateur INTEGER NOT NULL,PRIMARY KEY(idGroup,idUtilisateur));";
			stmt = c.prepareStatement(sql3);
			stmt.executeUpdate();
			
			/*String sql3 = "CREATE TABLE IF NOT EXISTS preferences (iden INTEGER NOT NULL, downloadPath VARCHAR(240) NOT NULL, PRIMARY KEY(iden));";
			stmt = c.prepareStatement(sql3);
			stmt.executeUpdate();*/
			
			//MYSQL more compatible : http://hsqldb.org/doc/guide/compatibility-chapt.html
			Statement s0=c.createStatement();
			s0.executeUpdate("SET DATABASE SQL SYNTAX MYS TRUE");
			/*Statement s=c.createStatement();
			s.executeUpdate("INSERT IGNORE INTO preferences VALUES (1,'.')");*/	
			
			//kind of finalize but secure for standard exit
			Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
		         deconnexion();
				    }});
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	/**
	 * Décide de créer une BD en fonction de s'il en existe une déjà (classe singleton)
	 * @see BD#c
	 * @return BD
	 */
	public synchronized static BD getBD() {
		return instance != null ? instance : (instance = new BD()).getBD();
	}
	/**
	 * Se connecte à une base de donnée hsqldb
	 * @throws SQLException
	 */
	public void connexion() throws SQLException {
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (Exception e) {
			System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
			e.printStackTrace();
			return;
		}
		c=DriverManager.getConnection(url,login, mp);
	}
	/**
	 * Se déconnecte
	 */
	public void deconnexion() {
		try {
			if (c != null) {
				System.out.print(" close BD !");
				c.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Attribue un id à un pseudo donné
	 * @param newPseudo
	 * @param id
	 */
	public void setIdPseudoLink(String newPseudo, int id) {
		try {
			PreparedStatement stmt;
			String sql = "REPLACE INTO identification (idUtilisateur, pseudo) VALUES (?, ?);";
			stmt = c.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.setNString(2, newPseudo);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*public boolean checkUnicity(String pseudo) {
		Boolean ok = false;
		try {
			PreparedStatement stmt;
			String sql = "SELECT * FROM identification WHERE pseudo = ?";
			stmt = c.prepareStatement(sql);
			stmt.setNString(1, pseudo);
			ResultSet rs = stmt.executeQuery(sql);
			ok = !rs.next(); //si la requête nous renvoie une table vide false, true sinon
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ok;
	}*/
	/**
	 * Retrouve id à partir du pseudo
	 * <p>
	 * /!\ ne garde pas en mémoire les anciens pseudos
	 * </p>
	 * @param pseudo
	 * @return id
	 */
	/*
	public int getIdPersonne(String pseudo) {
		int idPersonne = 0;
		try {
			PreparedStatement stmt;
			String sql = "SELECT idUtilisateur FROM identification WHERE pseudo = ?";
			stmt = c.prepareStatement(sql);
			stmt.setNString(1, pseudo);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			idPersonne = rs.getInt("idUtilisateur");
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return idPersonne;

	}*/
	
	//id car les pseudos peuvent changer, voire s'inverser (le plus grave !), ne rendant pas fiable de savoir qui a envoyé à qui
	/**
	 * Retourne la conversation entière entre deux personnes données
	 * @param user (mettre soi)
	 * @param Interlocuteur
	 * @param groupe
	 * @return liste de messages
	 */
	public ArrayList<Message> getHistorique(Personne user, Interlocuteurs interlocuteur, boolean groupe) {
		ArrayList<Message> messages = new ArrayList<Message>();
				try {
			PreparedStatement stmt;
			String sql;
			if(!groupe) {
				sql = "SELECT * FROM message WHERE ((idEmet = ? AND idDest = ?) OR (idEmet = ? AND idDest = ?)) ORDER BY sentDate;";
				stmt = c.prepareStatement(sql);
				stmt.setInt(1, user.getId());
				stmt.setInt(2, interlocuteur.getId());
				stmt.setInt(3, interlocuteur.getId());
				stmt.setInt(4, user.getId());
			}else {
				sql = "SELECT * FROM message WHERE (idDest = ?) ORDER BY sentDate;";
				stmt = c.prepareStatement(sql);
				stmt.setInt(1, interlocuteur.getId());
			}
			ResultSet rs = stmt.executeQuery();
			int idUser=user.getId();
			while (rs.next()) {
				Message mes=null;
				// Retrieve by column name
				int idEmet = rs.getInt("idEmet");
				java.util.Date date = Message.getStringToDate(rs.getString("sentDate")); 
				Blob btext = rs.getBlob("texte");
				byte[] data=btext.getBytes(1l, (int) btext.length());
				Message.Type typ = Message.Type.valueOf(rs.getString("type"));
				System.out.println("GET HISTORIQUE "+Integer.toString(idEmet) +" "+new String(data)+" ("+ rs.getString("sentDate")+") ["+ typ+"]");
				if(interlocuteur.getInterlocuteurs().size()>1) {
					for(Interlocuteurs i: interlocuteur.getInterlocuteurs())
						if(i.getId()==idEmet)
							mes = Message.Factory.recreateMessageFromData(typ,data,i,interlocuteur, date);
					if(mes==null) {
						System.out.print("\n Impossible de retrouver l'origine de ce message !");
						mes = Message.Factory.recreateMessageFromData(typ,data,interlocuteur,user, date);
					}
				}else  if (idEmet == idUser) {// emetteur = moi
					mes = Message.Factory.recreateMessageFromData(typ,data, user, interlocuteur, date);
				}//groupe 
				else {
					mes = Message.Factory.recreateMessageFromData(typ,data,interlocuteur,user, date);
				}
				messages.add(mes);
			}
			rs.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return messages;
	}
	public void addGroup(int id, ArrayList<Interlocuteurs> interlocuteurs) {
		try {
			System.out.print("\n GROUPE ADDED BD ! ");
			PreparedStatement stmt;
			for( Interlocuteurs i: interlocuteurs) {
			String sql = "INSERT IGNORE INTO groupe (idGroup,idUtilisateur) VALUES (?, ?);";
			stmt = c.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.setInt(2, i.getId());
			stmt.executeUpdate();
			}
			stmt = c.prepareStatement("SELECT * from groupe");
			ResultSet rs=stmt.executeQuery();
			while(rs.next()) {
				System.out.print("\n id groupe: "+rs.getInt("idGroup")+" personne :"+rs.getInt("idUtilisateur"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * retourne la liste des personnes à qui on a déjà parlé une fois sauf à soi (donner son id) 
	 * <p>
	 * parlé une fois = envoyé au moins eu un message default ou file à destination de cette personne
	 * </p>
	 * @param user 
	 * @param id 
	 * @return
	 */
	public ArrayList<Interlocuteurs> getInterlocuteursTalked(Personne user) {
		HashMap<Integer, Interlocuteurs> liste=new HashMap<Integer,Interlocuteurs> ();
		try {
			PreparedStatement stmt;
			String sql = "SELECT pseudo, idUtilisateur FROM identification";// JOIN (SELECT DISTINCT idDest FROM message WHERE type = 'DEFAULT' OR type = 'FILE') ON idDest = idUtilisateur";
			stmt = c.prepareStatement(sql);
			//stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String pseudo = rs.getString("pseudo");
				int idU = rs.getInt("idUtilisateur");
				liste.put(idU, new Personne(new SimpleEntry<InetAddress, Integer>(null,-1),pseudo,false,idU));
			}
			liste.put(user.getId(), user);
			rs.close();
			stmt.close();
			
			PreparedStatement s;
			sql = "SELECT * FROM groupe";
			s = c.prepareStatement(sql);
			ResultSet r = s.executeQuery();
			ArrayList<Interlocuteurs> allInGroup=new ArrayList<>();
			if(r.next()) {
			int idGroup=r.getInt("idGroup");
			int idUser=r.getInt("idUtilisateur");
			//System.out.print("\n id groupe :"+idGroup+" iduser "+idUser+" result :"+liste.get(idUser));
			allInGroup.add(liste.get(idUser));
			while (r.next()) {
				//System.out.print("\n array :"+allInGroup);
				int idG = r.getInt("idGroup");
				int idP = r.getInt("idUtilisateur");
				if(idG != idGroup) {
					liste.put(idGroup, new Group(allInGroup));
					//System.out.print("\n array2 :"+allInGroup);
					allInGroup=new ArrayList<>();
					idGroup=idG;
				}
				allInGroup.add(liste.get(idP));
			}
			liste.put(idGroup, new Group(allInGroup));
			}
			r.close();
			s.close();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ArrayList<Interlocuteurs> array=new ArrayList<>();
		for (Interlocuteurs i: liste.values())
			array.add(i);
		//System.out.print(" array bd :" +array.toString());
		return array;
	}
	/**
	 * Enregistre un nouveau message 
	 * <p>
	 * Attention : cette méthode n'enregistre pas le pseudo utilisé. Pour changement de pseudo, utiliser setIdPseudoLink
	 * </p>
	 * @param message
	 * @see BD#setIdPseudoLink(String, long)
	 */
	public void addData(Message message) {
		try {
			PreparedStatement stmt;
			String sql = "INSERT INTO message VALUES (?, ?, ?, ?, ?);"; 
			stmt = c.prepareStatement(sql);
			stmt.setInt(1, message.getEmetteur().getId()); 
			stmt.setInt(2, message.getDestinataire().getId());
			stmt.setNString(3, message.getDateToString());
			stmt.setNString(4, message.getType().toString());
			//stmt.setNString(5, message.getData());
			Blob b=c.createBlob();
			b.setBytes(1, message.getData());
			stmt.setBlob(5,b);
			stmt.executeUpdate();
			//System.out.println("ADD message");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Enregistre un nouveau message de type FILE
	 * @param message
	 * 
	 */
	public void addFile(Message message) {
		try {
			PreparedStatement stmt;
			String sql = "INSERT INTO message VALUES (?, ?, ?, ?, ?);"; 
			stmt = c.prepareStatement(sql);
			stmt.setInt(1, message.getEmetteur().getId()); 
			stmt.setInt(2, message.getDestinataire().getId());
			stmt.setNString(3, message.getDateToString());
			stmt.setNString(4, message.getType().toString());
			//stmt.setNString(5, message.getData());
			Blob b=c.createBlob();
			b.setBytes(1, message.getNameFile().getBytes());
			stmt.setBlob(5,b);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
}
	/*/** 
	 * Donne accès à l'emplacement de tout les fichiers téléchargés 
	 * @return chemin de téléchargement
	public File getDownloadPath() {
		String downloadPath = "";
		try {
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM preferences");
			rs.next();
			downloadPath = rs.getString("downloadPath");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(downloadPath+"\n");
		return new File(downloadPath); //def = "."
	}
	/**
	 * Affecte un nouveau chemin de téléchargement
	 * @param file
	public void setDownloadPath(File file) {
		try {
			String sql = "UPDATE preferences SET downloadPath='"+file.getPath()+"'";
			Statement s = c.createStatement();
			s.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//pour tests
	public void printMessage() {
		try {
			PreparedStatement stmt;
			String sql = "SELECT * FROM message";
			stmt = c.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String date = rs.getString("sentDate");
				String typ = rs.getString("type");
				java.sql.Blob ablob = rs.getBlob("texte");
				long emet = rs.getLong("idEmet");
				long dest = rs.getLong("idDest");
				String texte = new String(ablob.getBytes(1l, (int) ablob.length()));
				System.out.println("PRINT message "+Long.toString(emet) +"->"+Long.toString(dest)+" "+texte+" ("+ date+") ["+ typ+"]");
			}
			rs.close();
			stmt.close();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public void printIdentification() {
		try {
			PreparedStatement stmt;
			String sql = "SELECT * FROM identification";
			stmt = c.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String pseudo = rs.getString("pseudo");
				long idUtilisateur = rs.getLong("idUtilisateur");
				System.out.println("PRINT identification "+Long.toString(idUtilisateur) +" : "+pseudo);
			}
			rs.close();
			stmt.close();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
*/
