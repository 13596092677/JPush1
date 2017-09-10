package com.example.upload;


public class demo {
    public static void main(String[] args) {
        UploadUtil uploadUtil = new UploadUtil();
        String filePath = "C:\\dir";
        String requestUrl = "http://xueshu.qukaa.com/topic/ajax/upload_topic_pic/";
        uploadUtil.loop(filePath,requestUrl);
    }
}
