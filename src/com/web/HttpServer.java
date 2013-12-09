package com.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;

public class HttpServer {

    private int                iport      = 8080;                                                       // default port
    public static final String BASIC_ROOT = System.getProperty("user.dir");
    public static String       WEB_ROOT   = System.getProperty("user.dir") + File.separator + "webroot";

    public HttpServer() {
        System.out.println("service start");
        getConfig();
        start();
    }

    public static void main(String[] args) {
        HttpServer httpserver = new HttpServer();
    }

    private void getConfig() {
        File fileCon = new File(BASIC_ROOT, "config.properties");
        File fileDir = new File(WEB_ROOT);
        File fileWeb = new File(WEB_ROOT, "index.htm");
        if (!fileCon.exists()) {
            System.out.println("配置文件不存在");
            reBuildConfig(fileCon);
        }
        if (!fileDir.exists()) {
            System.out.println("网页存放文件夹不存在，重建中...");
            fileDir.mkdir();
            System.out.print("完成！请在");
            System.out.println(WEB_ROOT + "中放置网页文件...");
            System.out.println("Web服务器将重新初始化...");
            getConfig();
        }
        if (!fileWeb.exists()) {
            System.out.println("file not find： " + fileWeb.getName());
        }
        Properties pps = new Properties();
        try {
            pps.load(new FileInputStream("config.properties"));
            Enumeration enumer = pps.propertyNames();
            String strKey = (String) enumer.nextElement();
            String strValue = pps.getProperty(strKey);
            if (strValue.equals("") != true) {
                WEB_ROOT = strValue;
            }
            System.out.println("网页文件的存放路径为： " + WEB_ROOT);
            strKey = (String) enumer.nextElement();
            strValue = pps.getProperty(strKey);
            if (strValue.equals("") != true) {
                iport = Integer.parseInt(strValue);
            }
            System.out.println("Web服务器访问端口为：" + iport);
            System.out.println("您可以修改Config.properties文件重新设定以上配置");
            System.out.println("启动检查完成，服务器初始化中...");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Web 服务器启动中...");
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(iport);
            System.out.println("Web 启动完成！");
            System.out.println("您现在可以在浏览器中访问http://localhost:8080/，以测试服务器是否运行");
            ThreadPoolExecutor threadPool = ThreadPoolService.getThreadPool();
            Socket socket = null;
            System.out.println("等待连接...");
            while (true) {
                socket = serverSocket.accept();
                threadPool.execute(new ThreadPoolTask(socket));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex);
        }
    }

    private void reBuildConfig(File file) {
        try {
            file.createNewFile();
            Properties prop = new Properties();
            FileOutputStream fos = new FileOutputStream(file);
            prop.setProperty("WEB_ROOT", "");
            prop.setProperty("PORT", "");
            prop.store(fos, "empty file");
            fos.close();
            System.out.println("配置文件Config.properties创建成功，您可以修改WEB_ROOT的值改变网页文件的存放路径，修改PORT的值改变访问端口！");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("重建配置文件Config.properties失败");
            System.out.println("服务器将使用默认配置...");
        }
    }

}
