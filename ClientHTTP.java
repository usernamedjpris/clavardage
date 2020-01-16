
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

public class ClientHTTP {
	InetAddress ipServer;
	int portServer;
	URL urlServeur;
		
    public ClientHTTP(InetAddress ipServer, int portServer) throws MalformedURLException {
		this.ipServer = ipServer;
		this.portServer = portServer;
		this.urlServeur = new URL("http://localhost:8080/test/bonjour");
	}

	public void sendMessage (Message m) throws IOException{ //String data, Personne dest, Personne emmet //https://www.baeldung.com/java-http-request
		System.out.println(URLEncoder.encode("body", "UTF-8")+"="+Base64.getEncoder().encodeToString(Message.serialize(m)));

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
		

		
		
		/*//connexion à la classe gestion servlet java 11
    	HttpClient clientHTTP = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .proxy(ProxySelector.of(new InetSocketAddress(this.ipServer, this.portServer)))
                .build();
    	
	    HttpRequest request = HttpRequest.newBuilder()
		        .uri(URI.create("http://localhost:8080/test"))
		        .POST(BodyPublishers.ofByteArray(Message.serialize(m)))
		        .build();

		HttpResponse<String> response;
	    try {
	      response = clientHTTP.send(request, BodyHandlers.ofString()); 
	      System.out.println("Status  : " + response.statusCode());
	      System.out.println("Headers : " + response.headers());
	      System.out.println("Body    : " + response.body());
	    } catch (IOException | InterruptedException e) {
	      e.printStackTrace();
	    }*/
	}

}
