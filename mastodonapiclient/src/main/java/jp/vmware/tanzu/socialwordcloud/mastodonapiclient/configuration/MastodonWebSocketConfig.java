package jp.vmware.tanzu.socialwordcloud.mastodonapiclient.configuration;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.websocket.ClientWebSocketContainer;
import org.springframework.integration.websocket.inbound.WebSocketInboundChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
public class MastodonWebSocketConfig {

	@Value("${mastodon.scheme:wss}")
	String mastodonScheme;

	@Value("${mastodon.url:mstdn.social}")
	String mastodonUrl;

	@Value("${mastodon.port:443}")
	Integer mastodonPort;

	@Value("${mastodon.token}")
	String mastodonToken;

	@Value("${mastodon.hashtag}")
	String mastodonHashTag;

	@Value("${mastodon.path:/api/v1/streaming}")
	String mastodonPath;

	@Bean
	public ClientWebSocketContainer webSocketContainer() {

		URIBuilder uriBuilder = new URIBuilder().setScheme(mastodonScheme).setHost(mastodonUrl).setPort(mastodonPort)
				.setPath(mastodonPath).addParameter("access_token", mastodonToken).addParameter("stream", "hashtag")
				.addParameter("tag", mastodonHashTag);

		return new ClientWebSocketContainer(new StandardWebSocketClient(), uriBuilder.toString());
	}

	@Bean
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
