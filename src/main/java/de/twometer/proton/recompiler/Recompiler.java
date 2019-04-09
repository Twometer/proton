package de.twometer.proton.recompiler;

import de.twometer.proton.recompiler.javac.MemoryJavaFileManager;

import javax.tools.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Recompiler {

    private JavaCompiler javaCompiler;

    private JavaFileManager standardFileManager;

    public Recompiler() {
        javaCompiler = ToolProvider.getSystemJavaCompiler();
        if (javaCompiler == null)
            throw new RuntimeException("Please use the JDK!");
        standardFileManager = javaCompiler.getStandardFileManager(null, null, null);
    }

    public CompilerResult compile(String filename, List<JavaFileObject> sources) {
        PrintWriter writer = new PrintWriter(System.err);
        MemoryJavaFileManager<JavaFileManager> fileManager = new MemoryJavaFileManager<>(standardFileManager);
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        List<String> options = new ArrayList<>();

        JavaCompiler.CompilationTask task = javaCompiler.getTask(writer, fileManager, collector, options, null, sources);
        if (!task.call()) {
            return new CompilerResult(false, null, collector);
        }

        for (Map.Entry<String, byte[]> entry : fileManager.getClassBytes().entrySet())
            if (entry.getKey().equals(filename))
                return new CompilerResult(true, entry.getValue(), null);

        throw new RuntimeException("Could not find compiled class");
    }
}
