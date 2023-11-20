package tct.lib.storage.ftp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tct.lib.storage.abstraction.FileUtil;
import tct.lib.storage.abstraction.PublicMode;
import tct.lib.storage.abstraction.StorageAdapter;

import java.io.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FtpStorageAdapter extends StorageAdapter {

    static Logger logger = LoggerFactory.getLogger(FtpStorageAdapter.class);

    String host;
    int port;
    String username, password;

    public FTPClient getClient() throws IOException {
        FTPClient client = new FTPClient();
        client.connect(host, port);
        client.login(username, password);
        return client;
    }


    public boolean uploadFile(String fileId, InputStream stream, PublicMode isPublic) throws IOException {
        FTPClient ftpClient = getClient();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        String serverFolder = FileUtil.getFolderPath(fileId);
        makeFolders(ftpClient, serverFolder);
        ftpClient.enterLocalPassiveMode();
        return ftpClient.storeFile(FilenameUtils.getName(fileId), stream);
    }

    public boolean isExistFile(String fileId) throws IOException {
        FTPClient ftpClient = getClient();
        try {
            ftpClient.changeWorkingDirectory("/");
            String folderPath = FileUtil.getFolderPath(fileId);
            ftpClient.changeWorkingDirectory(folderPath);
            return ftpClient.listFiles(fileId).length > 0;
        } finally {
            closeFtpClient(ftpClient);
        }

    }

    protected static void closeFtpClient(FTPClient client) {
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
            } catch (IOException e) {
                logger.error("Disconnect Error", e);
            }
        }
    }

    public void deleteFile(String fileId) throws IOException {
        FTPClient ftpClient = getClient();
        try {
            ftpClient.deleteFile(fileId);
        } finally {
            closeFtpClient(ftpClient);
        }
    }

    @Override
    public void deleteDirectory(String s) throws IOException {
        //TODO
    }

    public void makeFolders(FTPClient client, String folder) throws IOException {
        String[] subFolders = folder.split(File.separator);
        for (String subFolder : subFolders) {
            client.makeDirectory(subFolder);
            client.changeWorkingDirectory(subFolder);
        }
    }


    public InputStream getFile(String fileId) throws IOException {
        FTPClient ftpClient = null;

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            logger.info("get file: " + fileId);
            String folderPath = FileUtil.getFolderPath(fileId);
            ftpClient = getClient();
            ftpClient.enterRemotePassiveMode();
            ftpClient.changeWorkingDirectory(folderPath);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.retrieveFile(FileUtil.getFileName(fileId), outputStream);
            outputStream.flush();
            return new ByteArrayInputStream(outputStream.toByteArray());
        } finally {
            closeFtpClient(ftpClient);
        }
    }

}
