package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller;

import jp.vmware.tanzu.socialwordcloud.ai_rag.rag.AugmentPrompt;
import jp.vmware.tanzu.socialwordcloud.ai_rag.rag.GenerateSummary;
import jp.vmware.tanzu.socialwordcloud.ai_rag.rag.RetriveVectorTable;
import jp.vmware.tanzu.socialwordcloud.ai_rag.record.VectorRecord;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageRepository;
import org.springframework.ai.client.Generation;
import org.springframework.ai.prompt.messages.Message;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class RagController {

	private final RetriveVectorTable retriveVectorTable;

	private final AugmentPrompt augmentPrompt;

	private final GenerateSummary generateSummary;

	private final SocialMessageRepository socialMessageRepository;

	public RagController(RetriveVectorTable retriveVectorTable, AugmentPrompt augmentPrompt,
			GenerateSummary generateSummary, SocialMessageRepository socialMessageRepository) {
		this.retriveVectorTable = retriveVectorTable;
		this.augmentPrompt = augmentPrompt;
		this.generateSummary = generateSummary;
		this.socialMessageRepository = socialMessageRepository;
	}

	@GetMapping("/aisearch")
	public ModelAndView defaultView() {
		ModelAndView mav = new ModelAndView("aisearch");
		mav.addObject("retrieve", new ArrayList<SocialMessage>());
		mav.addObject("augment", "No prompt");
		mav.addObject("generate", "No message");
		return mav;
	}

	@GetMapping(value = "/aisearch", params = "action=retrieve")
	public ModelAndView getRetrieve(@RequestParam(value = "message", defaultValue = "") String message,
			@RequestParam(value = "limits", defaultValue = "10") Integer limits) {

		ModelAndView mav = new ModelAndView("aisearch");

		List<SocialMessage> socialMessages = retrieve(message, limits);

		mav.addObject("message", message);
		mav.addObject("retrieve", socialMessages);
		mav.addObject("augment", "No prompt");
		mav.addObject("generate", "No message");
		return mav;
	}

	@GetMapping(value = "/aisearch", params = "action=augment")
	public ModelAndView setAugment(@RequestParam(value = "message", defaultValue = "") String message,
			@RequestParam(value = "limits", defaultValue = "10") Integer limits) {

		ModelAndView mav = new ModelAndView("aisearch");

		List<SocialMessage> socialMessages = retrieve(message, limits);
		Message systemPrompt = augment(socialMessages);

		mav.addObject("message", message);
		mav.addObject("retrieve", socialMessages);
		mav.addObject("augment", systemPrompt.toString());
		mav.addObject("generate", "No message");
		return mav;
	}

	@GetMapping(value = "/aisearch", params = "action=generate")
	public ModelAndView generate(@RequestParam(value = "message", defaultValue = "") String message,
			@RequestParam(value = "limits", defaultValue = "10") Integer limits,
			@RequestParam(value = "tokens", defaultValue = "3000") Integer maxTokens) {

		ModelAndView mav = new ModelAndView("aisearch");

		List<SocialMessage> socialMessages = retrieve(message, limits);
		Message systemPrompt = augment(socialMessages);
		Generation generation = generateSummary.getGeneration(message, systemPrompt, maxTokens);

		mav.addObject("message", message);
		mav.addObject("retrieve", socialMessages);
		mav.addObject("augment", systemPrompt.getContent());
		mav.addObject("generate", generation.getText());
		return mav;
	}

	private List<SocialMessage> retrieve(String message, Integer maxTokens) {
		List<SocialMessage> socialMessages = new ArrayList<>();
		if (!Objects.equals(message, "")) {
			List<VectorRecord> vectorRecords = retriveVectorTable.semanticSearchListId(message, maxTokens);
			List<String> messageIds = vectorRecords.stream().map(VectorRecord::messageId).collect(Collectors.toList());
			socialMessages = socialMessageRepository.findSocialMessageByMessageIdIn(messageIds);
		}
		return socialMessages;
	}

	private Message augment(List<SocialMessage> socialMessages) {
		List<String> documents = socialMessages.stream().map(SocialMessage::getContext).collect(Collectors.toList());
		return augmentPrompt.getSystemMessage(documents);
	}

}
