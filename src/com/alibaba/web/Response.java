package com.alibaba.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Response {

    private static final int           BUFFER_SIZE = 1024;
    Request                            request;
    OutputStream                       output;

    private static Map<String, String> fileTypeMap = new HashMap<String, String>();

    static {
        fileTypeMap.put("jpg", "image/jpeg");
        fileTypeMap.put("png", "image/png");
        fileTypeMap.put("gif", "image/gif");
        fileTypeMap.put("css", "text/css");
        fileTypeMap.put("js", "application/x-javascript");
        fileTypeMap.put("xls", "application/vnd.ms-excel");
        fileTypeMap.put("txt", "text/plain");

    }

    public Response(OutputStream output) {
        this.output = output;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void sendStaticResource()// 发送请求资源
                                    throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        try {
            File file = new File(HttpServer.WEB_ROOT, request.getUri());
            if (file != null && file.exists()) {
                System.out.println("开始发送用户请求资源...");
                output.write("HTTP/1.0 200 OK\r\n".getBytes("UTF-8"));
                if (file.isDirectory()) {
                    output.write("Content-Type: text/html; charset=utf-8\r\n".getBytes("UTF-8"));
                    output.write("\r\n".getBytes("UTF-8"));
                    listFile(file.listFiles(), HttpServer.WEB_ROOT);
                } else if (file.isFile()) {
                    String prefix = null;
                    if (file.getName().lastIndexOf(".") > 0) {
                        prefix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                    }

                    String contentType = getContentType(prefix);
                    output.write(("Content-Type:" + contentType + "; charset=utf-8\r\n").getBytes("UTF-8"));
                    output.write("\r\n".getBytes("UTF-8"));
                    fis = new FileInputStream(file);
                    int ch = fis.read(bytes, 0, BUFFER_SIZE);
                    while (ch != -1) {
                        output.write(bytes, 0, ch);
                        ch = fis.read(bytes, 0, BUFFER_SIZE);
                    }
                    if (contentType.equals("text/html")) {
                        listFile(file.getParentFile().listFiles(), HttpServer.WEB_ROOT);
                    }
                    System.out.println("发送完毕！");
                }
            } else {
                System.out.println("用户请求的资源不存在");
                String errorMessage = "HTTP/1.1 404 File Not Found\r\n" + "Content-Type:text/html\r\n" + "\r\n"
                                      + "<hl>File Not Found</hl>";
                output.write(errorMessage.getBytes());
            }
            output.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("获取请求资源错误，请检查本地资源设置！");
            System.exit(1);
        }
        if (fis != null) {
            fis.close();
        }
    }

    private void listFile(File[] filelist, String root) throws IOException {
        for (File aa : filelist) {
            if (aa.isDirectory()) {
                output.write(("<a href='"
                              + aa.getAbsolutePath().substring(aa.getAbsolutePath().indexOf(root) + root.length())
                              + "'>" + aa.getName() + "</a>").getBytes());
            } else {
                output.write(("<a href='"
                              + aa.getAbsolutePath().substring(aa.getAbsolutePath().indexOf(root) + root.length())
                              + "'>" + aa.getName() + "</a>" + "&nbsp;&nbsp;&nbsp;" + aa.length() + "bytes").getBytes());
            }
            output.write("<br/><br/>".getBytes());
        }
    }

    private static String getContentType(String prefix) {
        if (prefix == null || "".equals(prefix)) {
            return "text/plain";
        }
        String contentType = fileTypeMap.get(prefix);
        if (contentType != null) {
            return contentType;
        }
        return "text/html";
    }

}
