package simplechat.config;

import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

@Configuration
public class VertxWebConfig {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean
	public HttpServer httpServer(Vertx vertx) {
		return vertx.createHttpServer();
	}

	@Bean
	public Router httpRouter(Vertx vertx) {
		Router router = Router.router(vertx);

		// SockJS Bridge
		SockJSHandler sockJSHandler = SockJSHandler.create(vertx)
				.bridge(new BridgeOptions()
						.addInboundPermitted(new PermittedOptions().setAddress("chat.get-channels"))
						.addInboundPermitted(new PermittedOptions().setAddress("chat.get-messages"))
						.addInboundPermitted(new PermittedOptions().setAddress("chat.send-messages"))
						.addInboundPermitted(new PermittedOptions().setAddress("chat.messages"))
						.addInboundPermitted(new PermittedOptions().setAddressRegex("chat.message\\..+"))
						.addOutboundPermitted(new PermittedOptions().setAddress("chat.channels"))
						.addOutboundPermitted(new PermittedOptions().setAddressRegex("chat.messages\\..+")));
		router.route("/eventbus/*").handler(sockJSHandler);

		// Handle static file
		StaticHandler staticHandler = StaticHandler.create().setCachingEnabled(false);
		router.route().handler(staticHandler);

		return router;
	}

	@Inject
	public void configureAndStartHttpServer(HttpServer httpServer, Router router, @Value("${server.port:8080}") int port) {
		logger.info("Starting HTTP server");
		httpServer.requestHandler(router).listen(port, h -> {
			if (h.succeeded()) {
				logger.info("HTTP server started at {}", h.result().actualPort());
			} else {
				logger.error("Failed to start HTTP server", h.cause());
			}
		});
	}
}
