package com.ecommerce.sb_ecom.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

//        Get file names of current / original file
        String originalFileName = file.getOriginalFilename();


        //Generate unique file name
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.separator + fileName;


        //Check if path exists and create
        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }

        //upload files to server
        Files.copy(file.getInputStream(), Paths.get(filePath));

        //Return file name
        return fileName;
    }
}
