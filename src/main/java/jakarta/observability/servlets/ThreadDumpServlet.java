package jakarta.observability.servlets;

import java.io.IOException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;

import jakarta.observability.utils.PlainTextThreadDumpFormatter;
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

@WebServlet(urlPatterns = "/metrics/thread/dump")
public class ThreadDumpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


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
        req.getParameter("variavel_importante");
        req.setAttribute("parametro_de_atributo_importante", "valor_atributo");
        setRespAsTextPlain(resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setRespAsTextPlain(resp);
    }

    private void setRespAsTextPlain(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        Writer writer = resp.getWriter();
        PlainTextThreadDumpFormatter plainTextFormatter = new PlainTextThreadDumpFormatter();
        String formattedDump = getFormattedThreadDump(plainTextFormatter::format);
        createFileThreadDump(formattedDump);
        writer.write(formattedDump);
        writer.flush();
    }

    private <T> T getFormattedThreadDump(Function<ThreadInfo[], T> formatter) {
        return formatter.apply(ManagementFactory.getThreadMXBean().dumpAllThreads(true, true));
    }

    private void createFileThreadDump(String formattedString) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String todayDate = dateFormat.format(new Date());
        String fileName = "thread-" + todayDate + ".txt";
        String filePath = "//" + fileName;
        Path path = Path.of(filePath);
        Files.write(path, formattedString.getBytes());
    }
}
