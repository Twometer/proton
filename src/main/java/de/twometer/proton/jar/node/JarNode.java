package de.twometer.proton.jar.node;

import de.twometer.proton.jar.loader.PathInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class JarNode {

    private PathInfo pathInfo;

    private List<JarNode> children = new ArrayList<>();

    JarNode(PathInfo pathInfo) {
        this.pathInfo = pathInfo;
    }

    public List<JarNode> getChildren() {
        return children;
    }

    public void addChild(JarNode jarNode) {
        children.add(jarNode);
    }

    public PathInfo getPathInfo() {
        return pathInfo;
    }

    @Override
    public String toString() {
        return pathInfo.getName();
    }
}
