package confeti.confetibatchserver.global.module.restclient.builder;

import confeti.confetibatchserver.global.module.restclient.builder.step.MethodStep;
import confeti.confetibatchserver.global.module.restclient.builder.step.impl.MethodStepImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ApiRestClientBuilder {

    @Qualifier("customRestClient")
    private final RestClient restClient;

    // 체이닝 시작점
    public <T> MethodStep<T> request() {
        return new MethodStepImpl<>(this.restClient);
    }
}
