package com.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadPoolTask implements Runnable {

    private Socket           socket = null;
    public static AtomicLong count  = new AtomicLong(0);

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
            request.parse("UTF-8");
            // 开始发送请求资源
            Response response = new Response(output);
            response.setRequest(request);
            if (request.getRequestMethod().equals("GET")) {
                response.sendStaticResource();
            }
            if (request.getRequestMethod().equals("POST")) {
                response.post();
            }
            // 关闭连接
            socket.close();
            System.out.println("连接已关闭！");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getCount() {
        System.out.println("服务器开始处理第" + count.incrementAndGet() + "次连接");
    }
}
