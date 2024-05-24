package jakarta.observability.filters;

import com.google.gson.Gson;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import jakarta.observability.JaegerConfig;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = "/*" , filterName = "Jaeger Tracing")
public class TracingFilter implements Filter {

    private Tracer tracer;

    @Override
    public void init(FilterConfig filterConfig) {
        tracer = JaegerConfig.getInstance().getTracer();
        GlobalTracer.registerIfAbsent(tracer);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        Gson gson = new Gson();

        Span span = tracer.buildSpan("HTTP /" + req.getMethod().toUpperCase() + " " + req.getRequestURI().toUpperCase() + " " + req.getQueryString())
                .withTag("http.method", req.getMethod().toUpperCase() )
                .withTag("http.user-agent", req.getHeader("User-Agent").replaceAll(" ", "_"))
                .withTag("http.params", gson.toJson(req.getParameterMap()))
                .withTag("http.status_code", ((HttpServletResponse) response).getStatus())
                .withTag("http.x-forwarded-for", req.getHeader("X-Forwarded-For"))
                .withTag("jakarta.servlet.http.remote.addr", req.getRemoteAddr())
                .withTag("jakarta.servlet.http.url", req.getRequestURI())

                .start();

        try {
            chain.doFilter(request, response);
        } finally {
            span.finish();
        }
    }

    @Override
    public void destroy() {
    }
}
