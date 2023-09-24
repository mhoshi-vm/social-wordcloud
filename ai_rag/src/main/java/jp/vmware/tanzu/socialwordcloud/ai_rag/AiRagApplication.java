package jp.vmware.tanzu.socialwordcloud.ai_rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "jp.vmware.tanzu.socialwordcloud")
public class AiRagApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiRagApplication.class, args);
	}

}
