package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Resources {

    public static byte[] loadFileAsBytes(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String loadFileAsString(String path) {
        return new String(loadFileAsBytes(path));
    }
}
