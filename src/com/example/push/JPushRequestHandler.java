package com.example.push;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 项目名称：JPush
 * 类描述：推送请求业务逻辑
 * 创建人：刘禹尧
 * 创建时间：2017/8/21 14:38
 * 修改人：刘禹尧
 * 修改时间：2017/8/21 14:38
 * 修改备注：
 */
public class JPushRequestHandler {
    public static void topicPush(int page, int limit) {
        String url = new String("http://xueshu.qukaa.com/notifications/ajax/list_people/");
        try {
            String content = "?page=" + page + "&limit=" + limit;
            while (true) {
                URL httpUrl = new URL(url + content);
//                System.out.println(url + content);
                HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String str;
                while ((str = reader.readLine()) != null) {
                    sb.append(str);
                }
                JSONObject list = JSONObject.fromObject(sb.toString());
                //如果当前接口有效
                if (list.getInt("errno") == 1) {
                    JSONObject rsm = list.getJSONObject("rsm");
                    int total = rsm.getInt("total_rows");
                    //如果当前信息页有用户信息
                    if (total != 0) {
                        JSONArray rows = rsm.getJSONArray("rows");
                        for (int i = 0; i < rows.size(); i++) {
                            JSONObject row = rows.getJSONObject(i);
                            String alias = String.valueOf(row.getInt("recipient_uid"));
                            if (alias.equals("33")) {
                                String topic_id = String.valueOf(row.getInt("topic_id"));
                                String topic_url = "http://xueshu.qukaa.com/api/topic/topic/";
                                String content1 = topic_url + "?id=" + topic_id;
                                URL httpUrl1 = new URL(content1);
                                HttpURLConnection conn1 = (HttpURLConnection) httpUrl1.openConnection();
                                conn1.setRequestMethod("GET");
                                conn1.setReadTimeout(5000);
                                conn1.setDoOutput(true);
                                conn1.setDoInput(true);
                                BufferedReader reader1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                                StringBuffer sb1 = new StringBuffer();
                                String str1;
                                while ((str1 = reader1.readLine()) != null) {
                                    sb1.append(str1);
                                }
                                JSONObject topic_msg = JSONObject.fromObject(sb1.toString());
                                String topic_title = topic_msg.getJSONObject("rsm").getString("topic_title");
                                if (JPushClientUtil.sendToAllAndroid("期刊订阅提醒", "您订阅的期刊" + topic_title + "有更新", "", "", "topic_subscribe", alias) == 1) {
                                    System.out.println("success");
                                }
                            }
                        }
                        content = "?page=" + (++page) + "&limit=" + limit;
                    } else {
                        break;
                    }
                } else {
                    System.out.println("fail");
                    break;
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void authorPush(int page, int limit) {
        String url = new String("http://xueshu.qukaa.com/api/author/list_people/");
        try {
            String content = "?page=" + page + "&limit=" + limit;
            while(true) {
                URL httpUrl = new URL(url+content);
//                System.out.println(url + content);
                HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String str;
                while ((str = reader.readLine()) != null) {
                    sb.append(str);
                }
                JSONObject list = JSONObject.fromObject(sb.toString());
                //如果当前接口有效
                if (list.getInt("errno") == 1) {
                    JSONObject rsm = list.getJSONObject("rsm");
                    int total = rsm.getInt("total_rows");
                    //如果当前信息页有用户信息
                    if (total != 0) {
                        JSONArray rows = rsm.getJSONArray("rows");
                        for (int i = 0; i < rows.size(); i++) {
                            JSONObject row = rows.getJSONObject(i);
                            String alias = String.valueOf(row.getInt("uid"));
                            String author_id = String.valueOf(row.getInt("author_id"));
                            String author_url = "http://xueshu.qukaa.com/api/author/get_author/";
                            String content1 = author_url + "?author_id=" + author_id;
                            URL httpUrl1 = new URL(content1);
                            HttpURLConnection conn1 = (HttpURLConnection) httpUrl1.openConnection();
                            conn1.setRequestMethod("GET");
                            conn1.setReadTimeout(5000);
                            conn1.setDoOutput(true);
                            conn1.setDoInput(true);
                            BufferedReader reader1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                            StringBuffer sb1 = new StringBuffer();
                            String str1;
                            while ((str1 = reader1.readLine()) != null) {
                                sb1.append(str1);
                            }
                            JSONObject author_msg = JSONObject.fromObject(sb1.toString());
                            String author_name = author_msg.getJSONObject("rsm").getJSONObject("result").getString("name");
                            if (JPushClientUtil.sendToAllAndroid("作者更新提醒", "您订阅的作者"+author_name+"有更新", "", "", "author_subscribe", alias) == 1) {
                                System.out.println("success");
                            }
                        }
                        content = "?page=" + (++page) + "&limit=" + limit;
                    }
                    else{
                        break;
                    }

                }
                else{
                    System.out.println("fail");
                    break;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void conferencePush(int page, int limit) {
        String url = new String("http://xueshu.qukaa.com/api/cfp/list_people/");
        try {
            String content = "?page=" + page + "&limit=" + limit;
            while(true) {
                URL httpUrl = new URL(url+content);
//                System.out.println(url + content);
                HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String str;
                while ((str = reader.readLine()) != null) {
                    sb.append(str);
                }
                JSONObject list = JSONObject.fromObject(sb.toString());
                //如果当前接口有效
                if (list.getInt("errno") == 1) {
                    JSONObject rsm = list.getJSONObject("rsm");
                    int total = rsm.getInt("total_rows");
                    //如果当前信息页有用户信息
                    if (total != 0) {
                        JSONArray rows = rsm.getJSONArray("rows");
                        for (int i = 0; i < rows.size(); i++) {
                            JSONObject row = rows.getJSONObject(i);
                            String alias = String.valueOf(row.getInt("uid"));
                            String con_id = String.valueOf(row.getInt("con_id"));
                            String con_url = "http://xueshu.qukaa.com/api/cfp/get_cfp/";
                            String content1 = con_url + "?con_id=" + con_id;
                            URL httpUrl1 = new URL(content1);
                            HttpURLConnection conn1 = (HttpURLConnection) httpUrl1.openConnection();
                            conn1.setRequestMethod("GET");
                            conn1.setReadTimeout(5000);
                            conn1.setDoOutput(true);
                            conn1.setDoInput(true);
                            BufferedReader reader1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                            StringBuffer sb1 = new StringBuffer();
                            String str1;
                            while ((str1 = reader1.readLine()) != null) {
                                sb1.append(str1);
                            }
                            JSONObject con_msg = JSONObject.fromObject(sb1.toString());
                            String con_name = con_msg.getJSONObject("rsm").getJSONObject("result").getString("short_name");
                            String con_call = con_msg.getJSONObject("rsm").getJSONObject("result").getString("call_for_paper");
                            if (JPushClientUtil.sendToAllAndroid("您订阅的会议"+con_name+"快要截稿了",con_call, "", "", "conference_subscribe", alias) == 1) {
                                System.out.println("success");
                            }
                        }
                        content = "?page=" + (++page) + "&limit=" + limit;
                    }
                    else{
                        break;
                    }

                }
                else{
                    System.out.println("fail");
                    break;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void keywordPush(int page, int limit) {
        String url = new String("http://xueshu.qukaa.com/api/key/list_people/");
        try {
            String content = "?page=" + page + "&limit=" + limit;
            while (true) {
                URL httpUrl = new URL(url + content);
//                System.out.println(url + content);
                HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String str;
                while ((str = reader.readLine()) != null) {
                    sb.append(str);
                }
                JSONObject list = JSONObject.fromObject(sb.toString());
                //如果当前接口有效
                if (list.getInt("errno") == 1) {
                    JSONObject rsm = list.getJSONObject("rsm");
                    int total = rsm.getInt("total_rows");
                    //如果当前信息页有用户信息
                    if (total != 0) {
                        JSONArray rows = rsm.getJSONArray("rows");
                        for (int i = 0; i < rows.size(); i++) {
                            JSONObject row = rows.getJSONObject(i);
                            String alias = String.valueOf(row.getInt("uid"));
                            String key_id = String.valueOf(row.getInt("key_id"));
                            String key_url = "http://xueshu.qukaa.com/api/key/get_key/";
                            String content1 = key_url + "?key_id=" + key_id;
                            URL httpUrl1 = new URL(content1);
                            HttpURLConnection conn1 = (HttpURLConnection) httpUrl1.openConnection();
                            conn1.setRequestMethod("GET");
                            conn1.setReadTimeout(5000);
                            conn1.setDoOutput(true);
                            conn1.setDoInput(true);
                            BufferedReader reader1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                            StringBuffer sb1 = new StringBuffer();
                            String str1;
                            while ((str1 = reader1.readLine()) != null) {
                                sb1.append(str1);
                            }
                            JSONObject key_msg = JSONObject.fromObject(sb1.toString());
                            String keyword = key_msg.getJSONObject("rsm").getJSONObject("result").getString("name");
                            if (JPushClientUtil.sendToAllAndroid("关键词订阅更新", "您订阅的关键词"+keyword+"有更新", "", "", "keyword_subscribe", alias) == 1) {
                                System.out.println("success");
                            }
                        }
                        content = "?page=" + (++page) + "&limit=" + limit;
                    } else {
                        break;
                    }

                } else {
                    System.out.println("fail");
                    break;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    
    public static void main(String[] args) throws InterruptedException {
//            JPushClientUtil.topicPush(1,10);
            /*JPushClientUtil.login();
            JPushClientUtil.loop();*/
//            Thread.sleep(86400000);
//        topicPush(1,10);
//        authorPush(1, 10);
//        conferencePush(1,10);
//        keywordPush(1,10);
//        JPushClientUtil.sendToAllAndroid("Academicheadlines", "您订阅的关键词有更新", "", "", "topic" + "_subscribe", "33");
//        keywordPush(1,10);
        topicPush(1,10);
        conferencePush(1, 10);
    }
}
