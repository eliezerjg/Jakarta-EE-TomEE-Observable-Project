package jakarta.observability.openapi;

public class SwaggerOpenAPImpl extends io.smallrye.openapi.api.models.OpenAPIImpl{
    public String swagger;

    public SwaggerOpenAPImpl(String swagger){
        super();
        this.swagger = swagger;
    }
}
