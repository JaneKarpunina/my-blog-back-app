package ru.yandex.practicum.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.yandex.practicum",
        excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ParentConfiguration.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = PostCommentTestConfiguration.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = PostTestConfiguration.class)
})
public class TestWebConfiguration {
}
