package com.solidcode.gameprovider.config;

import static io.lettuce.core.ReadFrom.REPLICA_PREFERRED;
import static java.time.Duration.ofMinutes;
import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfig implements CachingConfigurer {

  @Value("${spring.cache.redis.time-to-live}")
  private long redisTimeToLive;

  private final RedisProperties redisProperties;

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(
        redisProperties.getHost(), redisProperties.getPort());

    LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
        .readFrom(REPLICA_PREFERRED)
        .build();
    return new LettuceConnectionFactory(configuration, clientConfig);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
    redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
    redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    return redisTemplate;
  }

  @Override
  @Bean
  public RedisCacheManager cacheManager() {
    return RedisCacheManager
        .builder(this.redisConnectionFactory())
        .cacheDefaults(this.cacheConfiguration())
        .withCacheConfiguration("game", this.cacheConfiguration().entryTtl(ofMinutes(10)))
        .build();
  }

  @Bean
  public RedisCacheConfiguration cacheConfiguration() {

    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(ofMinutes(redisTimeToLive))
        .disableCachingNullValues()
        .serializeValuesWith(fromSerializer(new GenericJackson2JsonRedisSerializer()));
  }

  @Override
  public CacheErrorHandler errorHandler() {
    return new CacheErrorHandler() {
      @Override
      public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.info("Failure getting from cache: {}, exception: {}", cache.getName(), exception);
      }

      @Override
      public void handleCachePutError(RuntimeException exception, Cache cache, Object key,
          Object value) {
        log.info("Failure putting into cache: {}, exception: {}", cache.getName(), exception);
      }

      @Override
      public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.info("Failure evicting from cache: {}, exception: {}", cache.getName(), exception);
      }

      @Override
      public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.info("Failure clearing cache: {}, exception: {}", cache.getName(), exception);
      }
    };
  }
}