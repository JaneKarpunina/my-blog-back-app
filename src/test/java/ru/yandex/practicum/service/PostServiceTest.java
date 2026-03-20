package ru.yandex.practicum.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.dto.PostRequest;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.exception.PostNotFoundException;
import ru.yandex.practicum.repository.PostCommentRepository;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.repository.TagPostRepository;
import ru.yandex.practicum.repository.TagRepository;
import ru.yandex.practicum.configuration.ParentConfiguration;
import ru.yandex.practicum.configuration.PostTestConfiguration;
import ru.yandex.practicum.utils.TestUtils;
import ru.yandex.practicum.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*@ExtendWith(SpringExtension.class)
@ContextHierarchy({
        @ContextConfiguration(name = "parent", classes = ParentConfiguration.class),
        @ContextConfiguration(name = "child", classes = PostTestConfiguration.class)
})
public class PostServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagPostRepository tagPostRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private PostService postService;

    @BeforeEach
    void resetMocks() {
        reset(postRepository);
        reset(tagRepository);
        reset(tagPostRepository);
        reset(postCommentRepository);
    }

    @AfterAll
    public static void deleteDirectory() throws IOException {
        Utils.deleteDirectory("uploads/");
    }

    @Test
    void testCreatePost() {
        PostRequest postRequest = new PostRequest();

        postRequest.setTitle("title");
        postRequest.setText("abc");
        postRequest.setTags(Arrays.asList("tag1", "tag2"));


        when(postRepository.savePost(postRequest.getTitle(), postRequest.getText())).thenReturn(1);
        when(tagRepository.getOrCreateTag("tag1")).thenReturn(1);
        when(tagRepository.getOrCreateTag("tag2")).thenReturn(2);
        doNothing().when(tagPostRepository).addTagsToPost(any(), any());

        postService.createPost(postRequest);

        verify(postRepository, times(1)).savePost(any(), any());
        verify(tagRepository, times(2)).getOrCreateTag(any());
        verify(tagPostRepository, times(1)).addTagsToPost(any(), any());
    }

    @Test
    void testUpdatePost_success() {

        PostRequest postRequest = new PostRequest();
        postRequest.setId(1);
        postRequest.setTitle("title");
        postRequest.setText("abc");
        postRequest.setTags(Arrays.asList("tag1", "tag2"));

        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("likesCount", 0);

        when(postRepository.updatePost(postRequest.getId(), postRequest.getTitle(),
                postRequest.getText())).thenReturn(1);
        doNothing().when(tagPostRepository).deleteTagsForPost(any());
        when(tagRepository.getOrCreateTag("tag1")).thenReturn(1);
        when(tagRepository.getOrCreateTag("tag2")).thenReturn(2);
        doNothing().when(tagPostRepository).addTagsToPost(any(), any());
        when(postRepository.getPostById(1)).thenReturn(updatedFields);
        when(postCommentRepository.countComments(1)).thenReturn(1);

        postService.updatePost(postRequest);


        verify(postRepository, times(1)).updatePost(any(), any(), any());
        verify(tagPostRepository, times(1)).deleteTagsForPost(any());
        verify(tagRepository, times(2)).getOrCreateTag(any());
        verify(tagPostRepository, times(1)).addTagsToPost(any(), any());

        verify(postRepository, times(1)).getPostById(any());
        verify(postCommentRepository, times(1)).countComments(any());


    }

    @Test
    void testUpdatePost_failure() {

        PostRequest postRequest = new PostRequest();
        postRequest.setId(1);
        postRequest.setTitle("title");
        postRequest.setText("abc");

        when(postRepository.updatePost(postRequest.getId(), postRequest.getTitle(),
                postRequest.getText())).thenReturn(0);

        assertThrows(PostNotFoundException.class, () -> postService.updatePost(postRequest));

        verify(tagPostRepository, never()).deleteTagsForPost(any());
        verify(tagRepository, never()).getOrCreateTag(any());
        verify(tagPostRepository, never()).addTagsToPost(any(), any());

        verify(postRepository, never()).getPostById(any());
        verify(postCommentRepository, never()).countComments(any());


    }

    @Test
    void testFindPosts() {

        when(postRepository.findPosts(any(), any())).thenReturn(new ArrayList<>());

        postService.getPosts("", 1, 1);

        verify(postRepository, times(1)).findPosts(any(), any());
    }

    @Test
    void testFindPostById() {
        Integer id = 1;
        PostResponse post = new PostResponse();
        when(postRepository.findPostById(id)).thenReturn(Optional.of(post));

        Optional<PostResponse> result = postService.findPostById(id);

        assertTrue(result.isPresent());
        assertEquals(post, result.get());
        verify(postRepository, times(1)).findPostById(id);
    }

    @Test
    void testDeletePost_success() {
        Integer id = 1;
        when(postRepository.existsById(id)).thenReturn(true);

        postService.deletePost(id);

        verify(postRepository, times(1)).existsById(id);
        verify(tagPostRepository, times(1)).deleteTagsForPost(id);
        verify(postCommentRepository, times(1)).deleteCommentsForPost(id);
        verify(postRepository, times(1)).deletePost(id);
    }

    @Test
    void testDeletePost_failure() {
        Integer id = 1;
        when(postRepository.existsById(id)).thenReturn(false);

        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> {
            postService.deletePost(id);
        });

        assertEquals("Пост с идентификатором: " + id + " не найден", exception.getMessage());

        verify(postRepository).existsById(id);
        verifyNoMoreInteractions(tagPostRepository, postCommentRepository, postRepository);
    }

    @Test
    void testIncrementLikes_success() {
        Integer id = 1;
        when(postRepository.existsById(id)).thenReturn(true);
        when(postRepository.incrementLikes(id)).thenReturn(5);

        Integer result = postService.incrementLikes(id);

        assertEquals(5, result);
        verify(postRepository).existsById(id);
        verify(postRepository).incrementLikes(id);
    }

    @Test
    void testIncrementLikes_postNotFound() {
        Integer id = 2;
        when(postRepository.existsById(id)).thenReturn(false);

        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> {
            postService.incrementLikes(id);
        });

        assertEquals("Пост c идентификатором: " + id + " не найден", exception.getMessage());
        verify(postRepository).existsById(id);
        verify(postRepository, never()).incrementLikes(id);
    }

    @Test
    void testUpdatePostImage_success() {

        Integer id = 1;
        MockMultipartFile image = new MockMultipartFile("image", new byte[]{1, 2, 3});

        when(postRepository.existsById(id)).thenReturn(true);

        postService.updatePostImage(id, image);

        verify(postRepository).existsById(id);
        verify(postRepository).updatePostImage(eq(id), anyString());

    }

    @Test
    void testUpdatePostImage_postNotFound() {
        Integer id = 2;
        MockMultipartFile image = new MockMultipartFile("image", new byte[]{1, 2, 3});
        when(postRepository.existsById(id)).thenReturn(false);

        PostNotFoundException ex = assertThrows(PostNotFoundException.class, () -> {
            postService.updatePostImage(id, image);
        });
        assertEquals("Пост c идентификатором: " + id + "не найден", ex.getMessage());

        verify(postRepository).existsById(id);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    void testGetPostImage_imageExists() throws IOException {
        Integer id = 1;
        String imagePath = "test/image.png";

        byte[] imageBytes = {10, 20, 30};
        when(postRepository.findImagePathById(id)).thenReturn(imagePath);
        when(postRepository.existsById(id)).thenReturn(true);

        Path path = Paths.get(imagePath);

        Path parentDir = path.getParent();

        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        java.nio.file.Files.write(path, imageBytes);
        try {
            Resource resource = postService.getPostImage(id);
            assertInstanceOf(ByteArrayResource.class, resource);
            byte[] resultBytes = ((ByteArrayResource) resource).getByteArray();
            assertArrayEquals(imageBytes, resultBytes);
        } finally {
            Files.deleteIfExists(path);
            if (parentDir != null) {
                Files.deleteIfExists(parentDir);
            }
        }

        verify(postRepository).findImagePathById(id);
    }

    @Test
    void testGetPostImage_imageNotFound() {
        Integer id = 2;
        when(postRepository.findImagePathById(id)).thenReturn(null);
        when(postRepository.existsById(id)).thenReturn(true);

        Resource resource = postService.getPostImage(id);

        assertInstanceOf(ByteArrayResource.class, resource);
        assertEquals(0, ((ByteArrayResource) resource).getByteArray().length);

        verify(postRepository).findImagePathById(id);
    }
}*/
