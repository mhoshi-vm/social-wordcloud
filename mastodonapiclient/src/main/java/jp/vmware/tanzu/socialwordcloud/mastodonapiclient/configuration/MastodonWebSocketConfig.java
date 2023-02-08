package jp.vmware.tanzu.socialwordcloud.mastodonapiclient.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.websocket.ClientWebSocketContainer;
import org.springframework.integration.websocket.inbound.WebSocketInboundChannelAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

@Configuration
public class MastodonWebSocketConfig {

	private static final Logger logger = LoggerFactory.getLogger(MastodonWebSocketConfig.class);

	@Value("${mastodon.scheme}")
	String mastodonScheme;

	@Value("${mastodon.url}")
	String mastodonUrl;

	@Value("${mastodon.port}")
	Integer mastodonPort;

	@Value("${mastodon.token}")
	String mastodonToken;

	@Value("${mastodon.hashtag}")
	String mastodonHashTag;

	@Bean
	public Supplier<Flux<Message<byte[]>>> websocketSupplier(Publisher<Message<byte[]>> websocketPublisher,
			WebSocketInboundChannelAdapter webSocketInboundChannelAdapter) {
		return () -> Flux.from(websocketPublisher).doOnEach(consumer -> logger.debug("new stream event detected"))
				.doOnError(error -> logger.error(error.toString()))
				.doOnSubscribe(subscription -> webSocketInboundChannelAdapter.start())
				.doOnTerminate(webSocketInboundChannelAdapter::stop).map(message -> {
					Object streamData = message.getPayload();

					ObjectMapper objectMapper = new ObjectMapper();
					JsonNode jsonNode;
					String tweetJsonString = "";
					try {
						jsonNode = objectMapper.readTree(streamData.toString());
					}
					catch (IOException e) {
						throw new RuntimeException(e);
					}
					if (jsonNode.get("event") != null && jsonNode.get("event").asText().equals("update")) {
						logger.debug("new update event, detected");
						if (jsonNode.get("payload") != null) {
							logger.debug("Payload : " + jsonNode.get("payload").asText());
							try {
								tweetJsonString = createJson(jsonNode.get("payload").asText());
							}
							catch (JsonProcessingException e) {
								throw new RuntimeException(e);
							}
						}

					}
					logger.debug("Sending message " + tweetJsonString);
					return MessageBuilder.withPayload(tweetJsonString.getBytes(StandardCharsets.UTF_8)).build();
				});
	}

	@Bean
	public ClientWebSocketContainer webSocketContainer() {

		URIBuilder uriBuilder = new URIBuilder().setScheme(mastodonScheme).setHost(mastodonUrl)
				.setPort(mastodonPort)
				.setPath("/api/v1/streaming")
				.addParameter("access_token", mastodonToken).addParameter("stream", "hashtag")
				.addParameter("tag", mastodonHashTag);

		return new ClientWebSocketContainer(new StandardWebSocketClient(), uriBuilder.toString());
	}

	@Bean
	public WebSocketInboundChannelAdapter webSocketInboundChannelAdapter(
			ClientWebSocketContainer clientWebSocketContainer) {
		return new WebSocketInboundChannelAdapter(clientWebSocketContainer);
	}

	@Bean
	public Publisher<Message<byte[]>> websocketPublisher(
			WebSocketInboundChannelAdapter webSocketInboundChannelAdapter) {
		return IntegrationFlows.from(webSocketInboundChannelAdapter).toReactivePublisher();
	}

	public String createJson(String payloadString) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode payload = mapper.readTree(payloadString);
		ObjectNode jNode = mapper.createObjectNode();
		ObjectNode dataNode = mapper.createObjectNode();
		ObjectNode includesNode = mapper.createObjectNode();
		ArrayNode usersNode = mapper.createArrayNode();
		ObjectNode nullNode = mapper.createObjectNode();

		jNode.set("data", nullNode);
		jNode.set("includes", nullNode);

		if (payload.get("id") != null) {
			dataNode.put("id", payload.get("id").asText());
		}
		if (payload.get("content") != null) {
			dataNode.put("text", Jsoup.parse(payload.get("content").asText()).text());
		}
		if (payload.get("language") != null) {
			dataNode.put("lang", payload.get("language").asText());
		}

		ObjectNode userNode = mapper.createObjectNode();
		if (payload.get("account") != null && payload.get("account").get("display_name") != null) {
			userNode.put("name", payload.get("account").get("display_name").asText());
		}
		usersNode.add(userNode);
		includesNode.set("users", usersNode);

		jNode.set("data", dataNode);
		jNode.set("includes", includesNode);

		return jNode.toString();
	}

}
