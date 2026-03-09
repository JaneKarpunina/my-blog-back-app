package ru.yandex.practicum.repository;

import ru.yandex.practicum.dto.CommentRequest;
import ru.yandex.practicum.model.PostComment;

import java.util.List;

public interface PostCommentRepository {
    Integer countComments(Integer postId);

    void deleteCommentsForPost(Integer postId);

    List<PostComment> findCommentsByPostId(int postId);

    PostComment findCommentsByPostIdCommentId(int commentId, int postId);

    Integer createComment(CommentRequest comment);

    int updateComment(Integer id, String text, Integer postId);

    boolean existsByIdAndPostId(Integer commentId, Integer postId);

    void deleteComment(Integer commentId, Integer postId);
}
