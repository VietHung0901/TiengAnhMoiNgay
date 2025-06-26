package Project.TiengAnhMoiNgay.AWS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;

@Configuration
public class AwsTranslateConfig {

    @Value("${aws.iam.access_key}")
    private String accessKey;
    @Value("${aws.iam.secret_key}")
    private String secretKey;

    @Bean
    public TranslateClient translateClient() {
        return TranslateClient.builder()
                .region(Region.AP_SOUTHEAST_1) // hoặc Region.of("us-east-1") tuỳ bạn
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }
}
