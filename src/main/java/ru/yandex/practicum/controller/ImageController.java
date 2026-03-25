package ru.yandex.practicum.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class ImageController {

    private final PostService postService;

    public ImageController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getPostImage(@PathVariable Integer id) {

        Resource file = postService.getPostImage(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);

    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Void> updatePostImage(@PathVariable Integer id, @RequestParam("image") MultipartFile image) {

        if (image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        postService.updatePostImage(id, image);
        return ResponseEntity.ok().build();

    }


}
