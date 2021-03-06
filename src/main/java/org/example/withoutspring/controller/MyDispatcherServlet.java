package org.example.withoutspring.controller;

import org.apache.commons.lang3.StringUtils;
import org.example.withoutspring.annotations.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @author fengyadong
 * @date 2022/5/26 11:22
 * @Description
 */
public class MyDispatcherServlet extends HttpServlet {


    private MyDispatcherConfig myDispatcherConfig = new MyDispatcherConfig();
    private List<String> classNameList = new ArrayList<String>();

    private Map<String,Object> iocContainerMap = new HashMap<>();
    private Map<String,HandlerMapping> handlerMappingMap = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.doDispatch(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String requestUrl = this.formatUrl(request.getRequestURI());
        HandlerMapping handlerMapping = handlerMappingMap.get(requestUrl);
        if (null == handlerMapping){
            response.getWriter().write("404 Not Found");
            return;
        }

        //获取方法中的参数类型
        Class<?>[] paramTypeArr = handlerMapping.getMethod().getParameterTypes();
        Object[] paramArr = new Object[paramTypeArr.length];

        for (int i=0;i<paramTypeArr.length;i++){
            Class<?> clazz = paramTypeArr[i];
            //参数只考虑三种类型，其他不考虑
            if (clazz == HttpServletRequest.class){
                paramArr[i] = request;
            }else if (clazz == HttpServletResponse.class){
                paramArr[i] = response;
            } else if (clazz == String.class){
                Map<Integer,String> methodParam = handlerMapping.getMethodParams();
                paramArr[i] = request.getParameter(methodParam.get(i));
            }else{
                System.out.println("暂不支持的参数类型");
            }
        }
        //反射调用controller方法
        handlerMapping.getMethod().invoke(handlerMapping.getTarget(), paramArr);
    }

    private String formatUrl(String requestUrl) {
        requestUrl = requestUrl.replaceAll("/+","/");
        if (requestUrl.lastIndexOf("/") == requestUrl.length() -1){
            requestUrl = requestUrl.substring(0,requestUrl.length() -1);
        }
        return requestUrl;
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        //1.加载配置文件
        try {
            doLoadConfig(config.getInitParameter("defaultConfig"));
        } catch (Exception e) {
            System.out.println("加载配置文件失败");
            return;
        }

        //2.根据获取到的扫描路径进行扫描
        doScanPacakge(myDispatcherConfig.getBasePackages());

        //3.将扫描到的类进行初始化，并存放到IOC容器
        doInitializedClass();

        //4.依赖注入
        doDependencyInjection();

        System.out.println("DispatchServlet Init End..." );
    }


    private void doDependencyInjection() {
        if (iocContainerMap.size() == 0){
            return;
        }
        //循环IOC容器中的类

        for (Map.Entry<String, Object> entry : iocContainerMap.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            Field[] fields = clazz.getDeclaredFields();

            //属性注入
            for (Field field : fields) {
                //如果属性有 MyAutowired 注解则注入值（暂时不考虑其他注解）
                if (field.isAnnotationPresent(MyAutowired.class)) {
                    String beanName = toLowerFirstLetterCase(field.getType().getSimpleName());//默认bean的value为类名首字母小写
                    if (field.getType().isAnnotationPresent(MyService.class)) {
                        MyService myService = field.getType().getAnnotation(MyService.class);
                        beanName = myService.value();
                    }
                    field.setAccessible(true);
                    try {
                        Object target = iocContainerMap.get(beanName);
                        if (null == target) {
                            System.out.println(clazz.getName() + "required bean:" + beanName + ",but we not found it");
                        }
                        field.set(entry.getValue(), iocContainerMap.get(beanName));//初始化对象，后面注入
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            //循环类中的方法，获取方法上的路径
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                //初始化HanderMapping
                String requestUrl = "";
                //获取Controller类上的请求路径
                if (clazz.isAnnotationPresent(MyController.class)) {
                    requestUrl = clazz.getAnnotation(MyController.class).value();
                }
                //假设只有 MyMapping 这一种注解
                if (!method.isAnnotationPresent(MyMapping.class)) {
                    continue;
                }
                MyMapping myMapping = method.getDeclaredAnnotation(MyMapping.class);
                requestUrl = requestUrl + "/" + myMapping.value();//拼成完成的请求路径

                //不考虑正则匹配路径/xx/* 的情况，只考虑完全匹配的情况
                if (handlerMappingMap.containsKey(requestUrl)) {
                    System.out.println("重复路径");
                    continue;
                }

                Annotation[][] annotationArr = method.getParameterAnnotations();//获取方法中参数的注解

                Map<Integer, String> methodParam = new HashMap<>();//存储参数的顺序和参数名
                retryParam:
                for (int i = 0; i < annotationArr.length; i++) {
                    for (Annotation annotation : annotationArr[i]) {
                        if (annotation instanceof MyRequestParam) {
                            MyRequestParam myRequestParam = (MyRequestParam) annotation;
                            methodParam.put(i, myRequestParam.value());//存储参数的位置和注解中定义的参数名
                            continue retryParam;
                        }
                    }
                }

                requestUrl = this.formatUrl(requestUrl);//主要是防止路径多了/导致路径匹配不上
                HandlerMapping handlerMapping = new HandlerMapping();
                handlerMapping.setRequestUrl(requestUrl);//请求路径
                handlerMapping.setMethod(method);//请求方法
                handlerMapping.setTarget(entry.getValue());//请求方法所在controller对象
                handlerMapping.setMethodParams(methodParam);//请求方法的参数信息
                handlerMappingMap.put(requestUrl, handlerMapping);//存入hashmap
            }
        }
    }


    /**
     * 初始化类，并放入容器iocContainerMap内
     */
    private void doInitializedClass() {
        if (classNameList.isEmpty()){
            return;
        }
        for (String className : classNameList){
            if (StringUtils.isEmpty(className)){
                continue;
            }
            Class clazz;
            try {
                clazz = Class.forName(className);//反射获取对象
                if (clazz.isAnnotationPresent(MyController.class)){
                    String value = ((MyController)clazz.getAnnotation(MyController.class)).value();
                    //如果直接指定了value则取value，否则取首字母小写类名作为key值存储类的实例对象
                    iocContainerMap.put(StringUtils.isBlank(value) ? toLowerFirstLetterCase(clazz.getSimpleName()) : value,clazz.newInstance());
                }else if(clazz.isAnnotationPresent(MyService.class)){
                    String value = ((MyService)clazz.getAnnotation(MyService.class)).value();
                    iocContainerMap.put(StringUtils.isBlank(value) ? toLowerFirstLetterCase(clazz.getSimpleName()) : value,clazz.newInstance());
                }else{
                    System.out.println("不考虑其他注解的情况");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("初始化类失败，className为" + className);
            }
        }

    }

    /**
     * 将首字母转换为小写
     * @param className
     * @return
     */
    private String toLowerFirstLetterCase(String className) {
        if (StringUtils.isBlank(className)){
            return "";
        }
        String firstLetter = className.substring(0,1);
        return firstLetter.toLowerCase() + className.substring(1);
    }


    /**
     * 扫描包下所有文件获取全限定类名
     * @param basePackages
     */
    private void doScanPacakge(String basePackages) {
        if (StringUtils.isBlank(basePackages)){
            return;
        }
        //把包名的.替换为/
        String scanPath = "/" + basePackages.replaceAll("\\.","/");
        URL url = this.getClass().getClassLoader().getResource(scanPath);//获取到当前包所在磁盘的全路径
        assert url != null;
        File files = new File(url.getFile());//获取当前路径下所有文件
        for (File file : Objects.requireNonNull(files.listFiles())){//开始扫描路径下的所有文件
            if (file.isDirectory()){//如果是文件夹则递归
                doScanPacakge(basePackages + "." + file.getName());
            }else{//如果是文件则添加到集合。因为上面是通过类加载器获取到的文件路径，所以实际上是class文件所在路径
                classNameList.add(basePackages + "." + file.getName().replace(".class",""));
            }
        }

    }


    /**
     * 加载配置文件
     * @param configPath - 配置文件所在路径
     */
    private void doLoadConfig(String configPath) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configPath);
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("加载配置文件失败");
        }

        properties.forEach((k, v) -> {
            try {
                Field field = myDispatcherConfig.getClass().getDeclaredField((String)k);
                field.setAccessible(true);
                field.set(myDispatcherConfig,v);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("初始化配置类失败");
            }
        });
    }

}
