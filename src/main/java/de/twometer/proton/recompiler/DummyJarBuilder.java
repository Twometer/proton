package de.twometer.proton.recompiler;

import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.assembler.metadata.TypeReference;
import de.twometer.proton.decompiler.ProcyonDecompiler;
import de.twometer.proton.jar.loader.PathType;
import de.twometer.proton.jar.node.JarFileNode;
import de.twometer.proton.jar.node.JarNode;
import de.twometer.proton.recompiler.classBuilder.ClassBuilderFactory;
import de.twometer.proton.recompiler.javac.StringInputBuffer;

import javax.tools.JavaFileObject;
import java.util.ArrayList;
import java.util.List;

public class DummyJarBuilder {

    private ProcyonDecompiler decompiler;

    private JarFileNode jarFile;

    private MethodDefinition retainedMethod;

    private String methodSrc;

    public DummyJarBuilder(ProcyonDecompiler decompiler, JarFileNode jarFile, MethodDefinition retainedMethod, String methodSrc) {
        this.decompiler = decompiler;
        this.jarFile = jarFile;
        this.retainedMethod = retainedMethod;
        this.methodSrc = methodSrc;
    }

    public List<JavaFileObject> buildSources() {
        List<JarNode> nodes = new ArrayList<>();
        List<JavaFileObject> sources = new ArrayList<>();
        scanJar(jarFile, nodes);
        for (JarNode node : nodes) {
            if (node.getPathInfo().getPathType() != PathType.CLASS) continue;
            String nodeName = node.getPathInfo().getTypeName();
            if (nodeName.contains("$"))
                continue;
            String typeSrc;
            if (nodeName.equals(retainedMethod.getDeclaringType().getFullName().replace(".", "/"))) {
                typeSrc = ClassBuilderFactory.buildInjected(decompiler, retainedMethod, methodSrc).buildSource();
            } else {
                MetadataSystem system = decompiler.getDecompilerContext().getMetadataSystem();
                TypeReference ref = system.lookupType(nodeName);
                if (ref == null) {
                    System.err.println("UNABLE TO RESOLVE  " + nodeName);
                    continue;
                }
                typeSrc = ClassBuilderFactory.buildDefault(decompiler, system.resolve(ref)).buildSource();
            }
            sources.add(new StringInputBuffer(nodeName + ".java", typeSrc));
        }
        return sources;
    }

    private void scanJar(JarNode parentNode, List<JarNode> nodes) {
        for (JarNode node : parentNode.getChildren()) {
            nodes.add(node);
            scanJar(node, nodes);
        }
    }

}
