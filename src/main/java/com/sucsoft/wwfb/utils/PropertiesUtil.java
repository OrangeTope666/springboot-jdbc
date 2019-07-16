package com.sucsoft.wwfb.utils;

import com.sucsoft.wwfb.service.NewWwfbService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    public static Properties getPro(String name) {
        Properties properties = new Properties();
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        InputStream in = NewWwfbService.class.getClassLoader().getResourceAsStream(name);
        // 使用properties对象加载输入流
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

}
