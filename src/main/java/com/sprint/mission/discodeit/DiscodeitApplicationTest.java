package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplicationTest {

	public static void main(String[] args) {
		// Spring Context 생성
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplicationTest.class, args);
	}
}
