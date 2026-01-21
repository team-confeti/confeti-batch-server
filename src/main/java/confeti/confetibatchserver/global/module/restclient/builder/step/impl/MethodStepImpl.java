package confeti.confetibatchserver.global.module.restclient.builder.step.impl;

import confeti.confetibatchserver.global.module.restclient.builder.step.ConnectStep;
import confeti.confetibatchserver.global.module.restclient.builder.step.MethodStep;
import java.net.URI;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

@Slf4j
@RequiredArgsConstructor
public class MethodStepImpl<T> implements MethodStep<T> {

    private final RestClient restClient;
    private RestClient.RequestHeadersSpec<?> methodType;

    /**
     * GET 요청 빌더
     */
    @Override
    public GetRequestBuilder get() {
        return new GetRequestBuilderImpl();
    }

    /**
     * POST 요청 빌더
     */
    @Override
    public PostRequestBuilder post() {
        return new PostRequestBuilderImpl();
    }

    private class GetRequestBuilderImpl implements GetRequestBuilder {

        private String baseUrl = "";
        private String path;
        private MultiValueMap<String, String> params;

        @Override
        public GetRequestBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        @Override
        public GetRequestBuilder path(String path) {
            this.path = path;
            return this;
        }

        @Override
        public GetRequestBuilder params(MultiValueMap<String, String> params) {
            this.params = params;
            return this;
        }

        @Override
        public ConnectStep build() {
            RestClient requestClient = restClient.mutate()
                .baseUrl(this.baseUrl)
                .build();

            Function<UriBuilder, URI> uriFunction = uriBuilder -> {
                // path
                if (StringUtils.hasText(path)) {
                    uriBuilder = uriBuilder.path(path);
                }

                // params
                uriBuilder = uriBuilder.queryParams(
                    params == null || params.isEmpty() ? new LinkedMultiValueMap<>() : params
                );

                return uriBuilder.build();
            };

            // GET 요청 생성
            methodType = requestClient
                .get()
                .uri(uriFunction);
            return new ConnectStepImpl(methodType);
        }
    }

    private class PostRequestBuilderImpl implements PostRequestBuilder {

        private String baseUrl = "";
        private String path;
        private MultiValueMap<String, String> params;
        private Object requestBody;
        private boolean hasBody = false;

        @Override
        public PostRequestBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        @Override
        public PostRequestBuilder path(String path) {
            this.path = path;
            return this;
        }

        @Override
        public PostRequestBuilder params(MultiValueMap<String, String> params) {
            this.params = params;
            return this;
        }

        @Override
        public <T> BodySpec<T> body(T requestBody) {
            return new BodySpecImpl<>(this, requestBody);
        }

        @Override
        public ConnectStep build() {
            RestClient requestClient = restClient.mutate()
                .baseUrl(this.baseUrl)
                .build();

            Function<UriBuilder, URI> uriFunction = uriBuilder -> {
                // path
                if (StringUtils.hasText(path)) {
                    uriBuilder = uriBuilder.path(path);
                }

                // params
                uriBuilder = uriBuilder.queryParams(
                    params == null || params.isEmpty() ? new LinkedMultiValueMap<>() : params
                );

                return uriBuilder.build();
            };

            RestClient.RequestBodySpec requestBodySpec = requestClient
                .post()
                .uri(uriFunction);

            RestClient.RequestHeadersSpec<?> requestHeadersSpec = requestBodySpec;

            if (hasBody) {
                requestHeadersSpec = requestBodySpec.body(requestBody);
            }

            methodType = requestHeadersSpec;
            return new ConnectStepImpl(methodType);
        }
    }

    private class BodySpecImpl<R> implements BodySpec<R> {

        private final PostRequestBuilderImpl builder;

        public BodySpecImpl(PostRequestBuilderImpl builder, R requestBody) {
            this.builder = builder;

            this.builder.requestBody = requestBody;
            this.builder.hasBody = true;
        }

        @Override
        public ConnectStep build() {
            return builder.build();
        }
    }
}
