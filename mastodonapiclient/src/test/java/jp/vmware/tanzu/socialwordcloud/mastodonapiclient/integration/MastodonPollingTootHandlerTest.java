package jp.vmware.tanzu.socialwordcloud.mastodonapiclient.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jp.vmware.tanzu.socialwordcloud.library.model.SocialMessageData;
import jp.vmware.tanzu.socialwordcloud.library.utils.SocialMessageHandler;
import jp.vmware.tanzu.socialwordcloud.mastodonapiclient.client.MastodonClient;
import jp.vmware.tanzu.socialwordcloud.mastodonapiclient.system.DummyHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MastodonPollingTootHandlerTest {

	@Mock
	MastodonClient mastodonClient;

	private RestTemplate restTemplate;

	MastodonPollingTootHandler mastodonPollingTootHandler;

	@BeforeEach
	void resetOutput() {

		SocialMessageHandler socialMessageHandler = new DummyHandler();
		restTemplate = mock(RestTemplate.class);

		Mockito.doReturn(111).when(mastodonClient).getMastodonPort();
		Mockito.doReturn("aaa").when(mastodonClient).getMastodonScheme();
		Mockito.doReturn("aaa").when(mastodonClient).getMastodonUrl();
		Mockito.doReturn("aaa").when(mastodonClient).getMastodonPollingPath();
		Mockito.doReturn("aaa").when(mastodonClient).getMastodonHashTag();
		Mockito.doReturn(restTemplate).when(mastodonClient).getRestTemplate();

		mastodonPollingTootHandler = spy(new MastodonPollingTootHandler(mastodonClient, socialMessageHandler));

	}

	@Test
	void checkSingleResponsePolling() {

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode statuses = mapper.createArrayNode();
		ObjectNode status = mapper.createObjectNode();
		status.put("id", "aaaa");
		status.put("content", "bbbb");
		status.put("language", "en");

		ObjectNode name = mapper.createObjectNode();
		name.put("display_name", "ccc");
		status.set("account", name);

		statuses.add(status);

		ResponseEntity<JsonNode> response = new ResponseEntity<>(statuses, HttpStatus.OK);

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<>(null, headers);

		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(entity), eq(JsonNode.class)))
			.thenReturn(response);

		// initial run (just to capture since id)
		List<SocialMessageData> socialMessageDataList = mastodonPollingTootHandler.mastodonPolling();

		// second poll
		socialMessageDataList = mastodonPollingTootHandler.mastodonPolling();

		Assertions.assertEquals(socialMessageDataList.size(), 1);
	}

	@Test
	void check40InitResponse() {

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode statuses = mapper.createArrayNode();

		for (int i = 0; i < 40; i++) {

			ObjectNode statusLoop = mapper.createObjectNode();
			statusLoop.put("id", "aaaa" + i);
			statusLoop.put("content", "bbbb");
			statusLoop.put("language", "en");

			ObjectNode nameLoop = mapper.createObjectNode();
			nameLoop.put("display_name", "ccc");
			statusLoop.set("account", nameLoop);

			statuses.add(statusLoop);
		}

		ResponseEntity<JsonNode> response = new ResponseEntity<>(statuses, HttpStatus.OK);

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<>(null, headers);

		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(entity), eq(JsonNode.class)))
			.thenReturn(response);

		List<SocialMessageData> socialMessageDataList = mastodonPollingTootHandler.mastodonPolling();

		Assertions.assertEquals(socialMessageDataList.size(), 0);

	}

	@Test
	void check40Polling() {

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode statuses = mapper.createArrayNode();

		ObjectNode status = mapper.createObjectNode();
		status.put("id", "aaaa");
		status.put("content", "bbbb");
		status.put("language", "en");

		ObjectNode name = mapper.createObjectNode();
		name.put("display_name", "ccc");
		status.set("account", name);

		statuses.add(status);

		ResponseEntity<JsonNode> response = new ResponseEntity<>(statuses, HttpStatus.OK);

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<>(null, headers);

		// Initital Run
		when(restTemplate.exchange("aaa://aaa:111/aaa/aaa?limit=40&since_id=0", HttpMethod.GET, entity, JsonNode.class))
			.thenReturn(response);

		ArrayNode statusesLoop = mapper.createArrayNode();
		for (int i = 0; i < 40; i++) {

			ObjectNode statusLoop = mapper.createObjectNode();
			statusLoop.put("id", "aaaa" + i);
			statusLoop.put("content", "bbbb");
			statusLoop.put("language", "en");

			ObjectNode nameLoop = mapper.createObjectNode();
			nameLoop.put("display_name", "ccc");
			statusLoop.set("account", nameLoop);

			statusesLoop.add(statusLoop);
		}

		ResponseEntity<JsonNode> responseLoop = new ResponseEntity<>(statusesLoop, HttpStatus.OK);

		// Second run
		when(restTemplate.exchange("aaa://aaa:111/aaa/aaa?limit=40&since_id=aaaa", HttpMethod.GET, entity,
				JsonNode.class))
			.thenReturn(responseLoop);

		List<SocialMessageData> socialMessageDataList = mastodonPollingTootHandler.mastodonPolling();
		socialMessageDataList = mastodonPollingTootHandler.mastodonPolling();

		Assertions.assertEquals(socialMessageDataList.size(), 40);

	}

	@Test
	void checkImage() throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode statuses = mapper.createArrayNode();

		ObjectNode status = mapper.createObjectNode();
		status.put("id", "aaaa");
		status.put("content", "bbbb");
		status.put("language", "en");

		ObjectNode name = mapper.createObjectNode();
		name.put("display_name", "ccc");
		status.set("account", name);

		ArrayNode mediaAttachments = mapper.createArrayNode();
		ObjectNode images = mapper.createObjectNode();
		images.put("type", "image");
		images.put("preview_url", "http://aaaa");
		mediaAttachments.add(images);
		status.set("media_attachments", mediaAttachments);

		statuses.add(status);

		ResponseEntity<JsonNode> response = new ResponseEntity<>(statuses, HttpStatus.OK);

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<>(null, headers);

		File initialFile = ResourceUtils.getFile("classpath:test.png");
		InputStream targetStream = new DataInputStream(new FileInputStream(initialFile));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(entity), eq(JsonNode.class)))
			.thenReturn(response);

		doReturn(targetStream).when(mastodonPollingTootHandler).openUrl(anyString());

		// Initital Run
		List<SocialMessageData> socialMessageDataList = mastodonPollingTootHandler.mastodonPolling();

		targetStream = new DataInputStream(new FileInputStream(initialFile));
		doReturn(targetStream).when(mastodonPollingTootHandler).openUrl(anyString());
		socialMessageDataList = mastodonPollingTootHandler.mastodonPolling();

		Assertions.assertEquals(socialMessageDataList.size(), 1);
		Assertions.assertEquals(socialMessageDataList.get(0).getImages().size(), 1);

		byte[] orginalImg = Files.readAllBytes(initialFile.toPath());

		byte[] decodedImg = Base64.getDecoder().decode(socialMessageDataList.get(0).getImages().get(0).toString());

		Assertions.assertTrue(Arrays.equals(orginalImg, decodedImg));
	}

}
