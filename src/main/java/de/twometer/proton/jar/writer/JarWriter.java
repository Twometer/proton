package de.twometer.proton.jar.writer;

import de.twometer.proton.jar.OverwrittenClassCache;
import de.twometer.proton.jar.node.JarFileNode;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JarWriter {

    private JarFileNode fileNode;

    private OverwrittenClassCache classCache;

    public JarWriter(JarFileNode fileNode, OverwrittenClassCache classCache) {
        this.fileNode = fileNode;
        this.classCache = classCache;
    }

    public void write(String path) throws IOException {
        ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(path));
        fileNode.getJarFile().stream().forEach(entry -> {
            try {
                if (classCache.isOverwritten(entry.getName())) {
                    ZipEntry replacement = new ZipEntry(entry.getName());
                    outputStream.putNextEntry(replacement);
                    outputStream.write(classCache.getClassData(entry.getName()));
                } else {
                    outputStream.putNextEntry(entry);
                    IOUtils.copy(fileNode.getJarFile().getInputStream(entry), outputStream);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        outputStream.close();
    }

}
