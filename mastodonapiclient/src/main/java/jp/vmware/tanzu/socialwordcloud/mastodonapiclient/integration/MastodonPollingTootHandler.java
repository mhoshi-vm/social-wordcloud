package jp.vmware.tanzu.socialwordcloud.mastodonapiclient.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import jp.vmware.tanzu.socialwordcloud.library.model.SocialMessageData;
import jp.vmware.tanzu.socialwordcloud.library.utils.SocialMessageHandler;
import jp.vmware.tanzu.socialwordcloud.mastodonapiclient.client.MastodonClient;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@ConditionalOnProperty(name = "mastodon.search.mode", havingValue = "interval")
public class MastodonPollingTootHandler {

	private static final Logger logger = LoggerFactory.getLogger(MastodonPollingTootHandler.class);

	MastodonClient mastodonClient;

	public Date previousDate;

	public String sinceId;

	SocialMessageHandler socialMessageHandler;

	public MastodonPollingTootHandler(MastodonClient mastodonClient, SocialMessageHandler socialMessageHandler) {
		this.mastodonClient = mastodonClient;
		this.previousDate = new Date();
		this.sinceId = "0";
		this.socialMessageHandler = socialMessageHandler;
	}

	@InboundChannelAdapter(value = "handlerChannel", poller = @Poller(fixedDelay = "15000"))
	public List<SocialMessageData> mastodonPolling() throws ParseException, JsonProcessingException {

		RestTemplate restTemplate = mastodonClient.getRestTemplate();

		// "2019-11-05T13:23:09.000Z"
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

		String maxId = "";

		Map<String, String> params = new HashMap<>();
		params.put("limit", "40");

		HttpEntity<Void> getEntity = new HttpEntity<>(mastodonClient.setHeaders());

		logger.debug("Request Headers :  " + getEntity.getHeaders());

		boolean loop = true;
		boolean firstLoop = true;

		List<SocialMessageData> socialMessageDataList = new ArrayList<>();

		while (loop) {

			logger.debug("Looping ...");

			if (firstLoop) {
				params.put("since_id", sinceId);
				logger.debug("First loop ...");
			}
			else {
				params.remove("since_id");
				params.put("max_id", maxId);
				logger.debug("Next loop ...");
			}

			URIBuilder builder = new URIBuilder();
			builder.setScheme(mastodonClient.getMastodonScheme());
			builder.setPort(mastodonClient.getMastodonPort());
			builder.setHost(mastodonClient.getMastodonUrl());
			builder.setPath(mastodonClient.getMastodonPollingPath() + "/" + mastodonClient.getMastodonHashTag());
			for (Map.Entry<String, String> param : params.entrySet()) {
				builder.addParameter(param.getKey(), param.getValue());
			}

			logger.debug("URL : " + builder);

			ResponseEntity<JsonNode> entity = restTemplate.exchange(builder.toString(), HttpMethod.GET, getEntity,
					JsonNode.class);

			JsonNode response = entity.getBody();

			logger.debug("Response Headers :" + entity.getHeaders());
			logger.debug("StatusCode : " + entity.getStatusCode());
			logger.debug("response : " + response);

			int count = 0;
			if (response != null && response.isArray()) {
				for (JsonNode status : response) {

					Date statusDate = parser.parse(status.get("created_at").asText());
					String statusId = status.get("id").asText();
					String statusContent = Jsoup.parse(status.get("content").asText()).text();
					String statusLanguage = status.get("language").asText();
					List<String> statusNames = new ArrayList<>();
					if (status.get("account") != null && status.get("account").get("display_name") != null
							&& status.get("account").get("display_name").isArray()) {
						for (JsonNode name : status.get("account").get("display_name")) {
							statusNames.add(name.asText());
						}
					}

					if (previousDate.compareTo(statusDate) < 0) {
						SocialMessageData socialMessageData = SocialMessageData.createSocialMessageData("mastodon",
								statusId, statusContent, statusLanguage, statusNames);
						logger.debug("adding social data :" + socialMessageData.createJson());
						socialMessageDataList.add(socialMessageData);
						count++;
					}
					else {
						logger.debug("Updating previous date : " + statusDate);
						this.previousDate = statusDate;
						logger.debug("Updating min id : " + statusId);
						this.sinceId = statusId;
						logger.debug("Ending event lookup");
						break;
					}
				}
			}
			firstLoop = false;

			if (count == 40 && response.get(39) != null) {
				logger.debug("Updating max id : " + response.get(39).get("id"));
				maxId = response.get(39).get("id").asText();
				logger.debug("Ending event lookup");
			}
			else {
				loop = false;
			}
		}
		logger.debug("Sending data");
		return socialMessageDataList;
	}

	@ServiceActivator(inputChannel = "handlerChannel")
	public void readEnvelopeInterval(List<SocialMessageData> socialMessageDataList) {
		socialMessageDataList.forEach(socialMessageData -> {

			try {
				sendMessage(socialMessageData.createJson());
			}
			catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		});
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
