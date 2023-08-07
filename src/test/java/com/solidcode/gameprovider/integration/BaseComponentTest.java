package com.solidcode.gameprovider.integration;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.solidcode.gameprovider.GameProviderApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {
    GameProviderApplication.class})
@ComponentScan(basePackages = {"com.solidcode.*"})
@Slf4j
@AutoConfigureWireMock
@DirtiesContext
public abstract class BaseComponentTest {

  public final static Integer WIREMOCK_PORT = 8080;

  @ClassRule
  public static WireMockRule WIREMOCK_SERVER = new WireMockRule(
      wireMockConfig().port(WIREMOCK_PORT));

  @LocalServerPort
  int randomServerPort;

  public static TestRestTemplate restTemplate;

  @BeforeAll
  public static void beforeClass() {
    restTemplate = new TestRestTemplate();
  }

  @AfterAll
  public static void afterClass() {
    WIREMOCK_SERVER.resetAll();
  }
}