package org.example.withoutspring.minio;

/**
 * @author fengyadong
 * @date 2022/8/5 14:12
 * @Description
 */
public class Test {

    public static void main(String[] args) throws Exception {
        String path = "D:\\file\\codemap";
        String fileName = MinIOUtils.upload(path);

        String dstPath = "D:\\file\\map";
        MinIOUtils.download(dstPath, "codemap");
    }

}
