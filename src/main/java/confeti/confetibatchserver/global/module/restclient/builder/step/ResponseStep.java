package confeti.confetibatchserver.global.module.restclient.builder.step;

public interface ResponseStep {

    <T> T retrieve(Class<T> responseType);

    void retrieve();
}
