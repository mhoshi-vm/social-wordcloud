package jp.vmware.tanzu.socialwordcloud.library.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class SocialMessageData {

	public String origin;

	public String id;

	public String text;

	public String lang;

	public List<String> names;

	public List<String> images;

	private SocialMessageData(String origin, String id, String text, String lang, List<String> names,
			List<String> images) {
		this.origin = origin;
		this.id = id;
		this.text = text;
		this.lang = lang;
		this.names = names;
		this.images = images;
	}

	public static SocialMessageData createSocialMessageData(String origin, String id, String text, String lang,
			List<String> names, List<String> images) {
		return new SocialMessageData(origin, id, text, lang, names, images);
	}

	public String createJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jNode = mapper.createObjectNode();
		ObjectNode dataNode = mapper.createObjectNode();
		ObjectNode includesNode = mapper.createObjectNode();
		ArrayNode usersNode = mapper.createArrayNode();
		ObjectNode nullNode = mapper.createObjectNode();

		jNode.put("origin", origin);
		jNode.set("data", nullNode);
		jNode.set("includes", nullNode);

		dataNode.put("id", id);
		dataNode.put("text", text);
		dataNode.put("lang", lang);

		ObjectNode userNode = mapper.createObjectNode();

		for (String name : names) {
			userNode.put("name", name);
		}

		ArrayNode imagesNode = mapper.createArrayNode();

		for (String image : images) {
			imagesNode.add(image);
		}

		usersNode.add(userNode);
		includesNode.set("users", usersNode);
		includesNode.set("images", imagesNode);

		jNode.set("data", dataNode);
		jNode.set("includes", includesNode);

		return jNode.toString();
	}

	public List<String> getImages() {
		return images;
	}

}
