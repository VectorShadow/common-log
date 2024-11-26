package org.vsdl.common.utils.file;

import org.vsdl.common.utils.exceptions.UtilityException;
import org.vsdl.common.utils.log.VLogger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    /**
     * Close streams used to read/write files
     * Call during the 'finally' portion of the relevant try/catch/finally blocks.
     * @param fileStream - the Closeable fileStream to close
     * @param objectStream - the Closeable objectStream to close
     */
    private static void closeStreams(Closeable fileStream, Closeable objectStream) {
        if (fileStream != null) {
            try {
                fileStream.close();
            } catch (IOException e) {
                VLogger.log("File IO error - failed to close file streams: " + e, VLogger.Level.ERROR);
            }
        }
        if (objectStream != null) {
            try {
                objectStream.close();
            } catch (IOException e) {
                VLogger.log("File IO error - failed to close file streams: " + e, VLogger.Level.ERROR);
            }
        }
    }

    private static boolean isDirectory(String pathName) {
        return pathName.indexOf('.') < 0;
    }

    /**
     * Detect whether the directory corresponding to the provided path names exists, and create it if it does not.
     * @param fullPathStringBuilder a StringBuilder which will contain the completed path
     * @param pathNames a list of names of directories along the path to ensure, possibly ending with a file name.
     * @return true if all directories along the path and the file itself existed, false if any needed to be created.
     * @throws UtilityException if an IOException occurs during file or directory creation.
     * @throws IllegalArgumentException if a file name is passed before the end of the list of names.
     */
    public static boolean ensurePathExists(StringBuilder fullPathStringBuilder, String... pathNames) {
        fullPathStringBuilder.append("./");
        int lastPathNameIndex = pathNames.length - 1;
        boolean create = false;
        for (int i = 0; i < pathNames.length; i++) {
            String pathName = pathNames[i];
            Path path = Paths.get(fullPathStringBuilder.append(pathName).toString());
            if (i < lastPathNameIndex) fullPathStringBuilder.append("/");
            if (!Files.exists(path)) {
                create = true;
                try {
                    if (i < lastPathNameIndex) {
                        if (!isDirectory(pathName)) {
                            throw new IllegalArgumentException("Passed a file path before the last path name");
                        }
                        Files.createDirectories(path);
                    } else {
                        if (isDirectory(pathName)) {
                            Files.createDirectories(path);
                        } else {
                            Files.createFile(path);
                        }
                    }

                } catch (IOException e) {
                    VLogger.log("File IO error - failed to create local data directory: " + e, VLogger.Level.ERROR);
                    throw new UtilityException(e.getMessage());
                }
            }
        }
        return create;
    }

    public static void saveData(Object data, String... filePathNames) {
        StringBuilder fullPathStringBuilder = new StringBuilder();
        ensurePathExists(fullPathStringBuilder, filePathNames);
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(fullPathStringBuilder.toString());
            oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
        } catch (Exception e) {
            VLogger.log("File IO error: " + e, VLogger.Level.ERROR);
        } finally {
            closeStreams(fos, oos);
        }
    }

    public static Object loadData(String... filePathNames) {
        StringBuilder fullPathStringBuilder = new StringBuilder();
        if (!ensurePathExists(fullPathStringBuilder, filePathNames)) return false;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        Object data = null;
        try {
            fis = new FileInputStream(fullPathStringBuilder.toString());
            ois = new ObjectInputStream(fis);
            data = ois.readObject();
        } catch (EOFException eofe) {
            return false; //tried to load an empty file - it was created but never written to
        } catch (Exception ex) {
            VLogger.log("File IO error - tried to load empty data record: " + ex, VLogger.Level.WARN);
        } finally {
            closeStreams(fis, ois);
        }
        return data;
    }
}
