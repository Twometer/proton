package de.twometer.proton.jar.node;

import com.strobel.assembler.metadata.ITypeLoader;
import de.twometer.proton.jar.loader.PathInfo;

import java.util.jar.JarFile;

public class JarFileNode extends JarNode {

    private JarFile jarFile;

    private ITypeLoader typeLoader;

    public JarFileNode(JarFile jarFile, PathInfo pathInfo, ITypeLoader typeLoader) {
        super(pathInfo);
        this.jarFile = jarFile;
        this.typeLoader = typeLoader;
    }

    public ITypeLoader getTypeLoader() {
        return typeLoader;
    }

    public JarFile getJarFile() {
        return jarFile;
    }

}
