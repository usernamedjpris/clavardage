import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Base64;

import javax.swing.JOptionPane;

import com.clava.serializable.Message;

public class ClientHTTP implements Runnable {
	private String ipServer;
	private int portServer;
	private HttpClient client;
	private Message message;
	private PropertyChangeSupport support;
    public ClientHTTP(String ipServer, int portServer) {
		this.ipServer = ipServer;
		this.portServer = portServer;
		client = HttpClient.newBuilder()
			      .version(Version.HTTP_2)
			      .followRedirects(Redirect.NORMAL)
			      .build();
		support = new PropertyChangeSupport(this);
		
	}
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

	public void sendMessage (Message m) { //String data, Personne dest, Personne emmet //https://www.baeldung.com/java-http-request
		///TODO refactoring
		//encodage inutile (cf reponse au retour non encode, taille de l'envoi àvoir si utile ou pas)
		message=m;
		//en bloquant si deconnexion (laisse le temps d'envoyer le message avant de kill
		if(message.getType()!=Message.Type.DECONNECTION) {
		Thread tu = new Thread(this);
        tu.start();
		}else
			run();

	}

	@Override
	public void run() {
		HttpRequest request;
		try {
		byte[] m= Message.serialize(message);
		/*
		ByteArrayOutputStream m2 =new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(m2);
		int len = m.length;
		dos.writeInt(len);
		if (len > 0) {
		    dos.write(m, 0, len);
		    dos.flush();
		}
		
		
		byte[] encodedBytes = Base64.getEncoder().encode(m2.toByteArray());*/
		request = HttpRequest.newBuilder()
			      .uri(URI.create("http://"+ipServer+":"+portServer+"/test/clavardeur"))
			      .timeout(Duration.ofMinutes(1))
			      .header("Content-Type", "application/octet-stream")
			      .POST(BodyPublishers.ofByteArray(m))
			      .build();
		client.send(request, BodyHandlers.ofByteArray());/*
		 HttpResponse<byte[]> response  = client.send(request, BodyHandlers.ofByteArray());
		 Message rep=Message.deserialize(response.body());
		 support.firePropertyChange("message","", rep);
		 System.out.println("\n" +rep.getType()+" \n contenu "+rep.toHtml());*/
			} catch (InterruptedException e) {
				e.printStackTrace();
			/*} catch (ClassNotFoundException e) {
				e.printStackTrace();*/
			} catch (IllegalArgumentException e) {
				JOptionPane.showMessageDialog(null, " La combinaison adresse IP/port fournie pour le serveur public dans config.ini n'est pas au format correct,"+
						" vérifiez votre saisie ", "Web Server", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}catch (ConnectException e) {
				JOptionPane.showMessageDialog(null, " La combinaison adresse IP/port fournie pour le serveur public dans config.ini n'est pas joignable,"+
						" vérifiez votre saisie", "Web Server", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}

}


/*System.out.println(URLEncoder.encode("body", "UTF-8")+"="+Base64.getEncoder().encodeToString(Message.serialize(m)));

HttpURLConnection con = (HttpURLConnection) this.urlServeur.openConnection();
con.setRequestMethod("GET");


con.setDoOutput(true);
DataOutputStream out = new DataOutputStream(con.getOutputStream());
out.writeBytes(URLEncoder.encode("body", "UTF-8")+"="+URLEncoder.encode(Base64.getEncoder().encodeToString(Message.serialize(m)), "UTF-8"));

out.flush();
out.close();

BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer content = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    content.append(inputLine);
	System.out.println("READING HTTP RESPONSE : "+inputLine);
}
in.close();
con.disconnect();
*/