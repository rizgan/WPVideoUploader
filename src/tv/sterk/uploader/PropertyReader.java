package tv.sterk.uploader;

import java.io.*;
import java.util.Properties;

public class PropertyReader {

    static Properties properties;

    public PropertyReader(String pathToConfigFile) throws IOException {

        properties = new Properties();
        FileInputStream in = new FileInputStream(pathToConfigFile);
        properties.load(in);
    }
}