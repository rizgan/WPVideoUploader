package tv.sterk.uploader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main {

    //E:\SterkTV\video\
    //D:\SterkTV\Misto\Configurations\sterk.png
    //D:\SterkTV\Misto\Configurations\config.properties

    static int counter;

    public static void main(String[] args) throws Exception {

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date) + " - Started");

        PropertyReader propertyReader = new PropertyReader("D:\\MedyaHaberTV\\Misto\\Configurations\\config.properties");
        AllFilesInDirectory allFilesInDirectory = new AllFilesInDirectory();

        FTPUploader ftpUploader = null;

        String localVideoDirectory = PropertyReader.properties.getProperty("localVideoDirectory") + "\\";
        String tmpVideoDirectory = PropertyReader.properties.getProperty("tmpVideoDirectory") + "\\";
        Runtime rt = Runtime.getRuntime();
        WPPoster wpPoster;
        Process proc;
        VideoEncodingFFMPEG videoEncodingFFMPEG;
        String inputVideoFile;
        String outputVideoFile;

        proc = rt.exec("cmd /c del " + tmpVideoDirectory + " /f /s /q");

        for (int i = 0; i < allFilesInDirectory.results.size(); i++) {

            inputVideoFile = localVideoDirectory + allFilesInDirectory.results.get(i);
            outputVideoFile = tmpVideoDirectory + allFilesInDirectory.results.get(i).replace(".mpg", ".mp4").replace(".avi", ".mp4");


            videoEncodingFFMPEG = new VideoEncodingFFMPEG(inputVideoFile, outputVideoFile, "D:\\MedyaHaberTV\\Misto\\Configurations\\mh.png");

            videoEncodingFFMPEG.executor.createJob(VideoEncodingFFMPEG.builder).run();
            date = new Date();
            System.out.println("\r\n" + dateFormat.format(date) + " - Converted video");
            videoEncodingFFMPEG.executor.createJob(VideoEncodingFFMPEG.thumbnailBuilder).run();
            date = new Date();
            System.out.println("\r\n" + dateFormat.format(date) + " - Created thumbnail");

            ftpUploader = new FTPUploader(PropertyReader.properties.getProperty("ftpHost"), PropertyReader.properties.getProperty("ftpUser"), PropertyReader.properties.getProperty("ftpPassword"));

            ftpUploader.uploadFile(tmpVideoDirectory + allFilesInDirectory.results.get(i).replace(".mpg", ".mp4").replace(".avi", ".mp4"), allFilesInDirectory.results.get(i).replace(".mpg", ".mp4").replace(  ".avi", ".mp4"), "/public_html/video/");

            date = new Date();
            System.out.println("\r\n" + dateFormat.format(date) + " - File uploaded on ftp-server");

            proc = rt.exec("cmd /c del \"" + inputVideoFile + "\" /s /q"); //Delete not to be repeated!

            wpPoster = new WPPoster(allFilesInDirectory.results.get(i));
            date = new Date();
            System.out.println("\r\n" + dateFormat.format(date) + " - Published on WP");

            proc = rt.exec("cmd /c copy \"" + outputVideoFile + "\" E:\\MedyaHaberTV\\video\\ /Y"); //Copy to Backup!

            proc = rt.exec("cmd /c del \"" + outputVideoFile + "\" /s /q");
            proc = rt.exec("cmd /c del \"" + outputVideoFile.replace(".mp4", ".png") + "\" /s /q");

        }
        ftpUploader.disconnect();
    }
}