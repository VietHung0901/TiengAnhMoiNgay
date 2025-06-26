package Project.TiengAnhMoiNgay.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

@Service
@RequiredArgsConstructor
public class TranslateService {

    private final TranslateClient translateClient;

    public String translate(String text, String sourceLang, String targetLang) {
        TranslateTextRequest request = TranslateTextRequest.builder()
                .sourceLanguageCode(sourceLang)
                .targetLanguageCode(targetLang)
                .text(text)
                .build();

        TranslateTextResponse response = translateClient.translateText(request);
        return response.translatedText();
    }
}