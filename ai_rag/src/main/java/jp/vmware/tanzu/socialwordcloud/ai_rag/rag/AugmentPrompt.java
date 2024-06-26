package jp.vmware.tanzu.socialwordcloud.ai_rag.rag;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AugmentPrompt {

	private Resource resource;

	public AugmentPrompt(@Value("classpath:/prompt/qa.st") Resource resource) {
		this.resource = resource;
	}

	public Message getPrompt(List<String> documents) {

		SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(resource);
		return systemPromptTemplate.createMessage(Map.of("documents", documents.toString()));
	}

}
