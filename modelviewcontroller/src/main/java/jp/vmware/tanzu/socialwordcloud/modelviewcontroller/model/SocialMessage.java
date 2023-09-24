package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class SocialMessage {

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

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String tweetId) {
		this.messageId = tweetId;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String text) {
		this.context = text;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

}
