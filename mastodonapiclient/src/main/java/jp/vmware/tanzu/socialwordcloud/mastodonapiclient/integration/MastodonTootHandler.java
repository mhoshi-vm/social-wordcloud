package jp.vmware.tanzu.socialwordcloud.mastodonapiclient.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jp.vmware.tanzu.socialwordcloud.library.utils.SocialMessageHandler;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MastodonTootHandler {

	private static final Logger logger = LoggerFactory.getLogger(MastodonTootHandler.class);

	SocialMessageHandler socialMessageHandler;

	public MastodonTootHandler(SocialMessageHandler socialMessageHandler) {
		this.socialMessageHandler = socialMessageHandler;
	}

	@ServiceActivator(inputChannel = "handlerChannel")
	public void readEnvelope(Message<?> message) {
		Object streamData = message.getPayload();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode;
		String tweetJsonString = "";
		logger.debug("New stream data :" + streamData);
		try {
			jsonNode = objectMapper.readTree(streamData.toString());
		}
		catch (IOException e) {
			logger.warn("Received non json object : " + streamData);
			throw new RuntimeException("Received non json object");
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
		try {
			socialMessageHandler.handle(tweetJsonString);
		}
		catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public String createJson(String payloadString) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode payload = mapper.readTree(payloadString);
		ObjectNode jNode = mapper.createObjectNode();
		ObjectNode dataNode = mapper.createObjectNode();
		ObjectNode includesNode = mapper.createObjectNode();
		ArrayNode usersNode = mapper.createArrayNode();
		ObjectNode nullNode = mapper.createObjectNode();

		jNode.put("origin", "mastodon");
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
