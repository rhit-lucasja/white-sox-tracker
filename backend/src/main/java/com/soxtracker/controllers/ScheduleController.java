package com.soxtracker.controllers;

import com.soxtracker.responses.ScheduleGameResponse;
import com.soxtracker.services.ScheduleService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.openapi.*;

import java.util.List;

public class ScheduleController {
    
    private final ScheduleService service;

    public ScheduleController() {
        this.service = new ScheduleService();
    }

    public static void register(Javalin app) {
        ScheduleController controller = new ScheduleController();
        app.get("/api/schedule", controller::getSchedule);
    }

    @OpenApi(
        path = "/api/schedule",
        methods = {HttpMethod.GET},
        summary = "Retrieves White Sox schedule",
        description = "Retrieves the White Sox schedule for a given season, proxied from MLB Stats API",
        queryParams = {
            @OpenApiParam(name="year", type=Integer.class, description="MLB season year (e.g. 2026)")
        },
        responses = {
            @OpenApiResponse(
                status="200",
                content=@OpenApiContent(from=ScheduleGameResponse[].class)
            ),
            @OpenApiResponse(
                status="500",
                description="Failed to retrieve schedule from MLB API"
            )
        }
    )
    private void getSchedule(Context ctx) {
        try {
            int year = Integer.parseInt(ctx.queryParamAsClass("year", String.class)
                .getOrDefault(String.valueOf(java.time.Year.now().getValue())));
            List<ScheduleGameResponse> schedule = service.getSchedule(year);
            ctx.json(schedule);
        } catch (Exception e) {
            ctx.status(500).result("Failed to fetch schedule: " + e.getMessage());
        }
    }
}
