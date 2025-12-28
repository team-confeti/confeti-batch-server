package confeti.confetibatchserver.config;

import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

    public static final String MUSIC_SYNC_EXECUTOR = "musicSyncExecutor";

    @Bean(MUSIC_SYNC_EXECUTOR)
    public ThreadPoolTaskExecutor batchThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("music-sync-worker-");

        executor.setRejectedExecutionHandler(
            new ThreadPoolExecutor.CallerRunsPolicy()); // 큐가 꽉 차면 메인 스레드가 작업을 처리
        executor.setWaitForTasksToCompleteOnShutdown(true); // 종료 시 작업 계속하도록
        executor.initialize();

        return executor;
    }
}
