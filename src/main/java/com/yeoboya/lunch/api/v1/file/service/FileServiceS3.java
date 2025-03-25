package com.yeoboya.lunch.api.v1.file.service;

import com.yeoboya.lunch.api.v1.file.constant.Directory;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import com.yeoboya.lunch.config.aws.AwsSecretsManagerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static software.amazon.awssdk.regions.Region.AP_NORTHEAST_2;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceS3 {

    private final S3Client s3Client;

    private static final String BUCKET_NAME = "yeoboya-lunch-s3";
    private static final String REGION = "ap-northeast-2";

    public FileServiceS3() {
        JSONObject secret = AwsSecretsManagerClient.getSecret("prod/lunch");
        String accessKey = secret.getString("accessKey");
        String secretKey = secret.getString("secretKey");

        this.s3Client = S3Client.builder()
                .region(AP_NORTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
//                .credentialsProvider(ProfileCredentialsProvider.create("default"))  // CLI 자격 증명 사용
                .build();

    }

    // 파일 업로드 (일반 파일 + 썸네일 생성)
    public <T extends FileResponse> T upload(MultipartFile multipartFile, Directory subDirectory, Function<FileResponse, T> mapper) {
        // 확장자 검증
        String originalFileName = Objects.requireNonNull(multipartFile.getOriginalFilename());
        String extension = this.getFileExtension(originalFileName);
        if (!isValidExtension(extension)) {
            throw new RuntimeException("Invalid file extension");
        }

        // 디렉토리 및 파일명 생성
        String directory = this.createDirectory(subDirectory);
        String fileName = this.generateFileName(extension);

        try {
            // 원본 파일 업로드
            String objectKey = directory + "/" + fileName;
            String fileUrl = this.uploadToS3(multipartFile.getInputStream(), multipartFile.getSize(), multipartFile.getContentType(), objectKey);

            // 썸네일 생성 및 업로드
            BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());
            BufferedImage thumbnailImage = this.createThumbnail(originalImage);
            byte[] thumbnailBytes = this.imageToBytes(thumbnailImage, extension);
            String thumbnailKey = directory + "/thumbnail_" + fileName;
            String thumbnailUrl = this.uploadToS3(thumbnailBytes, "image/" + extension, thumbnailKey);

            // 결과 객체 반환
            FileResponse response = FileResponse.builder()
                    .originalFileName(originalFileName)
                    .fileName(fileName)
                    .filePath(directory)
                    .extension(extension)
                    .imageUrl(fileUrl)
                    .size(multipartFile.getSize())
                    .mimeType(multipartFile.getContentType()) // 파일 MIME 타입 추가
                    .uploadDate(LocalDateTime.now()) // 업로드 시간 추가
                    .uploadedBy("admin") // 업로드한 사용자 ID (예제)
                    .isPublic(true) // 파일이 공개 여부 (기본값 true)
                    .thumbnailUrl(thumbnailUrl)
                    .checksum(generateChecksum(multipartFile.getInputStream())) // SHA256 체크섬 추가
                    .build();

            return mapper != null ? mapper.apply(response) : (T) response;
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    // 파일 업로드 (이미지 URL + 썸네일 생성)
    public <T extends FileResponse> T upload(String imageUrl, Directory directory, Function<FileResponse, T> mapper) {
        InputStream inputStream = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            // 안전한 URL 구성
            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection();
            String contentType = connection.getContentType();
            long fileSize = connection.getContentLengthLong();

            // 한번 읽어서 메모리에 저장 (InputStream 재사용용)
            inputStream = connection.getInputStream();
            byte[] imageBytes = inputStream.readAllBytes(); // Java 9+
            ByteArrayInputStream uploadStream = new ByteArrayInputStream(imageBytes);
            ByteArrayInputStream thumbnailStream = new ByteArrayInputStream(imageBytes);
            ByteArrayInputStream checksumStream = new ByteArrayInputStream(imageBytes);

            // 확장자 추출
            String extension = getFileExtension(imageUrl);
            String fileName = generateFileName(extension);
            String path = createDirectory(directory);
            String objectKey = path + "/" + fileName;

            // 원본 업로드
            String fileUrl = uploadToS3(uploadStream, fileSize, contentType, objectKey);

            // 썸네일 생성
            BufferedImage originalImage = ImageIO.read(thumbnailStream);
            BufferedImage thumbnailImage = createThumbnail(originalImage);
            byte[] thumbnailBytes = imageToBytes(thumbnailImage, extension);
            String thumbnailKey = directory + "/thumbnail_" + fileName;
            String thumbnailUrl = uploadToS3(thumbnailBytes, "image/" + extension, thumbnailKey);

            // 응답 생성
            FileResponse response = FileResponse.builder()
                    .originalFileName(fileName)
                    .fileName(fileName)
                    .filePath(path)
                    .extension(extension)
                    .imageUrl(fileUrl)
                    .size(fileSize)
                    .mimeType(contentType)
                    .uploadDate(LocalDateTime.now())
                    .uploadedBy("social")
                    .thumbnailUrl(thumbnailUrl)
                    .isPublic(true)
                    .checksum(generateChecksum(checksumStream))
                    .build();

            return mapper != null ? mapper.apply(response) : (T) response;

        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {}
            }
        }
    }


    // S3 업로드 (InputStream 방식)
    private String uploadToS3(InputStream inputStream, long fileSize, String contentType, String objectKey) throws IOException {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(objectKey)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, fileSize));

            return "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + objectKey;
        } catch (S3Exception e) {
            throw new IOException("S3 업로드 실패: " + e.getMessage());
        }
    }

    // S3 업로드 (Byte Array 방식 - 썸네일 업로드)
    private String uploadToS3(byte[] fileBytes, String contentType, String objectKey) throws IOException {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(objectKey)
                    .contentType(contentType)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));

            return "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + objectKey;
        } catch (S3Exception e) {
            throw new IOException("S3 썸네일 업로드 실패: " + e.getMessage());
        }
    }


    // 디렉토리 생성 (년/월/일 폴더 구조)
    public String createDirectory(Directory subDirectory) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("/yyyy/MM/dd");
        String s = subDirectory + dateFormat.format(new Date());
        return s;
    }

    // 파일명 생성 (yyyyMMddHHmmssSSS + 3자리 랜덤 숫자)
    public String generateFileName(String fileExtension) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(new Date()) + "_" + (new Random().nextInt(900) + 100) + "." + fileExtension;
    }

    // 파일 확장자 검증
    public boolean isValidExtension(String extension) {
        return Arrays.asList("png", "jpg", "jpeg", "gif").contains(extension.toLowerCase());
    }

    // 파일 확장자 추출
    private String getFileExtension(String fileName) {
        String noParams = fileName.split("\\?")[0]; // 쿼리 제거
        return noParams.substring(noParams.lastIndexOf('.') + 1);
    }


    private String getFileExtensionFromUrl(String url) {
        String path = url.contains("?") ? url.substring(0, url.indexOf("?")) : url;
        return path.substring(path.lastIndexOf('.') + 1).toLowerCase();
    }


    // 썸네일 생성
    private BufferedImage createThumbnail(BufferedImage originalImage) {
        int thumbnailWidth = 100;
        int thumbnailHeight = 100;
        BufferedImage thumbnail = new BufferedImage(thumbnailWidth, thumbnailHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = thumbnail.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, thumbnailWidth, thumbnailHeight, null);
        graphics2D.dispose();
        return thumbnail;
    }

    // 이미지 -> Byte Array 변환
    private byte[] imageToBytes(BufferedImage image, String formatName) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    /**
     * `SHA-256` 체크섬(해시) 생성 함수
     *
     * @param InputStream 업로드된 `MultipartFile 의 InputStream`
     * @return 파일의 SHA-256 해시 값 (Hexadecimal String)
     * @throws IOException 파일 읽기 오류 발생 시
     */
    public static String generateChecksum(InputStream inputStream) throws IOException {
        try  {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192]; // 8KB 버퍼
            int bytesRead;

            // 스트림을 읽으면서 해시값 계산
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            // 해시 결과를 Hex 문자열로 변환
            return bytesToHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
        }
    }

    /**
     *  바이트 배열을 16진수 문자열(Hex)로 변환
     *
     * @param hash SHA-256 해시 바이트 배열
     * @return 16진수 문자열 (Hex)
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0'); // 한 자리 수일 경우 앞에 0 추가
            hexString.append(hex);
        }
        return hexString.toString();
    }


    //todo file read
    public ResponseInputStream<GetObjectResponse> read() {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key("profile/2024/03/27/1/20240327162325842891.jpg")
                .build();
        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);
        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
        return object;

    }

}
