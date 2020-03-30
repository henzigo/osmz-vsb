package com.example.httpserver.parsers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class ResponseParser {

    private Map<String, String> headers;
    private int code = 404;
    private String message = "Ok";
    private ByteBuffer body = new ByteBuffer();
    private OutputStream outputStream;

    public ResponseParser(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.headers = new HashMap<String, String>();
        this.getBody().write("<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title></title></head><body>");
    }

    private void setHTTPType(PrintStream output) {
        output.append("HTTP/1.1" + " " + code + " " + message + "\n");
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ByteBuffer getBody() {
        return this.body;
    }

    public void outputContent() {
        if (body.getSize() > 0) {
            setCode(200);
        }

        try {
            getHeaders();
            getBodyContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void outputHeaders() {
        if (body.getSize() > 0) {
            setCode(200);
        }

        getHeaders();
    }

    private void getHeaders() {
        PrintStream stream = new PrintStream(outputStream);
        setHTTPType(stream);

        for (String key : headers.keySet())
        {
            stream.print(key);
            stream.print(": ");
            stream.println(headers.get(key));
        }

        stream.println();
        stream.flush();
    }

    private void getBodyContent() throws IOException {
        this.getBody().write("</body></html>");
        outputStream.write(getBody().getBuffer());
        outputStream.flush();
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
