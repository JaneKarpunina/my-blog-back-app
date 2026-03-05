package ru.yandex.practicum.repository;

public interface TagRepository {
    Integer getOrCreateTag(String tag);
}
