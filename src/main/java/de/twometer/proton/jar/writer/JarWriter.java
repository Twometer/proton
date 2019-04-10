package de.twometer.proton.jar.writer;

import de.twometer.proton.jar.node.JarFileNode;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JarWriter {

    private JarFileNode fileNode;

    private Map<String, byte[]> overwrittenClasses = new HashMap<>();

    public JarWriter(JarFileNode fileNode) {
        this.fileNode = fileNode;
    }

    public void overwriteClass(String path, byte[] contents) {
        overwrittenClasses.put(path, contents);
    }

    public void write(String path) throws IOException {
        ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(path));
        fileNode.getJarFile().stream().forEach(entry -> {
            try {
                if (overwrittenClasses.containsKey(entry.getName())) {
                    ZipEntry replacement = new ZipEntry(entry.getName());
                    outputStream.putNextEntry(replacement);
                    outputStream.write(overwrittenClasses.get(entry.getName()));
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
