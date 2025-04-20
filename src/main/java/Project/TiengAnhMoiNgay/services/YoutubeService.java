package Project.TiengAnhMoiNgay.services;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class YoutubeService {

    public void downloadSubtitle(String youtubeUrl, String outputDir) throws IOException, InterruptedException {
        List<String> command = List.of(
                "yt-dlp",
                "--write-auto-sub",
                "--sub-lang", "en",
                "--skip-download",
                "--convert-subs", "srt",
                "-o", outputDir + "/%(id)s.%(ext)s",
                youtubeUrl
        );

        runCommand(command);
    }

    public void downloadAudio(String youtubeUrl, String outputDir) throws IOException, InterruptedException {
        List<String> command = List.of(
                "yt-dlp",
                "-f", "bestaudio",
                "-x", "--audio-format", "mp3",
                "-o", outputDir + "/audio.%(ext)s",
                youtubeUrl
        );

        runCommand(command);
    }

    private void runCommand(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.inheritIO(); // log ra terminal
        Process process = builder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("yt-dlp execution failed");
        }
    }
}
