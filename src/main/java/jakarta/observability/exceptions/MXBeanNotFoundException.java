package jakarta.observability.exceptions;

public class MXBeanNotFoundException extends RuntimeException{

    public String customMessage;

    public MXBeanNotFoundException(String message) {
        super();
        this.customMessage = message;
    }
}
