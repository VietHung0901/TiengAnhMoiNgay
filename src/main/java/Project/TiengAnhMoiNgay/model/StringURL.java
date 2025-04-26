package Project.TiengAnhMoiNgay.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class StringURL {
    private final String http = "http://localhost:8080";
    private final String dirSubtitles = "/Users/tranviethung/Documents/Project/TiengAnhMoiNgay/subtitles/";
    private final String dirSubtitles_audio = "/Users/tranviethung/Documents/Project/TiengAnhMoiNgay/subtitles/audios/";
    private final String dirSubtitles_sub = "/Users/tranviethung/Documents/Project/TiengAnhMoiNgay/subtitles/subs/";
    private final String dirTools_ytdlp = "/Users/tranviethung/Documents/Project/TiengAnhMoiNgay/src/main/resources/tools/yt-dlp";
    private final String dirTools_whisper = "/Users/tranviethung/Documents/Project/TiengAnhMoiNgay/src/main/resources/tools/whisper-env/bin/python";
}
