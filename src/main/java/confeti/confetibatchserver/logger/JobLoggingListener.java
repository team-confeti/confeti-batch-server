package confeti.confetibatchserver.logger;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class JobLoggingListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("============== JOB STARTED ==============");
        log.info("Job Name:   {}", jobExecution.getJobInstance().getJobName());
        log.info("Job Param:  {}", jobExecution.getJobParameters());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long duration = Duration.between(jobExecution.getStartTime(), LocalDateTime.now())
            .toMillis();

        log.info("============== JOB FINISHED ==============");
        log.info("Job Name:   {}", jobExecution.getJobInstance().getJobName());
        log.info("Status:     {}", jobExecution.getStatus());
        log.info("Total Time: {}ms", duration);

        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Job Failed With: {}", jobExecution.getAllFailureExceptions());
        }
    }
}
