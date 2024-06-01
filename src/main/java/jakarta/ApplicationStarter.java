package jakarta;

import jakarta.annotation.PostConstruct;
import org.apache.openejb.testing.Application;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.ContainerProperties;
import org.apache.openejb.testing.RandomPort;
import org.apache.openejb.testng.PropertiesBuilder;
import org.apache.tomee.embedded.TomEEEmbeddedApplicationRunner;

import java.net.URL;
import java.util.Properties;

@Application
@Classes(context = "jakarta")
@ContainerProperties(@ContainerProperties.Property(name = "t", value = "set"))
public class ApplicationStarter {
    @RandomPort("http")
    private int port;

    @RandomPort("http")
    private URL base;

    @org.apache.openejb.testing.Configuration
    public Properties add() {
        return new PropertiesBuilder().p("programmatic", "property").build();
    }

    @PostConstruct
    public void appStarted() {

    }

    public static void main(String[] args) {
        TomEEEmbeddedApplicationRunner.run(ApplicationStarter.class);
    }
}