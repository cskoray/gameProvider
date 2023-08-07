package com.hymnai.ledgermanager.componenttest;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.hymnai.ledgermanager.util.ObjectFactoryTest.WIREMOCK_PORT;
import static java.lang.String.format;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.hymnai.ledgermanager.LedgerManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {LedgerManager.class})
@ComponentScan(basePackages = {"com.hymnai.*"})
@Slf4j
@AutoConfigureWireMock(port = 8888)
@DirtiesContext
public abstract class BaseComponentTest {

  @ClassRule
  public static WireMockRule WIREMOCK_SERVER = new WireMockRule(
      wireMockConfig().port(WIREMOCK_PORT));

  @LocalServerPort
  int randomServerPort;

  public static TestRestTemplate restTemplate;
  static ExecutorService executor;

  @BeforeAll
  public static void beforeClass() {
    restTemplate = new TestRestTemplate();
    executor = Executors.newSingleThreadExecutor();
  }

  @AfterAll
  public static void afterClass() {
    executor.shutdownNow();
    WIREMOCK_SERVER.resetAll();
  }

  public Function<String, String> serverUrl = (String path) -> format("http://localhost:%s%s",
      randomServerPort, path);
}