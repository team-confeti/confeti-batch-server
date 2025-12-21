package confeti.confetibatchserver.domain.batch.jobconfig.infra.repository;

import confeti.confetibatchserver.domain.batch.jobconfig.JobConfig;
import confeti.confetibatchserver.job.JobInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobConfigRepository extends JpaRepository<JobConfig, JobInfo> {

}
