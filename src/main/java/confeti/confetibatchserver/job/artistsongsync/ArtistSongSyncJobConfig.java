package confeti.confetibatchserver.job.artistsongsync;

import static confeti.confetibatchserver.global.config.ThreadPoolConfig.MUSIC_SYNC_EXECUTOR;
import static confeti.confetibatchserver.job.JobInfo.ARTIST_SONG_SYNC_JOB;
import static confeti.confetibatchserver.job.StepInfo.ARTIST_SONG_SYNC_STEP;
import static confeti.confetibatchserver.job.StepInfo.ARTIST_SYNC_STEP;

import confeti.confetibatchserver.api.music.facade.MusicSyncFacade;
import confeti.confetibatchserver.domain.batch.stepconfig.StepConfig;
import confeti.confetibatchserver.domain.batch.stepconfig.application.StepConfigService;
import confeti.confetibatchserver.domain.music.artist.batch.query.ArtistQueryProvider;
import confeti.confetibatchserver.domain.music.artist.batch.reader.ArtistIdReader;
import confeti.confetibatchserver.domain.music.artist.batch.reader.ConfetiArtistReader;
import confeti.confetibatchserver.domain.music.artist.batch.writer.BulkArtistSongUpsertWriter;
import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import confeti.confetibatchserver.domain.music.song.batch.writer.BulkArtistUpsertWriter;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ArtistSongSyncJobConfig {

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
        return new ArtistIdReader(dataSource, stepConfig, artistQueryProvider);
    }

    @Bean
    @StepScope
    public ItemReader<ConfetiArtist> confetiArtistReader(DataSource dataSource) throws Exception {
        StepConfig stepConfig = stepConfigService.getByStepInfo(ARTIST_SYNC_STEP);
        return new ConfetiArtistReader(dataSource, stepConfig, artistQueryProvider);
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
