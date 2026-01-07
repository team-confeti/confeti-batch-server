package confeti.confetibatchserver.scheduler;

import static confeti.confetibatchserver.job.JobInfo.RELATED_ARTIST_SYNC_JOB;

import confeti.confetibatchserver.domain.batch.jobconfig.JobConfig;
import confeti.confetibatchserver.domain.batch.jobconfig.application.JobConfigService;
import confeti.confetibatchserver.job.JobInfo;
import confeti.confetibatchserver.job.relatedartistsync.RelatedArtistSyncJobConfig;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
public class RelatedArtistSyncSchedule {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;
    private final JobConfigService jobConfigService;

    @Scheduled(
        cron = "${schedules.related-artist-sync.cron}",
        zone = "${schedules.related-artist-sync.zone}"
    )
    public void runRelatedArtistSyncJob() throws Exception {
        JobInfo jobInfo = RELATED_ARTIST_SYNC_JOB;
        JobConfig jobConfig = jobConfigService.getByJobInfo(jobInfo);
        if (jobConfig.isActive()) {
            String date = LocalDate.now().toString();

            JobParameters jobParameters = new JobParametersBuilder()
                .addString(RelatedArtistSyncJobConfig.JOB_PARAMETER_DATE, date)
                .toJobParameters();

            jobLauncher.run(jobRegistry.getJob(jobInfo.getJobName()), jobParameters);
        }
    }
}
