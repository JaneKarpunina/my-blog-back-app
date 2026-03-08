package ru.yandex.practicum.service;


import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.CommentResponse;
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

    public CommentResponse findCommentByPostIdCommentId(int postId, int commentId) {
        PostComment postComment = postCommentRepository.findCommentsByPostIdCommentId(commentId, postId);
        return new CommentResponse(postComment.getId(), postComment.getText(), postComment.getPostId());
    }
}
