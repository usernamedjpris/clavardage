import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/*import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;*/
//https://docs.oracle.com/javadb/10.8.3.0/getstart/rwwdactivity3.html
//rq si on veut embded la bd
public class BD {
	static BD instance=null;
	String mp="";
	String url="jdbc:mysql://localhost/clavardage_test";
	String login="root";
	Connection c=null;
	java.sql.Statement s=null;
	
	private BD() { //creation de la bdd ?
		try {
			this.connexion();
			PreparedStatement stmt;
			String sql1 = "CREATE TABLE message (pseudoEmet	VARCHAR(30),pseudoDest	VARCHAR(30), sentDate	VARCHAR(30),type	    VARCHAR(30),text    	TEXT,PRIMARY KEY(pseudoEmet,pseudoDest,sentDate));";
			stmt = c.prepareStatement(sql1);
	        stmt.executeUpdate();
	        String sql2 = "CREATE TABLE identification (idUtilisateur INTEGER,pseudo	VARCHAR(30));";	        
			stmt = c.prepareStatement(sql2);
	        stmt.executeUpdate();
	        String sql3 = "CREATE TABLE preference (downloadPath VARCHAR(30)); \r\n" + 
	        		"INSERT INTO preference VALUES ('cheminpardefaut');";	        
			stmt = c.prepareStatement(sql3);
	        stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("static-access")
	public static BD getBD() {
		return instance!=null?instance:(instance=new BD()).getBD();
	}

	public void connexion() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
			c=DriverManager.getConnection(url,login, mp);
	}
	public void deconnexion() {
		try {
			if(c!=null){
				c.close();
				c=null;
			}
			if(s!=null) {
				s.close();s=null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
    public void finalize() //dst //au cas où //NB: pas de garantie d'appel
	     {
	          this.deconnexion();
	     }

	public void delIdPseudoLink(String pseudo) {
		// TODO Auto-generated method stub
		try {
			this.connexion();
			PreparedStatement stmt;
			String sql = "DELETE FROM identification WHERE pseudo = ?;";
			stmt = c.prepareStatement(sql);
			stmt.setNString(1,pseudo);
			stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public void setIdPseudoLink(String newPseudo, long id) {
		try {
			this.connexion();
			PreparedStatement stmt;
			String sql = "INSERT INTO identification VALUES (?,?)";
			stmt = c.prepareStatement(sql);
			stmt.setLong(1,id);
			stmt.setNString(2,newPseudo);
			stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public boolean checkUnicity(String pseudo, Utilisateur user) {
		Boolean ok = false;
		long idPersonne = 0;
		try {			
			this.connexion();
			PreparedStatement stmt;
			String sql = "SELECT idUtilisateur FROM identification WHERE pseudo = ?";
			stmt = c.prepareStatement(sql);
			stmt.setNString(1,pseudo);
			ResultSet rs = stmt.executeQuery(sql);
			idPersonne = rs.getLong("idUtilisateur");
			rs.close();
			stmt.close();		   
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (idPersonne != user.getIdUtilisateur()) {
			ok=true;
		}
		return ok;
	}
	public long getIdPersonne(String pseudo) {
		long idPersonne = 0;
		try {			
			this.connexion();
			PreparedStatement stmt;
			String sql = "SELECT idUtilisateur FROM identification WHERE pseudo = ?";
			stmt = c.prepareStatement(sql);
			stmt.setNString(1,pseudo);
			ResultSet rs = stmt.executeQuery(sql);
			idPersonne = rs.getLong("idUtilisateur");
			rs.close();
			stmt.close();		   
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return idPersonne;
		
	}
	public Conversation getHistorique(Personne Interlocuteur, Utilisateur user) {
		Conversation historique = new Conversation(Interlocuteur,new ArrayList<Message>());
		try {			
			this.connexion();
			PreparedStatement stmt;
			String sql = "SELECT emet.idUtilisateur AS idEmet, pseudoEmet, text, type, dest.idUtilisateur AS idDest,pseudoDest FROM message JOIN identification AS emet ON pseudoEmet = emet.pseudo JOIN identification AS dest ON pseudoDest = dest.pseudo WHERE pseudoEmet IN(SELECT pseudo FROM identification WHERE idUtilisateur = (SELECT idUtilisateur FROM identification WHERE pseudo = ?)) OR pseudoDest IN(SELECT pseudo FROM identification WHERE idUtilisateur = (SELECT idUtilisateur FROM identification WHERE pseudo = ?)) ORDER BY sentDate;";
			stmt = c.prepareStatement(sql);
			stmt.setNString(1,Interlocuteur.getPseudo());
			stmt.setNString(2,Interlocuteur.getPseudo());
		    ResultSet rs = stmt.executeQuery(sql);
		    ArrayList<Message> messages = new ArrayList<Message>();
		    while(rs.next()){
		      Message mes;
	          //Retrieve by column name
		      long idEmet  = rs.getLong("idEmet");
	          String pseudoEmet  = rs.getString("pseudoEmet");     //ne sert pas encore...
	          String text  = rs.getString("text");
	          String typ  = rs.getString("type");
	          long idDest  = rs.getLong("idDest");				 //ne sert pas encore...
	          String pseudoDest  = rs.getString("pseudoDest");  //ne sert pas encore...
	          if (idEmet == user.getIdUtilisateur()) {//emetteur = moi
	          	  mes = new Message(text.getBytes(), user.getPersonne(), Interlocuteur);
	          	  mes.setType(Message.toType(typ));
		      } else {
		    	  mes = new Message(text.getBytes(), Interlocuteur, user.getPersonne()); 
		    	  mes.setType(Message.toType(typ));
		      }
	          messages.add(mes);
		    }
		   historique.AddMessage(messages);
	       rs.close();
	       stmt.close();
		   
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return historique;
	}
	public void getPseudoTalked(long id) { //qu'est-ce que c'est censé faire ? si c'est un "get" ça devrvait pouvoir renvoyer pas void... Non ?
		// TODO Auto-generated method stub
		
	}
	public void addData(Message message) {
		try {
			this.connexion();
			PreparedStatement stmt;
			String sql = "INSERT INTO message VALUES (?,?,?,?,?);";
			stmt = c.prepareStatement(sql);
			stmt.setNString(1,message.getEmetteur().getPseudo());
			stmt.setNString(2,message.getDestinataire().getPseudo());
			stmt.setNString(3,message.getDateToString());
			stmt.setNString(4,message.getType().toString());
			stmt.setNString(5,message.getData().toString());
			stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public File getDownloadPath() {
		String downloadPath = "";
		try {
			this.connexion();
			PreparedStatement stmt;
			String sql = "SELECT downloadPath FROM preference";
			stmt = c.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			downloadPath  = rs.getString("downloadPath");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new File(downloadPath);
		//return new File("D:\\JAVA\\JDK\\JDK");
	}
	public void setDownloadPath(File file) {
		try {
			this.connexion();
			PreparedStatement stmt;
			String sql = "UPDATE preference SET downloadPath = ?;";
			stmt = c.prepareStatement(sql);
			stmt.setNString(1,file.getAbsolutePath());
			stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}








/*
 	public ResultSet getAllScoresAndNames() throws SQLException {
			s=c.createStatement();
			//naturellement les plus vieux sont priorisés sur les plus jeunes
			ResultSet aux= s.executeQuery("SELECT idName,idScore,idTime FROM score ORDER BY idScore DESC LIMIT 0,20");

      //STEP 5: Extract data from result set
      //while(rs.next()){
         //Retrieve by column name
        // int id  = rs.getLong("id");
        // int age = rs.getLong("age");
        // String first = rs.getString("first");
        // String last = rs.getString("last");
			
		//	return aux;
			
	}
	
	public void setScore(String name,int score) throws SQLException {
		 PreparedStatement stmt = null;
		   String sql = "UPDATE Employees set age=? WHERE id=?";
		      stmt = c.prepareStatement(sql);
		      //Bind values into the parameters.
		      stmt.setInt(1, 35);
		      stmt.setInt(2, 102); 
		      int rows = stmt.executeUpdate();
		      ResultSet rs = stmt.executeQuery(sql);
		      while(rs.next()){
		          //Retrieve by column name
		          int id  = rs.getInt("id");
		          int age = rs.getInt("age");
		          String first = rs.getString("first");
		          String last = rs.getString("last");
		       }
		       //STEP 6: Clean-up environment
		       rs.close();
		       stmt.close();
		       
		s.executeUpdate("INSERT INTO `score`(`idName`, `idScore`) VALUES ('"+name+"','"+score+"')");
		//supprime le ou les (accès concurrent) dernier score
		//Limit offset,nombre de resultats
		//(SELECT * from score) pour créer une table dérivée (sinon mysql aime pas qu'on supprime en ce servant de ce qu'on supprime comme critère
		//1st :DELETE FROM score WHERE idScore<(SELECT idScore FROM (SELECT * from score) AS T ORDER BY idScore DESC LIMIT 20,1)
		s.executeUpdate("DELETE FROM `score`WHERE `PrimaryKey` NOT IN ( SELECT PrimaryKey FROM (SELECT * FROM (SELECT*FROM score) AS T2 ORDER BY idScore DESC LIMIT 20) AS T)");

	}
	public int getWorstScore() throws SQLException {
		ResultSet aux= s.executeQuery("SELECT idScore FROM score ORDER BY idScore DESC LIMIT 19,1");
		aux.next(); // nécessaire à appeler avant de pouvoir lire la query (verif que = 1 pour qu'elle soit bien passée
		return Integer.parseInt(aux.getString("idScore"));
		//System.out.print(minScore);
	}*/
