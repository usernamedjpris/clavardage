import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Serveur
 */
@WebServlet("/Serveur")
public class Serveur extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ArrayList<Personne> disponibilite;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Serveur() {
        super();
        disponibilite = new ArrayList<Personne>();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//response.getWriter().append("Served at: ").append(request.getContextPath());
        /*request.setAttribute("heure", "jour");
        this.getServletContext().getRequestDispatcher("/WEB-INF/bonjour.jsp").forward(request, response);*/
        /*request.setAttribute("dispo", this.disponibilite);
        this.getServletContext().getRequestDispatcher("/WEB-INF/bonjour.jsp").forward(request, response);*/
//		response.getOutputStream().write(b);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);
	}

}