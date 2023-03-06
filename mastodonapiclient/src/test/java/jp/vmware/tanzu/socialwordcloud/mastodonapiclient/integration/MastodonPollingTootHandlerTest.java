package jp.vmware.tanzu.socialwordcloud.mastodonapiclient.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MastodonPollingTootHandlerTest {

	@Mock
	MastodonClient mastodonClient;

	private RestTemplate restTemplate;

	MastodonPollingTootHandler mastodonPollingTootHandler;

	SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

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

		mastodonPollingTootHandler = new MastodonPollingTootHandler(mastodonClient, socialMessageHandler);

	}

	@Test
	void checkSingleResponsePolling() throws ParseException, JsonProcessingException {

		Date date = new Date();
		Date after = new Date(date.getTime() + 3600000);

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode statuses = mapper.createArrayNode();
		ObjectNode status = mapper.createObjectNode();
		status.put("id", "aaaa");
		status.put("created_at", parser.format(after));
		status.put("content", "bbbb");
		status.put("language", "en");

		ObjectNode name = mapper.createObjectNode();
		name.put("display_name", "ccc");
		status.set("account", name);

		statuses.add(status);

		ResponseEntity<JsonNode> response = new ResponseEntity<>(statuses, HttpStatus.OK);

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<>(null, headers);

		when(restTemplate.exchange("aaa://aaa:111/aaa/aaa?limit=40&since_id=0", HttpMethod.GET, entity, JsonNode.class))
				.thenReturn(response);

		List<SocialMessageData> socialMessageDataList = mastodonPollingTootHandler.mastodonPolling();

		Assertions.assertEquals(socialMessageDataList.size(), 1);
	}

	@Test
	void check30ResponseWithOnly1inFuturePolling() throws ParseException, JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode statuses = mapper.createArrayNode();
		Date date = new Date();

		Date after = new Date(date.getTime() + 3600000);
		ObjectNode status = mapper.createObjectNode();
		status.put("id", "aaaa" + 30);
		status.put("created_at", parser.format(after));
		status.put("content", "bbbb");
		status.put("language", "en");

		ObjectNode name = mapper.createObjectNode();
		name.put("display_name", "ccc");
		status.set("account", name);

		statuses.add(status);

		for (int i = 0; i < 29; i++) {

			Date before = new Date(date.getTime() - 3600000);

			ObjectNode statusLoop = mapper.createObjectNode();
			statusLoop.put("id", "aaaa" + i);
			statusLoop.put("created_at", parser.format(before));
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

		when(restTemplate.exchange("aaa://aaa:111/aaa/aaa?limit=40&since_id=0", HttpMethod.GET, entity, JsonNode.class))
				.thenReturn(response);

		List<SocialMessageData> socialMessageDataList = mastodonPollingTootHandler.mastodonPolling();

		Assertions.assertEquals(socialMessageDataList.size(), 1);

	}

	@Test
	void check40Polling() throws ParseException, JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode statuses = mapper.createArrayNode();
		Date date = new Date();

		for (int i = 0; i < 40; i++) {

			Date after = new Date(date.getTime() + 3600000);

			ObjectNode statusLoop = mapper.createObjectNode();
			statusLoop.put("id", "aaaa" + i);
			statusLoop.put("created_at", parser.format(after));
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

		when(restTemplate.exchange("aaa://aaa:111/aaa/aaa?limit=40&since_id=0", HttpMethod.GET, entity, JsonNode.class))
				.thenReturn(response);

		ArrayNode statusesNull = mapper.createArrayNode();
		ResponseEntity<JsonNode> nullResponse = new ResponseEntity<>(statusesNull, HttpStatus.OK);

		when(restTemplate.exchange("aaa://aaa:111/aaa/aaa?limit=40&max_id=aaaa39", HttpMethod.GET, entity,
				JsonNode.class)).thenReturn(nullResponse);

		List<SocialMessageData> socialMessageDataList = mastodonPollingTootHandler.mastodonPolling();

		Assertions.assertEquals(socialMessageDataList.size(), 40);

	}

	@Test
	void check60with50infuturePolling() throws ParseException, JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode statuses = mapper.createArrayNode();
		Date date = new Date();

		for (int i = 0; i < 40; i++) {

			Date after = new Date(date.getTime() + 3600000);

			ObjectNode statusLoop = mapper.createObjectNode();
			statusLoop.put("id", "aaaa" + i);
			statusLoop.put("created_at", parser.format(after));
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

		when(restTemplate.exchange("aaa://aaa:111/aaa/aaa?limit=40&since_id=0", HttpMethod.GET, entity, JsonNode.class))
				.thenReturn(response);

		ArrayNode statusesNext = mapper.createArrayNode();

		for (int i = 0; i < 10; i++) {

			Date after = new Date(date.getTime() + 3600000);

			ObjectNode statusNextLoop = mapper.createObjectNode();
			statusNextLoop.put("id", "aaaa" + i);
			statusNextLoop.put("created_at", parser.format(after));
			statusNextLoop.put("content", "bbbb");
			statusNextLoop.put("language", "en");

			ObjectNode nameLoop = mapper.createObjectNode();
			nameLoop.put("display_name", "ccc");
			statusNextLoop.set("account", nameLoop);

			statusesNext.add(statusNextLoop);
		}
		for (int i = 0; i < 10; i++) {

			Date before = new Date(date.getTime() - 3600000);

			ObjectNode statusBeforeLoop = mapper.createObjectNode();
			statusBeforeLoop.put("id", "aaaa" + i);
			statusBeforeLoop.put("created_at", parser.format(before));
			statusBeforeLoop.put("content", "bbbb");
			statusBeforeLoop.put("language", "en");

			ObjectNode nameLoop = mapper.createObjectNode();
			nameLoop.put("display_name", "ccc");
			statusBeforeLoop.set("account", nameLoop);

			statusesNext.add(statusBeforeLoop);
		}

		when(restTemplate.exchange("aaa://aaa:111/aaa/aaa?limit=40&since_id=0", HttpMethod.GET, entity, JsonNode.class))
				.thenReturn(response);

		ResponseEntity<JsonNode> nextResponse = new ResponseEntity<>(statusesNext, HttpStatus.OK);

		when(restTemplate.exchange("aaa://aaa:111/aaa/aaa?limit=40&max_id=aaaa39", HttpMethod.GET, entity,
				JsonNode.class)).thenReturn(nextResponse);

		List<SocialMessageData> socialMessageDataList = mastodonPollingTootHandler.mastodonPolling();

		Assertions.assertEquals(socialMessageDataList.size(), 50);

	}

	@Test
	void secondLoop() throws ParseException, JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode statuses = mapper.createArrayNode();
		Date date = new Date();

		for (int i = 0; i < 40; i++) {

			Date before = new Date(date.getTime() - 3600000);

			ObjectNode statusLoop = mapper.createObjectNode();
			statusLoop.put("id", "aaaa" + i);
			statusLoop.put("created_at", parser.format(before));
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

		when(restTemplate.exchange("aaa://aaa:111/aaa/aaa?limit=40&since_id=0", HttpMethod.GET, entity, JsonNode.class))
				.thenReturn(response);

		List<SocialMessageData> socialMessageDataList = mastodonPollingTootHandler.mastodonPolling();
		Assertions.assertEquals(socialMessageDataList.size(), 0);

		ArrayNode statuses2nd = mapper.createArrayNode();

		for (int i = 0; i < 10; i++) {

			Date after = new Date(date.getTime() + 3600000);

			ObjectNode status2ndLoop = mapper.createObjectNode();
			status2ndLoop.put("id", "AAAA" + i);
			status2ndLoop.put("created_at", parser.format(after));
			status2ndLoop.put("content", "bbbb");
			status2ndLoop.put("language", "en");

			ObjectNode nameLoop = mapper.createObjectNode();
			nameLoop.put("display_name", "ccc");
			status2ndLoop.set("account", nameLoop);

			statuses2nd.add(status2ndLoop);
		}

		ResponseEntity<JsonNode> nullResponse = new ResponseEntity<>(statuses2nd, HttpStatus.OK);

		when(restTemplate.exchange("aaa://aaa:111/aaa/aaa?limit=40&since_id=aaaa0", HttpMethod.GET, entity,
				JsonNode.class)).thenReturn(nullResponse);


		List<SocialMessageData> socialMessageData2ndList = mastodonPollingTootHandler.mastodonPolling();
		Assertions.assertEquals(socialMessageData2ndList.size(), 10);

	}


}
