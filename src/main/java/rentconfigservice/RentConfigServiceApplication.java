package rentconfigservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


// TODO Unit Tests
// TODO Swagger Open API
// TODO Dockerfile, Docker Compose
// TODO replace Russian messages with English
// TODO remove all comments
// TODO remove all empty lines, align spaces

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
public class RentConfigServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentConfigServiceApplication.class, args);
	}
}
