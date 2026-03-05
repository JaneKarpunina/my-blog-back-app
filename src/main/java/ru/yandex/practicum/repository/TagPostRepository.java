package ru.yandex.practicum.repository;

import java.util.List;

public interface TagPostRepository {
    void addTagsToPost(Integer postId, List<Integer> tagIds);

    void deleteTagsForPost(Integer postId);
}
