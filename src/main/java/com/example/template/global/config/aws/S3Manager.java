package com.example.template.global.config.aws;

import com.example.template.global.util.s3.entity.Uuid;
import com.example.template.global.util.s3.exxception.S3ErrorCode;
import com.example.template.global.util.s3.exxception.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Manager {

    private final S3Client s3Client;

    private final S3Config amazonConfig;

    // 단일 파일 업로드
    public String uploadFile(String keyName, MultipartFile file){
        // 원본 파일 이름 가져오기
        String originalFilename = file.getOriginalFilename();
        // 확장자 가져오기
        String extension;
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            extension = "";
        }

        // PutObjectRequest를 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(amazonConfig.getBucket())
                .key(keyName + extension)
                .contentType(file.getContentType())
                .build();

        try {
            // S3 API 메소드(putObject)로 파일 Stream을 열어서 S3에 파일 업로드
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            // S3에 업로드된 파일의 URL을 가져오기
            URL url = s3Client.utilities().getUrl(b -> b.bucket(amazonConfig.getBucket()).key(keyName + extension));
            return url.toString();
        } catch (IOException e){
            log.error("error at AmazonS3Manager uploadFile : {}", e.getMessage(), e);
            throw new S3Exception(S3ErrorCode.UPLOAD_FAILED);
        }
    }

    // 다중 파일 업로드
    public List<String> uploadFiles(List<String> keyNames, List<MultipartFile> files) {
        if (files.size() != keyNames.size()) {
            throw new S3Exception(S3ErrorCode.SIZE_MISMATCH);
        }

        List<String> uploadedFileUrls = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String fileUrl = uploadFile(keyNames.get(i), files.get(i));
            uploadedFileUrls.add(fileUrl);
        }

        return uploadedFileUrls;
    }

    // 파일 삭제
    public void deleteFile(String fileUrl) {
        try {
            // 파일 URL에서 버킷 이름과 키를 추출
            URL url = new URL(fileUrl);
            String bucket = url.getHost().split("\\.")[0];
            String key = url.getPath().substring(1);

            // DeleteObjectRequest를 생성하여 파일 삭제
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (IOException e) {
            log.error("error at S3Manager deleteFile: {}", e.getMessage(), e);
            throw new S3Exception(S3ErrorCode.DELETE_FAILED);
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

    // board 디렉토리
    public String generateBoardKeyName(Uuid uuid) {
        return amazonConfig.getBoardPath() + '/' + uuid.getUuid();
    }

    // review 디렉토리
    public String generateReviewKeyName(Uuid uuid) {
        return amazonConfig.getReviewPath() + '/' + uuid.getUuid();
    }

    // complaint 디렉토리
    public String generateComplaintKeyName(Uuid uuid) {
        return amazonConfig.getComplaintPath() + '/' + uuid.getUuid();
    }
}
