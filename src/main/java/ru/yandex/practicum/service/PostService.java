package ru.yandex.practicum.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.PostRequest;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.repository.TagPostRepository;
import ru.yandex.practicum.repository.TagRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    private final TagRepository tagRepository;

    private final TagPostRepository tagPostRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository,
                       TagPostRepository tagPostRepository){

        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.tagPostRepository = tagPostRepository;
    }

    @Transactional
    public Post createPost(PostRequest postRequest) {
        // Создаем пост
        Integer postId = postRepository.savePost(postRequest.getTitle(), postRequest.getText());

        // Обрабатываем теги
        List<Integer> tagIds = postRequest.getTags().stream()
                .map(tagRepository::getOrCreateTag)
                .collect(Collectors.toList());

        // Связываем пост и теги
        tagPostRepository.addTagsToPost(postId, tagIds);

        return new Post(postId, postRequest.getTitle(), postRequest.getText(), 0, 0,
                postRequest.getTags());
    }
}
