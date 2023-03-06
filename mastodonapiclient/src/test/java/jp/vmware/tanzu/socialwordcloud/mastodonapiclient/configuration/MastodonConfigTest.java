package jp.vmware.tanzu.socialwordcloud.mastodonapiclient.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jp.vmware.tanzu.socialwordcloud.mastodonapiclient.system.DummyHandler;
import jp.vmware.tanzu.socialwordcloud.mastodonapiclient.system.DummyWs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.integration.websocket.ClientWebSocketContainer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = { "spring.main.allow-bean-definition-overriding=true", "mastodon.token=aaaa",
		"mastodon.search.mode=stream" }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = DummyWs.class)
class MastodonConfigTest {

	@LocalServerPort
	private String port;

	@Autowired
	DummyHandler dummyHandler;

	@BeforeEach
	void resetOutput() {
		dummyHandler.setOutput("");
	}

	@Test
	void sendNonJson() throws IOException, InterruptedException {
		StandardWebSocketClient webSocketClient = new StandardWebSocketClient();

		ClientWebSocketContainer clientWebSocketContainer = new ClientWebSocketContainer(webSocketClient,
				"ws://localhost:" + this.port + "/test/websocket");
		clientWebSocketContainer.start();

		WebSocketSession session = clientWebSocketContainer.getSession(null);

		session.sendMessage(new TextMessage("foo"));
		Thread.sleep(1000);

		assertEquals("", dummyHandler.getOutput());

	}

	@Test
	void sendCorrectJson() throws IOException, InterruptedException {
		StandardWebSocketClient webSocketClient = new StandardWebSocketClient();

		ClientWebSocketContainer clientWebSocketContainer = new ClientWebSocketContainer(webSocketClient,
				"ws://localhost:" + this.port + "/test/websocket");
		clientWebSocketContainer.start();

		WebSocketSession session = clientWebSocketContainer.getSession(null);

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jNode = mapper.createObjectNode();

		jNode.put("event", "update");

		ObjectNode payloadNode = mapper.createObjectNode();

		payloadNode.put("id", "aaa");
		payloadNode.put("content", "aaaa");
		payloadNode.put("language", "dd");

		ObjectNode accountNode = mapper.createObjectNode();

		accountNode.put("display_name", "aaaa");

		payloadNode.set("account", accountNode);

		jNode.put("payload", payloadNode.toString());

		System.out.println(jNode.toString());

		session.sendMessage(new TextMessage(jNode.toString()));
		Thread.sleep(1000);

		JsonNode outputJson = mapper.readTree(dummyHandler.getOutput());

		assertEquals("mastodon", outputJson.get("origin").asText());
		assertEquals("aaa", outputJson.get("data").get("id").asText());
		assertEquals("aaaa", outputJson.get("data").get("text").asText());
		assertEquals("dd", outputJson.get("data").get("lang").asText());
		assertEquals("aaaa", outputJson.get("includes").get("users").get(0).get("name").asText());

	}

}