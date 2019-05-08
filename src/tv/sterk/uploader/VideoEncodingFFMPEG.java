package tv.sterk.uploader;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.IOException;
import java.util.Scanner;

public class VideoEncodingFFMPEG {

    FFmpeg ffmpeg;
    FFprobe ffprobe;
    static FFmpegBuilder builder;
    static FFmpegBuilder thumbnailBuilder;
    static FFmpegExecutor executor;

    public VideoEncodingFFMPEG(String inputVideoFile, String outputVideoFile, String pathToLogoFile) throws IOException {

        ffmpeg = new FFmpeg(PropertyReader.properties.getProperty("ffmpegDirectory"));
        ffprobe = new FFprobe(PropertyReader.properties.getProperty("ffprobeDirectory"));

        builder = new FFmpegBuilder()     // Filename, or a FFmpegProbeResult
                .overrideOutputFiles(true) // Override the output if it exists
                .addInput(inputVideoFile)
                .addInput(pathToLogoFile)
                .setComplexFilter("[0:v][1:v] overlay=55:55") //WORKING BAD
                .addOutput(outputVideoFile)   // Filename for the destination
                .setFormat("mp4")        // Format is inferred from filename, or can be set
                .setAudioCodec("aac")        // using the aac codec
                .setVideoCodec("libx264")    // Video using x264
                .setVideoCodec("h264_nvenc")
                .addExtraArgs("-level", "4.1")
//                .addExtraArgs("-qmin", "18")
//                .addExtraArgs("-qmax", "53")
                .addExtraArgs("-crf", "23")
                .setAudioFilter("ebur128")
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
                .done();

        thumbnailBuilder = new FFmpegBuilder().setInput(inputVideoFile)     // Filename, or a FFmpegProbeResult
                .overrideOutputFiles(true) // Override the output if it exists
                .addOutput(outputVideoFile.replace(".mp4", ".png"))
                .addExtraArgs("-ss", String.valueOf(getFrameNumberOfClip(inputVideoFile)))
                .setFrames(1)
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
                .done();

        executor = new FFmpegExecutor(ffmpeg, ffprobe);
    }

    public static int getFrameNumberOfClip(String inputVideoFile) throws java.io.IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(PropertyReader.properties.getProperty("ffprobeDirectory") + " -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 " + inputVideoFile).getInputStream()).useDelimiter("\\A");
        String clipSizeInSeconds = s.hasNext() ? s.next() : "";
        int result = Integer.parseInt(clipSizeInSeconds.replace(clipSizeInSeconds.substring(clipSizeInSeconds.indexOf('.')), ""));

        if ((result & 1) == 0) {
            return result / 2;
        } else {
            return (result + 1) / 2;
        }
    }
}