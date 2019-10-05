package simplechat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

@Configuration
public class VertxConfig {
	@Bean
	public EventBus eventBus(Vertx vertx) {
		return vertx.eventBus();
	}

	@Bean(destroyMethod = "close")
	public Vertx vertx() {
		return Vertx.vertx();
	}
}
