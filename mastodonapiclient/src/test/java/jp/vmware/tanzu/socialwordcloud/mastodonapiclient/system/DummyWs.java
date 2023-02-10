package jp.vmware.tanzu.socialwordcloud.mastodonapiclient.system;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.websocket.IntegrationWebSocketContainer;
import org.springframework.integration.websocket.ServerWebSocketContainer;
import org.springframework.integration.websocket.inbound.WebSocketInboundChannelAdapter;
import org.springframework.messaging.MessageChannel;

@TestConfiguration
public class DummyWs {

    @Bean
    @Primary
    public WebSocketInboundChannelAdapter webSocketInboundChannelAdapter(MessageChannel handlerChannel) {

        WebSocketInboundChannelAdapter adapter = new WebSocketInboundChannelAdapter(serverWebSocketContainer());
        adapter.setOutputChannel(handlerChannel);
        return adapter;
    }

    @Bean
    public IntegrationWebSocketContainer serverWebSocketContainer() {
        return new ServerWebSocketContainer("/test")
                .withSockJs()
                .setAllowedOrigins("*");
    }


}
