package jakarta.observability.openapi;

import com.google.gson.Gson;
import io.smallrye.openapi.api.models.ComponentsImpl;
import io.smallrye.openapi.api.models.PathItemImpl;
import io.smallrye.openapi.api.models.PathsImpl;
import io.smallrye.openapi.api.models.examples.ExampleImpl;
import io.smallrye.openapi.api.models.info.ContactImpl;
import io.smallrye.openapi.api.models.info.InfoImpl;
import io.smallrye.openapi.api.models.info.LicenseImpl;
import io.smallrye.openapi.runtime.io.Format;
import io.smallrye.openapi.runtime.io.OpenApiSerializer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.models.Operation;
import org.jboss.jandex.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static jakarta.observability.openapi.OpenApiUtils.extractFromAnnotationValue;
import static jakarta.observability.openapi.OpenApiUtils.getOperationFromAnnotations;

@WebServlet(urlPatterns = "/openapi", name = "Open Api Endpoint")
@Tag(name = "nome da api", description = "descricao da api")
public class OpenApiEndpoint extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String packageBase = "jakarta.observability.servlets";
        IndexView idxView = CustomIndexView.fromPackage(packageBase, getClass().getClassLoader());

        SwaggerOpenAPImpl openApi = new SwaggerOpenAPImpl("2.0");
        openApi.setPaths(new PathsImpl());

        // to-do: read this from application properties
        openApi.setInfo(new InfoImpl()
                .title("API de Observabilidade")
                .version("1.0.0")
                .description("Documentação da API de Observabilidade")
                .contact(new ContactImpl()
                        .name("Support")
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

                    String originalMethodName = methodInfo.name().replaceFirst(".*/", "");

                    // should replace known words for init and http methods
                    String[] reservedNames = new String[]{"doGet", "doPost", "doDelete", "doTrace", "doPut", "doHead", "doPatch", "<init>", "<clinit>"};

                    String finalMethodName = originalMethodName;
                    for (String reservedName : reservedNames) {
                        finalMethodName = finalMethodName.replaceAll(reservedName, "");
                    }

                    String finalPath = path + "/" + finalMethodName;
                    if (openApi.getPaths().hasPathItem(finalPath)) {
                        finalPath = path + "/" + finalMethodName;

                        while (openApi.getPaths().hasPathItem(finalPath)) {
                            finalPath += ".";
                        }
                    }
                    openApi.getPaths().addPathItem(finalPath, pathItem);





                    Operation operation = getOperationFromAnnotations(methodInfo);
                    operation.addTag(classInfo.simpleName());

                    // To-do: aqui tem que ir o scan eo metodo com reflection por getParameter e setAttribute
                    //operation.setRequestBody(new RequestBodyImpl().content(new ContentImpl()));

                    switch (originalMethodName){
                        case "doGet":
                            pathItem.setGET(operation);
                            break;
                        case "doPost":
                            pathItem.setPOST(operation);
                            break;
                        case "doPut":
                            pathItem.setPUT(operation);
                            break;
                        case "doDelete":
                            pathItem.setDELETE(operation);
                            break;
                        case "doHead":
                            pathItem.setHEAD(operation);
                            break;
                        case "doOptions":
                            pathItem.setOPTIONS(operation);
                            break;
                        case "doPatch":
                            pathItem.setPATCH(operation);
                            break;
                        case "doTrace":
                            pathItem.setTRACE(operation);
                            break;
                    }



                }

            }
        }

        String openApiYaml = new Gson().toJson(openApi);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(openApiYaml);
    }
}