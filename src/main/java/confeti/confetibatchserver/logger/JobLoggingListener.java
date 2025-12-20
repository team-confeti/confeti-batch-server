package confeti.confetibatchserver.logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class JobLoggingListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("============== JOB STARTED ==============");
        log.info("Job Name: {} | Param: {}",
            jobExecution.getJobInstance().getJobName(),
            jobExecution.getJobParameters()
        );
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            loggingWhenFailed(jobExecution);
            // TODO 알림 보내기 - 필요 시
            return;
        }
        loggingWhenSucceed(jobExecution);
    }

    private void loggingWhenSucceed(JobExecution jobExecution) {
        long duration = calculateDuration(jobExecution);

        log.info("============== JOB FINISHED ==============");
        log.info("Job Name:   {}", jobExecution.getJobInstance().getJobName());
        log.info("Status:     {}", jobExecution.getStatus());
        log.info("Total Time: {}ms", duration);
    }

    private void loggingWhenFailed(JobExecution jobExecution) {
        long duration = calculateDuration(jobExecution);

        List<Throwable> allFailureExceptions = jobExecution.getAllFailureExceptions();
        String errorCauses = getErrorCauseMessages(allFailureExceptions);

        log.error("============== JOB FAILED ==============");
        log.error("Job Name: {} | Param: {}",
            jobExecution.getJobInstance().getJobName(),
            jobExecution.getJobParameters()
        );
        log.error("Total Time: {}ms", duration);
        log.error("Error Causes {}", errorCauses);
        log.error("Main Exception Stack Trace:", allFailureExceptions.getFirst());
    }

    private long calculateDuration(JobExecution jobExecution) {
        if (jobExecution.getStartTime() == null) {
            return 0L;
        }
        return Duration.between(jobExecution.getStartTime(), LocalDateTime.now()).toMillis();
    }

    private String getCauseFrom(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause != rootCause.getCause()) {
            rootCause = rootCause.getCause();
        }

        String rootCauseMessage = rootCause.getMessage();
        return rootCauseMessage != null ? rootCauseMessage : "Did not found Error Message";
    }

    private String getErrorCauseMessages(List<Throwable> allFailureExceptions) {
        StringBuilder errorCauses = new StringBuilder("\n");
        for (Throwable throwable : allFailureExceptions) {
            errorCauses.append("Exception: ").append(throwable.getClass().getName())
                .append(" | ").append("Cause: ").append(getCauseFrom(throwable)).append("\n");
        }
        return errorCauses.toString();
    }
}
