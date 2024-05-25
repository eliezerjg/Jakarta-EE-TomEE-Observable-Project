package jakarta.observability.servlets;

import com.google.gson.Gson;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import jakarta.observability.JaegerConfig;
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

    private static final Tracer tracer;

    static {
        gson = new Gson();
        tracer = JaegerConfig.getInstance().getTracer();
        GlobalTracer.registerIfAbsent(tracer);
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

        Gson gson = new Gson();

        Span span = tracer.buildSpan("FROM EXCEPTION HANDLER - ERROR - HTTP /" + req.getMethod().toUpperCase() + " " + req.getRequestURI().toUpperCase() )
                .withTag("http.method", req.getMethod().toUpperCase() )
                .withTag("http.user-agent", req.getHeader("User-Agent").replaceAll(" ", "_"))
                .withTag("http.params", gson.toJson(req.getParameterMap()))
                .withTag("http.status_code", resp.getStatus())
                .withTag("http.origin", req.getHeader("Origin"))
                .withTag("http.x-forwarded-for", req.getHeader("X-Forwarded-For"))
                .withTag("jakarta.servlet.http.remote.addr", req.getRemoteAddr())
                .withTag("jakarta.servlet.http.url", req.getRequestURI())
                .withTag("exception.handler.message", exception.getMessage() + " "+  exception.getLocalizedMessage())

                .start();

        span.finish();
    }
}
