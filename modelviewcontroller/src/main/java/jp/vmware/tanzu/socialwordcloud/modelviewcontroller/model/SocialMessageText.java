package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SocialMessageText {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer id;

	public String messageId;

	public String text;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String tweetId) {
		this.messageId = tweetId;
	}

	public String getText() {
		return text;
	}

	public void setText(String txt) {
		this.text = txt;
	}

}
