package tct.lib.storage.ggstorage;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tct.lib.storage.abstraction.PublicMode;
import tct.lib.storage.abstraction.StorageAdapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GoogleStorageAdapter extends StorageAdapter {

    static Logger logger = LoggerFactory.getLogger(GoogleStorageAdapter.class);

    Bucket bucket;

    public GoogleStorageAdapter(String bucketName, InputStream credentialStream) throws IOException {
        Credentials credentials = GoogleCredentials
                .fromStream(credentialStream);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        bucket = storage.get(bucketName, Storage.BucketGetOption.fields());
    }

    @Override
    public boolean uploadFile(String fileId, InputStream stream, PublicMode isPublic) throws IOException {
        Blob blob = bucket.create(fileId, stream);
        if (isPublic == PublicMode.PUBLIC) makePublic(blob);
        return true;
    }

    @Override
    public boolean isExistFile(String fileId) throws IOException {
        Blob blob = bucket.get(fileId);
        return blob.exists();
    }

    @Override
    public void deleteFile(String fileId) throws IOException {
        Blob blob = bucket.get(fileId);
        blob.delete();
    }

    @Override
    public void deleteDirectory(String s) throws IOException {
        //TODO
    }

    @Override
    public InputStream getFile(String fileId) throws IOException {
        Blob blob = bucket.get(fileId);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            blob.downloadTo(outputStream);
            outputStream.flush();
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }


    public void makePublic(Blob blob) {
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
    }

    public static void main(String[] args) throws IOException {
        String cre = "{\"type\": \"service_account\",\"project_id\": \"fulfillmenthubtest\",\"private_key_id\": \"d67e530d60bc23587a5dc291b27026a0d156d5fc\",\"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDy6N0SP6rOizMT\\nD7rkc5ZLJRZclRnBkbrF+FWn7+GPlxft+3Yjd2P9XjzILqMDc/4ZsprrvVGTvsAp\\nAw2KMV99eYGdVLkUCZhA1y4Raf7MfSfhQ595E4EUvhaFoOVALEBBLKQPs6/e34J+\\n6u5++rmWZdfDuKmvWoffIBzs4y1PItVQBR2lbzO+3vNrg9c0CuBz0ClxUktYZrUC\\nMJfsewhriwVdbCsFtbfpKszc/0pYuE6yAiZy5z1j3mbDeE6YrDlmE4NBB0ll6A/i\\nz2TbV6UqPVo7Z9CHxUwMvLYxBCD/xGwd0NYa4nvTxoVDTiOI9L1Lw0FT8Diohupk\\nd4sX2iCvAgMBAAECggEAASgKpWOYNJOFEroocZ37QTBUAuzSypEfCoI8JiiGMmDN\\nxFxGG7UdzL7+GxXdwM50jzrCcgzcro9h6tAQ6G+DvN0vOMiGeQnyp5xeo8aVZ/PQ\\nlMn1oGXvX2HoSYqSjNOpGqZTwZ9hXvZBcMk9iW04JPcnJ89hP2x26SEGAjqypIXL\\njkTt9naN4F3iRmrpDi7qmFMoQEAu5snkGdFK+NCvCSi6ctz7xCkS4QHqIESST/eb\\nEkgynkrJBh2MFI78aDYgf0QliD1l42kEdaTayjDTWzeXVuNpYLFBTmZFATgvOctL\\nzQobxqQwgW7i4/6yDA1LDX+qo/69Hw+K+Z1NjWvBgQKBgQD9Rz2d1F27ZzflfRYb\\nLvFgQLmfte8Tym8Ko8Cneqcj6ipWoVjnrtLNz8ogREfeO8uIUMSHwfkI1j6oq8MN\\nmBE8wDQ4tQxphQ9TB7+dcBQ2mGKNDvAeN/ESN8xfe8247cFdzPVC1ncUv+dZFROg\\np9FRnUWX8pxlr+e5qJA7/kXSJwKBgQD1hRlcNmM+Jp5Sm2eaTVPwaXH2hoS7riM+\\nTOIpyrnCLDzm3VyXu/DUsvukPeNBARg6/BjHUJlu5CE5FTEt9/ZNjl6i2FhOzg1b\\nfhX8txDuiN9EkzxmvUIs/KvniZHwSwUHefUpoFhpBz4WH8yHMHLsrEJ4bVLoeBOs\\ngJk5LDK6OQKBgBl8yMuQKlqIeYyDeO0FXU7lVIio40UnkuMMNX/lDNrFXrtXAz5V\\nTmtdpKZ4mI7Zj5LJJe3PbEMiZC3PExLUIa/uW5L1TSl1NWSSnAq405/m18wAG9O+\\np2jA6rUVHU+lxVauIGQ4dsVExoAladM58At5ex5eULS+7d+2AcW3wcjzAoGBAOc5\\nX6XpjtOkmSyhMRQvyWK8W9jZ3T3IBWcSD1lelE1bTkv6o84+8NPQcI/AvTqmJkS+\\n0TM9JBM8hFF3NObfBTcLYvMonxowkoHdICXt3uNXQjYaGCOGEEHAAMHBXREaBfWB\\nlTOL0Fexu3Yoon2bUlC09rDl/NP06kpmnmcqyed5AoGAVhIUrqFtj3qM9/9VYlsF\\nyp565xrgUIy96Lxu3OGG/PR/K75LK0R5efu6ph9qhf3WchB/l6WA2YC64gPDeuvm\\nU2z001WENOpkvWqOMpTXMKR+zQV6n2MOasoJjFB7ofpKPXfER32xalHw23RJM+oF\\nUVUYerNvWwpsjEG3EzzzXv8=\\n-----END PRIVATE KEY-----\\n\",\"client_email\": \"fulfillment-hub@fulfillmenthubtest.iam.gserviceaccount.com\",\"client_id\": \"114653685465513887207\",\"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\"token_uri\": \"https://oauth2.googleapis.com/token\",\"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/fulfillment-hub%40fulfillmenthubtest.iam.gserviceaccount.com\"}";
        GoogleStorageAdapter storageAdapter = new GoogleStorageAdapter("simidoc-storage", new ByteArrayInputStream(cre.getBytes()));
        try {
            storageAdapter.uploadFile("test", new ByteArrayInputStream("abcdeabcdeabcdeabcdeabcdeabcde".getBytes()));
            System.out.println(new String(IOUtils.toByteArray(storageAdapter.getFile("test")).clone()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
