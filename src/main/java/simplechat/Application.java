package simplechat;

import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.EventBus;

@SpringBootApplication
public class Application {
	public static void main(String[] args) throws IOException {
		SpringApplication.run(Application.class, args);
	}
}
