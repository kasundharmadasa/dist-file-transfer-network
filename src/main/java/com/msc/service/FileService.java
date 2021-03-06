package com.msc.service;

import com.msc.model.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Random;

@Service
public class FileService {

    @Autowired
    public FileService() {
    }

    public Resource loadFileAsResource(String fileName) {
        Resource resource = null;
        File file = new File(fileName);
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            file.createNewFile();
            Integer randFileSize =
                    new Random().nextInt(CommonConstants.FILE_SIZE_MAX - CommonConstants.FILE_SIZE_MIN)
                            + CommonConstants.FILE_SIZE_MIN;
            raf.setLength(randFileSize);
            Path filePath = file.toPath();
            resource = new UrlResource(filePath.toUri());

            String fileHash;
            try (InputStream fileInputStream = new FileInputStream(file)) {
                fileHash = org.apache.commons.codec.digest.DigestUtils.md5Hex(fileInputStream);
            }

            System.out.println("File size: " + randFileSize + "MB");
            System.out.println("File hash: " + fileHash);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return resource;
    }
}
