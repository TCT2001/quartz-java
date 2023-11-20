package tct.lib.storage.abstraction;

import java.io.File;

public class FileUtil {
    public static String getFolderPath(String filePath) {
        filePath = filePath.replaceAll("[\\\\/]", File.separator);
        int lastSeparator = filePath.lastIndexOf(File.separator);
        if (lastSeparator >= 0) {
            return filePath.substring(0, lastSeparator);
        } else return "/";
    }

    public static String getFileName(String filePath) {
        filePath = filePath.replaceAll("[\\\\/]", File.separator);
        int lastSeparator = filePath.lastIndexOf(File.separator);
        return filePath.substring(lastSeparator + 1);
    }
}

