package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MyBlogBackAppSpringBootApplication {

	public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext =
                SpringApplication.run(MyBlogBackAppSpringBootApplication.class, args);
	}

}
