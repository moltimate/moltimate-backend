package com.moltimate.moltimatebackend.query;

import org.apache.commons.io.IOUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpHelper {

    public static String doPostGetBody(String urlString, String body) {
        HttpURLConnection httpURLConnection = doPost(urlString, body);
        try {
            return IOUtils.toString(httpURLConnection.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static HttpURLConnection doPost(String urlString, String body) {
        try {
            byte[] xmlContent = body.getBytes(StandardCharsets.UTF_8);

            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setFixedLengthStreamingMode(xmlContent.length);
            httpURLConnection.connect();

            try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                outputStream.write(xmlContent);
            }

            return httpURLConnection;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
