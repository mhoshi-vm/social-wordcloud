package jp.vmware.tanzu.socialwordcloud.standalone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
@TestConfiguration(proxyBeanMethods = false)
@TestPropertySource(properties = "database=pgvector")
public class WordcloudApplicationTest {

    @Bean
    @RestartScope
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(
                DockerImageName.parse("ghcr.io/postgresml/postgresml:2.7.3").asCompatibleSubstituteFor("postgres"))
                .withCommand("sleep", "infinity")
                .withLabel("org.springframework.boot.service-connection", "postgres")
                .withUsername("postgresml")
                .withPassword("postgresml")
                .withDatabaseName("postgresml")
                .waitingFor(new LogMessageWaitStrategy().withRegEx(".*Starting dashboard.*\\s")
                        .withStartupTimeout(Duration.of(60, ChronoUnit.SECONDS)));
    }

    public static void main(String[] args) {
        SpringApplication.from(WordcloudApplication::main).with(WordcloudApplicationTest.class).run(args);
    }
}