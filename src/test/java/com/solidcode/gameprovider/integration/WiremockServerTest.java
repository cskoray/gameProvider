package com.solidcode.gameprovider.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.function.BiFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class MockServerTest {

  private RestTemplate restTemplate;
  private WireMockServer wm;
  private static String baseUrl = "http://localhost:%s%s";

  public BiFunction<String, String, String> serverUrl = (String path, String port) -> format(
      baseUrl, port, path);
  
  @BeforeEach
  void setUp() {
    restTemplate = new RestTemplate();
    wm = new WireMockServer(options().port(8080));
    wm.start();
  }

  @Test
  public void givenWireMockEndpoint_whenGetWithoutParams_thenVerifyRequest() {
    wm.stubFor(get(urlEqualTo("/api/resource/"))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader("Content-Type", TEXT_PLAIN_VALUE)
            .withBody("test")));

    ResponseEntity response = restTemplate.getForEntity("http://localhost:8080/api/resource/",
        String.class);

    assertThat("Verify Response Body", response.getBody().toString().contains("test"));
    assertThat("Verify Status Code", response.getStatusCode().equals(HttpStatus.OK));
  }

}
