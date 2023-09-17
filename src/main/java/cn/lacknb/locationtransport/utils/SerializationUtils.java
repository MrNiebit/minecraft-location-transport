package cn.lacknb.locationtransport.utils;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author gitsilence
 * @date 2023-09-17
 */
public class SerializationUtils {


    public static void serializeStr(String obj, String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileUtils.write(file, obj, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 将对象序列化到文件
    public static void serializeObject(Object obj, String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
             objectOut.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 从文件中反序列化对象
    public static Object deserializeObject(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return null;
        }
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            return objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object deserializeStr(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return null;
        }
        String string = null;
        try {
            string = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return JSON.parseObject(string, Map.class);
    }

}
