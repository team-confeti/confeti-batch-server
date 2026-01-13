package confeti.confetibatchserver.global.aop;


import com.google.common.util.concurrent.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AppleRateLimitAspect {

    @SuppressWarnings("UnstableApiUsage")
    private final RateLimiter appleMusicRateLimiter;

    @Before("@annotation(confeti.confetibatchserver.global.annotation.AppleMusicLateLimit) || @within(confeti.confetibatchserver.global.annotation.AppleMusicLateLimit)")
    public void checkRateLimit() {
        appleMusicRateLimiter.acquire();
    }
}
