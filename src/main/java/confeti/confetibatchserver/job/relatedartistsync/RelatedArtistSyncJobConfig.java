package confeti.confetibatchserver.job.relatedartistsync;

import static confeti.confetibatchserver.config.ThreadPoolConfig.MUSIC_SYNC_EXECUTOR;
import static confeti.confetibatchserver.job.JobInfo.RELATED_ARTIST_SYNC_JOB;
import static confeti.confetibatchserver.job.StepInfo.RELATED_ARTIST_SYNC_STEP;

import confeti.confetibatchserver.api.music.facade.MusicSyncFacade;
import confeti.confetibatchserver.domain.batch.stepconfig.StepConfig;
import confeti.confetibatchserver.domain.batch.stepconfig.application.StepConfigService;
import confeti.confetibatchserver.external.service.MusicAPIHandler;
import confeti.confetibatchserver.job.artistsongsync.query.ArtistQueryProvider;
import confeti.confetibatchserver.job.artistsongsync.reader.ArtistIdReader;
import confeti.confetibatchserver.job.relatedartistsync.dto.ArtistRelations;
import confeti.confetibatchserver.job.relatedartistsync.processor.RelatedArtistSyncProcessor;
import confeti.confetibatchserver.job.relatedartistsync.writer.BulkRelatedArtistUpsertWriter;
import confeti.confetibatchserver.logger.JobLoggingListener;
import feign.RetryableException;
import java.io.IOException;
import java.util.concurrent.Future;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class RelatedArtistSyncJobConfig {

    public static final String JOB_PARAMETER_DATE = "requestDate";

    private final PlatformTransactionManager platformTransactionManager;

    private final StepConfigService stepConfigService;
    private final JobRepository jobRepository;
    private final ArtistQueryProvider artistQueryProvider;

    @Bean
    public Job relatedArtistSyncJob(Step relatedArtistSyncStep) {
        return new JobBuilder(RELATED_ARTIST_SYNC_JOB.getJobName(), jobRepository)
            .start(relatedArtistSyncStep)
            .listener(new JobLoggingListener())
            .build();
    }

    @Bean
    @JobScope
    public Step relatedArtistSyncStep(
        ItemReader<String> relatedArtistStepReader,
        AsyncItemProcessor<String, ArtistRelations> relatedArtistProcessor,
        AsyncItemWriter<ArtistRelations> artistSongSyncWriter
    ) throws Exception {
        StepConfig stepConfig = stepConfigService.getByStepInfo(RELATED_ARTIST_SYNC_STEP);

        return new StepBuilder(stepConfig.getStepInfo().getName(), jobRepository)
            .<String, Future<ArtistRelations>>chunk(stepConfig.getChunkSize(),
                platformTransactionManager)
            .reader(relatedArtistStepReader)
            .processor(relatedArtistProcessor)
            .writer(artistSongSyncWriter)
            .faultTolerant()
            .retry(RetryableException.class)     // Feign의 재시도 가능 예외
            .retry(IOException.class)
            .retryLimit(3)
            .build();
    }

    @Bean
    public ItemReader<String> relatedArtistStepReader(DataSource dataSource) throws Exception {
        StepConfig stepConfig = stepConfigService.getByStepInfo(RELATED_ARTIST_SYNC_STEP);
        return new ArtistIdReader(dataSource, stepConfig, artistQueryProvider);
    }

    @Bean
    public AsyncItemProcessor<String, ArtistRelations> relatedArtistSyncProcessor(
        MusicAPIHandler musicAPIHandler,
        @Qualifier(MUSIC_SYNC_EXECUTOR) TaskExecutor executor
    ) {
        AsyncItemProcessor<String, ArtistRelations> processor = new AsyncItemProcessor<>();
        processor.setDelegate(new RelatedArtistSyncProcessor(musicAPIHandler));
        processor.setTaskExecutor(executor);
        return processor;
    }

    @Bean
    public AsyncItemWriter<ArtistRelations> relatedArtistSyncWriter(
        MusicSyncFacade musicSyncFacade
    ) {
        AsyncItemWriter<ArtistRelations> writer = new AsyncItemWriter<>();
        writer.setDelegate(new BulkRelatedArtistUpsertWriter(musicSyncFacade));
        return writer;
    }
}
