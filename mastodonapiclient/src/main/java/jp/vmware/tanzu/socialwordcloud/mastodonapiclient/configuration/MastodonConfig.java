package jp.vmware.tanzu.socialwordcloud.mastodonapiclient.configuration;

import jp.vmware.tanzu.socialwordcloud.mastodonapiclient.client.MastodonClient;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.websocket.ClientWebSocketContainer;
import org.springframework.integration.websocket.inbound.WebSocketInboundChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
public class MastodonConfig {

	@Bean
	@ConditionalOnProperty(name = "mastodon.search.mode", havingValue = "stream")
	public ClientWebSocketContainer webSocketContainer(MastodonClient mastodonClient) {

		URIBuilder uriBuilder = new URIBuilder().setScheme(mastodonClient.getMastodonScheme())
				.setHost(mastodonClient.getMastodonUrl()).setPort(mastodonClient.getMastodonPort())
				.setPath(mastodonClient.getMastodonStreamingPath())
				.addParameter("access_token", mastodonClient.getMastodonToken()).addParameter("stream", "hashtag")
				.addParameter("tag", mastodonClient.getMastodonHashTag());

		return new ClientWebSocketContainer(new StandardWebSocketClient(), uriBuilder.toString());
	}

	@Bean
	@ConditionalOnProperty(name = "mastodon.search.mode", havingValue = "stream")
	public WebSocketInboundChannelAdapter webSocketInboundChannelAdapter(
			ClientWebSocketContainer clientWebSocketContainer, MessageChannel handlerChannel) {
		WebSocketInboundChannelAdapter adapter = new WebSocketInboundChannelAdapter(clientWebSocketContainer);
		adapter.setOutputChannel(handlerChannel);
		return adapter;
	}

	@Bean
	public MessageChannel handlerChannel() {
		return new DirectChannel();
	}

}
