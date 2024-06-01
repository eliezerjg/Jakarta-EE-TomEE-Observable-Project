package jakarta.observability.openapi;

import org.jboss.jandex.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class CustomIndexView implements IndexView {

    private IndexView delegate;
    private Index index;

    public CustomIndexView(IndexView delegate, String... packageNames) {
        this.delegate = delegate;
        Indexer indexer = new Indexer();
        for(String packageName : packageNames){
            for (ClassInfo classInfo : delegate.getClassesInPackage(DotName.createSimple(packageName))) {
                try {
                    String classFileName = classInfo.name().toString().replace('.', '/') + ".class";
                    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(classFileName);
                    if (inputStream != null) {
                        indexer.index(inputStream);
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        this.index = indexer.complete();
    }

    @Override
    public Collection<ClassInfo> getKnownClasses() {
        return index.getKnownClasses();
    }

    @Override
    public ClassInfo getClassByName(DotName dotName) {
        return index.getClassByName(dotName);
    }

    @Override
    public Collection<ClassInfo> getKnownDirectSubclasses(DotName dotName) {
        return index.getKnownDirectSubclasses(dotName);
    }

    @Override
    public Collection<ClassInfo> getAllKnownSubclasses(DotName dotName) {
        return index.getAllKnownSubclasses(dotName);
    }

    @Override
    public Collection<ClassInfo> getKnownDirectSubinterfaces(DotName dotName) {
        return index.getKnownDirectSubinterfaces(dotName);
    }

    @Override
    public Collection<ClassInfo> getAllKnownSubinterfaces(DotName dotName) {
        return index.getAllKnownSubinterfaces(dotName);
    }

    @Override
    public Collection<ClassInfo> getKnownDirectImplementors(DotName dotName) {
        return index.getKnownDirectImplementors(dotName);
    }

    @Override
    public Collection<ClassInfo> getAllKnownImplementors(DotName dotName) {
        return index.getAllKnownImplementors(dotName);
    }

    @Override
    public Collection<AnnotationInstance> getAnnotations(DotName dotName) {
        Collection<AnnotationInstance> annotations = new ArrayList<>();
        for (ClassInfo classInfo : index.getKnownClasses()) {
            if (classInfo.name().toString().startsWith(dotName.toString())) {
                annotations.addAll(classInfo.annotations());
            }
        }
        return annotations;
    }


    @Override
    public Collection<AnnotationInstance> getAnnotationsWithRepeatable(DotName dotName, IndexView indexView) {
        return index.getAnnotationsWithRepeatable(dotName, indexView);
    }

    @Override
    public Collection<ModuleInfo> getKnownModules() {
        return index.getKnownModules();
    }

    @Override
    public ModuleInfo getModuleByName(DotName dotName) {
        return index.getModuleByName(dotName);
    }

    @Override
    public Collection<ClassInfo> getKnownUsers(DotName dotName) {
        return index.getKnownUsers(dotName);
    }

    @Override
    public Collection<ClassInfo> getClassesInPackage(DotName dotName) {
        Collection<ClassInfo> classesInPackage = new ArrayList<>();
        for (ClassInfo classInfo : index.getKnownClasses()) {
            if (classInfo.name().toString().startsWith(dotName.toString())) {
                classesInPackage.add(classInfo);
            }
        }
        return classesInPackage;
    }

    @Override
    public Set<DotName> getSubpackages(DotName dotName) {
        Set<DotName> subpackages = new HashSet<>();
        for (ClassInfo classInfo : index.getKnownClasses()) {
            String className = classInfo.name().toString();
            if (className.startsWith(dotName.toString())) {
                int endIndex = className.indexOf('.', dotName.toString().length());
                if (endIndex != -1) {
                    String subpackageName = className.substring(0, endIndex);
                    subpackages.add(DotName.createSimple(subpackageName));
                }
            }
        }
        return subpackages;
    }

    private static Collection<Class<?>> getClassesInPackage(String packageName, ClassLoader classLoader) throws IOException {
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        Set<Class<?>> classes = new HashSet<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) { // Verifica se o protocolo é "file"
                File directory = new File(resource.getFile());
                if (directory.isDirectory()) {
                    File[] files = directory.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.isFile() && file.getName().endsWith(".class")) {
                                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                                try {
                                    classes.add(classLoader.loadClass(className));
                                } catch (ClassNotFoundException e) {
                                    // Ignore
                                }
                            }
                        }
                    }
                }
            }
        }
        return classes;
    }


    public static CustomIndexView fromPackages(ClassLoader classLoader, String... packageNames) throws IOException {
        Indexer indexer = new Indexer();
        for(String packageName : packageNames){
            for (Class<?> clazz : getClassesInPackage(packageName, classLoader)) {
                indexer.index(clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class"));
            }
        }
        return new CustomIndexView(indexer.complete(), packageNames);
    }
}
