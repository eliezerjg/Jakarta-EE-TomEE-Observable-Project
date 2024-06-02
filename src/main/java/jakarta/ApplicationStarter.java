package jakarta;

import jakarta.annotation.PostConstruct;
import org.apache.openejb.testing.Application;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testng.PropertiesBuilder;
import org.apache.tomee.embedded.TomEEEmbeddedApplicationRunner;

import java.util.Properties;

@Application
@Classes(context = "jakarta")
public class ApplicationStarter {

    private int port = 8080;

    @org.apache.openejb.testing.Configuration
    public Properties add() {
        Properties properties = new PropertiesBuilder().build();
        properties.put("path", "target/ROOT");
        return properties;
    }

    @PostConstruct
    public void appStarted() {
        System.out.println("Servidor iniciado - porta: " + port);
    }

    public static void main(String[] args) {
        TomEEEmbeddedApplicationRunner.run(ApplicationStarter.class);
    }
}