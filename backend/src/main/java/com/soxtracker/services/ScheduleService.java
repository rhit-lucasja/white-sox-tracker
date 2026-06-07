package com.soxtracker.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.soxtracker.exceptions.MlbApiException;
import com.soxtracker.repositories.ScheduleRepository;
import com.soxtracker.responses.ScheduleGameResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ScheduleService {
    
    private final ScheduleRepository repository;
    private final ObjectMapper mapper;

    public ScheduleService(ScheduleRepository repository) {
        this.repository = repository;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public List<ScheduleGameResponse> getSchedule(int year) throws MlbApiException, IOException, InterruptedException {
        
        List<ScheduleGameResponse> games = new ArrayList<>();

        // get the repository raw JSON response
        String raw = repository.getSchedule(year);
        JsonNode root = mapper.readTree(raw);

        // iterate through games on each date in season
        for (JsonNode dateNode : root.path("dates")) {
            for (JsonNode gameNode: dateNode.path("games")) {
                // parse the response data for each game
                String gameDate = gameNode.path("gameDate").asText();
                LocalDateTime dateTime = LocalDateTime.parse(
                    gameDate, DateTimeFormatter.ISO_DATE_TIME
                );
                int gamePk = gameNode.path("gamePk").asInt();
                
                // need to figure out which team is home or away
                JsonNode homeTeam = gameNode.path("teams").path("home").path("team");
                JsonNode awayTeam = gameNode.path("teams").path("away").path("team");
                boolean soxHome = homeTeam.path("id").asInt() == 145;
                // determine which one is the opponent to gather their data
                JsonNode oppTeam = soxHome ? awayTeam : homeTeam;
                String opponentNameLong = oppTeam.path("name").asText();
                String opponentNameShort = oppTeam.path("teamName").asText();
                String opponentAbbreviation = oppTeam.path("abbreviation").asText();
                int opponentWins = oppTeam.path("leagueRecord").path("wins").asInt();
                int opponentLosses = oppTeam.path("leagueRecord").path("losses").asInt();

                // more overall game details
                String venueName = gameNode.path("venue").path("name").asText();
                String status = gameNode.path("status").path("detailedState").asText();
                Integer homeScore = gameNode.path("teams").path("home").path("score").isMissingNode() ? null : gameNode.path("teams").path("home").path("score").asInt();
                Integer awayScore = gameNode.path("teams").path("away").path("score").isMissingNode() ? null : gameNode.path("teams").path("away").path("score").asInt();;

                games.add(new ScheduleGameResponse(
                    dateTime,
                    gamePk,
                    opponentNameLong,
                    opponentNameShort,
                    opponentAbbreviation,
                    opponentWins,
                    opponentLosses,
                    soxHome,
                    venueName,
                    status,
                    homeScore,
                    awayScore
                ));
            }
        }

        return games;
    }
}
