package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SocialMessageRepository extends CrudRepository<SocialMessage, Integer> {

	List<SocialMessage> findAllByOrderByMessageIdDesc();

	long deleteByMessageId(String tweetId);

	@Modifying
	@Query(value = "UPDATE social_message SET negative_sentiment = tweet_sent_vader_check(context) WHERE message_id=?1",
			nativeQuery = true)
	void updateNegativeSentiment(String messageId);

}
