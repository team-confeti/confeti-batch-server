package confeti.confetibatchserver.global.module.restclient.builder.step;

import java.util.Map;

public interface ConnectStep {

    ResponseStep connect();

    ResponseStep connect(Map<String, String> headers);
}
