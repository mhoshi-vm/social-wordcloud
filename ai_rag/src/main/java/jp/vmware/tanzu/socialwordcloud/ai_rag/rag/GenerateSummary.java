package jp.vmware.tanzu.socialwordcloud.ai_rag.rag;

import io.micrometer.observation.annotation.Observed;
import jp.vmware.tanzu.socialwordcloud.ai_rag.client.MyOpenAiClient;
import org.springframework.ai.client.AiResponse;
import org.springframework.ai.client.Generation;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.messages.Message;
import org.springframework.ai.prompt.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenerateSummary {

	private final MyOpenAiClient myOpenAiClient;

	public GenerateSummary(MyOpenAiClient myOpenAiClient) {
		this.myOpenAiClient = myOpenAiClient;
	}

	@Observed
	public Generation getGeneration(String message, Message systemMessage, Integer maxTokens) {
		UserMessage userMessage = new UserMessage(message);
		Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
		AiResponse response = myOpenAiClient.generate(prompt, maxTokens);
		return response.getGeneration();
	}

}
