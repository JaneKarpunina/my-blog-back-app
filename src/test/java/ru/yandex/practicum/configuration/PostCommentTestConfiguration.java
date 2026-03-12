package ru.yandex.practicum.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.repository.PostCommentRepository;
import ru.yandex.practicum.service.PostCommentService;

@Configuration
public class PostCommentTestConfiguration {

    @Bean
    public PostCommentService getPostCommentService(PostCommentRepository postCommentRepository) {
        return new PostCommentService(postCommentRepository);
    }
}
