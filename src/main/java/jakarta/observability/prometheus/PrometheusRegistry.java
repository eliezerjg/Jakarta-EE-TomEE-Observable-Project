package jakarta.observability.prometheus;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.core.instrument.binder.system.DiskSpaceMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.lang.management.ManagementFactory;

public class PrometheusRegistry {

    private PrometheusMeterRegistry prometheusMeterRegistry;
    private Gauge uptimeGauge;
    private Gauge starttimeGauge;

    private static PrometheusRegistry instance;

    public static PrometheusRegistry getInstance(){
        if(instance == null){
            instance = new PrometheusRegistry();
        }

        return instance;
    }

    @SuppressWarnings("resource")
    private PrometheusRegistry() {
        prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        new ClassLoaderMetrics().bindTo(prometheusMeterRegistry);
        new ProcessorMetrics().bindTo(prometheusMeterRegistry);
        new JvmThreadMetrics().bindTo(prometheusMeterRegistry);
        new JvmCompilationMetrics().bindTo(prometheusMeterRegistry);
        new JvmMemoryMetrics().bindTo(prometheusMeterRegistry);
        new JvmGcMetrics().bindTo(prometheusMeterRegistry);
        new JvmHeapPressureMetrics().bindTo(prometheusMeterRegistry);
        new JvmInfoMetrics().bindTo(prometheusMeterRegistry);
        new JvmMemoryMetrics().bindTo(prometheusMeterRegistry);
        new DiskSpaceMetrics(new File(SystemUtils.IS_OS_LINUX ? "/" : "C://")).bindTo(prometheusMeterRegistry);

        uptimeGauge = Gauge.builder("jakarta_watcher_process_uptime_seconds", this, PrometheusRegistry::calculateUptime)
                .description("Process uptime in seconds")
                .register(prometheusMeterRegistry);

        starttimeGauge = Gauge.builder("jakarta_watcher_process_start_time_seconds", this, PrometheusRegistry::calculateStartTime)
                .description("Process start time in seconds")
                .register(prometheusMeterRegistry);
    }

    public PrometheusMeterRegistry getPrometheusMeterRegistry() {
        return prometheusMeterRegistry;
    }

    public void setPrometheusMeterRegistry(PrometheusMeterRegistry prometheusMeterRegistry) {
        this.prometheusMeterRegistry = prometheusMeterRegistry;
    }

    public Gauge getUptimeGauge() {
        return uptimeGauge;
    }

    public Gauge getStarttimeGauge() {
        return starttimeGauge;
    }

    private static double calculateUptime(PrometheusRegistry registry) {
        long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        long uptimeMillis = System.currentTimeMillis() - startTime;
        return uptimeMillis / 1000.0;
    }

    private static double calculateStartTime(PrometheusRegistry registry) {
        long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        return startTime / 1000.0;
    }
}
