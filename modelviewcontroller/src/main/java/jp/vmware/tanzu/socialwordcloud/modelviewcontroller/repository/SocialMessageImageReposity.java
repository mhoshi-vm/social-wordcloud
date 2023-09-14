package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessageImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialMessageImageReposity extends JpaRepository<SocialMessageImage, Integer> {

}
