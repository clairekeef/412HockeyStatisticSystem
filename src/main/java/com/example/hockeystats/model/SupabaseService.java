package com.example.hockeystats.model;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SupabaseService {

    private static final String API_KEY = "sb_publishable_T9Bp76-AFViD1OFaR-bdhQ_QfotgWyd";
    private static final String BASE_URL = "https://obgfwjsuexlqhsmrbpju.supabase.co/rest/v1/players";

    public static String getAllPlayers() throws Exception {
        return fetchBySex("M");
    }

    public static String getWomenPlayers() throws Exception {
        return fetchBySex("W");
    }

    private static String fetchBySex(String sex) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        StringBuilder allRows = new StringBuilder("[");
        int pageSize = 1000;
        int offset = 0;
        boolean firstPage = true;

        while (true) {
            String url = BASE_URL
                + "?select=Name,team,Position,G,A,P,PIM,url_game"
                + "&Sex=eq." + sex
                + "&limit=" + pageSize
                + "&offset=" + offset;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "count=none")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body().trim();
            String inner = body.substring(1, body.length() - 1).trim();

            if (inner.isEmpty()) break;

            long count = 0;
            for (int i = 0; i < inner.length(); i++) {
                if (inner.charAt(i) == '{') count++;
            }

            if (!firstPage) allRows.append(",");
            allRows.append(inner);
            firstPage = false;

            if (count < pageSize) break;
            offset += pageSize;
        }

        allRows.append("]");
        return allRows.toString();
    }
}