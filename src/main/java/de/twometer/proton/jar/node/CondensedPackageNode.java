package de.twometer.proton.jar.node;

import de.twometer.proton.jar.loader.PathInfo;

public class CondensedPackageNode extends JarEntryNode {

    private String text;

    private CondensedPackageNode(PathInfo pathInfo) {
        super(pathInfo);
        text = pathInfo.getName();
    }

    public static CondensedPackageNode createFrom(JarEntryNode node) {
        return new CondensedPackageNode(node.getPathInfo());
    }

    public void appendPackage(String pack) {
        text += "." + pack;
    }

    @Override
    public String toString() {
        return text;
    }
}
