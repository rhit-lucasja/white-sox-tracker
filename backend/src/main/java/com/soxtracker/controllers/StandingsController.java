package com.soxtracker.controllers;

import com.soxtracker.responses.StandingsTeamResponse;
import com.soxtracker.services.StandingsService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.openapi.*;

import java.util.List;

public class StandingsController {
    
    private final StandingsService service;

    public StandingsController() {
        this.service = new StandingsService();
    }

    public static void register(Javalin app) {
        StandingsController controller = new StandingsController();
        app.get("/api/standings", controller::getStandings);
    }

    @OpenApi(
        path = "/api/standings",
        methods = { HttpMethod.GET },
        tags = { "Standings" },
        summary = "Retrieves MLB standings",
        description = "Retrieves the full MLB team standings for a given season, proxied from MLB Stats API",
        queryParams = {
            @OpenApiParam(name="year", type=Integer.class, description="MLB season year (e.g. 2026)")
        },
        responses = {
            @OpenApiResponse(
                status="200",
                content=@OpenApiContent(from=StandingsTeamResponse[].class)
            ),
            @OpenApiResponse(
                status="500",
                description="Failed to retrieve standings from MLB API"
            )
        }
    )
    private void getStandings(Context ctx) {
        try {
            int year = Integer.parseInt(ctx.queryParamAsClass("year", String.class)
                .getOrDefault(String.valueOf(java.time.Year.now().getValue())));
            List<StandingsTeamResponse> standings = service.getStandings(year);
            ctx.json(standings);
        } catch (Exception e) {
            ctx.status(500).result("Failed to fetch standings: " + e.getMessage());
        }
    }
}
