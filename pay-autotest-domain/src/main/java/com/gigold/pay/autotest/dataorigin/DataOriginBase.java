package com.gigold.pay.autotest.dataorigin;

import java.net.JarURLConnection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by chenkuan
 * on 16/6/21.
 */
public class DataOriginBase {

    // 所有的数据源类
    private static List<Class<?>> allAssignedClass;
    /**
     * 填充数据源
     * @param requestStr 原始数据
     * @param replacedStrs 替换历史
     * @return 返回替换后的数据
     */
    public static String fillDate(String requestStr, Map<String, String> replacedStrs){
        try {

            // 数据源类是否初始化
            if(allAssignedClass==null)
                allAssignedClass = getAllAssignedClass(DataOriginMaker.class);

            // 遍历所有类
            for (Class<?> c : allAssignedClass) {
                // 迭代每个数据源
                DataOriginMaker dataOriginMaker = (DataOriginMaker) Class.forName(c.getName()).newInstance();
                requestStr = dataOriginMaker.process(requestStr,replacedStrs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestStr;
    }


    /**
     * 获取同一路径下所有子类或接口实现类
     */
    public static List<Class<?>> getAllAssignedClass(Class<?> cls) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Class<?> c : getClasses(cls)) {
            if (cls.isAssignableFrom(c) && !cls.equals(c)) {
                classes.add(c);
            }
        }
        return classes;
    }


    /**
     * 取得当前类路径下的所有类
     */
    public static List<Class<?>> getClasses(Class<?> cls) {
        String pk = cls.getPackage().getName();
        String path = pk.replace('.', '/');
        System.out.println("path=>"+path);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL url = classloader.getResource(path);
        System.out.println(url);
        assert url != null;
        System.out.println("url.getProtocol()"+url.getProtocol()); // jar file

        // 遍历jar包中的类
        if("jar".equals(url.getProtocol())){
            try {
                JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                JarFile jarFile = jarURLConnection.getJarFile();
                return getClassesInJar(jarFile, pk);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // 遍历普通文件中的类
        return getClasses(new File(url.getFile()), pk);

    }

    /**
     * 迭代查找jar 包中的类
     * chenkuan 2016-07-13
     */
    private static List<Class<?>> getClassesInJar(JarFile jarFile,String pkgName) {
        List<Class<?>> classes = new ArrayList<>();
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        System.out.println("dir=>"+jarFile);

        // 遍历jar包
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName(); // 类似：sun/security/internal/interfaces/TlsMasterSecret.class
            String clazzName = jarEntryName.replace("/", ".");
            int endIndex = clazzName.lastIndexOf(".");
            String prefix = null;
            if (endIndex > 0) {
                String prefix_name = clazzName.substring(0, endIndex);
                endIndex = prefix_name.lastIndexOf(".");
                if(endIndex > 0){
                    prefix = prefix_name.substring(0, endIndex);
                }
            }
            if (prefix != null && jarEntryName.endsWith(".class")) {
                // 如果在当前包中或者包下
                if(prefix.equals(pkgName)|| prefix.startsWith(pkgName)){
                    try{
                        //根据文件名得到类
                        Class<?> newClass = Class.forName(clazzName.substring(0, clazzName.length() - 6));
                        classes.add(newClass);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return classes;
    }


    /**
     * 迭代查找类
     */
    private static List<Class<?>> getClasses(File dir, String pk) {
        List<Class<?>> classes = new ArrayList<Class<?>>();

        if (!dir.exists()) {
            return classes;
        }

        File[] files = dir.listFiles();

        if(null!=files)
        for (File f : files) {
            if (f.isDirectory()) {
                classes.addAll(getClasses(f, pk + "." + f.getName()));
            }
            String name = f.getName();
            if (name.endsWith(".class")) {
                try {
                    classes.add(Class.forName(pk + "." + name.substring(0, name.length() - 6)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }
}
