package ru.yandex.practicum.repository;

public interface PostRepository {
    Integer savePost(String title, String text);
}
