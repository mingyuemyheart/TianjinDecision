package com.shidian.mail;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2017/4/10.
 */

public class SendMailUtil {

    //qq
    private static final String HOST = "smtp.qq.com";
    private static final String PORT = "587";
    private static final String FROM_ADD = "459781532@qq.com";
    private static final String FROM_PSW = "uzlatwejcijdbjeg";

//    //163
//    private static final String HOST = "smtp.163.com";
//    private static final String PORT = "465"; //或者465  994
//    private static final String FROM_ADD = "teprinciple@163.com";
//    private static final String FROM_PSW = "teprinciple163";
////    private static final String TO_ADD = "2584770373@qq.com";


//    public static void send(final File file,String toAdd){
//        final MailInfo mailInfo = creatMail(toAdd);
//        final MailSender sms = new MailSender();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                sms.sendFileMail(mailInfo,file);
//            }
//        }).start();
//    }


    public static void send(String toAdd, String location){
        final MailInfo mailInfo = creatMail(toAdd, location);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendTextMail(mailInfo);
            }
        }).start();
    }

    @NonNull
    private static MailInfo creatMail(String toAdd, String location) {
        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerHost(HOST);
        mailInfo.setMailServerPort(PORT);
        mailInfo.setValidate(true);
        mailInfo.setUserName(FROM_ADD); // 你的邮箱地址
        mailInfo.setPassword(FROM_PSW);// 您的邮箱密码
        mailInfo.setFromAddress(FROM_ADD); // 发送的邮箱
        mailInfo.setToAddress(toAdd); // 发到哪个邮件去
        String subject = "", content = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//安装SDCard
            File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Number.txt");
//            if (!file.exists()) {
//                writeFile(file);
//            }
            if (file.exists()) {//文件存在
                subject = readFile(file)+","+location+", 广告机程序显示异常";
            }else {
                subject = "文件不存在";
            }
        }else {
            subject = "未安装SDCard";
        }
        mailInfo.setSubject(subject); // 邮件主题
        mailInfo.setContent(subject); // 邮件文本
        return mailInfo;
    }

    /**
     * 读取文件内容
     * @return
     */
    public static String readFile(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            BufferedReader reader = new BufferedReader (new InputStreamReader(bis));
            //之所以用BufferedReader,而不是直接用BufferedInputStream读取,是因为BufferedInputStream是InputStream的间接子类,
            //InputStream的read方法读取的是一个byte,而一个中文占两个byte,所以可能会出现读到半个汉字的情况,就是乱码.
            //BufferedReader继承自Reader,该类的read方法读取的是char,所以无论如何不会出现读个半个汉字的.
            StringBuffer result = new StringBuffer();
            while (reader.ready()) {
                result.append((char)reader.read());
            }
            reader.close();
            return result.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 写入文件内容
     */
    public static void writeFile(File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            // 构建FileOutputStream对象,文件不存在会自动新建
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write("001我是中文".getBytes());
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
