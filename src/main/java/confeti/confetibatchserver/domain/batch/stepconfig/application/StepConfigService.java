package confeti.confetibatchserver.domain.batch.stepconfig.application;

import confeti.confetibatchserver.domain.batch.stepconfig.StepConfig;
import confeti.confetibatchserver.domain.batch.stepconfig.infra.repository.StepConfigRepository;
import confeti.confetibatchserver.job.StepInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StepConfigService {

    private final StepConfigRepository stepConfigRepository;

    public StepConfig getByStepInfo(StepInfo stepInfo) {
        return stepConfigRepository.findById(stepInfo)
            .orElseThrow(() -> {
                log.error("Not Found BatchStepConfig. Step: {}", stepInfo);
                throw new IllegalArgumentException("Not Found BatchStepConfig. Step: " + stepInfo);
            });
    }
}
