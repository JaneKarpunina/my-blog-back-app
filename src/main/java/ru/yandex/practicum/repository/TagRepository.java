package ru.yandex.practicum.repository;

import java.util.List;

public interface TagRepository {
    Integer getOrCreateTag(String tag);

    Integer findTagIdByName(String name);
    Integer createTag(String name);

    List<String> getTagsForPost(Integer postId);
}
