package com.example.httpserver.handlers;

import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.example.httpserver.parsers.RequestParser;
import com.example.httpserver.parsers.ResponseParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ListingFileHandler implements HttpHandler {
    @Override
    public boolean shouldHandle(RequestParser request) {
        return request.getHeaderByName(RequestParser.METHOD).equals("GET");
    }

    @Override
    public boolean handle(RequestParser request, ResponseParser response) {
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, request.getPath());

        if (file.isFile()) {
            returnFile(response, file);
        } else if (file.isDirectory()) {
            returnDirectory(request, response, file);
        } else {
            response.setCode(404);
        }

        return true;
    }

    private ResponseParser returnDirectory(RequestParser request, ResponseParser response, File directory) {
        File files[] = directory.listFiles();
        StringBuilder body = new StringBuilder();
        body.append("<ul>");

        if (!request.getPath().equals("/")) {
            String requestPathParts[] = request.getPath().split("/");
            body
                    .append("<li><a href=\"")
                    .append(TextUtils.join("/", Arrays.copyOf(requestPathParts, requestPathParts.length - 1)))
                    .append("/..\">..</a></li>");
        }

        String prefix = request.getPath().equals("/") ? "" : "/";

        if (files != null) {
            for (File file : files) {
                body.append("<li><a href=\"")
                        .append(request.getPath())
                        .append(prefix)
                        .append(file.getName())
                        .append("\">")
                        .append(file.getName())
                        .append("</a></li>");
            }
        }

        body.append("</ul>\n");
        response.getBody().write(body.toString());

        return response;
    }

    private ResponseParser returnFile(ResponseParser response, File file) {
        try {
            InputStream fileStream = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fileStream.read(data);
            fileStream.close();

            response.getBody().write(data);

            String mimeType = getMimeType(file.getAbsolutePath());
            if (mimeType == null || mimeType.isEmpty()) {
                response.setHeader("Content-Type", "application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            } else response.setHeader("Content-Type", mimeType);
            response.setHeader("Content-Length", String.valueOf(file.length()));
        } catch (FileNotFoundException e) {
            response.setCode(404);
        } catch (IOException e) {
            response.setCode(500);
        }

        return response;
    }

    private static String getMimeType(String url)
    {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null)
        {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        return type;
    }
}
