package confeti.confetibatchserver.global.config;

import confeti.confetibatchserver.external.client.AppleMusicFeignClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {AppleMusicFeignClient.class})
public class FeignClientConfig {

}
