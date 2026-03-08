package ru.yandex.practicum.controller;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class CommentController {

    private final CommentRepository commentRepository;

    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable int postId) {
        List<Comment> comments = commentRepository.findCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }
}
