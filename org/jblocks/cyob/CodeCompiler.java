package org.jblocks.cyob;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import java.util.HashMap;
import java.util.Map;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

class CodeCompiler {

    private static void deleteFileTree(File dir) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                deleteFileTree(f);
            } else {
                f.delete();
            }
        }
        dir.delete();
    }

    private static void putInMap(String rel, File dir, Map<String, byte[]> map) throws IOException {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                putInMap(rel + f.getName() + ".", f, map);
            } else {
                FileInputStream in = new FileInputStream(f);
                ByteArrayOutputStream data = new ByteArrayOutputStream();
                byte[] buf = new byte[8096];
                int len;
                while ((len = in.read(buf)) != -1) {
                    data.write(buf, 0, len);
                }
                in.close();
                map.put(rel + f.getName(), data.toByteArray());
            }
        }
    }

    public static Map<String, byte[]> compile(String className, String source, StringBuilder sb) throws ClassNotFoundException, IOException {
        File tmpDir = null;
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        Map<String, byte[]> files = new HashMap<String, byte[]>(100);
        try {
            StandardJavaFileManager mng = compiler.getStandardFileManager(null, null, null);

            tmpDir = new File(System.getProperty("java.io.tmpdir") + (int) (Math.random() * 10000) + "/");
            tmpDir.mkdir();
            if (tmpDir.exists()) {
                mng.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(tmpDir));

                DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
                JavaFileObject file = new JavaSourceFromString(className, source);

                Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
                CompilationTask task = compiler.getTask(null, mng, diagnostics, null, null, compilationUnits);
                task.call();
                mng.close();

                putInMap("", tmpDir, files);

                for (Diagnostic msg : diagnostics.getDiagnostics()) {
                    sb.append("\t").append(msg.getKind()).append(" line ").
                            append(msg.getLineNumber()).append(": ").
                            append(msg.getMessage(null)).append("\n");
                }
            }
        } finally {
            if (tmpDir != null) {
                deleteFileTree(tmpDir);
            }
        }
        return files;
    }

    public static boolean compilerAvailable() {
        return ToolProvider.getSystemJavaCompiler() != null;
    }

    private static class JavaSourceFromString extends SimpleJavaFileObject {

        final String code;

        public JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}
