package jakarta.observability.servlets;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.openapi.api.models.OpenAPIImpl;
import io.smallrye.openapi.api.models.OperationImpl;
import io.smallrye.openapi.api.models.PathItemImpl;
import io.smallrye.openapi.api.models.PathsImpl;
import io.smallrye.openapi.api.models.info.ContactImpl;
import io.smallrye.openapi.api.models.info.InfoImpl;
import io.smallrye.openapi.api.models.info.LicenseImpl;
import io.smallrye.openapi.runtime.io.Format;
import io.smallrye.openapi.runtime.io.OpenApiSerializer;
import jakarta.observability.openapi.CustomIndexView;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.jboss.jandex.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

@WebServlet(urlPatterns = "/openapi")
@RegisterForReflection
public class OpenApiEndpoint extends HttpServlet {

    public String extractFromAnnotationValue(AnnotationValue annotationValue){
        return (annotationValue + "").replaceAll(".*\\[\"(.*?)\"\\].*", "$1");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String packageBase = "jakarta.observability.servlets";
        IndexView idxView = CustomIndexView.fromPackage(packageBase, getClass().getClassLoader());

        // especificação do OPEN API (FORMATO DE GERACAO)
        OpenAPI openApi = new OpenAPIImpl();
        openApi.setPaths(new PathsImpl());

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


        // Itere sobre as classes no pacote base e processe as anotações OpenAPI
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
                    openApi.getPaths().addPathItem(path, pathItem);
                    pathItem.GET(new OperationImpl());

                    if (annotationValue != null) {
                        String httpMethod = annotationValue.asStringArray()[0];
                        switch (httpMethod) {
                            case "GET":
                                pathItem.GET(new OperationImpl());
                                break;
                            case "POST":
                                pathItem.POST(new OperationImpl());
                                break;
                            case "PUT":
                                pathItem.PUT(new OperationImpl());
                                break;
                            case "DELETE":
                                pathItem.DELETE(new OperationImpl());
                                break;
                        }
                    }

                }

            }
        }

        // Serializar o objeto OpenAPI para JSON
        String openApiJson = OpenApiSerializer.serialize(openApi, Format.JSON);

        // Configurar a resposta HTTP
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(openApiJson);
    }
}