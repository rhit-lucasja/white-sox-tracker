package com.soxtracker;

import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;

public class Main {

	public static void main(String[] args) {
		Javalin app = Javalin.create(config -> {
			config.registerPlugin(new OpenApiPlugin(openApi ->
				openApi.withDefinitionConfiguration((version, def) ->
					def.withInfo(info -> {
						info.title("Sox Tracker API");
						info.version("0.0.1");
					})
				)
			));
			config.registerPlugin(new SwaggerPlugin());
		});
		app.get("/api/health", ctx -> ctx.result("ok"));
		app.start(8080);
	}

}
