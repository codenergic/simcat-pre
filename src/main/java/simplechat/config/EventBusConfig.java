package simplechat.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageCodec;

@Configuration
public class EventBusConfig {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	private List<MessageCodec<?, ?>> messageCodecs = new ArrayList<>();
	
	@Bean
	public EventBus eventBus(Vertx vertx) {
		logger.info("Configuring clustered event bus");
		EventBus eventBus = vertx.eventBus();
		messageCodecs.forEach(eventBus::registerCodec);

		return eventBus;
	}
}
