package confeti.confetibatchserver.global.module.restclient.builder.step;

import org.springframework.util.MultiValueMap;

public interface MethodStep<T> {

    PostRequestBuilder post();

    GetRequestBuilder get();

    interface PostRequestBuilder {

        PostRequestBuilder baseUrl(String baseUrl);

        PostRequestBuilder path(String path);

        PostRequestBuilder params(MultiValueMap<String, String> params);

        <T> BodySpec<T> body(T requestBody);

        ConnectStep build();
    }

    // body 설정 이후 사용
    interface BodySpec<T> {

        ConnectStep build();
    }

    interface GetRequestBuilder {

        GetRequestBuilder baseUrl(String baseUrl);

        GetRequestBuilder path(String path);

        GetRequestBuilder params(MultiValueMap<String, String> params);

        ConnectStep build();
    }
}
