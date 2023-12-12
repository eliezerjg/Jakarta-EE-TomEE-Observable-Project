package jakarta.observability.servlets;


import java.io.IOException;
import java.io.Writer;
import io.prometheus.client.exporter.common.TextFormat;
import jakarta.observability.prometheus.PrometheusRegistry;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/metrics/prometheus")
public class PrometheusServlet extends HttpServlet {
    private final PrometheusRegistry registry = PrometheusRegistry.getInstance();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
			resp.setContentType(TextFormat.CONTENT_TYPE_004);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            Writer writer = resp.getWriter();
            writer.write(registry.getPrometheusMeterRegistry().scrape());
            writer.flush();
            writer.close();
	}

}
