package jp.vmware.tanzu.socialwordcloud.ai_rag.client;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.client.AiResponse;
import org.springframework.ai.client.Generation;
import org.springframework.ai.openai.client.OpenAiClient;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.messages.Message;

import java.util.ArrayList;
import java.util.List;

public class MyOpenAiClient extends OpenAiClient {

	private static final Logger logger = LoggerFactory.getLogger(MyOpenAiClient.class);

	private final OpenAiService openAiService;

	public MyOpenAiClient(OpenAiService openAiService) {
		super(openAiService);
		this.openAiService = openAiService;
	}

	public AiResponse generate(Prompt prompt, Integer maxTokens) {
		List<Message> messages = prompt.getMessages();
		List<ChatMessage> theoMessages = new ArrayList<>();
		for (Message message : messages) {
			String messageType = message.getMessageType().getValue();
			theoMessages.add(new ChatMessage(messageType, message.getContent()));
		}
		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model(super.getModel())
			.temperature(super.getTemperature())
			.messages(theoMessages)
			.maxTokens(maxTokens)
			.build();
		return getAiResponse(chatCompletionRequest);
	}

	public String generate(String text, Integer maxTokens) {
		ChatCompletionRequest chatCompletionRequest = this.getChatCompletionRequest(text, maxTokens);
		return this.getResponse(chatCompletionRequest);
	}

	private String getResponse(ChatCompletionRequest chatCompletionRequest) {
		StringBuilder builder = new StringBuilder();
		this.openAiService.createChatCompletion(chatCompletionRequest)
			.getChoices()
			.forEach(choice -> builder.append(choice.getMessage().getContent()));

		return builder.toString();
	}

	private ChatCompletionRequest getChatCompletionRequest(String text, Integer maxTokens) {
		List<ChatMessage> chatMessages = List.of(new ChatMessage("user", text));
		logger.trace("ChatMessages: ", chatMessages);
		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model(super.getModel())
			.temperature(super.getTemperature())
			.messages(List.of(new ChatMessage("user", text)))
			.maxTokens(maxTokens)
			.build();
		logger.trace("ChatCompletionRequest: ", chatCompletionRequest);
		return chatCompletionRequest;
	}

	private AiResponse getAiResponse(ChatCompletionRequest chatCompletionRequest) {
		List<Generation> generations = new ArrayList<>();
		logger.trace("ChatMessages: ", chatCompletionRequest.getMessages());
		List<ChatCompletionChoice> chatCompletionChoices = this.openAiService
			.createChatCompletion(chatCompletionRequest)
			.getChoices();
		logger.trace("ChatCompletionChoice: ", chatCompletionChoices);
		for (ChatCompletionChoice chatCompletionChoice : chatCompletionChoices) {
			ChatMessage chatMessage = chatCompletionChoice.getMessage();
			// TODO investigate mapping of additional metadata/runtime info to the
			// general model.
			Generation generation = new Generation(chatMessage.getContent());
			generations.add(generation);
		}
		return new AiResponse(generations);
	}

}
