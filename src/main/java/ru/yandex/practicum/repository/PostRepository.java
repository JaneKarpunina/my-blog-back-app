package ru.yandex.practicum.repository;

import java.util.Map;

public interface PostRepository {
    Integer savePost(String title, String text);

    Integer updatePost(Integer postId, String title, String text);

    Map<String, Object> getPostById(Integer postId);
}
