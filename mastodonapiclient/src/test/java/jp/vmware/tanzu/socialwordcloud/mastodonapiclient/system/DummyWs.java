package jp.vmware.tanzu.socialwordcloud.mastodonapiclient.system;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.websocket.IntegrationWebSocketContainer;
import org.springframework.integration.websocket.ServerWebSocketContainer;

@Configuration
public class DummyWs {

	@Bean
	public IntegrationWebSocketContainer serverWebSocketContainer() {
		return new ServerWebSocketContainer("/api/v1/streaming").withSockJs().setAllowedOrigins("*");
	}

}
