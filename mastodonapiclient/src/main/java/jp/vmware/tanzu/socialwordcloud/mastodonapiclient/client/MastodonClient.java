package jp.vmware.tanzu.socialwordcloud.mastodonapiclient.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MastodonClient {

	String mastodonScheme;

	String mastodonUrl;

	Integer mastodonPort;

	String mastodonToken;

	String mastodonHashTag;

	String mastodonStreamingPath;

	String mastodonPollingPath;

	HttpHeaders mastodonHttpHeaders;

	RestTemplate restTemplate;

	private MastodonClient(@Value("${mastodon.scheme:wss}") String mastodonScheme,
			@Value("${mastodon.url:mstdn.social}") String mastodonUrl,
			@Value("${mastodon.port:443}") Integer mastodonPort, @Value("${mastodon.token}") String mastodonToken,
			@Value("${mastodon.hashtag}") String mastodonHashTag,
			@Value("${mastodon.path:/api/v1/streaming}") String mastodonStreamingPath,
			@Value("${mastodon.path:/api/v1/timelines/tag}") String mastodonPollingPath,
			RestTemplateBuilder restTemplateBuilder) {
		this.mastodonScheme = mastodonScheme;
		this.mastodonUrl = mastodonUrl;
		this.mastodonPort = mastodonPort;
		this.mastodonToken = mastodonToken;
		this.mastodonHashTag = mastodonHashTag;
		this.mastodonStreamingPath = mastodonStreamingPath;
		this.mastodonPollingPath = mastodonPollingPath;
		this.mastodonHttpHeaders = setHeaders();
		this.restTemplate = restTemplateBuilder.build();
	}

	public HttpHeaders setHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		headers.set("Authorization", "Bearer " + this.mastodonToken);
		return headers;
	}

	public String getMastodonScheme() {
		return mastodonScheme;
	}

	public String getMastodonUrl() {
		return mastodonUrl;
	}

	public Integer getMastodonPort() {
		return mastodonPort;
	}

	public String getMastodonToken() {
		return mastodonToken;
	}

	public String getMastodonHashTag() {
		return mastodonHashTag;
	}

	public HttpHeaders getMastodonHttpHeaders() {
		return mastodonHttpHeaders;
	}

	public String getMastodonStreamingPath() {
		return mastodonStreamingPath;
	}

	public String getMastodonPollingPath() {
		return mastodonPollingPath;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

}
