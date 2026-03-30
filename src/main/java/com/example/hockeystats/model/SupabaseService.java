package com.example.hockeystats.model;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SupabaseService {

    private static final String API_KEY = "sb_publishable_T9Bp76-AFViD1OFaR-bdhQ_QfotgWyd";
    private static final String BASE_URL = "https://obgfwjsuexlqhsmrbpju.supabase.co/rest/v1/players";

    public static String getAllPlayers() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        StringBuilder allRows = new StringBuilder("[");
        int pageSize = 1000;
        int offset = 0;
        boolean firstPage = true;

        while (true) {
            String url = BASE_URL
                + "?select=Name,team,Position,G,A,P,PIM,url_game"
                + "&Sex=eq.M"
                + "&limit=" + pageSize
                + "&offset=" + offset;

            System.out.println("Fetching offset " + offset);

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

            if (inner.isEmpty()) {
                System.out.println("Empty page at offset " + offset + ", stopping.");
                break;
            }

            // Count objects by counting { at root level
            long count = 0;
            for (int i = 0; i < inner.length(); i++) {
                if (inner.charAt(i) == '{') count++;
            }
            System.out.println("Objects at offset " + offset + ": " + count);

            if (!firstPage) allRows.append(",");
            allRows.append(inner);
            firstPage = false;

            if (count < pageSize) {
                System.out.println("Last page reached.");
                break;
            }

            offset += pageSize;
        }

        allRows.append("]");
        System.out.println("Total JSON length: " + allRows.length());
        return allRows.toString();
    }
}