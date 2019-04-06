package de.twometer.proton.jar.node;

import com.strobel.assembler.metadata.ITypeLoader;
import de.twometer.proton.jar.loader.PathInfo;

public class JarFileNode extends JarNode {

    private ITypeLoader typeLoader;

    public JarFileNode(PathInfo pathInfo, ITypeLoader typeLoader) {
        super(pathInfo);
        this.typeLoader = typeLoader;
    }

    public ITypeLoader getTypeLoader() {
        return typeLoader;
    }

}
