package tv.sterk.uploader;

import java.io.*;
import java.util.Properties;

public class PropertyReader {

    static Properties properties;

    public PropertyReader() throws IOException {

        properties = new Properties();
        FileInputStream in = new FileInputStream("D:\\MedyaHaberTV\\Misto\\Configurations\\config.properties");
        properties.load(in);
    }
}