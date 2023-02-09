package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SocialMessageRepository extends CrudRepository<SocialMessage, Integer> {

	List<SocialMessage> findAllByOrderByMessageIdDesc();

	long deleteByMessageId(String tweetId);

}
