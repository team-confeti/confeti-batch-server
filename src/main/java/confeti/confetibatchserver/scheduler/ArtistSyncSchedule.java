package confeti.confetibatchserver.scheduler;

import static confeti.confetibatchserver.job.JobInfo.ARTIST_SYNC_JOB;

import confeti.confetibatchserver.domain.batch.jobconfig.JobConfig;
import confeti.confetibatchserver.domain.batch.jobconfig.application.JobConfigService;
import confeti.confetibatchserver.job.artist.ArtistSyncJobConfig;
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
public class ArtistSyncSchedule {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;
    private final JobConfigService jobConfigService;

    @Scheduled(
        cron = "${schedules.artist-sync.cron}",
        zone = "${schedules.artist-sync.zone}"
    )
    public void runArtistSyncJob() throws Exception {
        JobConfig jobConfig = jobConfigService.getByJobInfo(ARTIST_SYNC_JOB);
        if (jobConfig.isActive()) {
            String date = LocalDate.now().toString();

            JobParameters jobParameters = new JobParametersBuilder()
                .addString(ArtistSyncJobConfig.JOB_PARAMETER_DATE, date)
                .toJobParameters();

            jobLauncher.run(jobRegistry.getJob(ARTIST_SYNC_JOB.getJobName()), jobParameters);
        }
    }

}
