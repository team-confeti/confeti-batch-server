package confeti.confetibatchserver.global.module.restclient.builder.step.impl;

import confeti.confetibatchserver.global.exectpion.ConfetiException;
import confeti.confetibatchserver.global.message.ErrorMessage;
import confeti.confetibatchserver.global.module.restclient.builder.step.ResponseStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
public class ResponseStepImpl implements ResponseStep {

    private final RestClient.ResponseSpec methodType;

    /**
     * 전달받은 클래스 타입으로 변환 후 반환
     *
     * @param responseType
     * @param <T>
     * @return
     */
    @Override
    public <T> T retrieve(Class<T> responseType) {
        try {
            return this.methodType
                .onStatus(HttpStatusCode::isError, (clientRequest, clientResponse) -> {
                    log.error("RestClient HTTP Error with code : {}, url : {}",
                        clientResponse.getStatusCode(),
                        clientRequest.getURI());
                })
                .body(responseType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConfetiException(ErrorMessage.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void retrieve() {
        try {
            this.methodType
                .onStatus(HttpStatusCode::isError, (clientRequest, clientResponse) -> {
                    log.error("RestClient HTTP Error with code : {}, url : {}",
                        clientResponse.getStatusCode(),
                        clientRequest.getURI());
                })
                .body(Void.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConfetiException(ErrorMessage.INTERNAL_SERVER_ERROR);
        }
    }
}
