package com.example.file;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    public static void main(String[] args) throws IOException {
        File file = new File("D:\\folder.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        File child = new File(file, "child");
        
        
    }
}
