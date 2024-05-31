package jakarta.observability.openapi;

import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.api.OutputSinkFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Decompiler {
    public static void main(String[] args) throws Exception {
        String classFilePath = "path/to/YourClass.class";
        String decompiledCode = decompileClassFile(classFilePath);
        System.out.println(decompiledCode);
    }

    public static String decompileClassFile(String classFilePath) throws Exception {
        Path path = Paths.get(classFilePath);
        byte[] classBytes = Files.readAllBytes(path);

        StringBuilder sb = new StringBuilder();

        Map<String, String> options = new HashMap<>();
        options.put("outputdir", "none");

        CfrDriver driver = new CfrDriver.Builder()
                .withOptions(options)
                .withOutputSink(new OutputSinkFactory() {

                    @Override
                    public List<SinkClass> getSupportedSinks(SinkType sinkType, Collection<SinkClass> collection) {
                        return null;
                    }

                    @Override
                    public <T> Sink<T> getSink(SinkType sinkType, SinkClass sinkClass) {
                        return (Sink<T>) new Sink<String>() {
                            @Override
                            public void write(String s) {

                            }


                        };
                    }
                })
                .build();

        driver.analyse(Collections.singletonList(classFilePath));

        return sb.toString();
    }
}
