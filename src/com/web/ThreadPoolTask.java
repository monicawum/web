package com.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

public class ThreadPoolTask implements Runnable, Serializable {

    private static final long serialVersionUID = 7767458791521442626L;
    private Socket            socket           = null;
    public static int         count            = 0;

    public ThreadPoolTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            InputStream input = null;
            OutputStream output = null;
            System.out.println(socket.getInetAddress().toString() + "请求连接");
            input = socket.getInputStream();
            output = socket.getOutputStream();
            getCount();
            // 开始处理并分析请求信息
            Request request = new Request(input);
            request.parse();
            // 开始发送请求资源
            Response response = new Response(output);
            response.setRequest(request);
            response.sendStaticResource();
            // 关系连接
            socket.close();
            System.out.println("连接已关闭！");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getCount() {
        synchronized (this) {
            System.out.println("服务器开始处理第" + (++count) + "次连接");
        }
    }

}
