package jakarta.observability.openapi;

import com.google.gson.Gson;
import io.smallrye.openapi.api.models.PathItemImpl;
import io.smallrye.openapi.api.models.PathsImpl;
import io.smallrye.openapi.api.models.info.ContactImpl;
import io.smallrye.openapi.api.models.info.InfoImpl;
import io.smallrye.openapi.api.models.info.LicenseImpl;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.jboss.jandex.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static jakarta.observability.openapi.OpenApiUtils.extractFromAnnotationValue;
import static jakarta.observability.openapi.OpenApiUtils.getOperationFromAnnotations;

public class OpenApiDocumentationService {

    private static final Gson gson;

    static {
        gson = new Gson();
    }

    public String generateDocumentation(String... packageNames) throws IOException {
        IndexView idxView = CustomIndexView.fromPackages(getClass().getClassLoader(), packageNames);

        SwaggerOpenAPImpl openApi = new SwaggerOpenAPImpl("2.0");
        openApi.setPaths(new PathsImpl());

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


            for (MethodInfo methodInfo : classInfo.methods()) {

                String methodName = methodInfo.name();
                if(methodName.contains("init")){
                    continue;
                }

                List<AnnotationInstance> webServletAnnotation = classInfo.annotations()
                        .stream()
                        .filter(annotation -> annotation.name().toString().toLowerCase().contains("webservlet"))
                        .toList();

                if(webServletAnnotation.isEmpty()){
                    break;
                }

                PathItemImpl pathItem = new PathItemImpl();
                openApi.getPaths().addPathItem(extractFromAnnotationValue(webServletAnnotation.get(0).value("urlPatterns")) + "/" + methodName, pathItem);
                Operation operation = getOperationFromAnnotations(methodInfo, fullClassNameWithPackage);

                operation.setParameters(getParameterList(CfrDecompilerUtils.getParameterAndAttributeCalls(fullClassNameWithPackage, methodInfo.name())));
                operation.addTag(classInfo.simpleName());

                switch (methodName){
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

        return renameTypesToLowercase(gson.toJson(openApi));
    }

    private List<Parameter> getParameterList(Map<String, String> parameterAndAttributeCalls){
        List<Parameter> parameters = new ArrayList<>();
        if (parameterAndAttributeCalls != null) {
            parameterAndAttributeCalls.forEach((k, v) -> {
                if (k.contains("getParameter")) {
                    OpenApiParameter param = new OpenApiParameter("string");
                    String paramName = v.replace("getParameter", "").replace("\"", "").trim();
                    if(!paramName.isEmpty()){
                        param.setName(paramName);
                        param.setIn(OpenApiParameter.In.QUERY);
                        parameters.add(param);
                    }

                }
            });
        }
        return parameters;
    }
    private String renameTypesToLowercase(String openApiJson){
        String[] typesInUppercase = new String[] { "QUERY", "BODY", "PATH", "HEADER", "FORMDATA"};
        for(String type : typesInUppercase){
            openApiJson = openApiJson.replaceAll(type, type.toLowerCase());
        }

        return openApiJson;
    }
}
