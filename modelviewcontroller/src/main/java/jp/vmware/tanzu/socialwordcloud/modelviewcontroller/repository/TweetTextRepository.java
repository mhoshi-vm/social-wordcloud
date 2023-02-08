package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.TweetText;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TweetTextRepository extends CrudRepository<TweetText, Integer> {

	long deleteByTweetId(String tweetId);

	@Query("select text as text, count(text) as size from TweetText group by text order by size desc")
	List<TextCount> listTextCount(Pageable pageable);

	interface TextCount {

		String getText();

		Long getSize();

	}

}
