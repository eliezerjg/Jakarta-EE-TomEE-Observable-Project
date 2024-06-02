package jakarta.observability.servlets;


import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@WebServlet("/HelloWorld")
public class HelloWorldServlet extends HttpServlet {

    @Override
    @Operation(summary = "Say hello to the world")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Successful operation",
                            content = @Content(mediaType = "text/plain",
                                    schema = @Schema(type = SchemaType.STRING))
                    )
            }
    )
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        writer.println("Hello: Testing metrics endpoint");
        writer.close();
    }




}