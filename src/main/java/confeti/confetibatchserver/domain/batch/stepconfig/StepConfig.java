package confeti.confetibatchserver.domain.batch.stepconfig;

import confeti.confetibatchserver.job.StepInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class StepConfig {

    @Id
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StepInfo stepInfo;

    @Column(nullable = false)
    private int chunkSize;

    private Integer fetchSize;

    private Integer pageSize;

    private String description;
}
