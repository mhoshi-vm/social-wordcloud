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

	public String coordinates;

	public List<String> names;

	private SocialMessageData(String origin, String id, String text, String lang, List<String> names, String coordinates) {
		this.origin = origin;
		this.id = id;
		this.text = text;
		this.lang = lang;
		this.names = names;
		this.coordinates = coordinates;
	}

	public static SocialMessageData createSocialMessageData(String origin, String id, String text, String lang,
			List<String> names) {
		return new SocialMessageData(origin, id, text, lang, names, "");
	}

	public static SocialMessageData createSocialMessageData(String origin, String id, String text, String lang,
															List<String> names, String coordinates) {
		return new SocialMessageData(origin, id, text, lang, names, coordinates);
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
		dataNode.put("coordinates", coordinates);

		ObjectNode userNode = mapper.createObjectNode();

		for (String name : names) {
			userNode.put("name", name);
		}

		usersNode.add(userNode);
		includesNode.set("users", usersNode);

		jNode.set("data", dataNode);
		jNode.set("includes", includesNode);

		return jNode.toString();
	}

}
