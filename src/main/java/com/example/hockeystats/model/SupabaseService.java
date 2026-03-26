package com.example.hockeystats.model;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SupabaseService {

    private static final String API_KEY = "sb_publishable_T9Bp76-AFViD1OFaR-bdhQ_QfotgWyd";
    private static final String URL = "https://obgfwjsuexlqhsmrbpju.supabase.co/rest/v1/players"; 

    public static String getAllPlayers() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("apikey", API_KEY)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}