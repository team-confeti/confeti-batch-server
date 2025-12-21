package confeti.confetibatchserver.job.artist;

import static confeti.confetibatchserver.job.JobInfo.ARTIST_SYNC_JOB;
import static confeti.confetibatchserver.job.StepInfo.ARTIST_SYNC_STEP;
import static confeti.confetibatchserver.job.artist.ArtistQueryProvider.ARTIST_MAPPER;

import confeti.confetibatchserver.domain.batch.stepconfig.StepConfig;
import confeti.confetibatchserver.domain.batch.stepconfig.application.StepConfigService;
import confeti.confetibatchserver.domain.music.artist.Artist;
import confeti.confetibatchserver.domain.music.artist.application.ArtistService;
import confeti.confetibatchserver.external.client.AppleMusicFeignClient;
import confeti.confetibatchserver.logger.JobLoggingListener;
import feign.RetryableException;
import java.io.IOException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ArtistSyncJobConfig {

    public static final String JOB_PARAMETER_DATE = "requestDate";

    private final PlatformTransactionManager platformTransactionManager;

    private final StepConfigService stepConfigService;
    private final JobRepository jobRepository;
    private final ArtistQueryProvider artistQueryProvider;

    @Bean
    public Job artistSyncJob(Step artistSyncStep) {
        return new JobBuilder(ARTIST_SYNC_JOB.getJobName(), jobRepository)
            .start(artistSyncStep)
            .listener(new JobLoggingListener())
            .build();
    }

    @Bean
    @JobScope
    public Step artistSyncStep(
        ItemReader<Artist> artistSyncReader,
        ItemWriter<Artist> artistSyncWriter
    ) throws Exception {
        StepConfig stepConfig = stepConfigService.getByStepInfo(ARTIST_SYNC_STEP);

        return new StepBuilder(ARTIST_SYNC_STEP.getName(), jobRepository)
            .<Artist, Artist>chunk(stepConfig.getChunkSize(),
                platformTransactionManager)
            .reader(artistSyncReader)
            .writer(artistSyncWriter)
            .faultTolerant()
            .retry(RetryableException.class)     // Feign의 재시도 가능 예외
            .retry(IOException.class)
            .retryLimit(3)
            .build();
    }

    @Bean
    @StepScope
    public ItemReader<Artist> artistSyncReader(DataSource dataSource) throws Exception {
        StepConfig stepConfig = stepConfigService.getByStepInfo(ARTIST_SYNC_STEP);
        return new JdbcPagingItemReaderBuilder<Artist>()
            .name("artistSyncReader")
            .dataSource(dataSource)
            .fetchSize(stepConfig.getFetchSize())
            .pageSize(stepConfig.getPageSize())
            .rowMapper(ARTIST_MAPPER)
            .queryProvider(artistQueryProvider.selectAllArtists(dataSource))
            .build();
    }

    @Bean
    public ItemWriter<Artist> artistSyncWriter(
        ArtistService artistService,
        AppleMusicFeignClient appleMusicFeignClient
    ) {
        return new BulkArtistUpsertWriter(artistService, appleMusicFeignClient);
    }

}
