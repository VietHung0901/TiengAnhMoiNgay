package Project.TiengAnhMoiNgay.services;

import Project.TiengAnhMoiNgay.entities.Subtitle_line;
import Project.TiengAnhMoiNgay.model.StringURL;
import Project.TiengAnhMoiNgay.repositories.IListeningLessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class YoutubeSubtitleService {
    private final IListeningLessonRepository lessonRepo;

    public boolean isYoutubeVideoExists(String youtubeUrl) {
        String ytDlpPath = StringURL.dirTools_ytdlp; // Nơi chứa yt-dlp
        try {
            ProcessBuilder pb = new ProcessBuilder(ytDlpPath, "--get-title", youtubeUrl);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();

            return exitCode == 0; // Nếu exitCode = 0 => video tồn tại
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public String extractVideoId(String url) {
        String[] parts = url.split("v=");      // Tách phần sau v=
        return parts[1].split("&")[0];         // Lấy ID (trước dấu & nếu có)
    }

    // Tải về audio từ YouTube để Whisper xử lý
    public String downloadAudioForWhisper(String youtubeUrl, String outputDir, String videoId) throws IOException, InterruptedException {
        String ytDlpPath = StringURL.dirTools_ytdlp; // Nơi chứa yt-dlp
        String audioPath = outputDir + videoId + ".mp3";

        List<String> command = List.of(
                ytDlpPath,
                "-f", "bestaudio",
                "--extract-audio",
                "--audio-format", "mp3",
                "-o", audioPath,
                youtubeUrl
        );

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process process = builder.start();
        int code = process.waitFor();

        if (code != 0) {
            throw new RuntimeException("Tải audio từ YouTube thất bại");
        }

        return audioPath;
    }

    public File generateSRTWithWhisper(String videoId, String outputDir, String audioPath) throws IOException, InterruptedException {
        String pythonPath = StringURL.dirTools_whisper;
        // Câu lệnh gọi whisper bằng Python
        List<String> command = new ArrayList<>();
        command.add(pythonPath);
        command.add("-m");
        command.add("whisper");
        command.add(audioPath);
        command.add("--model");
        command.add("medium");
        command.add("--output_format");
        command.add("srt");
        command.add("-o");
        command.add(outputDir);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Whisper xử lý phụ đề thất bại với mã lỗi: " + exitCode);
        }

        // Kiểm tra file phụ đề .srt
        File newSubtitleFile = Paths.get(outputDir, videoId + ".srt").toFile();
        if (!newSubtitleFile.exists()) {
            throw new FileNotFoundException("Phụ đề Whisper không tạo ra file tại: " + newSubtitleFile.getAbsolutePath());
        }

        return newSubtitleFile;
    }

    public List<Subtitle_line> parseSrt(File srtFile) throws IOException {
        List<Subtitle_line> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(srtFile));

        String line;
        String startTime = "", endTime = "";
        int contentLineIndex = 0;
        String contentToUse = null;

        while ((line = reader.readLine()) != null) {

            // Nếu là số
            if (line.matches("\\d+") && contentLineIndex == 0) {
                contentLineIndex = 1;
            } else {
                contentLineIndex++;
            }

            if (line.contains("-->") && contentLineIndex == 2) {
                String[] times = line.split("-->");
                startTime = times[0].trim();
                endTime = times[1].trim();
            }

            if (contentLineIndex == 3) {
                // Nếu dòng text[2] != null sẽ lưu nội dung
                if (!line.trim().isEmpty()) {
                    contentToUse = line;
                    lines.add(Subtitle_line.builder()
                            .startTime(startTime)
                            .endTime(endTime)
                            .content(contentToUse.trim())
                            .build());
                } else {    // Nếu dòng text[2] == null sẽ cho endtime của block dưới là endtime của block trên
                    int lastIndex = lines.size() - 1;
                    if (lastIndex >= 0) {
                        Subtitle_line oldLine = lines.get(lastIndex);

                        // Tạo bản mới với dữ liệu được cập nhật
                        Subtitle_line updatedLine = Subtitle_line.builder()
                                .startTime(oldLine.getStartTime())
                                .endTime(endTime)                   // cập nhật endTime
                                .content(oldLine.getContent())
                                .build();

                        lines.set(lastIndex, updatedLine); // Cập nhật dòng cuối cùng
                    }
                }
            }

            if (contentLineIndex == 4) {
                contentLineIndex = 0;
            }

        }
        reader.close();
        return lines;
    }

    public String parseSrtFromVoice(File srtFile) throws IOException {
        StringBuilder lines = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(srtFile));

        String line;
        int contentLineIndex = 0;

        while ((line = reader.readLine()) != null) {

            // Nếu là số
            if (line.matches("\\d+") && contentLineIndex == 0) {
                contentLineIndex = 1;
            } else {
                contentLineIndex++;
            }
            if (contentLineIndex == 3) {
                // Nếu dòng text[2] != null sẽ lưu nội dung
                if (!line.trim().isEmpty()) {
                    lines.append(line);
                }
            }
            if (contentLineIndex == 4) {
                contentLineIndex = 0;
            }
        }
        reader.close();
        return lines.toString();
    }
}