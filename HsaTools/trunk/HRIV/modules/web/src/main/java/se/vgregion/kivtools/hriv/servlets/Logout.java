package se.vgregion.kivtools.hriv.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet implementation class Logout.
 */
public class Logout extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final Log LOG = LogFactory.getLog(Logout.class);

  /**
   * @see HttpServlet#HttpServlet()
   */
  public Logout() {
    super();
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    doPost(request, response);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    request.getSession().invalidate();
    try {
      response.sendRedirect("startpage.jsp?startpage=1");
    } catch (IOException e) {
      LOG.error(e);
    }
  }
}
