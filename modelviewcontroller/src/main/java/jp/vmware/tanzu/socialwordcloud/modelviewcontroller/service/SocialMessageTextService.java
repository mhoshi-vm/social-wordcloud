package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageTextRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class SocialMessageTextService {

	public static final int pageSize = 400;

	private final SocialMessageTextRepository socialMessageTextRepository;

	public SocialMessageTextService(SocialMessageTextRepository socialMessageTextRepository) {
		this.socialMessageTextRepository = socialMessageTextRepository;
	}

	public List<SocialMessageTextRepository.TextCount> listMessagePage() {
		return socialMessageTextRepository.listTextCount(PageRequest.of(0, pageSize));
	}

}
