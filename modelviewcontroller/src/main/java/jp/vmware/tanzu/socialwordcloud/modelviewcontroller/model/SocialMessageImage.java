package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model;

import jakarta.persistence.*;

@Entity
public class SocialMessageImage {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer id;

	public String messageId;

	@Column(length = 10485760)
	public byte[] image;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

}
