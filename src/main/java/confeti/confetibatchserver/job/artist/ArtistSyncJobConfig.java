package confeti.confetibatchserver.job.artist;

import confeti.confetibatchserver.domain.music.artist.Artist;
import confeti.confetibatchserver.domain.music.artist.infra.repository.ArtistRepository;
import confeti.confetibatchserver.external.client.AppleMusicFeignClient;
import feign.RetryableException;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ArtistSyncJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job artistSyncJob(Step artistSyncStep) {
        return new JobBuilder("artistSyncJob", jobRepository)
            .start(artistSyncStep)
            .build();
    }

    @Bean
    public Step artistSyncStep(
        ItemReader<List<Artist>> artistSyncReader,
        ItemProcessor<List<Artist>, List<Artist>> artistSyncProcessor,
        ItemWriter<List<Artist>> artistSyncWriter
    ) {
        return new StepBuilder("artistSyncStep", jobRepository)
            .<List<Artist>, List<Artist>>chunk(1, platformTransactionManager)
            .reader(artistSyncReader)
            .processor(artistSyncProcessor)
            .writer(artistSyncWriter)
            .faultTolerant()
            .retry(RetryableException.class)     // Feign의 재시도 가능 예외
            .retry(IOException.class)
            .retryLimit(3)
            .build();
    }

    @Bean
    @StepScope
    public ItemReader<List<Artist>> artistSyncReader(ArtistRepository artistRepository) {
        return new BulkArtistJpaReader(artistRepository);
    }

    @Bean
    public ItemProcessor<List<Artist>, List<Artist>> artistSyncProcessor(
        AppleMusicFeignClient appleMusicFeignClient) {
        return new BulkArtistSyncProcessor(appleMusicFeignClient);
    }

    @Bean
    public ItemWriter<List<Artist>> artistSyncWriter(JdbcTemplate jdbcTemplate) {
        return new BulkArtistJdbcUpsertWriter(jdbcTemplate);
    }

}
