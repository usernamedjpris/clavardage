import java.io.File;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;



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
			
			String sql3 = "CREATE TABLE IF NOT EXISTS preferences (iden INTEGER NOT NULL, downloadPath VARCHAR(240) NOT NULL, PRIMARY KEY(iden));";
			stmt = c.prepareStatement(sql3);
			stmt.executeUpdate();
			
			//MYSQL more compatible : http://hsqldb.org/doc/guide/compatibility-chapt.html
			Statement s0=c.createStatement();
			s0.executeUpdate("SET DATABASE SQL SYNTAX MYS TRUE");
			Statement s=c.createStatement();
			s.executeUpdate("INSERT IGNORE INTO preferences VALUES (1,'.')");		
			
			
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
	public static BD getBD() {
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
				c.close();
				c = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void finalize() // dst //NB: pas de garantie d'appel
	{
		this.deconnexion();
	}
	/**
	 * Attribue un id à un pseudo donné
	 * @param newPseudo
	 * @param id
	 */
	public void setIdPseudoLink(String newPseudo, long id) {
		try {
			PreparedStatement stmt;
			String sql = "REPLACE INTO identification (idUtilisateur, pseudo) VALUES (?, ?);";
			stmt = c.prepareStatement(sql);
			stmt.setLong(1, id);
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
	public long getIdPersonne(String pseudo) {
		long idPersonne = 0;
		try {
			PreparedStatement stmt;
			String sql = "SELECT idUtilisateur FROM identification WHERE pseudo = ?";
			stmt = c.prepareStatement(sql);
			stmt.setNString(1, pseudo);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			idPersonne = rs.getLong("idUtilisateur");
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return idPersonne;

	}
	
	//id car les pseudos peuvent changer, voire s'inverser (le plus grave !), ne rendant pas fiable de savoir qui a envoyé à qui
	/**
	 * Retourne la conversation entière entre deux personnes données
	 * @param user (mettre soi)
	 * @param Interlocuteur
	 * @return liste de messages
	 */
	public ArrayList<Message> getHistorique(Personne user, Personne Interlocuteur) {
		ArrayList<Message> messages = new ArrayList<Message>();
				try {
			PreparedStatement stmt;
			String sql = "SELECT * FROM message WHERE ((idEmet = ? AND idDest = ?) OR (idEmet = ? AND idDest = ?)) ORDER BY sentDate;";
			stmt = c.prepareStatement(sql);
			stmt.setLong(1, user.getId());
			stmt.setLong(2, Interlocuteur.getId());
			stmt.setLong(3, Interlocuteur.getId());
			stmt.setLong(4, user.getId());
			ResultSet rs = stmt.executeQuery();
			Long idUser=user.getId();
			while (rs.next()) {
				Message mes;
				// Retrieve by column name
				long idEmet = rs.getLong("idEmet");
				java.util.Date date = Message.getStringToDate(rs.getString("sentDate")); 
				Blob btext = rs.getBlob("texte");
				byte[] data=btext.getBytes(1l, (int) btext.length());
				Message.Type typ = Message.Type.valueOf(rs.getString("type"));
				System.out.println("GET HISTORIQUE "+Long.toString(idEmet) +" "+new String(data)+" ("+ rs.getString("sentDate")+") ["+ typ+"]");
				if (idEmet == idUser) {// emetteur = moi
					mes = Message.Factory.recreateMessageFromData(typ,data, user, Interlocuteur, date);
				} else {
					mes = Message.Factory.recreateMessageFromData(typ,data,Interlocuteur,user, date);
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
	/**
	 * retourne la liste des personnes à qui on a déjà parlé une fois sauf à soi (donner son id) 
	 * <p>
	 * parlé une fois = envoyé au moins eu un message default ou file à destination de cette personne
	 * </p>
	 * @param id 
	 * @return
	 */
	public ArrayList<Personne> getPseudoTalked(long id) {
		ArrayList<Personne> list = new ArrayList<Personne>();
		try {
			PreparedStatement stmt;
			String sql = "SELECT pseudo, idUtilisateur FROM identification JOIN (SELECT DISTINCT idDest FROM message WHERE type = 'DEFAULT' OR type = 'FILE') ON idDest = idUtilisateur WHERE idUtilisateur != ?";
			stmt = c.prepareStatement(sql);
			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String pseudo = rs.getString("pseudo");
				Long idF = rs.getLong("idUtilisateur");
				list.add(new Personne(null,-1,pseudo,false,idF));
			}
			rs.close();
			stmt.close();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
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
			stmt.setLong(1, message.getEmetteur().getId()); 
			stmt.setLong(2, message.getDestinataire().getId());
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
			stmt.setLong(1, message.getEmetteur().getId()); 
			stmt.setLong(2, message.getDestinataire().getId());
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
