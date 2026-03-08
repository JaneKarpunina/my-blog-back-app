package ru.yandex.practicum.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.CommentResponse;
import ru.yandex.practicum.service.PostCommentService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class CommentController {

    private final PostCommentService postCommentService;

    public CommentController(PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable int postId) {
        List<CommentResponse> comments = postCommentService.findCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }
}
