package Project.TiengAnhMoiNgay.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class StringURL {

    // Listing Lesson
    public static final String dirSubtitles = "/Users/tranviethung/Documents/Project/TiengAnhMoiNgay/subtitles/";
    public static final String dirSubtitles_audio = "/Users/tranviethung/Documents/Project/TiengAnhMoiNgay/subtitles/audios/";
    public static final String dirSubtitles_sub = "/Users/tranviethung/Documents/Project/TiengAnhMoiNgay/subtitles/subs/";
    public static final String dirTools_ytdlp = "/Users/tranviethung/Documents/Project/TiengAnhMoiNgay/src/main/resources/tools/yt-dlp";
    public static final String dirTools_whisper = "/Users/tranviethung/Documents/Project/TiengAnhMoiNgay/src/main/resources/tools/whisper-env/bin/python";

    // Writing Lesson
    public static final String dirFilePath = "/Users/tranviethung/Documents/Project/TiengAnhMoiNgay/writings/";
}
