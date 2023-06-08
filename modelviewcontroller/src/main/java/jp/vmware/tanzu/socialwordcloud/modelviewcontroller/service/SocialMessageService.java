package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageRepository;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageTextRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SocialMessageService {

	public final SocialMessageRepository socialMessageRepository;

	public final SocialMessageTextRepository socialMessageTextRepository;

	public SocialMessageService(SocialMessageRepository socialMessageRepository,
			SocialMessageTextRepository socialMessageTextRepository) {
		this.socialMessageRepository = socialMessageRepository;
		this.socialMessageTextRepository = socialMessageTextRepository;
	}

	public Page<SocialMessage> findAll(int pageNum, int pageSize, String sortBy) {
		Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortBy).descending());
		return socialMessageRepository.findAll(pageable);
	}

	@Transactional
	public void deleteMessage(String tweetId) {
		socialMessageRepository.deleteByMessageId(tweetId);
		socialMessageTextRepository.deleteByMessageId(tweetId);
	}

}
