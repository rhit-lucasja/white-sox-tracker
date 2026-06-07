package com.soxtracker.responses;

public record StandingsTeamResponse (
    boolean isAL,
    Integer leagueRank,
    String divisionName,
    Integer divisionRank,
    Integer sportRank,
    String teamName,
    int wins,
    int losses,
    String winningPercentage,
    String gamesBack,
    String wildCardGamesBack,
    int homeWins,
    int homeLosses,
    int awayWins,
    int awayLosses,
    int leagueWins,
    int leagueLosses,
    int divisionWins,
    int divisionLosses,
    boolean winStreak,
    int streakNumber,
    int runsScored,
    int runsAllowed,
    int runDifferential
)
{}