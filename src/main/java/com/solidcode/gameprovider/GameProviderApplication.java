package com.solidcode.gameprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GameProviderApplication {

  public static void main(String[] args) {
    SpringApplication.run(GameProviderApplication.class, args);
  }

}
