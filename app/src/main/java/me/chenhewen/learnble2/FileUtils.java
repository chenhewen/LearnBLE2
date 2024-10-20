package me.chenhewen.learnble2;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtils {

    // 保存 String 到本地文件
    public static void saveStringToFile(Context context, String fileName, String data) {
        FileOutputStream fos = null;
        try {
            // 打开应用内部存储中的文件输出流
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            // 写入数据
            fos.write(data.getBytes());
            System.out.println("数据已保存到文件: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 从本地文件读取 String 数据
    public static String readStringFromFile(Context context, String fileName) {
        FileInputStream fis = null;
        StringBuilder data = new StringBuilder();
        try {
            // 打开应用内部存储中的文件输入流
            fis = context.openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            // 逐行读取数据
            while ((line = reader.readLine()) != null) {
                data.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data.toString();
    }
}
