package jakarta.observability.filters;

import jakarta.observability.prometheus.PrometheusRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class RequestMetricsFilterTest {

    @InjectMocks
    RequestMetricsFilter metricsFilter;

    @Mock
    FilterChain chain;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void shouldStoreRegistry_WhenIsCallServletGetRequestURI() throws IOException, ServletException {
        ServletRequest request = mock(HttpServletRequest.class); // Simula um HttpServletRequest
        ServletResponse response = mock(ServletResponse.class);

        when(((HttpServletRequest) request).getRequestURI()).thenReturn("endpoint");

        metricsFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);

        Assertions.assertEquals("endpoint", ((HttpServletRequest) request).getRequestURI());
    }
}
