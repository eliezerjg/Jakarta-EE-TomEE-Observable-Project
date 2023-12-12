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
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/metrics/thread/dump")
public class ThreadDumpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setRespAsTextPlain(resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setRespAsTextPlain(resp);
    }

    private void setRespAsTextPlain(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        Writer writer = resp.getWriter();
        try {
            PlainTextThreadDumpFormatter plainTextFormatter = new PlainTextThreadDumpFormatter();
            String formattedDump = getFormattedThreadDump(plainTextFormatter::format);
            createFileThreadDump(formattedDump);
            writer.write(formattedDump);
            writer.flush();
        } finally {
            writer.close();
        }
    }

    private <T> T getFormattedThreadDump(Function<ThreadInfo[], T> formatter) {
        return formatter.apply(ManagementFactory.getThreadMXBean().dumpAllThreads(true, true));
    }
    
    private void createFileThreadDump(String formattedString) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String todayDate = dateFormat.format(new Date());
        String fileName = "thread-"+ todayDate + ".txt";
        String filePath = "//" + fileName;
        Path path = Path.of(filePath);
        Files.write(path, formattedString.getBytes());
    }
}
