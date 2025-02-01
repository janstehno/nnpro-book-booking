package cz.upce.nnpro.bookbooking.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfiguration {
    @Value("${spring.redis.host}")
    private String HOST;

    @Value("${spring.redis.port}")
    private String PORT;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + HOST + ":" + PORT);
        return Redisson.create(config);
    }

}
