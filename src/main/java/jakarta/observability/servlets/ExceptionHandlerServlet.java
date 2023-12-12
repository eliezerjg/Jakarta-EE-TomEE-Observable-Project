package jakarta.observability.servlets;

import com.google.gson.Gson;
import jakarta.observability.dto.DefaultErrorDTO;
import jakarta.observability.exceptions.MXBeanNotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;

import static jakarta.servlet.RequestDispatcher.*;

@WebServlet(urlPatterns = "/exceptionHandlerServlet")
public class ExceptionHandlerServlet extends HttpServlet {
    private static final Gson gson;

    static {
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType(MediaType.APPLICATION_JSON_PATCH_JSON);

        Exception exception = (Exception) req.getAttribute(ERROR_EXCEPTION);
        DefaultErrorDTO errorDTO = new DefaultErrorDTO();
        errorDTO.setCustomMessage(exception.getMessage());
        errorDTO.setTitle("Error");

        if (exception instanceof MXBeanNotFoundException mxBeanNotFoundException) {
            errorDTO.setHttpStatusCode(mxBeanNotFoundException.httpStatusCode);
            resp.setStatus(mxBeanNotFoundException.httpStatusCode);
        }else{
            errorDTO.setHttpStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        String jsonResponse = gson.toJson(errorDTO);
        resp.getWriter().write(jsonResponse);
    }
}
