package ru.yandex.prcticum.service.configuration;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.repository.PostCommentRepository;

@Configuration
public class ParentConfiguration {

    @Bean
    public PostCommentRepository mockPostCommentRepository() {
        return Mockito.mock(PostCommentRepository.class);
    }
}
