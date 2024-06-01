package jakarta.observability.openapi;

import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.api.OutputSinkFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CfrDecompilerUtils {

    public static String classToString(String packageName) throws Exception {
        return decompileClassFile(convertPackageToPath(packageName));
    }

    private static String convertPackageToPath(String packageName) throws URISyntaxException {
        String basePath = new File(CfrDecompilerUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        return basePath + "/" + packageName.replace('.', '/') + ".class";
    }

    private static String decompileClassFile(String classFilePath) throws Exception {
        Path path = Paths.get(classFilePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Class file not found at " + classFilePath);
        }

        StringBuilder sb = new StringBuilder();

        Map<String, String> options = new HashMap<>();
        options.put("outputdir", "none");

        CfrDriver driver = new CfrDriver.Builder()
                .withOptions(options)
                .withOutputSink(new OutputSinkFactory() {

                    @Override
                    public List<SinkClass> getSupportedSinks(SinkType sinkType, Collection<SinkClass> collection) {
                        return Collections.singletonList(SinkClass.STRING);
                    }

                    @Override
                    public <T> Sink<T> getSink(SinkType sinkType, SinkClass sinkClass) {
                        return (Sink<T>) s -> sb.append(s).append(System.lineSeparator());
                    }
                })
                .build();

        driver.analyse(Collections.singletonList(classFilePath));

        return sb.toString();
    }

    public static Map<String, String> getMethodByName(String packageName, String methodName) throws Exception {
        String classContent = classToString(packageName);

        Map<String, String> methods = new HashMap<>();

        String regex = "(?s)\\b(?:public|private|protected|static)\\s+[^\\{;]+\\b" + methodName + "\\s*\\([^\\)]*\\)\\s*(?:throws\\s+[^\\{;]+)?\\s*\\{[^{}]*\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(classContent);

        while (matcher.find()) {
            methods.put(methodName, matcher.group());
        }

        return methods;
    }

    public static Map<String, String> getParameterAndAttributeCalls(String packageName, String methodName) throws Exception {
        Map<String, String> methodInfo = getMethodByName(packageName, methodName);
        Map<String, String> parameterAndAttributeCalls = new HashMap<>();

        for (String method : methodInfo.values()) {
            String parameterCalls = findCalls(method, "getParameter");
            String attributeCalls = findCalls(method, "setAttribute");

            parameterAndAttributeCalls.put("getParameter", parameterCalls);
            parameterAndAttributeCalls.put("setAttribute", attributeCalls);
        }

        return parameterAndAttributeCalls;
    }

    private static String findCalls(String methodBody, String methodName) {
        StringBuilder calls = new StringBuilder();

        String regex = "\\b" + methodName + "\\s*\\([^\\)]*\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(methodBody);

        while (matcher.find()) {
            calls.append(matcher.group()).append(System.lineSeparator());
        }

        return calls.toString();
    }

    public static void main(String[] args) {
        try {
            String packageName = "jakarta.observability.servlets.HeapDumpServlet";
            String methodName = "doGet";

            Map<String, String> parameterAndAttributeCalls = getParameterAndAttributeCalls(packageName, methodName);

            System.out.println("Chamadas de getParameter:");
            System.out.println(parameterAndAttributeCalls.get("getParameter"));

            System.out.println("\nChamadas de setAttribute:");
            System.out.println(parameterAndAttributeCalls.get("setAttribute"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
