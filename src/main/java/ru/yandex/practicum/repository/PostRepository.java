package ru.yandex.practicum.repository;

import ru.yandex.practicum.dto.PostResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PostRepository {
    Integer savePost(String title, String text);

    Integer updatePost(Integer postId, String title, String text);

    Map<String, Object> getPostById(Integer postId);

    List<PostResponse> findPosts(List<String> tags, String titleWords);

    Optional<PostResponse> findPostById(Integer id);
}
