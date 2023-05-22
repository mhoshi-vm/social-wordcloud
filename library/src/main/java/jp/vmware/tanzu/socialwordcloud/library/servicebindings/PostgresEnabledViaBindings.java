package jp.vmware.tanzu.socialwordcloud.library.servicebindings;

import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.boot.BindingsPropertiesProcessor;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

public class PostgresEnabledViaBindings implements BindingsPropertiesProcessor {

	public static final String TYPE = "postgresql";

	@Override
	public void process(Environment environment, Bindings bindings, Map<String, Object> map) {

		List<Binding> postgresBindings = bindings.filterBindings(TYPE);
		if (postgresBindings.size() > 0) {
			map.put("db.type", "postgresql");
			if (postgresBindings.get(0).getSecret().get("greenplum") != null) {
				map.put("database", "greenplum");
				map.put("spring.sql.init.mode", "never");
				map.put("db.instance", "Greenplum DB");
			}
			else {
				map.put("database", "postgres");
			}
		}
		else {
			map.put("database", "h2");
		}

	}

}
