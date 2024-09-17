## In First Place
Coffee is good and i like it, if i helped you by some way:<br/><br/>
<a href="https://buymeacoffee.com/eliezerjg" target="_blank"><img src="https://img.shields.io/badge/-buy_me_a%C2%A0coffee-gray?logo=buy-me-a-coffee" /></a>

# Understanding the History of Java EE 
<p>
    Originally conceived as J2EE, Java Web technologies have evolved significantly since their inception, 
    with many features being added over time. Large projects have been developed and maintained by major companies, 
    including Struts, JBoss, Payara, Jersey, WebSphere, Open Liberty, and others. 
    These projects were created to simplify and strengthen the implementation of REST and Web Services patterns. 
    As these patterns evolved, new features like CDI, annotations such as @Path, and useful EJB components (e.g., @Singleton, @Inject) 
    were introduced. For these reasons, the best options for maintaining projects using JAX-RS or JAX-WS today are TomEE, Spring, or Quarkus,
    as they offer robust support for EJB and CDI features.
</p>



# Jakarta EE Webservlet With Observability - TomEE

A Boilerplate project for TomEE (Never `Version` of tomcat With CDI) Webservlet Project using JSP Pages and observability
system for Prometheus / Grafana Observability System, use Grafana in Jakarta EE like Spring ! (read more in tomitribe)


# Java Melody - Follow, Track and Measure Http and Java Memory Life Cycle
+ Go to http://localhost:8080/monitoring (admin:admin)

# Open API Implementation for Jakarta EE WebServlets
+ In progress implementation for Jakarta EE, see in http://localhost:8080/openapi


# Jaeger Tracing and Tracing Metrics 

+ New feature to collect HTTP metrics 

**Libraries used**

- Micrometer Metrics
- Prometheus
- Gson
- Apache Commons
- Jakarta Apache tomEE dependencies
- Spring ThreadDumpFormatter
- FernFlower Decompiler
- Eclipse microprofile
- Smallrye Open Api Core
- Java Melody

## Available Metrics

```
  HttpRequestMetrics
  ClassLoaderMetrics
  ProcessorMetrics
  JvmThreadMetrics
  JvmCompilationMetrics
  JvmMemoryMetrics
  JvmGcMetric
  JvmHeapPressureMetrics
  JvmInfoMetrics
  JvmMemoryMetrics
  DiskSpaceMetrics
```

## Available Dumps

```
  Thread Dump
  Heap Dump
```

## Error Handling

- Exception Handler
- MXBeanNotFoundException
- DefaultErrorDTO

## Project Structure

- `src/main/java`: Java source files for servlets and other backend logic.
- `src/main/webapp`: JSP pages and web-related resources.
- `src/test`: Test classes and resources.
- `src/main/resources/org.apache.tomee.configs`: Tomcat / TomEE Web Server Configs.

## Setup Instructions

1. Clone the repository
2. Open the project in your preferred IDE.
3. Run the Application Bootstrap.

## Observability Configuration

This project includes integrations for Prometheus and Grafana to provide observability. Follow these steps to set up the
observability system:

1. Install and configure Prometheus on your server.
2. Set up Grafana and connect it to Prometheus.
3. Import the provided dashboards for monitoring your application.

## Contribution Guidelines

Feel free to contribute by opening issues or submitting pull requests. Your feedback and contributions are highly
appreciated!

## License

This project is licensed under the [MIT License](MIT-LICENSE).
