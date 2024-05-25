package jakarta.observability;

import io.jaegertracing.Configuration;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class JaegerConfig {
    private static JaegerConfig instance;
    private Tracer tracer;

    private JaegerConfig(){
        tracer = new Configuration("jakarta-ee-service")
                .withSampler(Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(4444))
                .withReporter(Configuration.ReporterConfiguration.fromEnv()
                        .withLogSpans(true)
                        .withSender(
                                new Configuration.SenderConfiguration()
                                        .withAgentHost("127.0.0.1")
                                        .withAgentPort(6831)
                        )
                )
                .getTracer();

        GlobalTracer.register(tracer);

    }

    public static JaegerConfig getInstance(){
        if(instance == null){
            instance = new JaegerConfig();
        }
        return instance;
    }

    public Tracer getTracer() {
        return tracer;
    }
}
