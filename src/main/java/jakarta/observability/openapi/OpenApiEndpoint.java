package jakarta.observability.openapi;

import com.google.gson.Gson;
import io.smallrye.openapi.api.models.PathItemImpl;
import io.smallrye.openapi.api.models.PathsImpl;
import io.smallrye.openapi.api.models.info.ContactImpl;
import io.smallrye.openapi.api.models.info.InfoImpl;
import io.smallrye.openapi.api.models.info.LicenseImpl;
import io.smallrye.openapi.api.models.media.ContentImpl;
import io.smallrye.openapi.api.models.parameters.ParameterImpl;
import io.smallrye.openapi.api.models.parameters.RequestBodyImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
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
                .description("Documentação da API de Observabilidade Operações nativas CRUD doGet, doPost e etc estão nos nomes dos paths favor ignorar Os retornos dos metodos e das annotations estão nas responses As requests são mapeadas apartir do getParameter e as saidas o setAttribute.")
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

                    // ignore super methods for init
                    if(originalMethodName.contains("init")){
                        continue;
                    }

                    openApi.getPaths().addPathItem(path + "/" + originalMethodName, pathItem);
                    Operation operation = getOperationFromAnnotations(methodInfo);



                    // ****** DO IMPLEMENTATION BY REFLECTION HERE  FOR PARAMETERS ****
                    // se for por parameters
                    OpenApiParameter param = new OpenApiParameter("string");
                    param.setName("name");
                    param.setIn(OpenApiParameter.In.QUERY);

                    operation.setParameters(List.of(param));

                    // se for por request body
                    //operation.setRequestBody();

                    // ****** DO IMPLEMENTATION BY REFLECTION HERE  FOR PARAMETERS ****

                    operation.addTag(classInfo.simpleName());

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
                        default:
                            pathItem.setGET(operation);
                            break;
                    }

                }

            }
        }

        String openApiJson = new Gson().toJson(openApi);

        // fixing types in Uppercase (POO PROBLEM)
        String[] typesInUppercase = new String[] { "QUERY", "BODY", "PATH", "HEADER", "FORMDATA"};
        for(String type : typesInUppercase){
            openApiJson = openApiJson.replaceAll(type, type.toLowerCase());
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(openApiJson);
    }
}