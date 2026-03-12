package ru.yandex.practicum.configuration;


import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.repository.PostCommentRepository;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.repository.TagPostRepository;
import ru.yandex.practicum.repository.TagRepository;
import ru.yandex.practicum.service.PostService;

@Configuration
public class PostTestConfiguration {

    @Bean
    public PostRepository mockPostRepository() {
        return Mockito.mock(PostRepository.class);
    }

    @Bean
    public TagRepository mockTagRepository() {
        return Mockito.mock(TagRepository.class);
    }

    @Bean
    public TagPostRepository mockTagPostRepository() {
        return Mockito.mock(TagPostRepository.class);
    }

    @Bean
    public PostService getPostService(PostRepository postRepository, TagRepository tagRepository,
                                      TagPostRepository tagPostRepository, PostCommentRepository postCommentRepository) {

        return new PostService(postRepository, tagRepository,
                tagPostRepository, postCommentRepository);
    }
}
