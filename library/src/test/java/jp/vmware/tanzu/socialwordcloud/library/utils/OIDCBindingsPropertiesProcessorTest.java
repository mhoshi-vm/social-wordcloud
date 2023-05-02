package jp.vmware.tanzu.socialwordcloud.library.utils;

import jp.vmware.tanzu.socialwordcloud.library.servicebindings.OIDCEnabledViaBindings;
import jp.vmware.tanzu.socialwordcloud.library.test_utils.FluentMap;
import org.assertj.core.api.MapAssert;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.mock.env.MockEnvironment;

import java.nio.file.Paths;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.bindings.boot.SpringSecurityOAuth2BindingsPropertiesProcessor.TYPE;

class OIDCBindingsPropertiesProcessorTest {

	private final Bindings bindings = new Bindings(new Binding("test-name", Paths.get("test-path"),
			new FluentMap().withEntry(Binding.TYPE, TYPE)
				.withEntry("provider", "github")
				.withEntry("client-id", "github-client-id")
				.withEntry("client-secret", "github-client-secret")));

	private final MockEnvironment environment = new MockEnvironment();

	private final HashMap<String, Object> properties = new HashMap<>();

	@Test
	void test() {
		new OIDCEnabledViaBindings().process(environment, bindings, properties);
		MapAssert<String, Object> stringObjectMapAssert = assertThat(properties).containsEntry("oauth2.enabled",
				"true");
	}

}