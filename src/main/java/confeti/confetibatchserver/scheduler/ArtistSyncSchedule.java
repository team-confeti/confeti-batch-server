package confeti.confetibatchserver.scheduler;

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

    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void runArtistSyncJob() throws Exception {
        String date = LocalDate.now().toString();

        JobParameters jobParameters = new JobParametersBuilder()
            .addString("requestDate", date)
            .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("artistSyncJob"), jobParameters);
    }

}
