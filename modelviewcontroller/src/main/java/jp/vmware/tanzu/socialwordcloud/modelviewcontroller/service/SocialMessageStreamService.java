package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.vmware.tanzu.socialwordcloud.ai_rag.rag.RetriveVectorTable;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessageImage;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessageText;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageImageReposity;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageRepository;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageTextRepository;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.utils.MorphologicalAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SocialMessageStreamService {

	private static final Logger logger = LoggerFactory.getLogger(SocialMessageStreamService.class);

	private final List<SseEmitter> emitters;

	public SocialMessageRepository socialMessageRepository;

	public SocialMessageTextRepository socialMessageTextRepository;

	public SocialMessageImageReposity socialMessageImageReposity;

	public MorphologicalAnalysis morphologicalAnalysis;

	public RetriveVectorTable retriveVectorTable;

	public String lang;

	public String database;

	Pattern nonLetterPattern;

	public SocialMessageStreamService(SocialMessageRepository socialMessageRepository,
			SocialMessageTextRepository socialMessageTextRepository,
			SocialMessageImageReposity socialMessageImageReposity, MorphologicalAnalysis morphologicalAnalysis,
			RetriveVectorTable retriveVectorTable, @Value("${twitter.search.lang}") String lang,
			@Value("${database}") String database) {
		this.socialMessageRepository = socialMessageRepository;
		this.socialMessageTextRepository = socialMessageTextRepository;
		this.socialMessageImageReposity = socialMessageImageReposity;
		this.lang = lang;
		this.database = database;
		this.morphologicalAnalysis = morphologicalAnalysis;
		this.retriveVectorTable = retriveVectorTable;
		this.emitters = new CopyOnWriteArrayList<>();
		this.nonLetterPattern = Pattern.compile("^\\W+$", Pattern.UNICODE_CHARACTER_CLASS);
	}

	public List<SseEmitter> getEmitters() {
		return emitters;
	}

	@Transactional
	public void handler(String line) throws InterruptedException {

		SocialMessage socialMessage = setSocialMessage(line);
		List<SocialMessageImage> socialMessageImages = setImage(line);

		if (socialMessage == null || socialMessage.getMessageId() == null) {
			Thread.sleep(100);
			return;
		}

		logger.debug("Handling Tweet : " + socialMessage.getContext());

		socialMessageRepository.save(socialMessage);
		if (database.equals("greenplum")) {
			socialMessageRepository.updateNegativeSentiment(socialMessage.getMessageId());
			retriveVectorTable.insertIntoDb(socialMessage.getMessageId(), socialMessage.getContext());
		}
		socialMessageImageReposity.saveAll(socialMessageImages);

		boolean nextSkip = false;

		for (String text : morphologicalAnalysis.getToken(socialMessage.getContext())) {

			SocialMessageText socialMessageText = new SocialMessageText();

			// Skip until blank character when hast tag or username tag found
			if (nextSkip) {
				if (text.isBlank()) {
					nextSkip = false;
				}
				continue;
			}
			// Skip hashtag and username and also set next skip to true
			if (text.equals("#") || text.equals("@")) {
				nextSkip = true;
				continue;
			}
			// Skip RT, blank, and non letter words
			Matcher m = nonLetterPattern.matcher(text);
			if (text.equals("RT") || text.isBlank() || m.matches()) {
				continue;
			}

			socialMessageText.setMessageId(socialMessage.getMessageId());
			socialMessageText.setText(text);

			socialMessageTextRepository.save(socialMessageText);

		}

	}

	public void notifyTweetEvent(String line) {
		SocialMessage socialMessage = setSocialMessage(line);

		if (socialMessage == null) {
			return;
		}

		for (SseEmitter emitter : emitters) {
			try {
				emitter.send(
						SseEmitter.event().name("newTweet").data("New Tweet Arrived : " + socialMessage.getContext()));
			}
			catch (IOException e) {
				logger.warn("Failed to send SSE :" + e);
			}
		}
	}

	public SocialMessage setSocialMessage(String line) {

		JsonNode jsonFullNode = decodeJson(line);

		SocialMessage socialMessage = new SocialMessage();

		if (jsonFullNode != null) {
			if (jsonFullNode.get("origin") != null) {
				socialMessage.setOrigin(jsonFullNode.get("origin").asText());
			}

			JsonNode jsonDataNode = jsonFullNode.get("data");
			JsonNode jsonExpansionNode = jsonFullNode.get("includes");

			if (jsonDataNode != null) {
				socialMessage.setMessageId(jsonDataNode.get("id").asText());
				socialMessage.setContext(jsonDataNode.get("text").asText());
				socialMessage.setLang(jsonDataNode.get("lang").asText());
			}

			if (jsonExpansionNode != null) {
				JsonNode jsonUserNode = jsonExpansionNode.get("users");
				socialMessage.setUsername(jsonUserNode.get(0).get("name").asText());
			}

		}
		return socialMessage;
	}

	public List<SocialMessageImage> setImage(String line) {

		JsonNode jsonFullNode = decodeJson(line);

		List<SocialMessageImage> socialMessageImages = new ArrayList<>();

		if (jsonFullNode != null) {
			JsonNode jsonDataNode = jsonFullNode.get("data");
			JsonNode jsonExpansionNode = jsonFullNode.get("includes");

			if (jsonDataNode != null && jsonExpansionNode != null) {
				JsonNode jsonImageNode = jsonExpansionNode.get("images");
				if (jsonImageNode.isArray() && !jsonImageNode.isEmpty()) {
					for (JsonNode image : jsonImageNode) {
						SocialMessageImage socialMessageImage = new SocialMessageImage();
						byte[] decodedImg = Base64.getDecoder().decode(image.asText());
						socialMessageImage.setMessageId(jsonDataNode.get("id").asText());
						socialMessageImage.setImage(decodedImg);
						socialMessageImages.add(socialMessageImage);
					}
				}
			}
		}

		return socialMessageImages;
	}

	private JsonNode decodeJson(String line) {

		if (line.isEmpty()) {
			return null;
		}

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonFullNode;
		try {
			jsonFullNode = objectMapper.readTree(line);
		}
		catch (Exception e) {
			logger.warn("Received non Json string");
			return null;
		}
		return jsonFullNode;
	}

}
