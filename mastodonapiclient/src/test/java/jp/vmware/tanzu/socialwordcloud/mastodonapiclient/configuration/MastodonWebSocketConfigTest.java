package jp.vmware.tanzu.socialwordcloud.mastodonapiclient.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.integration.websocket.ClientWebSocketContainer;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = {
                "mastodon.scheme = ws",
                "mastodon.url = localhost",
                "mastodon.port = 18081",
                "mastodon.token = dummy",
                "server.port = 18081"
        }
)
class MastodonWebSocketConfigTest {
    @Value("mastodon.port")
    private Integer port;


    @Test
    void testWebSocketStreamSource() throws IOException, InterruptedException {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();



        ClientWebSocketContainer clientWebSocketContainer =
                new ClientWebSocketContainer(webSocketClient, "ws://localhost:" + this.port + "/api/v1/streaming");
        clientWebSocketContainer.start();

        WebSocketSession session = clientWebSocketContainer.getSession(null);

        session.sendMessage(new TextMessage("aaa"));

        Thread.sleep(10000);
    }

}