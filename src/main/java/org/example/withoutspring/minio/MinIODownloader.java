package org.example.withoutspring.minio;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author fengyadong
 * @date 2022/5/30 18:05
 * @Description
 */
public class MinIODownloader {

    private MinioClient client;

    public MinIODownloader(String endPoint, String accessKey, String secretKey) {
        client = MinioClient.builder()
                .endpoint(endPoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public MinIODownloader(MinioClient client) {
        this.client = client;
    }

    /** MinioClient.getObject() example. */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            /* play.min.io for test and development. */
            MinIODownloader downloader = new MinIODownloader("https://play.min.io", "Q3AM3UQ867SPQQA43P2F",
                    "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG");
            MinioClient minioClient = downloader.client;

            // Get input stream to have content of 'my-objectname' from 'my-bucketname'
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket("asiatrip")
                    .object("book.pdf")
                    .build());

            // Read the input stream and print to the console till EOF.
            byte[] buf = new byte[16384];
            int bytesRead;
            while ((bytesRead = stream.read(buf, 0, buf.length)) >= 0) {
                System.out.println(new String(buf, 0, bytesRead, StandardCharsets.UTF_8));
            }

            // Close the input stream.
            stream.close();
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }

}
