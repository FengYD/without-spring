package org.example.withoutspring.minio;

import io.minio.*;
import org.example.withoutspring.redis.RedisUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author fengyadong
 * @date 2022/8/5 13:59
 * @Description
 */
public class MinIOUtils {

    private static String endpoint;

    private static String accessKey;

    private static String secretKey;

    private static MinioClient client;

    private static String defaultBucket = "ruoyikf";

    static {
        InputStream inputStream = RedisUtils.class.getClassLoader().getResourceAsStream("minio.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("加载配置文件失败");
        }
        MinIOUtils.endpoint = properties.getProperty("endpoint", "http://127.0.0.1:9000");
        MinIOUtils.accessKey = properties.getProperty("accessKey");
        MinIOUtils.secretKey = properties.getProperty("secretKey");

        client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public static String upload (String filePath) throws Exception {
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(defaultBucket).build();
        boolean isExist = client.bucketExists(bucketExistsArgs);
        if(isExist) {
            System.out.println("Bucket exists.");
        } else {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(defaultBucket).build();
            client.makeBucket(makeBucketArgs);
            System.out.println("create Bucket.");
        }
        File file = new File(filePath);
        String saveName = file.getName();
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(defaultBucket)
                .object(saveName)
                .stream(new FileInputStream(file), file.length(), -1)
                .build();
        client.putObject(putObjectArgs);
        return saveName;
    }

    public static void download(String dstPath, String fileName) throws Exception {
        File downLoad = new File(dstPath);
        try(InputStream in = client.getObject(GetObjectArgs.builder().bucket(defaultBucket).object(fileName).build());
            FileOutputStream out = new FileOutputStream(downLoad)
            ) {
            byte[] buf = new byte[16384];
            int bytesRead;
            while ((bytesRead = in.read(buf, 0, buf.length)) >= 0) {
                out.write(buf, 0, bytesRead);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
