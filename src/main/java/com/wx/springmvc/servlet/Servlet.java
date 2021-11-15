package com.wx.springmvc.servlet;

import com.wx.springmvc.annotation.Controller;
import com.wx.springmvc.annotation.Qualifier;
import com.wx.springmvc.annotation.RequestMapping;
import com.wx.springmvc.annotation.Service;
import com.wx.springmvc.handlerAdapter.HandlerAdapter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class Servlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    List<String> classNames = new ArrayList<>();

    Map<String,Object> beans = new HashMap<>();

    Map<String,Object> handlerMap = new HashMap<>();

    Properties prop = null;


    private static String HANDLERADAPTER = "com.wx.springmvc.handlerAdapter";

    @Override
    public void init(ServletConfig config) throws ServletException {
        //根据一个基本包进行扫描，扫描里面的子包以及子包下的类
        scanPackage("com.wx");

        for(String classname : classNames){
            System.out.println(classname);
        }

        instance();
        for(Map.Entry<String,Object> entry : beans.entrySet()){
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

        ioc();

        //找到对应mapping的执行方法
        HandlerMapping();
        for(Map.Entry<String,Object> entry : handlerMap.entrySet()){
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        InputStream is = this.getClass()
                .getResourceAsStream("/spring.properties");
        //扫描配置

        prop = new Properties();
        try {
            prop.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String context = req.getContextPath();
        String path = uri.replace(context,"");
        Method method = (Method) handlerMap.get(path);
        Object instance = beans.get("/" + path.split("/")[1]);

        HandlerAdapter hand = (HandlerAdapter) beans.get(prop.getProperty(HANDLERADAPTER));
        Object[] args = hand.hand(req,resp,method,beans);

        try {
            method.invoke(instance,args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void HandlerMapping() {
        if (beans.entrySet().size() <= 0) {
            System.out.println("没有类的实例化！");
            return;
        }

        for(Map.Entry<String,Object> entry : beans.entrySet()){
            Object instance = entry.getValue();
            Class clazz = instance.getClass();

            if(clazz.isAnnotationPresent(Controller.class)){
                RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);

                String classPath = requestMapping.value();
                Method[] methods = clazz.getMethods();

                for(Method method : methods){
                    if(method.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping methodrm = method.getAnnotation(RequestMapping.class);
                        String methodPath = methodrm.value();

                        handlerMap.put(classPath + methodPath,method);
                    } else {
                        continue;
                    }
                }

            }
        }
    }

    private void ioc() {
        if (beans.entrySet().size() <= 0) {
            System.out.println("没有类的实例化！");
            return;
        }

        for(Map.Entry<String,Object> entry : beans.entrySet()){
            Object instance = entry.getValue();

            Class clazz = instance.getClass();
            if(clazz.isAnnotationPresent(Controller.class)){
                Field[] fields = clazz.getDeclaredFields();

                for(Field field:fields){
                    if(field.isAnnotationPresent(Qualifier.class)){
                        Qualifier qualifier = field.getAnnotation(Qualifier.class);
                        String value = qualifier.value();
                        field.setAccessible(true);

                        try {
                            field.set(instance,beans.get(value));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        continue;
                    }
                }
            } else {
                continue;
            }
        }
    }

    //创建方法
    private void instance() {
        if (classNames.size() <= 0) {
            System.out.println("包扫描失败！");
            return;
        }

        for(String className : classNames){
            String cn = className.replace(".class","");

            try {
                Class clazz = Class.forName(cn);
                if(clazz.isAnnotationPresent(Controller.class)){
                    Controller controller = (Controller) clazz.getAnnotation(Controller.class);
                        Object instance = clazz.newInstance();
                    RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
                    String rmvalue = requestMapping.value();
                    beans.put(rmvalue,instance);
                } else if (clazz.isAnnotationPresent(Service.class)){
                    Service service = (Service) clazz.getAnnotation(Service.class);
                    Object instance = clazz.newInstance();
                    beans.put(service.value(),instance);
                } else {
                    continue;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

    }

    private void scanPackage(String basePackage) {
        URL url = this.getClass()
                .getClassLoader()
                .getResource("/" + replaceTo(basePackage));
        String fileStr = url.getFile();
        File file = new File(fileStr);
        String[] filesStr = file.list();

        for(String path : filesStr){
            File filePath = new File(fileStr + path);
            if(filePath.isDirectory()){
                scanPackage(basePackage + "." + path);
            } else {
                classNames.add(basePackage + "." + filePath.getName());
            }

        }
    }

    private String replaceTo(String basePackage) {
        return basePackage.replaceAll("\\.","/");
    }
}
