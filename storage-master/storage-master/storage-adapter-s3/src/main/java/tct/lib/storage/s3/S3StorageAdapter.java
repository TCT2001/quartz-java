package tct.lib.storage.s3;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import tct.lib.storage.abstraction.PublicMode;
import tct.lib.storage.abstraction.StorageAdapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Slf4j
public class S3StorageAdapter extends StorageAdapter {

    private final S3Client s3Client;
    private final String bucket;

    public S3StorageAdapter(String bucketName, S3Client s3Client) {
        // Way to Create S3Client
//        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
//        s3Client = S3Client.builder()
//                .region(Region.of(region))
//                .endpointOverride(URI.create(endpointUrl))
//                .credentialsProvider(StaticCredentialsProvider.create(credentials))
//                .build();
        this.s3Client = s3Client;
        if (!isBucketExists(bucketName)) {
            throw new RuntimeException("Bucket not found: " + bucketName);
        }
        bucket = bucketName;
    }

    public boolean isBucketExists(String bucketName) {
        HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                .bucket(bucketName)
                .build();
        try {
            s3Client.headBucket(headBucketRequest);
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }

    @Override
    public boolean uploadFile(String fileId, InputStream fileStream, PublicMode isPublic) throws IOException {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileId)
                .build();

        s3Client.putObject(objectRequest, RequestBody.fromBytes(fileStream.readAllBytes()));
        if (PublicMode.PUBLIC.equals(isPublic)) {
            makePublic(fileId);
        }
        return false;
    }

    @Override
    public boolean isExistFile(String fileId) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder().bucket(bucket).key(fileId).build();
        try {
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    @Override
    public void deleteFile(String fileId) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileId)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public void deleteDirectory(String directoryId) {
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket(bucket).prefix(directoryId).build();
        for (S3Object file : s3Client.listObjects(listObjectsRequest).contents()) {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(file.key()).build());
        }
    }

    @Override
    public InputStream getFile(String fileId) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileId)
                .build();

        return s3Client.getObject(getObjectRequest);
    }

    public void makePublic(String fileId) {
        PutObjectAclRequest aclRequest = PutObjectAclRequest.builder().key(fileId).bucket(bucket).acl(ObjectCannedACL.PUBLIC_READ).build();
        s3Client.putObjectAcl(aclRequest);
    }

    public static void main(String[] args) throws IOException {
        // endpointUrl = API url
        // Create bucket before run this
        AwsCredentials credentials = AwsBasicCredentials.create("123", "12345678");
        S3Client s3Client = S3Client.builder()
                .region(Region.of("ap-southeast-3"))
                .endpointOverride(URI.create("http://172.18.0.3:9000/"))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        S3StorageAdapter s3 = new S3StorageAdapter("avatar", s3Client);
        s3.uploadFile("/dir/test", FileUtils.openInputStream(new File(".gitignore")));
//        System.out.println(s3.isExistFile("data/main/admin@user.com/1610455258642-Ho_c/Ho_c.docx"));
//        FileUtils.copyInputStreamToFile(s3.getFile("data/main/admin@user.com/1610455258642-Ho_c/Ho_c.docx"), new File("xx.txt"));
//        s3.deleteFile("test");
    }
}
