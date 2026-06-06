package com.soxtracker;

import io.javalin.Javalin;

public class Main {

	public static void main(String[] args) {
		Javalin app = Javalin.create();
		app.get("/api/health", ctx -> ctx.result("ok"));
		app.start(8080);
	}

}
