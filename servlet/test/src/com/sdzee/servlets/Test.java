package com.sdzee.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.clava.serializable.Message;
import com.clava.serializable.Personne;
//https://openclassrooms.com/fr/courses/626954-creez-votre-application-web-avec-java-ee/619584-la-servlet
//add  <Context docBase="test" path="/test" reloadable="true" source="org.eclipse.jst.jee.server:test"/></Host>
//to server.xml
public class Test extends HttpServlet {
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10 ko

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	   // String description = request.getParameter("description"); // Retrieves <input type="text" name="description">
	    //Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
	   // String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
	    try {
	   InputStream fileContent = request.getInputStream();
	   
	   byte[] decodedBytes = Base64.getDecoder().decode(fileContent.readAllBytes());
	   ByteArrayInputStream m3 =new ByteArrayInputStream(decodedBytes);
	   DataInputStream dis = new DataInputStream(m3);
       int len = dis.readInt();
       byte[] data = new byte[len];
       if (len > 0) {
           dis.read(data, 0,len);
       }
	    System.out.print(data);
			Message m= Message.deserialize(data);
		
	    // ... (do your job here)
	    
	   // System.out.print(m.getType()); 
	    
	    Message r=Message.Factory.sendText("hello nice !".getBytes(),new Personne(null, 0, null, false, 0) ,
	    		m.getEmetteur());
	    
	    byte[] rep=Message.serialize(r);
	    
	    response.reset();
		response.setBufferSize( DEFAULT_BUFFER_SIZE );
		String  type = "application/octet-stream";
		response.setContentType( type );
		response.setHeader( "Content-Length", ""+rep.length );
		BufferedOutputStream sortie = null;
		try {
		    /* Ouvre les flux */
		   // entree = new BufferedInputStream( new FileInputStream( fichier ), TAILLE_TAMPON );
		    sortie = new BufferedOutputStream( response.getOutputStream(), DEFAULT_BUFFER_SIZE );
		    sortie.write( rep, 0, rep.length );
		} finally {
		    try {
		        sortie.close();
		    } catch ( IOException ignore ) {
		    }
		}
	    } catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	   
	}
	/*
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
		response.setContentType("text/html");
		response.setCharacterEncoding( "UTF-8" );
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta charset=\"utf-8\" />");
		out.println("<title>Test</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<p>Ceci est une page générée depuis une servlet.</p>");
		out.println("</body>");
		out.println("</html>");
		
		//traitement de la demande
		if(false) {
			// Si non, alors on envoie une erreur 404, qui signifie que la ressource demandée n'existe pas 
		    response.sendError(HttpServletResponse.SC_NOT_FOUND);
		    return;
		}
		// Initialise la réponse HTTP 
		response.reset();
		response.setBufferSize( DEFAULT_BUFFER_SIZE );
		String  type = "application/octet-stream";
		response.setContentType( type );
		String data=" hello jolie fille !";
		response.setHeader( "Content-Length", ""+data.length() );
		BufferedOutputStream sortie = null;
		try {
		   // entree = new BufferedInputStream( new FileInputStream( fichier ), TAILLE_TAMPON );
		    sortie = new BufferedOutputStream( response.getOutputStream(), DEFAULT_BUFFER_SIZE );
		    sortie.write( data.getBytes(), 0, data.length() );
		} finally {
		    try {
		        sortie.close();
		    } catch ( IOException ignore ) {
		    }
		}
	}
	*/
}