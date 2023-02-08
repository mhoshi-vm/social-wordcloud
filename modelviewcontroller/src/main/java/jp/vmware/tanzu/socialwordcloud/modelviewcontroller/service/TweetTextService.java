package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.TweetTextRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TweetTextService {

	public static final int pageSize = 400;

	private final TweetTextRepository tweetTextRepository;

	public TweetTextService(TweetTextRepository tweetTextRepository) {
		this.tweetTextRepository = tweetTextRepository;
	}

	public List<TweetTextRepository.TextCount> listTweetsPage() {
		return tweetTextRepository.listTextCount(PageRequest.of(0, pageSize));
	}

}
