package jakarta.observability.servlets;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;
import io.smallrye.openapi.api.OpenApiConfigImpl;
import io.smallrye.openapi.api.models.OpenAPIImpl;
import io.smallrye.openapi.api.models.PathsImpl;
import io.smallrye.openapi.api.models.info.ContactImpl;
import io.smallrye.openapi.api.models.info.InfoImpl;
import io.smallrye.openapi.api.models.info.LicenseImpl;
import io.smallrye.openapi.runtime.io.Format;
import io.smallrye.openapi.runtime.io.OpenApiSerializer;
import io.smallrye.openapi.runtime.scanner.OpenApiAnnotationScanner;
import jakarta.observability.openapi.CustomIndexView;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Paths;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;

import java.io.IOException;
import java.util.Collection;

@WebServlet("/openapi")
@RegisterForReflection
public class OpenApiEndpoint extends HttpServlet {
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

        // to-do: revisar pois teoricamente aqui é as configuracoes do server
        SmallRyeConfig quarkusServerConfig = new SmallRyeConfigBuilder().build();

        // to-do: configurar pois nao pode ser null
        OpenApiConfigImpl config = new OpenApiConfigImpl(quarkusServerConfig);

        // SCANNER DO QUARKUS PARA ANOTACOES (IMPLEMENTACAO EM SI DA ESPECIFICACAO)
        OpenApiAnnotationScanner scanner = new OpenApiAnnotationScanner(config, idxView);

        // Itere sobre as classes no pacote base e processe as anotações OpenAPI
        Collection<ClassInfo> classes = idxView.getKnownClasses();
        for (ClassInfo classInfo : classes) {
            if (classInfo.name().toString().startsWith(packageBase)) {
                OpenAPI scannedOpenApi = scanner.scan(classInfo.name().toString());

                // Mescle os paths do OpenAPI escaneado com o objeto OpenAPI principal
                Paths scannedPaths = scannedOpenApi.getPaths();
                if (scannedPaths != null) {
                    for (String path : scannedPaths.getPathItems().keySet()) {
                        openApi.getPaths().addPathItem(path, scannedPaths.getPathItem(path));
                    }
                }

                // Mescle outras partes do OpenAPI escaneado, se necessário (ex.: components, tags)
                if (scannedOpenApi.getComponents() != null) {
                    openApi.setComponents(scannedOpenApi.getComponents());
                }
                if (scannedOpenApi.getTags() != null) {
                    openApi.setTags(scannedOpenApi.getTags());
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
