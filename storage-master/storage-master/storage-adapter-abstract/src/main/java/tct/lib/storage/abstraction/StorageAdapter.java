package tct.lib.storage.abstraction;

import org.apache.commons.io.IOUtils;
import org.xerial.snappy.Snappy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class StorageAdapter {

    public abstract boolean uploadFile(String filePath, InputStream fileStream, PublicMode isPublic) throws IOException;

    public boolean uploadFile(String filePath, InputStream fileStream) throws IOException {
        return uploadFile(filePath, fileStream, CompressMode.NONE, PublicMode.PRIVATE);
    }

    public boolean uploadFile(String filePath, InputStream fileStream, CompressMode compressMode) throws IOException {
        return uploadFile(filePath, fileStream, compressMode, PublicMode.PRIVATE);
    }

    public boolean uploadFile(String filePath, InputStream fileStream, CompressMode isCompress, PublicMode isPublic) throws IOException {
        if (isCompress == CompressMode.COMPRESSED) {
            fileStream = new ByteArrayInputStream(Snappy.compress(IOUtils.toByteArray(fileStream)));
        }
        return uploadFile(filePath, fileStream, isPublic);
    }

    public boolean uploadFile(String filePath, byte[] byteData, CompressMode isCompress, PublicMode isPublic) throws IOException {
        if (isCompress == CompressMode.COMPRESSED) {
            return uploadFile(filePath, new ByteArrayInputStream(Snappy.compress(byteData)), isPublic);
        }
        return uploadFile(filePath, new ByteArrayInputStream(byteData), isPublic);
    }

    public boolean uploadFile(String filePath, byte[] data, CompressMode compressMode) throws IOException {
        return uploadFile(filePath, data, compressMode, PublicMode.PRIVATE);
    }

    public abstract boolean isExistFile(String filePath) throws IOException;

    public abstract void deleteFile(String filePath) throws IOException;

    public abstract void deleteDirectory(String directoryId) throws IOException;

    public abstract InputStream getFile(String filePath) throws IOException;

    public InputStream getFile(String filePath, CompressMode compressMode) throws IOException {
        InputStream stream = getFile(filePath);
        if (compressMode.equals(CompressMode.COMPRESSED)) {
            stream = new ByteArrayInputStream(Snappy.uncompress(IOUtils.toByteArray(stream)));
        }
        return stream;
    }
}
