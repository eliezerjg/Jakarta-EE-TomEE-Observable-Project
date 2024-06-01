## In First Place
Coffee is good and i like it, if i helped you by some way:<br/><br/>
<a href="https://buymeacoffee.com/eliezerjg" target="_blank"><img src="https://img.shields.io/badge/-buy_me_a%C2%A0coffee-gray?logo=buy-me-a-coffee" /></a>


# Jakarta EE Webservlet With Observability - Tomcat 10

A Boilerplate project for TomcatEE (Never Version of tomcat With CDI) Webservlet Project using JSP Pages and observability
system for Prometheus / Grafana Observability System, use Grafana in Jakarta EE like Spring !

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

## Setup Instructions

1. Clone the repository: `git clone https://github.com/eliezerjg/Jakarta-EE-Tomcat-10-Observable-Project.git`.
2. Open the project in your preferred IDE.
3. Configure Tomcat 10 in your IDE and set it as the deployment target.
4. Build and run the project.

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
