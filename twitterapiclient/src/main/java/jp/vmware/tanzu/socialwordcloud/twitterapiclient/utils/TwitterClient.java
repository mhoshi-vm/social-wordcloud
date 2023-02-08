package jp.vmware.tanzu.socialwordcloud.twitterapiclient.utils;

import com.twitter.clientlib.api.TweetsApi;

public interface TwitterClient {

	String UP = "UP";

	String DOWN = "DOWN";

	TweetsApi getApiInstance();

	String getStatus();

	void setStatus(String status);

}
