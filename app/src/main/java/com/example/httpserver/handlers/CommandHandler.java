package com.example.httpserver.handlers;

import android.util.Log;

import com.example.httpserver.parsers.RequestParser;
import com.example.httpserver.parsers.ResponseParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandHandler implements HttpHandler {
    @Override
    public boolean shouldHandle(RequestParser request) {
        return request.getHeaderByName(RequestParser.METHOD).equals("GET") && request.getPath().startsWith("/cgi-bin/");
    }

    @Override
    public boolean handle(RequestParser request, ResponseParser response) {

        String command = request.getPath().replace("/cgi-bin/", ""). replaceAll("%20", " ");
        Log.d("Command handler", "Command: " + command);
        StringBuilder body = new StringBuilder();

        try {
            String line;
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            body.append("<h1>Command: " + command + "</h1><pre>");

            while((line=bufferedReader.readLine())!=null) body.append(line + "\n");
            body.append("</pre>");

        } catch (IOException e) {
            Log.d("Command handler", "getResponse: " + e.getMessage());
            e.printStackTrace();
        }

        response.getBody().write(body.toString());

        return true;
    }
}
