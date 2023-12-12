package jakarta.observability.servlets;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sun.management.HotSpotDiagnosticMXBean;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.observability.exceptions.MXBeanNotFoundException;

@WebServlet(urlPatterns = "/metrics/heap/dump")
public class HeapDumpServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        Writer writer = resp.getWriter();
        File file = dumpHeap(true);
        byte[] bytes = Files.readAllBytes(Paths.get(file.getPath()));
        String content = new String(bytes, StandardCharsets.UTF_8);
        writer.write(content);
        writer.flush();
        writer.close();
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        Writer writer = resp.getWriter();
        File file = dumpHeap(true);
        byte[] bytes = Files.readAllBytes(Paths.get(file.getPath()));
        String content = new String(bytes, StandardCharsets.UTF_8);
        writer.write(content);
        writer.flush();
        writer.close();
    }

    private File dumpHeap(Boolean live) throws MXBeanNotFoundException, IOException {
        HotSpotDiagnosticMXBean diagnosticMXBean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        if (diagnosticMXBean == null) {
            throw new MXBeanNotFoundException("MXBean not found.");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String dateToday = dateFormat.format(new Date());
        String fileName = "heap-" + dateToday + " - " + ".hprof";
        String filePath = "/" + fileName;

        File file = new File(filePath);
        diagnosticMXBean.dumpHeap(file.getAbsolutePath(), live);
        return file;
    }


}
