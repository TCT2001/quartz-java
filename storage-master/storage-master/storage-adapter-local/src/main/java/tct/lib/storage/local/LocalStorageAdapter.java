package tct.lib.storage.local;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tct.lib.storage.abstraction.FileUtil;
import tct.lib.storage.abstraction.PublicMode;
import tct.lib.storage.abstraction.StorageAdapter;

import java.io.*;


public class LocalStorageAdapter extends StorageAdapter {

    static Logger logger = LoggerFactory.getLogger(LocalStorageAdapter.class);

    public boolean uploadFile(String fileId, InputStream stream, PublicMode isPublic) throws IOException {
        String folder = FileUtil.getFolderPath(fileId);
        new File(folder).mkdirs();
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileId)) {
            IOUtils.copy(stream, fileOutputStream);
        }
        return true;
    }

    public boolean isExistFile(String fileId) throws IOException {
        return new File(fileId).exists();
    }

    public void deleteFile(String fileId) throws IOException {
        new File(fileId).delete();
    }

    @Override
    public void deleteDirectory(String directoryId) throws IOException {
        FileUtils.deleteDirectory(new File(directoryId));
    }

    public InputStream getFile(String fileId) throws IOException {
        return new FileInputStream(new File(fileId));
    }

    public static void main(String[] args) {
        LocalStorageAdapter storageAdapter = new LocalStorageAdapter();
        try {
            storageAdapter.uploadFile("folder/test", new ByteArrayInputStream("abcdeabcdeabcdeabcdeabcdeabcde".getBytes()));
            System.out.println(new String(IOUtils.toByteArray(storageAdapter.getFile("test")).clone()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
