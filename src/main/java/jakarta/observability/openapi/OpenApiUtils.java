package jakarta.observability.openapi;

import com.google.gson.Gson;
import io.smallrye.openapi.api.models.OperationImpl;
import io.smallrye.openapi.api.models.responses.APIResponseImpl;
import io.smallrye.openapi.api.models.responses.APIResponsesImpl;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.MethodInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class OpenApiUtils {
    public static String extractFromAnnotationValue(AnnotationValue annotationValue){
        return (annotationValue + "").replaceAll(".*\\[\"(.*?)\"\\].*", "$1");
    }

    public static Operation getOperationFromAnnotations(MethodInfo methodInfo) {
        OperationImpl operation = new OperationImpl();
        List<AnnotationInstance> annotations = methodInfo.annotations();

        for (AnnotationInstance annotation : annotations) {
            if (annotation.name().toString().toLowerCase().contains("tag")) {

                AnnotationValue name = annotation.value("name");
                if (name != null) {
                    List<String> tags = new ArrayList<String>();

                    if(operation.getTags() != null){
                        tags.addAll(operation.getTags());
                    }

                    tags.add(name.asString());
                    operation.setTags(tags);
                }

                AnnotationValue description = annotation.value("description");
                if (description != null) {
                    List<String> tags = new ArrayList<String>();

                    if(operation.getTags() != null){
                        tags.addAll(operation.getTags());
                    }
                    tags.add(description.asString());
                    operation.setTags(tags);
                }
            }
            if (annotation.name().toString().toLowerCase().contains("operation")) {

                AnnotationValue operationId = annotation.value("operationId");
                if (operationId != null) {
                    operation.setSummary(operationId.asString());
                }

                AnnotationValue summary = annotation.value("summary");
                if (summary != null) {
                    operation.setSummary(summary.asString());
                }

                AnnotationValue description = annotation.value("description");
                if (description != null) {
                    operation.setDescription(description.asString());
                }
            }

            if (annotation.name().toString().toLowerCase().contains("apiresponses")) {
                AnnotationValue responses = annotation.value();
                if (responses != null) {
                    APIResponses apiResponses = new APIResponsesImpl();
                    for (AnnotationInstance response : responses.asNestedArray()) {
                        OpenApiResponse apiResponse = new OpenApiResponse();
                        AnnotationValue responseCode = response.value("responseCode");
                        AnnotationValue responseDescription = response.value("description");
                        AnnotationValue content = response.value("content");
                        apiResponse.setDescription(responseDescription.asString());

                        List<AnnotationInstance> valuesFromAnnotation = Arrays.stream(content.asNestedArray()).toList();
                        AnnotationInstance valueFromParent = valuesFromAnnotation.get(0);
                        String schemaFromAnnotation = valueFromParent.value("schema").asString();
                        if (content != null) {
                            String jsonObject = null;
                            if(schemaFromAnnotation.contains("implementation")) {
                                String packageName = schemaFromAnnotation.split("=")[1].trim().replaceAll("\\ ", "").replaceAll("\\)", "");

                                try {
                                    Class<?> clazz = Class.forName(packageName);
                                    Object instance = clazz.getDeclaredConstructor().newInstance();
                                    jsonObject = new Gson().toJson(instance);
                                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                    System.out.println("Não foi possivel instanciar: " + e.getMessage());
                                }
                            }

                            jsonObject = jsonObject == null ? schemaFromAnnotation : jsonObject;

                            apiResponse.setSchema("example", "method return Type: " + methodInfo.returnType() +  " ***  DTO FROM SCHEMA ANNOTATION *** -> " + jsonObject);
                        }



                        apiResponses.addAPIResponse(responseCode.asString(), apiResponse);

                    }
                    operation.setResponses(apiResponses);
                }
            }
        }

        if(operation.getResponses() == null) {
            APIResponse apiResponse = new APIResponseImpl();
            apiResponse.setDescription("Não documentado");
            operation.setResponses(new APIResponsesImpl().addAPIResponse("200", apiResponse) );
        }

        return operation;
    }
}
