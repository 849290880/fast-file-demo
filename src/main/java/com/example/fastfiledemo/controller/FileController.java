package com.example.fastfiledemo.controller;

import com.example.fastfiledemo.bo.FileChunkBO;
import com.example.fastfiledemo.bo.FileReqBO;
import com.example.fastfiledemo.handler.FileHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@Slf4j
public class FileController {

    @Autowired
    private FileHandler fileHandler;

    @PostMapping("uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile){

        File dest = new File("F:/github/fast-file-demo/file/" + multipartFile.getOriginalFilename());
        try {
            multipartFile.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @PostMapping("uploadFileUrl")
    public String uploadFileUrl(@RequestBody @Validated  FileReqBO fileReqBO){
        String url = "/file/" + fileReqBO.getFilename();
        fileHandler.createQueueByUrl(url,fileReqBO);
        return url;
    }

    @PostMapping("uploadChunk")
    public String uploadChunk(@RequestParam("chunk") MultipartFile multipartFile,
                              @RequestParam("order") String order,
                              @RequestParam("url") String url) throws IOException {
        FileChunkBO fileChunkBO = new FileChunkBO(multipartFile.getInputStream(), Integer.valueOf(order), url);
        fileHandler.addToQueue(fileChunkBO);
        return "OK";
    }


}
