package jp.vmware.tanzu.socialwordcloud.ai_rag.controller;

import jp.vmware.tanzu.socialwordcloud.ai_rag.client.MyOpenAiClient;
import jp.vmware.tanzu.socialwordcloud.ai_rag.rag.AugmentPrompt;
import jp.vmware.tanzu.socialwordcloud.ai_rag.rag.GenerateSummary;
import jp.vmware.tanzu.socialwordcloud.ai_rag.rag.RetriveVectorTable;
import jp.vmware.tanzu.socialwordcloud.ai_rag.record.VectorRecord;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SimpleAiController {

	private final MyOpenAiClient aiClient;

	public final RetriveVectorTable retriveVectorTable;

	public final AugmentPrompt augmentPrompt;

	public final GenerateSummary generateSummary;

	public SimpleAiController(MyOpenAiClient aiClient, RetriveVectorTable retriveVectorTable,
			AugmentPrompt augmentPrompt, GenerateSummary generateSummary) {
		this.aiClient = aiClient;
		this.retriveVectorTable = retriveVectorTable;
		this.augmentPrompt = augmentPrompt;
		this.generateSummary = generateSummary;
	}

	@GetMapping("/aichat")
	public Completion completion(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message,
			@RequestParam(value = "tokens", defaultValue = "4000") Integer maxTokens) {
		return new Completion(aiClient.generate(message, maxTokens));
	}

	@GetMapping("/semanticsearch")
	public List<VectorRecord> semanticsearch(@RequestParam String message,
			@RequestParam(defaultValue = "10") Integer limit) {
		return retriveVectorTable.semanticSearchListId(message, limit);
	}

	@PostMapping("/insert")
	public void insert(@RequestParam String messageId, @RequestParam String context) {
		retriveVectorTable.insertIntoDb(messageId, context);
	}

	@PostMapping(value = "/augmentprompt", consumes = { "application/json" })
	public String setAugmentPrompt(@RequestBody List<String> documents) {
		return augmentPrompt.getSystemMessage(documents).toString();
	}

	@PostMapping(value = "/generatesummary", consumes = { "application/json" })
	public String generateSummary(@RequestBody Form form,
			@RequestParam(value = "tokens", defaultValue = "4000") Integer maxTokens) {
		return generateSummary.getGeneration(form.message, augmentPrompt.getSystemMessage(form.documents), maxTokens)
			.getText();
	}

	public record Form(String message, List<String> documents) {
	}

}
