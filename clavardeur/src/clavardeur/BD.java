package clavardeur;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BD {
	static BD instance=null;
	String mp="";
	String url="jdbc:mysql://mysql-dataonly.alwaysdata.net/dataonly_test";
	String login="dataonly";
	java.sql.Connection c=null;
	java.sql.Statement s=null;

	@SuppressWarnings("static-access")
	public static BD getInstance() {
		return instance!=null?instance:(instance=new BD()).getInstance();
	}
	public void connexion() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
			c=DriverManager.getConnection(url,login, mp);
			s=c.createStatement();
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
	public ResultSet getAllScoresAndNames() throws SQLException {
			s=c.createStatement();
			//naturellement les plus vieux sont priorisés sur les plus jeunes
			ResultSet aux= s.executeQuery("SELECT idName,idScore,idTime FROM score ORDER BY idScore DESC LIMIT 0,20");
			return aux;
			
	}
	
	public void setScore(String name,int score) throws SQLException {
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
	}
    public void finalize() //dst //au cas où //NB: pas de garantie d'appel
	     {
	          this.deconnexion();
	     }
	private BD() {
		this.connexion();
	}

}
