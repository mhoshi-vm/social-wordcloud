package jp.vmware.tanzu.socialwordcloud.mastodonapiclient.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.vmware.tanzu.socialwordcloud.library.model.SocialMessageData;
import jp.vmware.tanzu.socialwordcloud.library.utils.SocialMessageHandler;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@ConditionalOnProperty(name = "mastodon.search.mode", havingValue = "stream")
public class MastodonTootHandler {

	private static final Logger logger = LoggerFactory.getLogger(MastodonTootHandler.class);

	SocialMessageHandler socialMessageHandler;

	public MastodonTootHandler(SocialMessageHandler socialMessageHandler) {
		this.socialMessageHandler = socialMessageHandler;
	}

	@ServiceActivator(inputChannel = "handlerChannel")
	public void readEnvelopeStream(Message<?> message) {
		Object streamData = message.getPayload();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode;
		String tootJsonString = "";
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
					JsonNode payload = objectMapper.readTree(jsonNode.get("payload").asText());
					String id = payload.get("id").asText();
					String content = Jsoup.parse(payload.get("content").asText()).text();
					String lang = payload.get("language").asText();
					String name = payload.get("account").get("display_name").asText();
					List<String> images = new ArrayList<>();

					if (payload.get("media_attachments") != null && payload.get("media_attachments").isArray()) {
						for (JsonNode attachment : payload.get("media_attachments")) {
							if (attachment.get("type") != null
									&& Objects.equals(attachment.get("type").asText(), "image")
									&& attachment.get("preview_url") != null) {
								InputStream inputStream = new URL(attachment.get("preview_url").asText()).openStream();
								byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(inputStream);
								images.add(Base64.encodeBase64String(bytes));
							}
						}
					}

					SocialMessageData socialMessageData = SocialMessageData.createSocialMessageData("mastodon", id,
							content, lang, Collections.singletonList(name), images);
					tootJsonString = socialMessageData.createJson();
				}
				catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
				catch (MalformedURLException e) {
					logger.debug("Failed to download image attachment");
				}
				catch (IOException e) {
					logger.debug("Failed to stream image attachment");
				}
			}

		}
		sendMessage(tootJsonString);
	}

	public void sendMessage(String tootJsonString) {
		logger.debug("Sending message " + tootJsonString);
		try {
			socialMessageHandler.handle(tootJsonString);
		}
		catch (IOException | InterruptedException e) {
			logger.warn("Failed to send data : " + e);
		}
	}

}
