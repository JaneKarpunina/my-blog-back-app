package ru.yandex.practicum.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.PostRequest;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
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

//    @DeleteMapping(value = "/{id}")
//    public void delete(@PathVariable(name = "id") Long id) {
//        service.deleteById(id);
//    }

//    @PutMapping("/{id}")
//    public void update(@PathVariable(name = "id") Integer id, @RequestBody PostRequest postRequest) {
//        postService.updatePost(id, user);
//    }
}
