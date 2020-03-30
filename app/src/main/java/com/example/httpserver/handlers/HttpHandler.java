package com.example.httpserver.handlers;

import com.example.httpserver.parsers.RequestParser;
import com.example.httpserver.parsers.ResponseParser;

public interface HttpHandler {
    boolean shouldHandle(RequestParser request);

    boolean handle(RequestParser request, ResponseParser response);
}
