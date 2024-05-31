package jakarta.observability.openapi;

import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.jetbrains.java.decompiler.util.InterpreterUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Decompiler implements IBytecodeProvider, IResultSaver {
    private final File tempDir;
    private final Fernflower engine;
    private final Map<String, ZipOutputStream> mapArchiveStreams = new HashMap<>();
    private final Map<String, Set<String>> mapArchiveEntries = new HashMap<>();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Decompiler <package>");
            return;
        }

        String pacote= "";

        String packagePath = pacote.replace(".", File.separator);

        try {
            File tempDir = createTempDir();
            Decompiler decompiler = new Decompiler(tempDir, null, null);
            decompiler.addSource(new File(packagePath));
            decompiler.decompileContext();

            System.out.println("Decompilation completed. Output directory: " + tempDir.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Decompiler(File tempDir, Map<String, Object> options, IFernflowerLogger logger) {
        this.tempDir = tempDir;
        this.engine = new Fernflower(this, this, options, logger);
    }

    public void addSource(File source) {
        this.engine.addSource(source);
    }

    public void decompileContext() {
        try {
            this.engine.decompileContext();
        } finally {
            this.engine.clearContext();
        }
    }

    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        File file = new File(externalPath);
        if (internalPath == null) {
            return InterpreterUtil.getBytes(file);
        } else {
            try (ZipFile archive = new ZipFile(file)) {
                ZipEntry entry = archive.getEntry(internalPath);
                if (entry == null) {
                    throw new IOException("Entry not found: " + internalPath);
                }
                return InterpreterUtil.getBytes(archive, entry);
            }
        }
    }

    private String getAbsolutePath(String path) {
        return new File(this.tempDir, path).getAbsolutePath();
    }

    @Override
    public void saveFolder(String path) {
        File dir = new File(getAbsolutePath(path));
        if (!dir.mkdirs() && !dir.isDirectory()) {
            throw new RuntimeException("Cannot create directory " + dir);
        }
    }

    @Override
    public void copyFile(String source, String path, String entryName) {
        try {
            InterpreterUtil.copyFile(new File(source), new File(getAbsolutePath(path), entryName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
        File file = new File(getAbsolutePath(path), entryName);
        try (Writer out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            out.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createArchive(String s, String s1, Manifest manifest) {}

    @Override
    public void saveDirEntry(String s, String s1, String s2) {}

    @Override
    public void copyEntry(String s, String s1, String s2, String s3) {}

    @Override
    public void saveClassEntry(String s, String s1, String s2, String s3, String s4) {}

    @Override
    public void closeArchive(String s, String s1) {}

    private static File createTempDir() throws IOException {
        File tempDir = File.createTempFile("temp", Long.toString(System.nanoTime()));
        if (!tempDir.delete()) {
            throw new IOException("Could not delete temp file: " + tempDir.getAbsolutePath());
        }
        if (!tempDir.mkdirs()) {
            throw new IOException("Could not create temp directory: " + tempDir.getAbsolutePath());
        }
        return tempDir;
    }
}
