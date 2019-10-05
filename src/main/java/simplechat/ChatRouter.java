package simplechat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;

@Service
public class ChatRouter {
	public ChatRouter(Vertx vertx, EventBus eventBus) {
		final ConcurrentMap<String, List<Message>> messagesMap = new ConcurrentHashMap<>();
		handleMessages(eventBus, messagesMap);
		handleClearMessages(vertx, messagesMap);
	}

	private List<Message> getMessages(String channel, EventBus eventBus, ConcurrentMap<String, List<Message>> messagesMap) {
		messagesMap.putIfAbsent(channel, new ArrayList<>());
		List<Message> messages = messagesMap.get(channel);
		eventBus.send("chat.get-channels", null);
		return messages;
	}

	private void handleClearMessages(Vertx vertx, ConcurrentMap<String, List<Message>> messagesMap) {
		vertx.setPeriodic(1_000L * 60 * 60 * 24 * 7, event -> messagesMap.clear());
	}

	private void handleMessages(EventBus eventBus, ConcurrentMap<String, List<Message>> messagesMap) {
		eventBus.consumer("chat.get-channels", msg -> {
			String responseBody = Json.encode(messagesMap.keySet());
			msg.reply(responseBody);
			eventBus.publish("chat.channels", responseBody);
		});

		eventBus.consumer("chat.get-messages", msg -> {
			MultiMap headers = msg.headers();
			String channel = headers.get("channel");

			List<Message> messages = getMessages(channel, eventBus, messagesMap);
			msg.reply(Json.encode(messages));
		});

		eventBus.consumer("chat.send-messages", msg -> {
			MultiMap headers = msg.headers();
			String channel = headers.get("channel");
			Message message = Json.decodeValue(msg.body().toString(), Message.class);
			message.setDate(new Date());

			List<Message> messages = getMessages(channel, eventBus, messagesMap);
			messages.add(message);

			String responseBody = Json.encode(message);
			msg.reply(responseBody);
			eventBus.publish("chat.messages.".concat(channel), responseBody);
		});
	}

	static class Message {
		String sender;
		String content;
		Date date;

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public String getSender() {
			return sender;
		}

		public void setSender(String sender) {
			this.sender = sender;
		}
	}
}
