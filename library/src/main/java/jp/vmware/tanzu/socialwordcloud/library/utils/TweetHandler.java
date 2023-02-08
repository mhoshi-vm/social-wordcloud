package jp.vmware.tanzu.socialwordcloud.library.utils;

import org.springframework.cloud.sleuth.annotation.NewSpan;

import java.io.IOException;

public interface TweetHandler {

	@NewSpan(name = "tweet-stream-handler")
	void handle(String tweet) throws IOException, InterruptedException;

}
