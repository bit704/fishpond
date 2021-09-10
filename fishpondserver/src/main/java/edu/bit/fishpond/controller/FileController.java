package edu.bit.fishpond.controller;


import edu.bit.fishpond.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileController {
    @Value("${upload.file.path}")
    private String fileStoragePath;

    private final MessageService messageService;

    private final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    public FileController(MessageService messageService){
        this.messageService = messageService;
    }

    @PostMapping("/upload")
    public @ResponseBody String upload(@RequestParam MultipartFile file, @RequestParam String filename,
                                        @RequestParam int id)
    {
        if(file == null || file.isEmpty() || filename == null || filename.isEmpty())
            return "";
        try
        {
            byte[] bytes = file.getBytes();
            Path personUploadPath = Paths.get(fileStoragePath, String.valueOf(id));
            if (!personUploadPath.toFile().exists()) {
                boolean res = personUploadPath.toFile().mkdirs();
                if (!res) {
                    return "";
                }
            }
            String localFileName = filename;
            File localFile = Paths.get(fileStoragePath, String.valueOf(id)).resolve(localFileName).toFile();
            int index = 1;
            while (localFile.exists()) {
                String name = filename.substring(0, filename.lastIndexOf("."));
                String extensionName = filename.substring(filename.lastIndexOf("."));
                localFileName = name.concat("(" + index + ")").concat(extensionName);
                localFile = Paths.get(fileStoragePath, String.valueOf(id)).resolve(localFileName).toFile();
                index++;
            }
            FileOutputStream outputStream = new FileOutputStream(localFile);
            outputStream.write(bytes);
            outputStream.close();

            logger.info("upload file , filename is " + localFile.getName());

            return localFile.getName();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "";
        }
    }

    @PostMapping("/download")
    public ResponseEntity<FileSystemResource> download(@RequestParam int messageId)
    {
        String filename = messageService.getFileName(messageId);
        //String filename = "20181575/test1.pdf";
        if(filename == null || filename.isEmpty())
            return null;
        File file = Paths.get(fileStoragePath, filename).toFile();
        if(file.exists() && file.canRead())
        {
            logger.info("download file , filename is "+filename);
            return ResponseEntity.ok().contentType(MediaType.MULTIPART_FORM_DATA).body(new FileSystemResource(file));
        }
        else
            return null;
    }

    @PostMapping("/download2")
    public ResponseEntity<FileSystemResource> download(@RequestParam String filename)
    {
        if(filename == null || filename.isEmpty())
            return null;
        File file = Paths.get(fileStoragePath, "20181575").resolve(filename).toFile();
        if(file.exists() && file.canRead())
        {
            System.out.println("download file , filename is "+filename);
            return ResponseEntity.ok().contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(new FileSystemResource(file));
        }
        else
            return null;
    }
}
