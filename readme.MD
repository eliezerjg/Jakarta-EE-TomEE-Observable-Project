# Jakarta EE Webservlet With Observability - Tomcat 10

A Boilerplate project for Tomcat 10 Webservlet Project using JSP Pages and observability
system for Prometheus / Grafana Observability System.

**Libraries used**

- Micrometer Metrics
- Prometheus
- Gson
- Apache Commons
- Jakarta dependencies
- Spring ThreadDumpFormatter

## Project Structure

- `src/main/java`: Java source files for servlets and other backend logic.
- `src/main/webapp`: JSP pages and web-related resources.
- `src/test`: Test classes and resources.

## Setup Instructions

1. Clone the repository: `git clone <repository-url>`.
2. Open the project in your preferred IDE.
3. Configure Tomcat 10 in your IDE and set it as the deployment target.
4. Build and run the project.

## Observability Configuration

This project includes integrations for Prometheus and Grafana to provide observability. Follow these steps to set up the observability system:

1. Install and configure Prometheus on your server.
2. Set up Grafana and connect it to Prometheus.
3. Import the provided dashboards for monitoring your application.

## Contribution Guidelines

Feel free to contribute by opening issues or submitting pull requests. Your feedback and contributions are highly appreciated!

## License

This project is licensed under the [MIT License](LICENSE).
