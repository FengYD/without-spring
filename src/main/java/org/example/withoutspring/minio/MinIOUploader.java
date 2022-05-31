package org.example.withoutspring.minio;

import io.minio.*;
import io.minio.errors.MinioException;

import javax.imageio.stream.FileImageInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author fengyadong
 * @date 2022/5/30 18:05
 * @Description
 */
public class MinIOUploader {

    private MinioClient client;

    public MinIOUploader(String endPoint, String accessKey, String secretKey) {
        client = MinioClient.builder()
                .endpoint(endPoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public MinIOUploader(MinioClient client) {
        this.client = client;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            MinIOUploader uploader = new MinIOUploader("https://play.min.io", "Q3AM3UQ867SPQQA43P2F",
                    "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG");
            MinioClient minioClient = uploader.client;
            // 检查存储桶是否已经存在
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket("asiatrip").build();
            boolean isExist = minioClient.bucketExists(bucketExistsArgs);
            if(isExist) {
                System.out.println("Bucket exists.");
            } else {
                // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket("asiatrip").build();
                minioClient.makeBucket(makeBucketArgs);
                System.out.println("create Bucket.");
            }

            // 使用putObject上传一个文件到存储桶中。
            File file = new File("C:\\Users\\fengy\\Downloads\\Api接口调用操作指南.pdf");
            FileImageInputStream stream = new FileImageInputStream(file);
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket("asiatrip")
                    .object("book.pdf")
                    .stream(new FileInputStream(file), file.length(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
            System.out.println("Api接口调用操作指南.pdf is successfully uploaded as book.pdf to `asiatrip` bucket.");
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }

}
