package com.web;

import java.io.IOException;
import java.io.InputStream;

public class Request {

    public Request() {
    }

    private InputStream input;
    private String      uri;
    private String      requestString;
    private String      requestParams;

    public Request(InputStream input) {
        this.input = input;
    }

    public void parse(String charset) throws IOException {
        StringBuffer request = new StringBuffer(2048);
        int i;
        byte[] buffer = new byte[2048];
        try {
            i = input.read(buffer);
        } catch (Exception ex) {
            ex.printStackTrace();
            i = -1;
        }
        for (int j = 0; j < i; j++) {
            request.append((char) buffer[j]);
        }
        System.out.println(request.toString());
        int nIndex = request.toString().indexOf("\n\r");
        if (request.toString().length() > nIndex + 1) {
            String temp = request.toString().substring(nIndex + 1, request.toString().length());
            if (!temp.equals("")) {
                requestParams = temp;
            }
        }
        requestString = request.toString();
        uri = parseUri(request.toString());
        System.out.println("用户请求：" + this.getUri());
    }

    private String parseUri(String requestString) { // 分析请求信息，并返回
        int index1, index2;
        index1 = requestString.indexOf(" ");
        if (index1 != -1) {
            index2 = requestString.indexOf(" ", index1 + 1);
            if (index2 > index1) {
                return requestString.substring(index1 + 1, index2);
            }
        }
        return null;
    }

    public String getUri() {
        if (uri.compareTo("/") == 0) {
            uri = "/index.htm";
        }
        return uri;
    }

    public String getRequestMethod() {
        if (requestString.startsWith("GET") && uri.indexOf("?") > 0) {
            return "GET";
        } else if (requestString.startsWith("POST")) {
            return "POST";
        }
        return "GET";
    }

    public String getParamString() {
        if (getRequestMethod().equals("GET")) {
            if (uri.indexOf("?") > 0) {
                return uri.substring(uri.indexOf("?") + 1, uri.length());
            }
        }
        if (getRequestMethod().equals("POST")) {
            if (requestParams != null) return requestParams;
        }
        return "";
    }

}
