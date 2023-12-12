package jakarta.observability.exceptions;

import jakarta.servlet.http.HttpServletResponse;

public class MXBeanNotFoundException extends RuntimeException{

    public String customMessage;
    public int httpStatusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

    public MXBeanNotFoundException(String message) {
        super(message);
        this.customMessage = message;
    }
}
