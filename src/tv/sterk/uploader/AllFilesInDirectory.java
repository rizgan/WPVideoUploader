package tv.sterk.uploader;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AllFilesInDirectory {

    List<String> results;
    public PropertyReader propertyReader = new PropertyReader("D:\\MedyaHaberTV\\Misto\\Configurations\\config.properties");

    public AllFilesInDirectory() throws IOException {
        results = new ArrayList<String>();

        File[] files = new File(PropertyReader.properties.getProperty("localVideoDirectory")).listFiles();

        for (File file : files) {
            if (file.isFile() && FilenameUtils.isExtension(file.getName(), "mpg") || FilenameUtils.isExtension(file.getName(), "avi") || FilenameUtils.isExtension(file.getName(), "mp4")) {
                results.add(file.getName());
            }
        }
    }
}