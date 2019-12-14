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
	public ResultSet getAllScoresAndNames() throws SQLException {
			s=c.createStatement();
			//naturellement les plus vieux sont priorisés sur les plus jeunes
			ResultSet aux= s.executeQuery("SELECT idName,idScore,idTime FROM score ORDER BY idScore DESC LIMIT 0,20");

      /*STEP 5: Extract data from result set
      while(rs.next()){
         //Retrieve by column name
         int id  = rs.getInt("id");
         int age = rs.getInt("age");
         String first = rs.getString("first");
         String last = rs.getString("last");
			 */
			return aux;
			
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
	}
    public void finalize() //dst //au cas où //NB: pas de garantie d'appel
	     {
	          this.deconnexion();
	     }
	private BD() {
		try {
			this.connexion();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void delIdPseudoLink(String string) {
		// TODO Auto-generated method stub
		
	}
	public void setIdPseudoLink(String newPseudo, long id) {
		// TODO Auto-generated method stub
		
	}
	public boolean checkUnicity() {
		// TODO Auto-generated method stub
		return false;
	}
	public long getIdPersonne(String string) {
		// TODO Auto-generated method stub
		return 0;
	}
	public ArrayList<Message> getHistorique(Object idPersonne) {
		// TODO Auto-generated method stub
		return null;
	}
	public void getPseudoTalked(long id) {
		// TODO Auto-generated method stub
		
	}
	public void addData(Message message, long idPersonne) {
		// TODO Auto-generated method stub
		
	}


}
