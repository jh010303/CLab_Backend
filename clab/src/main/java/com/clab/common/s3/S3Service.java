package com.clab.common.s3;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

	private final S3Client s3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region.static}")
	private String region;

	public String upload(String folder, MultipartFile file) {
		String key = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

		try {
			PutObjectRequest request = PutObjectRequest.builder()
					.bucket(bucket)
					.key(key)
					.contentType(file.getContentType())
					.build();

			s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
		} catch (IOException e) {
			log.error("S3 업로드 실패: {}", key, e);
			throw new CustomException(ErrorCode.S3_UPLOAD_FAILED);
		}

		return getUrl(key);
	}

	public void delete(String url) {
		String key = extractKey(url);

		try {
			DeleteObjectRequest request = DeleteObjectRequest.builder()
					.bucket(bucket)
					.key(key)
					.build();

			s3Client.deleteObject(request);
		} catch (Exception e) {
			log.error("S3 삭제 실패: {}", key, e);
			throw new CustomException(ErrorCode.S3_DELETE_FAILED);
		}
	}

	private String getUrl(String key) {
		return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
	}

	private String extractKey(String url) {
		String prefix = "https://" + bucket + ".s3." + region + ".amazonaws.com/";
		return url.substring(prefix.length());
	}
}
