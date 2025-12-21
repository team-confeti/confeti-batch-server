package confeti.confetibatchserver.domain.batch.jobconfig;

import confeti.confetibatchserver.job.JobInfo;
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
public class JobConfig {

    @Id
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobInfo jobInfo;

    private boolean isActive;

    private String description;
}
