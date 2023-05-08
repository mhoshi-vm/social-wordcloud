package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessageVader;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageRepository;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageTextRepository;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageVaderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SocialMessageService {

	public final SocialMessageRepository socialMessageRepository;

	public final SocialMessageTextRepository socialMessageTextRepository;

	public final SocialMessageVaderRepository socialMessageVaderRepository;

	public SocialMessageService(SocialMessageRepository socialMessageRepository,
			SocialMessageTextRepository socialMessageTextRepository,
			SocialMessageVaderRepository socialMessageVaderRepository) {
		this.socialMessageRepository = socialMessageRepository;
		this.socialMessageTextRepository = socialMessageTextRepository;
		this.socialMessageVaderRepository = socialMessageVaderRepository;
	}

	public List<SocialMessage> findAllByOrderByMessageIdDesc() {
		return socialMessageRepository.findAllByOrderByMessageIdDesc();
	}

	public List<SocialMessageVader> findAllByOrderByMessageIdDescSentiment() {
		return socialMessageVaderRepository.findAllByOrderByMessageIdDesc();
	}

	@Transactional
	public void deleteMessage(String tweetId) {
		socialMessageRepository.deleteByMessageId(tweetId);
		socialMessageTextRepository.deleteByMessageId(tweetId);
	}

}
