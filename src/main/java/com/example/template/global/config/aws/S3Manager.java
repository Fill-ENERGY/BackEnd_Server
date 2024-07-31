package com.example.template.global.config.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.template.global.util.s3.entity.Uuid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Manager {

    private final AmazonS3 amazonS3;

    private final S3Config amazonConfig;

    // 어떤 디렉토리의 어떤 식별자인지는 KeyName으로 지정
    public String uploadFile(String keyName, MultipartFile file){
        // 원본 파일 이름 가져오기
        String originalFilename = file.getOriginalFilename();
        // 확장자 가져오기
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 업로드할 파일의 사이즈를 S3에 알려주기 위함
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType()); // Content-Type 설정(브라우저에서 바로 열어볼 수 있도록)

        try {
            // S3 API 메소드(putObject)로 파일 Stream을 열어서 S3에 파일 업로드
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName + extension, file.getInputStream(), metadata));
        } catch (IOException e){
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
        }

        // S3에 업로드된 사진 url 가져오기
        return amazonS3.getUrl(amazonConfig.getBucket(), keyName + extension).toString();
    }

    // 파일 삭제
    public void deleteFile(String fileUrl) {
        try {
            // 파일 URL에서 버킷 이름과 키를 추출
            AmazonS3URI uri = new AmazonS3URI(fileUrl);
            String bucket = uri.getBucket();
            String key = uri.getKey();

            // 파일 삭제
            amazonS3.deleteObject(bucket, key);
        } catch (IllegalArgumentException e) {
            log.error("error at AmazonS3Manager deleteFile : {}", (Object) e.getStackTrace());
        }
    }

    // KeyName을 만들어서 리턴 해주는 메서드 - 파일 이름이 중복되지 않게 경로와 uuid 값 연결

    // profile 디렉토리
    public String generateProfileKeyName(Uuid uuid) {
        return amazonConfig.getProfilePath() + '/' + uuid.getUuid();
    }

    // message 디렉토리
    public String generateMessageKeyName(Uuid uuid) {
        return amazonConfig.getMessagePath() + '/' + uuid.getUuid();
    }

    // message 디렉토리
    public String generateBoardKeyName(Uuid uuid) {
        return amazonConfig.getBoardPath() + '/' + uuid.getUuid();
    }

    // message 디렉토리
    public String generateReviewKeyName(Uuid uuid) {
        return amazonConfig.getReviewPath() + '/' + uuid.getUuid();
    }

    // message 디렉토리
    public String generateComplaintKeyName(Uuid uuid) {
        return amazonConfig.getComplaintPath() + '/' + uuid.getUuid();
    }
}
