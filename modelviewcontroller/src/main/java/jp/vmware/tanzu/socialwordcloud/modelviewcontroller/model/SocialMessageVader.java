package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.time.LocalDateTime;

@Entity
@ConditionalOnProperty(value = "database", havingValue = "greenplum")
public class SocialMessageVader {

	@Id
	public String messageId;

	@Column(length = 10485760)
	public String context;

	public String origin;

	public String username;

	public String lang;

	public Float negativeSentiment;

	@CreationTimestamp
	public LocalDateTime createDateTime;

}
