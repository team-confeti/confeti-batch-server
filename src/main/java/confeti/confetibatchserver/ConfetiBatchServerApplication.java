package confeti.confetibatchserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ConfetiBatchServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfetiBatchServerApplication.class, args);
    }

}
