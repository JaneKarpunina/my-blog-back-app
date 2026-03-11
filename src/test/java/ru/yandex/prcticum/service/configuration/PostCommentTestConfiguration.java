package ru.yandex.prcticum.service.configuration;


import org.springframework.context.annotation.Bean;
import ru.yandex.practicum.repository.PostCommentRepository;
import ru.yandex.practicum.service.PostCommentService;

public class PostCommentTestConfiguration {

    @Bean
    public PostCommentService getPostCommentService(PostCommentRepository postCommentRepository) {
        return new PostCommentService(postCommentRepository);
    }
}
