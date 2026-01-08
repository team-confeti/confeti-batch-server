package confeti.confetibatchserver.global.config;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Value("${apple-music.api.rate-limit}")
    private double appleMusicRequestPerSecond;

    @Bean
    public RateLimiter appleMusicRateLimiter() {
        return RateLimiter.create(appleMusicRequestPerSecond);
    }
}
