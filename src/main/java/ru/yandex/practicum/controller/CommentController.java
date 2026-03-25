package ru.yandex.practicum.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.CommentRequest;
import ru.yandex.practicum.dto.CommentResponse;
import ru.yandex.practicum.model.PostComment;
import ru.yandex.practicum.service.PostCommentService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class CommentController {

    private final PostCommentService postCommentService;

    public CommentController(PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Integer postId) {
        List<CommentResponse> comments = postCommentService.findCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable Integer postId,
                                                      @PathVariable Integer commentId) {
        CommentResponse comment = postCommentService.findCommentByPostIdCommentId(postId, commentId);
        return ResponseEntity.ok(comment);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Integer postId,
            @RequestBody CommentRequest comment
    ) {
        if (comment.getText() == null || comment.getPostId() == null || !postId.equals(comment.getPostId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        PostComment savedComment = postCommentService.saveComment(comment);
        return ResponseEntity.ok(new CommentResponse(savedComment.getId(), savedComment.getText(),
                savedComment.getPostId()));
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Integer postId,
                                                         @PathVariable Integer commentId,
                                                         @RequestBody CommentRequest comment) {

        if (comment.getId() == null || !commentId.equals(comment.getId()) || comment.getText() == null ||
                comment.getPostId() == null || !postId.equals(comment.getPostId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        PostComment postComment = postCommentService.updateComment(comment);
        return ResponseEntity.ok(new CommentResponse(postComment.getId(), postComment.getText(),
                postComment.getPostId()));


    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer postId, @PathVariable Integer commentId) {

        postCommentService.deleteComment(postId, commentId);
        return ResponseEntity.ok().build();

    }
}
