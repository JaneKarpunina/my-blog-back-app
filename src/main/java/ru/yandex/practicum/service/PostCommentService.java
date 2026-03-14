package ru.yandex.practicum.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.CommentRequest;
import ru.yandex.practicum.dto.CommentResponse;
import ru.yandex.practicum.exception.PostCommentNotFoundException;
import ru.yandex.practicum.model.PostComment;
import ru.yandex.practicum.repository.PostCommentRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;

    public PostCommentService(PostCommentRepository postCommentRepository) {
        this.postCommentRepository = postCommentRepository;
    }

    @Transactional
    public List<CommentResponse> findCommentsByPostId(int postId) {
        List<CommentResponse> commentResponses = new ArrayList<>();
        List<PostComment> postComments = postCommentRepository.findCommentsByPostId(postId);
        for (PostComment postComment : postComments) {
           CommentResponse commentResponse = new CommentResponse(postComment.getId(),
                    postComment.getText(),
                    postComment.getPostId());
           commentResponses.add(commentResponse);

        }

        return commentResponses;
    }

    @Transactional
    public CommentResponse findCommentByPostIdCommentId(int postId, int commentId) {
        PostComment postComment = postCommentRepository.findCommentsByPostIdCommentId(commentId, postId);
        return new CommentResponse(postComment.getId(), postComment.getText(), postComment.getPostId());
    }

    @Transactional
    public PostComment saveComment(CommentRequest comment) {
      Integer id = postCommentRepository.createComment(comment);
      return new PostComment(id, comment.getText(), comment.getPostId());
    }

    @Transactional
    public PostComment updateComment(CommentRequest comment) {
        int rows = postCommentRepository.updateComment(comment.getId(), comment.getText(),
                comment.getPostId());
        if (rows == 0) {
            throw new PostCommentNotFoundException("Комментарий  с идентификатором: " + comment.getId() +
                    " у поста " + comment.getPostId() + " не найден");
        }

        return new PostComment(comment.getId(), comment.getText(), comment.getPostId());
    }

    @Transactional
    public void deleteComment(Integer postId, Integer commentId) {
        if (!postCommentRepository.existsByIdAndPostId(commentId, postId)) {
            throw new PostCommentNotFoundException("Комментарий  с идентификатором: " + commentId +
                    " у поста " + postId + " не найден");
        }
        postCommentRepository.deleteComment(commentId, postId);
    }
}
