package de.twometer.proton.recompiler.javac;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MemoryJavaFileManager<M extends JavaFileManager> extends ForwardingJavaFileManager<M> {

    private Map<String, byte[]> classBytes;

    /**
     * Creates a new instance of ForwardingJavaFileManager.
     *
     * @param fileManager delegate to this file manager
     */
    public MemoryJavaFileManager(M fileManager) {
        super(fileManager);
        classBytes = new HashMap<>();
    }

    public Map<String, byte[]> getClassBytes() {
        return classBytes;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        if (kind == JavaFileObject.Kind.CLASS)
            return new ClassOutputBuffer(className);
        return super.getJavaFileForOutput(location, className, kind, sibling);
    }

    private class ClassOutputBuffer extends SimpleJavaFileObject {
        private String name;

        ClassOutputBuffer(String name) {
            super(CompilerHelper.toURI(name), Kind.CLASS);
            this.name = name;
        }

        public OutputStream openOutputStream() {
            return new FilterOutputStream(new ByteArrayOutputStream()) {
                public void close() throws IOException {
                    out.close();
                    ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
                    classBytes.put(name, bos.toByteArray());
                }
            };
        }
    }


}
