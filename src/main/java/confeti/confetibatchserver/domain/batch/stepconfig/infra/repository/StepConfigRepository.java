package confeti.confetibatchserver.domain.batch.stepconfig.infra.repository;

import confeti.confetibatchserver.domain.batch.stepconfig.StepConfig;
import confeti.confetibatchserver.job.StepInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepConfigRepository extends JpaRepository<StepConfig, StepInfo> {

}
