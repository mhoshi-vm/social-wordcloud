package jp.vmware.tanzu.socialwordcloud.library.utils;

import java.io.IOException;

public interface SocialMessageHandler {

	void handle(String tweet) throws IOException, InterruptedException;

}
