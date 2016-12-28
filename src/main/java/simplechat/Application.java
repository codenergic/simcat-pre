package simplechat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.EventBus;

@SpringBootApplication
public class Application {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) throws IOException {
		ConfigurableApplicationContext app = SpringApplication.run(Application.class, args);

		EventBus eventBus = app.getBean(EventBus.class);

		// get input from console
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		while (true) {
			eventBus.publish("chat.message", reader.readLine());
		}
	}

	@Inject
	public void listenMessage(EventBus eventBus) {
		eventBus.consumer("chat.message", msg -> {
			logger.info("Input: {}", msg.body());

			// Sending message back to user
			MultiMap headers = msg.headers();
			String token = headers.get("token");
			logger.debug("Sending message back to chat.message.{}", token);
			eventBus.publish("chat.message." + token, "Server: " + msg.body());
		});
	}

	@Inject
	public void sendToken(EventBus eventBus) {
		eventBus.consumer("chat.token", msg -> {
			logger.debug("Generating token");
			msg.reply(UUID.randomUUID().toString());
		});
	}
}
