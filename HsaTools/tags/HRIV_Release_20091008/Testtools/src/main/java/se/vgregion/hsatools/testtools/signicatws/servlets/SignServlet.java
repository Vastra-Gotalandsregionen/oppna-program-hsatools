package se.vgregion.hsatools.testtools.signicatws.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.vgregion.hsatools.testtools.services.Service;

/**
 * Servlet implementation class SignServlet.
 */
public class SignServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String ssn = request.getParameter("ssn");
    String documentArtifact = request.getParameter("documentArtifact");
    String target = request.getParameter("target");
    Service.addSignature(documentArtifact, ssn);
    response.sendRedirect(target);
  }
}
