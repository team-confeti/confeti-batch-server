package confeti.confetibatchserver.domain.batch.jobconfig.application;

import confeti.confetibatchserver.domain.batch.jobconfig.JobConfig;
import confeti.confetibatchserver.domain.batch.jobconfig.infra.repository.JobConfigRepository;
import confeti.confetibatchserver.job.JobInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobConfigService {

    private final JobConfigRepository jobConfigRepository;

    public JobConfig getByJobInfo(JobInfo jobInfo) {
        return jobConfigRepository.findById(jobInfo)
            .orElseThrow(() -> {
                log.error("Not Found BatchJobConfig. Job: {}", jobInfo);
                throw new IllegalArgumentException("Not Found BatchJobConfig. Job: " + jobInfo);
            });
    }

}
