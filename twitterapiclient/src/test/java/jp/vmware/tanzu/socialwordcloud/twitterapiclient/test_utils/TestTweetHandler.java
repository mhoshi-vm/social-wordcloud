package jp.vmware.tanzu.socialwordcloud.twitterapiclient.test_utils;

import jp.vmware.tanzu.socialwordcloud.library.utils.TweetHandler;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
public class TestTweetHandler implements TweetHandler {

	List<String> tweets = new ArrayList<>();

	@Override
	public void handle(String tweet) {
		tweets.add(tweet);
	}

	public List<String> getTweets() {
		return tweets;
	}

}
