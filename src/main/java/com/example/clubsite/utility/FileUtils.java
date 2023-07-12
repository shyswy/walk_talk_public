package com.example.clubsite.utility;

import java.io.File;
import java.util.UUID;

public class FileUtils {
    private static FileUtils instance;

    private FileUtils() {
    }

    public static FileUtils getInstance() {
        if (instance == null) {
            synchronized (FileUtils.class) {
                instance = new FileUtils();
            }
        }
        return instance;
    }

    public void makeDirIfNeeded(String dirPath) {
        File dirPathFile = new File(dirPath);
        if (!dirPathFile.exists()) {
            dirPathFile.mkdir();
        }
    }

    public String getModifiedFileName(String originalFileName) {
        String ext = getFileExtension(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    public String getFileExtension(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(pos + 1);
    }
}
