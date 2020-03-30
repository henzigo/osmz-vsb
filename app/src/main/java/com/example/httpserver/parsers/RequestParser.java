package com.example.httpserver.parsers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestParser {

    public static String METHOD = "Method";
    public static String PATH = "Path";

    private Map<String, String> headers;
    private InputStream stream;

    public RequestParser(InputStream stream) {
        this.stream = stream;
        parse();
    }

    public void parse() {
        String content = "";

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));

            String line;
            StringBuilder buffer = new StringBuilder();

            while (!(line = in.readLine()).isEmpty()) {
                buffer.append(line);
                buffer.append("\n");
            }

            content = buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (content.isEmpty()) {
            return;
        }

        String requestLines[] = content.split("\\n");
        headers = new HashMap<String, String>();

        parseHTTPType(requestLines[0]);

        for (int i = 1; i < requestLines.length - 1; i++) {
            Pattern pattern = Pattern.compile("^([\\w-]+)\\: (.*)$");
            Matcher matcher = pattern.matcher(requestLines[i]);
            matcher.find();

            headers.put(matcher.group(1), matcher.group(2));
        }
    }

    private void parseHTTPType(String input) {
        Pattern pattern = Pattern.compile("(GET|POST) ([^ ]*) HTTP/1.(:?0|1)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            headers.put(METHOD, matcher.group(1));
            headers.put(PATH, matcher.group(2));

            Log.d("Request parser", "Parsed HTTP Type");
            Log.d("Request parser", "Method " + headers.get(METHOD));
            Log.d("Request parser", "Path " + headers.get(PATH));
        } else {
            Log.d("Request parser", "No Match");
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Object o : headers.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        return builder.toString();
    }

    public String getHeaderByName(String field) {
        return headers.get(field);
    }

    public String getPath() {
        return headers.get(PATH);
    }
}