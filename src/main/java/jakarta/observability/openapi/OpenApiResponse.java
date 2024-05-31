package jakarta.observability.openapi;

import io.smallrye.openapi.api.models.responses.APIResponseImpl;

import java.util.HashMap;
import java.util.Map;

public class OpenApiResponse extends APIResponseImpl {
    Map<String, Object> schema = new HashMap<>();

    public Map<String, Object> getSchemas() {
        return schema;
    }

    public void setSchema(String key, Object value) {
        this.schema.put(key, value);
    }
}
