package ru.yandex.practicum.repository;

public interface PostCommentRepository {
    Integer countComments(Integer postId);

    void deleteCommentsForPost(Integer postId);
}
