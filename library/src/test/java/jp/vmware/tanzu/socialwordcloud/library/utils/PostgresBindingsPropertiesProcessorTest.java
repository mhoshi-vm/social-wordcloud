package jp.vmware.tanzu.socialwordcloud.library.utils;

import jp.vmware.tanzu.socialwordcloud.library.servicebindings.PostgresEnabledViaBindings;
import jp.vmware.tanzu.socialwordcloud.library.test_utils.FluentMap;
import org.assertj.core.api.MapAssert;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.mock.env.MockEnvironment;

import java.nio.file.Paths;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class PostgresBindingsPropertiesProcessorTest {

	private final Bindings bindings = new Bindings(new Binding("test-name", Paths.get("test-path"),
			new FluentMap().withEntry(Binding.TYPE, PostgresEnabledViaBindings.TYPE).withEntry("dummy", "dummy")));

	private final MockEnvironment environment = new MockEnvironment();

	private final HashMap<String, Object> properties = new HashMap<>();

	@Test
	void test() {
		new PostgresEnabledViaBindings().process(environment, bindings, properties);
		MapAssert<String, Object> stringObjectMapAssert = assertThat(properties).containsEntry("db.type", "postgresql");
	}

}