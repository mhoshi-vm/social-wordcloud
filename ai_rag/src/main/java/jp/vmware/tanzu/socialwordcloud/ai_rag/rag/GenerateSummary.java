package jp.vmware.tanzu.socialwordcloud.ai_rag.rag;

import io.micrometer.observation.annotation.Observed;

import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;

import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenerateSummary {

	private final OpenAiChatClient aiChatClient;

	public GenerateSummary(OpenAiChatClient aiChatClient) {
		this.aiChatClient = aiChatClient;
	}

	@Observed
	public Generation getGeneration(String message, Message systemMessage) {
		UserMessage userMessage = new UserMessage(message);
		Prompt prompt = new Prompt((List.of(userMessage, systemMessage)));
		return aiChatClient.call(prompt).getResult();
	}

}
