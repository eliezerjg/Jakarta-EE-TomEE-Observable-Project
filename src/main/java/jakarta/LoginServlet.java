package jakarta;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/loginteste")
public class LoginServlet extends HttpServlet {

    private final String TEMPLATE = "/WEB-INF/jsp/base/login.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(TEMPLATE)
                .forward(req, resp);
    }
}
