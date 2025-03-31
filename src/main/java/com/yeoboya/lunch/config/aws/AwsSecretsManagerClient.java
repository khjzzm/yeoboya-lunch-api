package com.yeoboya.lunch.config.aws;

import com.yeoboya.lunch.config.util.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Arrays;

/**
 * AWS Secrets Manager 클라이언트 유틸리티 클래스.
 * AWS Secrets Manager에서 시크릿 값을 안전하게 가져오는 기능을 제공.
 */
@Slf4j
public class AwsSecretsManagerClient {

    /**
     * AWS Secrets Manager에서 시크릿 값을 가져옵니다.
     *
     * @param secretName 가져올 시크릿의 이름
     * @return 시크릿 값이 포함된 JSONObject 객체
     * @throws IllegalArgumentException secretName이 null 또는 빈 문자열이면 예외 발생
     */
    public static JSONObject getSecret(String secretName) {
        if (secretName == null || secretName.trim().isEmpty()) {
            throw new IllegalArgumentException("Secret name must not be null or empty.");
        }

        try {
            log.info("Attempting to load AWS credentials profile: only-read-yeoboya-secrets-key");

//            ProfileCredentialsProvider provider = ProfileCredentialsProvider.create("only-read-yeoboya-secrets-key");
            SecretsManagerClient secretsManagerClient = SecretsManagerClient.builder()
//                    .credentialsProvider(provider)
                    .region(software.amazon.awssdk.regions.Region.AP_NORTHEAST_2)
                    .build();

            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);

            String secret = response.secretString();
            JSONObject jsonObject = new JSONObject(secret);

            log.info("Secret [{}] successfully retrieved", secretName);
            return jsonObject;

        } catch (Exception e) {
            log.error("Failed to retrieve secret from AWS Secrets Manager", e);
            throw new RuntimeException("AWS Secrets Manager access failed: " + e.getMessage(), e);
        }
    }
}