package confeti.confetibatchserver.job.artist;

import static confeti.confetibatchserver.config.ThreadPoolConfig.MUSIC_SYNC_EXECUTOR;
import static confeti.confetibatchserver.job.JobInfo.ARTIST_SONG_SYNC_JOB;
import static confeti.confetibatchserver.job.StepInfo.ARTIST_SONG_SYNC_STEP;
import static confeti.confetibatchserver.job.StepInfo.ARTIST_SYNC_STEP;
import static confeti.confetibatchserver.job.artist.ArtistQueryProvider.ARTIST_ID_MAPPER;
import static confeti.confetibatchserver.job.artist.ArtistQueryProvider.CONFETI_ARTIST_MAPPER;

import confeti.confetibatchserver.api.music.facade.MusicSyncFacade;
import confeti.confetibatchserver.domain.batch.stepconfig.StepConfig;
import confeti.confetibatchserver.domain.batch.stepconfig.application.StepConfigService;
import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import confeti.confetibatchserver.logger.JobLoggingListener;
import feign.RetryableException;
import java.io.IOException;
import java.util.concurrent.Executor;
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
import org.springframework.beans.factory.annotation.Qualifier;
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
    public Job artistSongSyncJob(Step artistSyncStep, Step artistSongSyncStep) {
        return new JobBuilder(ARTIST_SONG_SYNC_JOB.getJobName(), jobRepository)
            .start(artistSyncStep)
            .next(artistSongSyncStep)
            .listener(new JobLoggingListener())
            .build();
    }

    @Bean
    @JobScope
    public Step artistSongSyncStep(
        ItemReader<String> artistIdReader,
        ItemWriter<String> artistSongSyncWriter
    ) throws Exception {
        StepConfig stepConfig = stepConfigService.getByStepInfo(ARTIST_SONG_SYNC_STEP);

        return new StepBuilder(stepConfig.getStepInfo().getName(), jobRepository)
            .<String, String>chunk(stepConfig.getChunkSize(),
                platformTransactionManager)
            .reader(artistIdReader)
            .writer(artistSongSyncWriter)
            .faultTolerant()
            .retry(RetryableException.class)     // Feign의 재시도 가능 예외
            .retry(IOException.class)
            .retryLimit(3)
            .build();
    }

    @Bean
    @JobScope
    public Step artistSyncStep(
        ItemReader<ConfetiArtist> confetiArtistReader,
        ItemWriter<ConfetiArtist> artistSyncWriter
    ) throws Exception {
        StepConfig stepConfig = stepConfigService.getByStepInfo(ARTIST_SYNC_STEP);

        return new StepBuilder(stepConfig.getStepInfo().getName(), jobRepository)
            .<ConfetiArtist, ConfetiArtist>chunk(stepConfig.getChunkSize(),
                platformTransactionManager)
            .reader(confetiArtistReader)
            .writer(artistSyncWriter)
            .faultTolerant()
            .retry(RetryableException.class)     // Feign의 재시도 가능 예외
            .retry(IOException.class)
            .retryLimit(3)
            .build();
    }

    @Bean
    @StepScope
    public ItemReader<String> artistIdReader(DataSource dataSource) throws Exception {
        StepConfig stepConfig = stepConfigService.getByStepInfo(ARTIST_SYNC_STEP);
        return new JdbcPagingItemReaderBuilder<String>()
            .name("artistIdReader")
            .dataSource(dataSource)
            .fetchSize(stepConfig.getFetchSize())
            .pageSize(stepConfig.getPageSize())
            .rowMapper(ARTIST_ID_MAPPER)
            .queryProvider(artistQueryProvider.selectAllArtistIds(dataSource))
            .build();
    }

    @Bean
    @StepScope
    public ItemReader<ConfetiArtist> confetiArtistReader(DataSource dataSource) throws Exception {
        StepConfig stepConfig = stepConfigService.getByStepInfo(ARTIST_SYNC_STEP);
        return new JdbcPagingItemReaderBuilder<ConfetiArtist>()
            .name("confetiArtistReader")
            .dataSource(dataSource)
            .fetchSize(stepConfig.getFetchSize())
            .pageSize(stepConfig.getPageSize())
            .rowMapper(CONFETI_ARTIST_MAPPER)
            .queryProvider(artistQueryProvider.selectAllArtists(dataSource))
            .build();
    }

    @Bean
    public ItemWriter<ConfetiArtist> artistSyncWriter(
        MusicSyncFacade musicSyncFacade
    ) {
        return new BulkArtistUpsertWriter(musicSyncFacade);
    }

    @Bean
    public ItemWriter<String> artistSongSyncWriter(
        MusicSyncFacade musicSyncFacade,
        @Qualifier(MUSIC_SYNC_EXECUTOR) Executor executor
    ) {
        return new BulkArtistSongUpsertWriter(musicSyncFacade, executor);
    }

}
