package de.twometer.proton.jar.node;

import de.twometer.proton.jar.loader.PathInfo;

import java.util.jar.JarEntry;

public class JarEntryNode extends JarNode {

    private JarEntry jarEntry;

    public JarEntryNode(JarEntry jarEntry, PathInfo pathInfo) {
        super(pathInfo);
        this.jarEntry = jarEntry;
    }

    public JarEntry getJarEntry() {
        return jarEntry;
    }
}
