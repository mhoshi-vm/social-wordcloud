package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessageVader;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SocialMessageVaderRepository extends CrudRepository<SocialMessageVader, Integer> {

	List<SocialMessageVader> findAllByOrderByMessageIdDesc();

}
