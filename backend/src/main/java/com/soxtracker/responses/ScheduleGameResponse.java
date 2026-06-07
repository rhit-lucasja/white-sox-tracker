package com.soxtracker.responses;

import java.time.LocalDateTime;

public record ScheduleGameResponse (
    LocalDateTime date,
    int gamePk,
    String opponentNameLong,
    String opponentNameShort,
    String opponentAbbreviation,
    int opponentWins,
    int opponentLosses,
    boolean soxHome,
    String venueName,
    String status,
    Integer homeScore,
    Integer awayScore
) 
{}