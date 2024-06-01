package jakarta.observability.openapi;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.io.IOException;

@WebServlet(urlPatterns = "/openapi", name = "Open Api Endpoint")
@Tag(name = "Open API", description = "descricao da api")
public class OpenApiEndpoint extends HttpServlet {
    private static final OpenApiDocumentationService service;

    static {
        service = new OpenApiDocumentationService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType(MediaType.APPLICATION_JSON);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(service.generateDocumentation("jakarta.observability.servlets", "jakarta.observability.openapi"));
    }
}