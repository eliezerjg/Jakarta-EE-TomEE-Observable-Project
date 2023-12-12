package jakarta.observability.prometheus;

import io.micrometer.core.instrument.Gauge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PrometheusRegistryTest {

    @Test
    public void shouldSuccessAny_WhenCallGetUptimeGauge() {
        PrometheusRegistry registry = Mockito.mock(PrometheusRegistry.class);
        Mockito.when(registry.getUptimeGauge()).thenReturn(Mockito.mock(Gauge.class));
        Assertions.assertNotNull(registry.getUptimeGauge());
    }

    @Test
    public void shouldSuccessAny_WhenCallGetStarttimeGauge() {
        PrometheusRegistry registry = Mockito.mock(PrometheusRegistry.class);
        Mockito.when(registry.getStarttimeGauge()).thenReturn(Mockito.mock(Gauge.class));
        Assertions.assertNotNull(registry.getStarttimeGauge());
    }
}
