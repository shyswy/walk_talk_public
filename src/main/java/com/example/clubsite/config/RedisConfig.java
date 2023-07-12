package com.example.clubsite.config;

import com.example.clubsite.redis.object.ChatRoomCacheDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@EnableRedisRepositories(basePackages = "com.example.clubsite.redis.repository", redisTemplateRef = "redisTemplate")//Redis를 사용하는 레포지토리를 활성화
@EnableCaching//스프링 캐시를 활성화
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));//LettuceConnectionFactory 사용해서 연결
    }


    /*
    RedisTemplate을 생성하는 메서드.
    RedisTemplate은 Redis에서 데이터를 저장하고 검색하기 위한 핵심 클래스.
    이 메서드에서는 RedisTemplate의 키와 값의 직렬화 방식을 설정하고, redisConnectionFactory()를 사용하여 Redis 연결을 설정한다.
     */
    @Bean
    public RedisTemplate<String, List<ChatRoomCacheDTO>> redisTemplate() {//
        RedisTemplate<String, List<ChatRoomCacheDTO>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer()); //Key: 문자열로 설정
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());//Value: JSON 타입으로 설정
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}





