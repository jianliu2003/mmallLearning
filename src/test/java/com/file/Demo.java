package com.file;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by jianl on 2018/3/31.
 */
public class Demo {
    public static void main(String[] args) {
        File file = new File("F:\\aaa\\a1.java");
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String fileName = file.getName();
        System.out.println(fileName);
    }


    @Test
    public void run() throws IOException{
        File file = new File("F:\\pic\\20.jpg");
        String path = "F:\\pic\\liujian\\upload";
        File fileDir = new File(path);
        if (!fileDir.exists()){
            fileDir.mkdirs();
        }
        String fileName = file.getName();
        String extName = fileName.substring(fileName.indexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString() + "."+extName;
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(new File(path,uploadFileName));
        int by;
        while ((by=fis.read())!=-1){
            fos.write(by);
        }
        fos.close();
        fis.close();
    }












}
