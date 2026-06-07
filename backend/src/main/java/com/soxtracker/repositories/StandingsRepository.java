package com.soxtracker.repositories;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.soxtracker.exceptions.MlbApiException;

public class StandingsRepository {
    
    private static final String MLB_BASE = "https://statsapi.mlb.com";
    private static final int AL_ID = 103;
    private static final int NL_ID = 104;
    private final HttpClient httpClient;

    public StandingsRepository() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String getStandings(int year) throws MlbApiException, IOException, InterruptedException {
        // form HTTP request URL
        String url = String.format(
            "%s/api/v1/standings?leagueId=%d,%d&season=%d&standingsType=regularSeason",
            MLB_BASE, AL_ID, NL_ID, year
        );

        // actually create and send request
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        // capture response
        HttpResponse<String> response = httpClient.send(
            request, 
            HttpResponse.BodyHandlers.ofString()
        );
        
        // check if API failed, else populate up response
        if (response.statusCode() != 200) {
            throw new MlbApiException(response.statusCode());
        }
        return response.body();
        
    }
}
