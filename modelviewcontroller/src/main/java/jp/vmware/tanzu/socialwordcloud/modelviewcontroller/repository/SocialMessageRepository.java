package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SocialMessageRepository extends JpaRepository<SocialMessage, Integer> {

	Page<SocialMessage> findAll(Pageable pageable);

	List<SocialMessage> findSocialMessageByMessageIdIn(List<String> messageId);

	long deleteByMessageId(String tweetId);

	@Modifying
	@Query(value = "UPDATE social_message SET negative_sentiment = tweet_sent_vader_check(context) WHERE message_id=?1",
			nativeQuery = true)
	void updateNegativeSentiment(String messageId);

}
