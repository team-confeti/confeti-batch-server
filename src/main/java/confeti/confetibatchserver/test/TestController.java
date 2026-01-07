package confeti.confetibatchserver.test;

import static confeti.confetibatchserver.job.JobInfo.ARTIST_SONG_SYNC_JOB;
import static confeti.confetibatchserver.job.JobInfo.RELATED_ARTIST_SYNC_JOB;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @GetMapping("/test")
    public String test(
        @RequestParam("value") String value
    ) throws Exception {
        JobParameters jobParameter = new JobParametersBuilder()
            .addString("date", value)
            .toJobParameters();

        jobLauncher.run(jobRegistry.getJob(ARTIST_SONG_SYNC_JOB.getJobName()), jobParameter);

        return "ok";
    }

    @GetMapping("/test/related-artist")
    public String testRelatedArtist(
        @RequestParam("value") String value
    ) throws Exception {
        JobParameters jobParameter = new JobParametersBuilder()
            .addString("date", value)
            .toJobParameters();

        jobLauncher.run(jobRegistry.getJob(RELATED_ARTIST_SYNC_JOB.getJobName()), jobParameter);

        return "ok";
    }
}
