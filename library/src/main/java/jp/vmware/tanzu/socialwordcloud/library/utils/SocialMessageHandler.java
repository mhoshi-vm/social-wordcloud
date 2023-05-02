package jp.vmware.tanzu.socialwordcloud.library.utils;

import io.micrometer.observation.annotation.Observed;

import java.io.IOException;

public interface SocialMessageHandler {

	void handle(String tweet) throws IOException, InterruptedException;

}
