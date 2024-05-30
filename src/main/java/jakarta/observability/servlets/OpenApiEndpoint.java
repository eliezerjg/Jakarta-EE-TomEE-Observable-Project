package jakarta.observability.servlets;

import io.smallrye.openapi.api.models.OpenAPIImpl;
import io.smallrye.openapi.api.models.OperationImpl;
import io.smallrye.openapi.api.models.PathItemImpl;
import io.smallrye.openapi.api.models.PathsImpl;
import io.smallrye.openapi.api.models.info.ContactImpl;
import io.smallrye.openapi.api.models.info.InfoImpl;
import io.smallrye.openapi.api.models.info.LicenseImpl;
import io.smallrye.openapi.api.models.parameters.RequestBodyImpl;
import io.smallrye.openapi.api.models.responses.APIResponseImpl;
import io.smallrye.openapi.api.models.responses.APIResponsesImpl;
import io.smallrye.openapi.api.models.servers.ServerImpl;
import io.smallrye.openapi.runtime.io.Format;
import io.smallrye.openapi.runtime.io.OpenApiSerializer;
import jakarta.observability.openapi.CustomIndexView;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.eclipse.microprofile.openapi.models.servers.Server;
import org.jboss.jandex.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@WebServlet(urlPatterns = "/openapi")
public class OpenApiEndpoint extends HttpServlet {

    public String extractFromAnnotationValue(AnnotationValue annotationValue){
        return (annotationValue + "").replaceAll(".*\\[\"(.*?)\"\\].*", "$1");
    }

    private Operation getOperationFromAnnotations(MethodInfo methodInfo) {
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
                        APIResponse apiResponse = new APIResponseImpl();
                        AnnotationValue responseCode = response.value("responseCode");
                        AnnotationValue responseDescription = response.value("description");
                        apiResponse.setDescription(responseDescription.asString());
                        apiResponses.addAPIResponse(responseCode.asString() , apiResponse);
                    }
                    operation.setResponses(apiResponses);
                }
            }
        }

        return operation;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String packageBase = "jakarta.observability.servlets";
        IndexView idxView = CustomIndexView.fromPackage(packageBase, getClass().getClassLoader());

        OpenAPI openApi = new OpenAPIImpl();
        openApi.setPaths(new PathsImpl());
        Server server = new ServerImpl();
        server.setUrl("http://localhost");
        server.setDescription("Aplicação documentada");

        openApi.setServers(List.of(server));

        openApi.setInfo(new InfoImpl()
                .title("API de Observabilidade")
                .version("1.0.0")
                .description("Documentação da API de Observabilidade")
                .contact(new ContactImpl()
                        .name("Suporte")
                        .email("suporte@exemplo.com"))
                .license(new LicenseImpl()
                        .name("MIT")
                        .url("https://opensource.org/licenses/MIT")));


        Collection<ClassInfo> classes = idxView.getKnownClasses();
        for (ClassInfo classInfo : classes) {
            String fullClassNameWithPackage = classInfo.name().toString();
            if (fullClassNameWithPackage.startsWith(packageBase)) {

                ClassInfo info = idxView.getClassByName(fullClassNameWithPackage);


                for (MethodInfo methodInfo : info.methods()) {
                    List<AnnotationInstance> annotations = info.annotations();
                    AnnotationInstance webServletAnnotation = annotations.stream().filter(annotation -> annotation.name().toString().toLowerCase().contains("webservlet")).toList().get(0);
                    AnnotationValue annotationValue = webServletAnnotation.value("urlPatterns");

                    String path = extractFromAnnotationValue(annotationValue);
                    PathItemImpl pathItem = new PathItemImpl();
                    openApi.getPaths().addPathItem(path + "/" + methodInfo.name(), pathItem);

                    Operation operation = getOperationFromAnnotations(methodInfo);

                    // To-do: aqui tem que ir o scan eo metodo com reflection por getParameter e setAttribute
                    operation.setRequestBody(new RequestBodyImpl());

                    pathItem.setGET(operation);


                }

            }
        }

        String openApiJson = OpenApiSerializer.serialize(openApi, Format.JSON);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(openApiJson);
    }
}