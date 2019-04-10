package de.twometer.proton;

import com.strobel.assembler.metadata.MethodDefinition;
import de.twometer.proton.decompiler.DecompiledClass;
import de.twometer.proton.decompiler.ProcyonDecompiler;
import de.twometer.proton.jar.OverwrittenClassCache;
import de.twometer.proton.jar.node.JarEntryNode;
import de.twometer.proton.jar.node.JarFileNode;
import de.twometer.proton.jar.writer.JarWriter;
import de.twometer.proton.recompiler.Recompiler;

public class Context {

    private ProcyonDecompiler decompiler;

    private Recompiler recompiler;

    private JarFileNode currentJar;

    private JarEntryNode currentJarEntry;

    private DecompiledClass currentClass;

    private MethodDefinition currentMethod;

    private JarWriter jarWriter;

    private OverwrittenClassCache classCache;

    public Context(ProcyonDecompiler decompiler, JarFileNode currentJar, JarWriter jarWriter, OverwrittenClassCache classCache) {
        this.decompiler = decompiler;
        this.currentJar = currentJar;
        this.recompiler = new Recompiler();
        this.jarWriter = jarWriter;
        this.classCache = classCache;
    }

    public ProcyonDecompiler getDecompiler() {
        return decompiler;
    }

    public Recompiler getRecompiler() {
        return recompiler;
    }

    public JarFileNode getCurrentJar() {
        return currentJar;
    }

    public JarEntryNode getCurrentJarEntry() {
        return currentJarEntry;
    }

    public void setCurrentJarEntry(JarEntryNode currentJarEntry) {
        this.currentJarEntry = currentJarEntry;
    }

    public DecompiledClass getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(DecompiledClass currentClass) {
        this.currentClass = currentClass;
    }

    public MethodDefinition getCurrentMethod() {
        return currentMethod;
    }

    public void setCurrentMethod(MethodDefinition currentMethod) {
        this.currentMethod = currentMethod;
    }

    public JarWriter getJarWriter() {
        return jarWriter;
    }

    public OverwrittenClassCache getClassCache() {
        return classCache;
    }
}
