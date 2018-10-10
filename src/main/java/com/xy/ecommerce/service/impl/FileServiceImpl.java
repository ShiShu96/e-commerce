package com.xy.ecommerce.service.impl;

import com.google.common.collect.Lists;
import com.xy.ecommerce.service.FileService;
import com.xy.ecommerce.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private static Logger logger=LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path){
        String fileName=file.getOriginalFilename();

        String fileExtension=fileName.substring(fileName.lastIndexOf("."));
        String uploadFileName=UUID.randomUUID().toString()+"."+fileExtension;

        File dir=new File(path);
        if(!dir.exists()){
            dir.setWritable(true);
            dir.mkdirs();
        }

        File targetFile=new File(path, uploadFileName);

        try {
            file.transferTo(targetFile);
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            targetFile.delete();
        }catch (IOException e){
            logger.error(e.getMessage());
        }

        return targetFile.getName();
    }

}
