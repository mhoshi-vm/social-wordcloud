package jp.vmware.tanzu.socialwordcloud.library.servicebindings;

import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.boot.BindingsPropertiesProcessor;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

public class RedisEnabledViaBindings implements BindingsPropertiesProcessor {

	public static final String TYPE = "redis";

	@Override
	public void process(Environment environment, Bindings bindings, Map<String, Object> map) {

		List<Binding> redisBindings = bindings.filterBindings(TYPE);
		if (redisBindings.size() == 0) {
			return;
		}
		map.put("spring.session.store-type", "redis");
		map.put("management.health.redis.enabled", "true");

	}

	static class BindingPropertiesMapper {

		private final Map<String, String> secret;

		private final Map<String, Object> properties;

		private final PropertyMapper mapper;

		public BindingPropertiesMapper(Map<String, String> secret, Map<String, Object> properties) {
			this.secret = secret;
			this.properties = properties;
			this.mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		}

		public void map(String from, String to) {
			mapper.from(secret.get(from)).to(value -> this.properties.put(to, value));
		}

	}

}
