package de.twometer.proton.jar.loader;

import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.JarTypeLoader;
import de.twometer.proton.jar.node.JarEntryNode;
import de.twometer.proton.jar.node.JarFileNode;
import de.twometer.proton.jar.node.JarNode;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class JarLoader {

    private Pattern anonymousClassPattern = Pattern.compile(".*\\$[0-9]+");

    public JarFileNode load(File file) throws IOException {
        JarFile jarFile = new JarFile(file);
        ITypeLoader typeLoader = new JarTypeLoader(jarFile);
        JarFileNode jarFileNode = new JarFileNode(jarFile, PathInfo.parse(file.getAbsolutePath()), typeLoader);

        jarFile.stream().forEach(entry -> {
            PathInfo info = PathInfo.parse(entry.getName());
            if (info.getPathType() == PathType.CLASS) {
                if (anonymousClassPattern.matcher(info.getName()).matches())
                    return;

                String[] pathParts = info.getPath().split("/");
                StringBuilder path = new StringBuilder();

                JarNode current = jarFileNode;
                for (String part : pathParts) {
                    path.append(part).append("/");
                    current = addAndGet(current, entry, PathInfo.parse(path.toString()));
                }
            }
        });
        return jarFileNode;
    }

    private JarNode addAndGet(JarNode current, JarEntry entry, PathInfo pathInfo) {
        for (JarNode child : current.getChildren())
            if (child.getPathInfo().getName().equals(pathInfo.getName()))
                return child;
        JarNode child = new JarEntryNode(entry, pathInfo);
        current.addChild(child);
        return child;
    }

}
