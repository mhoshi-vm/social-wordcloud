package jp.vmware.tanzu.socialwordcloud.twitterapiclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "jp.vmware.tanzu.socialwordcloud")
public class TwitterAPiClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitterAPiClientApplication.class, args);
	}

}
