package com.example.fastfiledemo;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileSplitUnit {

    public static void main(String[] args) throws IOException {
        int _400kb = 400*1024;
        String filePrefix = "F:/github/fast-file-demo/file/";
        File mp4 = new File("C:\\Users\\84929\\Desktop\\11月10日.mp4");
        FileChannel channel = new FileInputStream(mp4).getChannel();
        long count = mp4.length() % _400kb == 0?mp4.length()/_400kb:mp4.length()/_400kb+1;
        for (long i = 0; i < count; i++) {
            File file = new File(filePrefix + i+".mp4");
            if (i == count - 1) {
                long lastSize = mp4.length() % _400kb;
                channel.transferTo(i*_400kb,lastSize,new FileOutputStream(file).getChannel());
                break;
            }
            channel.transferTo(i*_400kb,_400kb,new FileOutputStream(file).getChannel());
        }
        System.out.println("ok");
    }

}
