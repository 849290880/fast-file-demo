package com.example.fastfiledemo;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploadFileUnit {

    public static void main(String[] args) {
        int _400kb = 400*1024;
        String filePrefix = "F:/github/fast-file-demo/file/";
        File mp4 = new File("C:\\Users\\84929\\Desktop\\11月10日.mp4");
        long count = mp4.length() % _400kb == 0?mp4.length()/_400kb:mp4.length()/_400kb+1;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (long i = 0; i < count; i++) {
            long finalI = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    RestTemplate restTemplate = new RestTemplate();
                    String url = "http://localhost:8080/uploadChunk";

                    //读取本地文件  "D:/upload/file";
                    String filePath = filePrefix + finalI +".mp4";
                    FileSystemResource fileSystemResource = new FileSystemResource(filePath);

                    MultiValueMap<String,Object> params = new LinkedMultiValueMap<>();
                    HttpHeaders headers = new HttpHeaders();
                    params.add("chunk",fileSystemResource);
                    params.add("order", finalI);
                    params.add("url","/file/11月10日.mp4");
                    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                    HttpEntity<MultiValueMap<String,Object>> requestEntity  = new HttpEntity<>(params, headers);
                    String result = restTemplate.postForObject(url,requestEntity ,String.class);
                    System.out.println(result);
                }
            });
        }
        executorService.shutdown();
    }

}
