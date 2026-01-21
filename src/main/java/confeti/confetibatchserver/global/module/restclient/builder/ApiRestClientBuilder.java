package confeti.confetibatchserver.global.module.restclient.builder;

import confeti.confetibatchserver.global.module.restclient.builder.step.MethodStep;
import confeti.confetibatchserver.global.module.restclient.builder.step.impl.MethodStepImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ApiRestClientBuilder {

    private final RestClient.Builder restClientBuilder;


    // 체이닝 시작점
    public <T> MethodStep<T> request() {
        return new MethodStepImpl<>(this.restClientBuilder);
    }
}
