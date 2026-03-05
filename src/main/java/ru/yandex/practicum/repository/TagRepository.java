package ru.yandex.practicum.repository;

public interface TagRepository {
    Integer getOrCreateTag(String tag);

    Integer findTagIdByName(String name);
    Integer createTag(String name);
}
