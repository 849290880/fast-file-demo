package com.example.fastfiledemo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileChunkBO {

    private InputStream fileInputStream;

    private Integer order;

    private String url;



}
