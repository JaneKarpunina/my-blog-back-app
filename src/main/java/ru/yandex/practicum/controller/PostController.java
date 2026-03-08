package ru.yandex.practicum.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.PostListResponse;
import ru.yandex.practicum.dto.PostRequest;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.exception.PostNotFoundException;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.PostService;

import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<PostListResponse> getPosts(
            @RequestParam String search,
            @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize
    ) {

        return ResponseEntity.ok(postService.getPosts(search, pageNumber, pageSize));

    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Integer id) {
        Optional<PostResponse> postOpt = postService.findPostById(id);
        return postOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest postRequest) {

        if (postRequest.getTitle() == null || postRequest.getText() == null || postRequest.getTags() == null) {
            return ResponseEntity.badRequest().build();
        }

        Post post = postService.createPost(postRequest);

        return ResponseEntity.ok(new PostResponse(post.getId(), post.getTitle(), post.getText(),
                post.getTags(), post.getLikesCount(), post.getCommentsCount()));

    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(@PathVariable(name = "id") Integer id,
                                               @RequestBody PostRequest postRequest) {

        if (!id.equals(postRequest.getId()) || postRequest.getTitle() == null || postRequest.getText() == null ||
                postRequest.getTags() == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            PostResponse updatedPost = postService.updatePost(postRequest);
            return ResponseEntity.ok(updatedPost);
        } catch (PostNotFoundException exception) {
            return ResponseEntity.badRequest().build();
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok().build();
        } catch (PostNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<Integer> incrementLikes(@PathVariable Integer id) {
        try {
            int newLikesCount = postService.incrementLikes(id);
            return ResponseEntity.ok(newLikesCount);
        } catch (PostNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
