package com.soxtracker.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.soxtracker.exceptions.MlbApiException;
import com.soxtracker.repositories.StandingsRepository;
import com.soxtracker.responses.StandingsTeamResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StandingsService {
    
    private final StandingsRepository repository;
    private final ObjectMapper mapper;

    public StandingsService() {
        this.repository = new StandingsRepository();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public List<StandingsTeamResponse> getStandings(int year) throws MlbApiException, IOException, InterruptedException {

        List<StandingsTeamResponse> teams = new ArrayList<>();

        // get the repository raw JSON response
        String raw = repository.getStandings(year);
        JsonNode root = mapper.readTree(raw);

        // iterate through teams in every division/league
        for (JsonNode recordNode : root.path("records")) {
            // some information comes from the division itself
            int leagueId = recordNode.path("league").path("id").asInt();
            boolean isAL = (leagueId == 103);
            int divisionId = recordNode.path("division").path("id").asInt();
            // parse data pertaining to each team in division
            for (JsonNode teamNode : recordNode.path("teamRecords")) {
                System.out.println("entered into teamRecord JSON node");
                // team info and ranks
                String teamName = teamNode.path("team").path("name").asText();
                int leagueRank = Integer.parseInt(teamNode.path("leagueRank").asText());
                int divisionRank = Integer.parseInt(teamNode.path("divisionRank").asText());
                int sportRank = Integer.parseInt(teamNode.path("sportRank").asText());

                // streaks and games back
                String gamesBack = teamNode.path("gamesBack").asText();
                String wildCardGamesBack = teamNode.path("wildCardGamesBack").asText();
                boolean winStreak = (teamNode.path("streak").path("streakType").asText().equals("wins"));
                int streakNumber = teamNode.path("streak").path("streakNumber").asInt();

                // overall record
                int wins = teamNode.path("wins").asInt();
                int losses = teamNode.path("losses").asInt();
                String winningPercentage = teamNode.path("winningPercentage").asText();

                // run differential
                int runsScored = teamNode.path("runsScored").asInt();
                int runsAllowed = teamNode.path("runsAllowed").asInt();
                int runDifferential = runsScored - runsAllowed;

                // iterate thru record splits
                int homeWins = 0; int homeLosses = 0; int awayWins = 0; int awayLosses = 0;
                for (JsonNode splitNode : teamNode.path("records").path("overallRecords")) {
                    String type = splitNode.path("type").asText();
                    switch (type) {
                        case "home":
                            homeWins = splitNode.path("wins").asInt();
                            homeLosses = splitNode.path("losses").asInt();
                            break;
                        case "away":
                            awayWins = splitNode.path("wins").asInt();
                            awayLosses = splitNode.path("losses").asInt();
                            break;
                        default:
                            break;
                    }
                }
                // AL/NL record
                int leagueWins = 0;
                int leagueLosses = 0;
                for (JsonNode splitNode : teamNode.path("records").path("leagueRecords")) {
                    if (leagueId == splitNode.path("league").path("id").asInt()) {
                        leagueWins = splitNode.path("wins").asInt();
                        leagueLosses = splitNode.path("losses").asInt();
                        break;
                    }
                }
                // division record
                int divisionWins = 0;
                int divisionLosses = 0;
                String divisionName = "ERROR - DIVISION NAME NOT FOUND";
                for (JsonNode splitNode : teamNode.path("records").path("divisionRecords")) {
                    JsonNode divisionNode = splitNode.path("division");
                    if (divisionId == divisionNode.path("id").asInt()) {
                        divisionName = divisionNode.path("name").asText();
                        divisionWins = splitNode.path("wins").asInt();
                        divisionLosses = splitNode.path("losses").asInt();
                        break;
                    }
                }

                // add team info to response
                teams.add(new StandingsTeamResponse(
                    isAL,
                    leagueRank,
                    divisionName,
                    divisionRank,
                    sportRank,
                    teamName,
                    wins,
                    losses,
                    winningPercentage,
                    gamesBack,wildCardGamesBack,
                    homeWins,
                    homeLosses,
                    awayWins,
                    awayLosses,
                    leagueWins,
                    leagueLosses,
                    divisionWins,
                    divisionLosses,
                    winStreak,
                    streakNumber,
                    runsScored,
                    runsAllowed,
                    runDifferential
                ));
            }
        }

        return teams;
    }
}
