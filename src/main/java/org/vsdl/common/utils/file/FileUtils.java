package org.vsdl.common.utils.file;

import org.vsdl.common.utils.exceptions.UtilityException;
import org.vsdl.common.utils.log.VLogger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

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

    /**
     * Detect whether the directory corresponding to the provided path names exists, and create it if it does not.
     * @param fullPathStringBuilder a StringBuilder which will contain the completed path.
     * @param isFilePath whether the final pathName should be treated as a file or another directory
     * @param pathNames a list of names of directories along the path to ensure, possibly ending with a file name.
     * @return true if all directories along the path and the file itself existed, false if any needed to be created.
     * @throws UtilityException if an IOException occurs during file or directory creation.
     * @throws IllegalArgumentException if a file name is passed before the end of the list of names.
     */
    public static boolean ensurePathExists(StringBuilder fullPathStringBuilder, boolean isFilePath, String... pathNames) {
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
                        if (isFilePath) {
                            throw new IllegalArgumentException("Passed a file path before the last path name");
                        }
                        Files.createDirectory(path);
                    } else {
                        if (isFilePath) {
                            Files.createFile(path);
                        } else {
                            Files.createDirectory(path);
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

    /**
     * Save Object data to a file.
     * @param data the object data to be saved.
     * @param pathNames the names of the directories and file to save to.
     * @throws UtilityException if an IOException occurs during file operations.
     */
    public static void saveDataToFile(Object data, String... pathNames) {
        StringBuilder fullPathStringBuilder = new StringBuilder();
        ensurePathExists(fullPathStringBuilder, true, pathNames);
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(fullPathStringBuilder.toString());
            oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
        } catch (Exception e) {
            VLogger.log("File IO error: " + e, VLogger.Level.ERROR);
            throw new UtilityException(e.getMessage());
        } finally {
            closeStreams(fos, oos);
        }
    }

    /**
     * Load object data from a file.
     * @param pathNames the names of the directories and file to save to.
     * @return the object loaded with the file data.
     * @throws UtilityException if an IOException occurs during file operations.
     */
    public static Object loadDataFromFile(String... pathNames) {
        StringBuilder fullPathStringBuilder = new StringBuilder();
        if (!ensurePathExists(fullPathStringBuilder, true, pathNames)) return false;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        Object data;
        try {
            fis = new FileInputStream(fullPathStringBuilder.toString());
            ois = new ObjectInputStream(fis);
            data = ois.readObject();
        } catch (EOFException eofe) {
            return false; //tried to load an empty file - it was created but never written to
        } catch (Exception ex) {
            VLogger.log("File IO error - tried to load empty data record: " + ex, VLogger.Level.WARN);
            throw new UtilityException(ex.getMessage());
        } finally {
            closeStreams(fis, ois);
        }
        return data;
    }
}
