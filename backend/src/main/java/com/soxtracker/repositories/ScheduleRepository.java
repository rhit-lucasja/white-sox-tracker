package com.soxtracker.repositories;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.soxtracker.exceptions.MlbApiException;

public class ScheduleRepository {
    
    private static final String MLB_BASE = "https://statsapi.mlb.com";
    private static final int SOX_TEAM_ID = 145;
    private final HttpClient httpClient;

    public ScheduleRepository() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String getSchedule(int year) throws MlbApiException, IOException, InterruptedException {
        // form HTTP request URL
        String url = String.format(
            "%s/api/v1/schedule?sportId=1&teamId=%d&season=%d&gameType=R&hydrate=team",
            MLB_BASE, SOX_TEAM_ID, year
        );

        // actually create and send request
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        // capture response
        HttpResponse<String> response = httpClient.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        // check if API failed
        if (response.statusCode() != 200) {
            throw new MlbApiException(response.statusCode());
        }

        return response.body();

    }
}
