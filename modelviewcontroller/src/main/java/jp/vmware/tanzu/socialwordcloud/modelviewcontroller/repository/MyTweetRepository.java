package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.MyTweet;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MyTweetRepository extends CrudRepository<MyTweet, Integer> {

	List<MyTweet> findAllByOrderByTweetIdDesc();

	long deleteByTweetId(String tweetId);

}
