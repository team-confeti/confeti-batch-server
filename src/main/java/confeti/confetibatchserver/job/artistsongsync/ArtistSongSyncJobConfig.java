package confeti.confetibatchserver.job.artistsongsync;

import static confeti.confetibatchserver.config.ThreadPoolConfig.MUSIC_SYNC_EXECUTOR;
import static confeti.confetibatchserver.job.JobInfo.ARTIST_SONG_SYNC_JOB;
import static confeti.confetibatchserver.job.StepInfo.ARTIST_SONG_SYNC_STEP;
import static confeti.confetibatchserver.job.StepInfo.ARTIST_SYNC_STEP;

import confeti.confetibatchserver.api.music.facade.MusicSyncFacade;
import confeti.confetibatchserver.domain.batch.stepconfig.StepConfig;
import confeti.confetibatchserver.domain.batch.stepconfig.application.StepConfigService;
import confeti.confetibatchserver.domain.music.artist.batch.query.ArtistQueryProvider;
import confeti.confetibatchserver.domain.music.artist.batch.reader.ArtistIdReader;
import confeti.confetibatchserver.domain.music.artist.batch.reader.ConfetiArtistReader;
import confeti.confetibatchserver.domain.music.artist.batch.writer.BulkArtistUpsertWriter;
import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import confeti.confetibatchserver.domain.music.song.application.SongService;
import confeti.confetibatchserver.domain.music.song.batch.processor.ArtistSongSyncProcessor;
import confeti.confetibatchserver.domain.music.song.batch.writer.BulkArtistSongUpsertWriter;
import confeti.confetibatchserver.external.service.MusicAPIHandler;
import confeti.confetibatchserver.job.artistsongsync.dto.ArtistIdWithSongs;
import confeti.confetibatchserver.logger.ArtistSongSyncSkipLogger;
import confeti.confetibatchserver.logger.JobLoggingListener;
import feign.RetryableException;
import java.io.IOException;
import java.util.concurrent.Future;
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
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.TransientDataAccessException;
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
        AsyncItemProcessor<String, ArtistIdWithSongs> itemProcessor,
        AsyncItemWriter<ArtistIdWithSongs> itemWriter
    ) throws Exception {
        StepConfig stepConfig = stepConfigService.getByStepInfo(ARTIST_SONG_SYNC_STEP);

        return new StepBuilder(stepConfig.getStepInfo().getName(), jobRepository)
            .<String, Future<ArtistIdWithSongs>>chunk(stepConfig.getChunkSize(),
                platformTransactionManager)
            .reader(artistIdReader)
            .processor(itemProcessor)
            .writer(itemWriter)
            .faultTolerant()
            .retry(RetryableException.class)     // Feign의 재시도 가능 예외
            .retry(IOException.class)
            .retry(TransientDataAccessException.class)
            .retryLimit(3)
            .skip(Exception.class)
            .listener(new ArtistSongSyncSkipLogger())
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
            .retry(TransientDataAccessException.class)
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
    public AsyncItemProcessor<String, ArtistIdWithSongs> asyncArtistSongSyncProcessor(
        MusicAPIHandler musicAPIHandler,
        SongService songService,
        @Qualifier(MUSIC_SYNC_EXECUTOR) TaskExecutor executor
    ) {
        AsyncItemProcessor<String, ArtistIdWithSongs> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(new ArtistSongSyncProcessor(musicAPIHandler, songService));
        asyncItemProcessor.setTaskExecutor(executor);
        return asyncItemProcessor;
    }

    @Bean
    public ItemWriter<ConfetiArtist> artistSyncWriter(
        MusicSyncFacade musicSyncFacade
    ) {
        return new BulkArtistUpsertWriter(musicSyncFacade);
    }

    @Bean
    public AsyncItemWriter<ArtistIdWithSongs> artistSongSyncWriter(
        MusicSyncFacade musicSyncFacade
    ) {
        AsyncItemWriter<ArtistIdWithSongs> itemWriter = new AsyncItemWriter<>();
        itemWriter.setDelegate(new BulkArtistSongUpsertWriter(musicSyncFacade));
        return itemWriter;
    }

}
