package com.example.push;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
public class TimerDemo {
    static int count = 0;
    private static TimerTask task = new TimerTask() {
        @Override
        public void run() {
            ++count;
            System.out.println("时间=" + new Date() + " 执行了" + count + "次"); // 1次
            JPushRequestHandler.topicPush(1,10);
            JPushRequestHandler.authorPush(1, 10);
            JPushRequestHandler.conferencePush(1, 10);
            JPushRequestHandler.keywordPush(1,10);
        }
    };
    public static void main(String[] args) throws ParseException {
        //创建定时器对象
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);//每天
        //定制每天的8:00或20:00执行，
        //获取当前小时，如果在8-20之间，则设置执行时间为当天20点，如果为21-23之间则设置为第二天8点，如果为0-7之间，则设置为当天8点
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int hour = 8;
        if (currentHour >= 0 && currentHour <= 7) {
            hour = 8;
        } else if (currentHour >= 8 && currentHour <= 20) {
            hour = 20;
        } else if (currentHour >= 21 && currentHour <= 23) {
            day = day + 1;
            hour = 8;
        }
        calendar.set(year, month, day, hour, 00, 00);
        Date date = calendar.getTime();
        Timer timer = new Timer();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        System.out.println(df.format(date) + " 新的定时器开始运行");
        int period = 12 * 60 * 60 * 1000;
        //每天重复执行
        timer.schedule(task, date, period);
    }
}