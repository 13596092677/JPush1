package com.example.HttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HttpClientThread extends Thread{
    private String url;
    private File file;
    private static String sessionid;
    /**
     * Allocates a new {@code Thread} object. This constructor has the same
     * effect as
     * {@code (null, null, gname)}, where {@code gname} is a newly generated
     * name. Automatically generated names are of the form
     * {@code "Thread-"+}<i>n</i>, where <i>n</i> is an integer.
     */
    public HttpClientThread(String url, File file) {
        this.url = url;
        this.file = file;
    }

    private void doHttpGet(){
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse response=httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String content = EntityUtils.toString(response.getEntity());
                System.out.println("content="+content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void doHttpPost() {
        login();
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        //在请求头中设置cookie
        post.setHeader("Cookie",sessionid);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addTextBody("topic_id", file.getName().substring(0,file.getName().indexOf(".")));
        builder.addBinaryBody("aws_upload_file", file, ContentType.DEFAULT_BINARY, file.getName());
        try {
            HttpEntity entity = builder.build();
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String content = EntityUtils.toString(response.getEntity());
                System.out.println(content);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            client.close();
        }
    }

    private void login() {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://xueshu.qukaa.com/api/account/login_process/");
        ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>();
        arrayList.add(new BasicNameValuePair("user_name", "networks"));
        arrayList.add(new BasicNameValuePair("password", "NETworks"));
        try {
            post.setEntity(new UrlEncodedFormEntity(arrayList));
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                List<org.apache.http.cookie.Cookie> cookies = ((AbstractHttpClient) client).getCookieStore().getCookies();
                if (cookies.isEmpty()) {
                    System.out.println("None");
                } else {
                    for (int i = 0; i < cookies.size(); i++) {
                        if ("zlb__user_login".equals(cookies.get(i).getName())) {
                            sessionid = cookies.get(i).getName() + "=" + cookies.get(i).getValue();
                            System.out.println(sessionid);
                            break;
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            client.close();
        }
            
    }
    @Override
    public void run() {
        super.run();
//        doHttpGet();
        doHttpPost();
    }

    public static void main(String[] args) {
        String url = "http://xueshu.qukaa.com/topic/ajax/upload_topic_pic/";
        File file = new File("C:\\dir\\304.png");
        Thread thread = new HttpClientThread(url, file);
        thread.start();
    }
}
