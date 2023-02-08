package jp.vmware.tanzu.socialwordcloud.standalone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "jp.vmware.tanzu.socialwordcloud")
public class WordcloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(WordcloudApplication.class, args);
	}

}
