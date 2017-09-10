package com.example.upload;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.LinkedList;
import java.util.UUID;

public class UploadUtil {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10*1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    private static String sessionid;
    public static void loop(final String filePath,final String requestUrl) {
        LinkedList<File> list = null;
        LinkedList<File> list1 = null;
        int fileNum = 0;
        int folderNum = 0;
        int fileName = 301;
        File file = new File(filePath);
        if (file.exists()) {
            list = new LinkedList<File>();
            list1 = new LinkedList<File>();
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    System.out.println("文件夹:" + file2.getAbsolutePath());
                    list.add(file2);
                    folderNum++;
                } else {
                    if (Integer.parseInt(file2.getName().substring(0, file2.getName().indexOf("."))) != fileName) {
                        System.out.println("缺失的文件是:"+fileName);
                        fileName++;
                    }
                    System.out.println("文件:" + file2.getAbsolutePath());
                    list1.add(file2);
                    fileNum++;
                    fileName++;
                }
            }
            File temp_file;
            while (!list.isEmpty()) {
                temp_file = list.removeFirst();
                files = temp_file.listFiles();
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        System.out.println("文件夹:" + file2.getAbsolutePath());
                        list.add(file2);
                        folderNum++;
                    } else {
                        System.out.println("文件:" + file2.getAbsolutePath());
                        fileNum++;
                    }
                }
            }

        } else {
            System.out.println("文件不存在!");
        }
        System.out.println(list1.size());
        for (int i = 0; i < list1.size(); i++) {
            File result = list1.get(i);
            uploadFile(result, requestUrl);
        }
//        System.out.println("文件夹共有:" + folderNum + ",文件共有:" + fileNum);
    }
    public static void login() {
        try {
            String url = "http://xueshu.qukaa.com/api/account/login_process/";
            URL httpUrl = null;
            httpUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            String content = "user_name=" + "networks" + "&password=" + "NETworks";
            out.write(content.getBytes());
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            String responseCookie = conn.getHeaderField("Set-Cookie");
            System.out.println(responseCookie);
            if (responseCookie != null) {
                sessionid = responseCookie.substring(0, responseCookie.indexOf(";"));
                System.out.println(sessionid);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * android上传文件到服务器
     * @param file  需要上传的文件
     * @param RequestURL  请求的rul
     * @return  返回响应的内容
     */
    public static String uploadFile(final File file, final String RequestURL){
        login();
        String result = null;
        final String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
        final String PREFIX = "--" , LINE_END = "\r\n";
        final String CONTENT_TYPE = "multipart/form-data";   //内容类型
        new Thread() {
            public void run() {
                try {
                    URL url = new URL(RequestURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    if (sessionid != null) {
                        conn.setRequestProperty("Cookie", sessionid);
                    }
                    conn.setReadTimeout(TIME_OUT);
                    conn.setConnectTimeout(TIME_OUT);
                    conn.setDoInput(true);  //允许输入流
                    conn.setDoOutput(true); //允许输出流
                    conn.setUseCaches(false);  //不允许使用缓存
                    conn.setRequestMethod("POST");  //请求方式
                    conn.setRequestProperty("Charset", CHARSET);  //设置编码
                    conn.setRequestProperty("connection", "keep-alive");
                    conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
                    
                    conn.connect();
                    
                    if(file != null){
                        /**
                         * 当文件不为空，把文件包装并且上传
                         */
                        DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
                        StringBuffer sb = new StringBuffer();
                        sb.append(LINE_END);
                        sb.append(PREFIX);
                        sb.append(BOUNDARY);
                        sb.append(LINE_END);
                        /**
                         * 这里重点注意：
                         * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                         * filename是文件的名字，包含后缀名的   比如:abc.png
                         */

                        sb.append("Content-Disposition: form-data; name=\"aws_upload_file\"; filename=\""+file.getName()+"\""+LINE_END);
                        sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
                        sb.append(LINE_END);
//                        System.out.print(sb.toString());
                        dos.write(sb.toString().getBytes());
                        InputStream is = new FileInputStream(file);
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        while((len=is.read(bytes))!=-1){
                            dos.write(bytes, 0, len);
                        }
                        is.close();
                        dos.write(LINE_END.getBytes());
                        byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                        dos.write(end_data);
                        /**
                         * 获取响应码  200=成功
                         * 当响应成功，获取响应的流
                         */
                        OutputStream out = conn.getOutputStream();
                        StringBuffer strBuf = new StringBuffer();
                        strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\"" + "topic_id" + "\"\r\n\r\n");
                        strBuf.append(file.getName().substring(0,file.getName().indexOf(".")));
                        out.write(strBuf.toString().getBytes());
//                        System.out.print(strBuf.toString());
                        int res = conn.getResponseCode();
                        if(res==200){
                            InputStream input =  conn.getInputStream();
                            StringBuffer sb1= new StringBuffer();
                            int ss ;
                            while((ss=input.read())!=-1){
                                sb1.append((char)ss);
                            }
                            String result = sb1.toString();
                            System.out.println(file.getName());
                                System.out.println(result);
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return result;
    }
    public static void main(String[] args) throws IOException {
        String filePath = "C:\\dir";
        String requestUrl = "http://xueshu.qukaa.com/topic/ajax/upload_topic_pic/";
        loop(filePath,requestUrl);
    }
}
