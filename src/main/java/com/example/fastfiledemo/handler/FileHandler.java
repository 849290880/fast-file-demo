package com.example.fastfiledemo.handler;

import com.example.fastfiledemo.bo.FileChunkBO;
import com.example.fastfiledemo.bo.FileReqBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class FileHandler{

    private Map<String, BlockingQueue<FileChunkBO>> queueMap;

    private static final long _400kb = 400 * 1024;

    private static final long _1GB = 1024 * 1024 * 1024;

    public FileHandler(){
        this.queueMap = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) {
        //总记录数
        int size= 1024 * 5;
        //每页显示的记录数
        int _4kb= 1024 * 4;
        //页数
        int pageSum=(size-1)/_4kb+1;
        System.out.println(pageSum);
    }

    public void createQueueByUrl(String url, FileReqBO fileReqBO){
        BlockingQueue<FileChunkBO> queue = new LinkedBlockingQueue<>();
        queueMap.put(url,queue);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File("F:/github/fast-file-demo/file/"+fileReqBO.getFilename());
                    MappedByteBuffer mapperByteBuffer = new RandomAccessFile(file, "rw").getChannel()
                            .map(FileChannel.MapMode.READ_WRITE, 0, fileReqBO.getSize());
                    Long size = fileReqBO.getSize();
                    long count = size % _400kb == 0?size/_400kb:size/_400kb+1;
                    AtomicLong countAtomic = new AtomicLong(count);
                    do{
                        try {
                            FileChunkBO fileChunkBO = queue.take();
                            Integer order = fileChunkBO.getOrder();
                            log.info("一块文件,文件序号:{}",order);
                            byte[] bytes = null;
                            if(order+1==count){
                                bytes = new byte[(int) (size % _400kb)];
                            }else {
                                bytes = new byte[(int) _400kb];
                            }

                            fileChunkBO.getFileInputStream().read(bytes);
                            long src = order * _400kb;
                            mapperByteBuffer.position((int) src);
                            mapperByteBuffer.put(bytes);
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        }
                    }while (countAtomic.decrementAndGet()>0);
                    log.info("发送结束");
                    queueMap.remove(url,queue);
                    mapperByteBuffer.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },"mergerFile").start();
    }


    public void addToQueue(FileChunkBO fileChunkBO) {
        BlockingQueue<FileChunkBO> fileChunkBOS = queueMap.get(fileChunkBO.getUrl());
        try {
            fileChunkBOS.put(fileChunkBO);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
