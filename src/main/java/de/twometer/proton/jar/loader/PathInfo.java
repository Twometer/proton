package de.twometer.proton.jar.loader;

public class PathInfo {

    private static final String CLASS_EXT = ".class";

    private static final String JAR_EXT = ".jar";

    private String name;

    private String path;

    private PathType pathType;

    private PathInfo(String name, String path, PathType pathType) {
        this.name = name;
        this.path = path;
        this.pathType = pathType;
    }

    static PathInfo parse(String path) {
        path = trimSlashes(path.replace("\\", "/"));
        String name = path.contains("/") ? path.substring(path.lastIndexOf("/") + 1) : path;
        if (path.endsWith(CLASS_EXT))
            return new PathInfo(stripEnd(name, CLASS_EXT), path, PathType.CLASS);
        else if (path.endsWith(JAR_EXT))
            return new PathInfo(name, path, PathType.JAR);
        else return new PathInfo(name, path, PathType.PACKAGE);
    }

    private static String trimSlashes(String path) {
        int offset = path.length();
        for (int i = path.length() - 1; i >= 0; i--)
            if (path.charAt(i) == '/')
                offset--;
            else break;
        return path.substring(0, offset);
    }

    private static String stripEnd(String text, String end) {
        if (text.endsWith(end))
            return text.substring(0, text.length() - end.length());
        return text;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public PathType getPathType() {
        return pathType;
    }

    public String getTypeName() {
        if (pathType != PathType.CLASS)
            throw new IllegalStateException("Only classes can have a type name");
        return stripEnd(path, CLASS_EXT);
    }

}