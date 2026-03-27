package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.dto.CommentRequest;
import ru.yandex.practicum.dto.CommentResponse;
import ru.yandex.practicum.exception.PostCommentNotFoundException;
import ru.yandex.practicum.model.PostComment;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = PostCommentService.class)
public class PostCommentServiceTest extends BaseTest {

    @Autowired
    PostCommentService postCommentService;

    @BeforeEach
    void resetMocks() {
        reset(postCommentRepository);
    }


    @Test
    void testFindCommentsByPostId() {
        // Данные, которые вернет мок
        PostComment pc1 = new PostComment(1, "Comment 1", 100);
        PostComment pc2 = new PostComment(2, "Comment 2", 100);

        when(postCommentRepository.findCommentsByPostId(100))
                .thenReturn(Arrays.asList(pc1, pc2));

        List<CommentResponse> responses = postCommentService.findCommentsByPostId(100);

        assertNotNull(responses);
        assertEquals(2, responses.size());

        // Убедимся, что репозиторий вызван именно так
        verify(postCommentRepository).findCommentsByPostId(100);
    }

    @Test
    void testFindCommentByPostIdCommentId() {
        int postId = 10;
        int commentId = 20;

        // Создаем фиктивный PostComment
        PostComment mockComment = new PostComment(commentId, "Sample text", postId);

        // Настраиваем мок репозитория
        when(postCommentRepository.findCommentsByPostIdCommentId(commentId, postId))
                .thenReturn(mockComment);

        // Вызов метода
        CommentResponse response = postCommentService.findCommentByPostIdCommentId(postId, commentId);

        assertNotNull(response);
        assertEquals(commentId, response.getId());
        assertEquals("Sample text", response.getText());
        assertEquals(postId, response.getPostId());

        // Проверяем, что метод вызван с правильными параметрами
        verify(postCommentRepository).findCommentsByPostIdCommentId(commentId, postId);

    }

    @Test
    void testSaveComment() {
        // Входные данные
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setPostId(5);
        commentRequest.setText("abc");
        Integer generatedId = 123;

        when(postCommentRepository.createComment(commentRequest)).thenReturn(generatedId);

        // Вызов метода
        PostComment savedComment = postCommentService.saveComment(commentRequest);

        assertNotNull(savedComment);
        assertEquals(generatedId, savedComment.getId());
        assertEquals(commentRequest.getText(), savedComment.getText());
        assertEquals(commentRequest.getPostId(), savedComment.getPostId());

        // Проверяем, что репозиторий вызван именно так
        verify(postCommentRepository).createComment(commentRequest);
    }


    @Test
    void testUpdateComment_success() {
        // Входные данные
        CommentRequest comment = new CommentRequest(10, "Обновленный комментарий", 5);

        when(postCommentRepository.updateComment(comment.getId(), comment.getText(), comment.getPostId()))
                .thenReturn(1);

        // Вызов метода
        PostComment result = postCommentService.updateComment(comment);

        // Проверки
        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getPostId(), result.getPostId());

        // Проверка вызова
        verify(postCommentRepository).updateComment(comment.getId(), comment.getText(), comment.getPostId());

    }

    @Test
    void testUpdateComment_notFound() {
        // Входные данные
        CommentRequest comment = new CommentRequest(20, "Комментарий не найден", 7);

        when(postCommentRepository.updateComment(comment.getId(), comment.getText(), comment.getPostId()))
                .thenReturn(0);

        PostCommentNotFoundException thrown = assertThrows(PostCommentNotFoundException.class, () -> {
            postCommentService.updateComment(comment);
        });

        assertTrue(thrown.getMessage().contains(String.valueOf(comment.getId())));

        // Проверка вызова
        verify(postCommentRepository).updateComment(comment.getId(), comment.getText(), comment.getPostId());

    }

    @Test
    void testDeleteComment_success() {
        Integer postId = 10;
        Integer commentId = 20;

        when(postCommentRepository.existsByIdAndPostId(commentId, postId)).thenReturn(true);

        postCommentService.deleteComment(postId, commentId);

        verify(postCommentRepository).deleteComment(commentId, postId);
    }

    @Test
    void testDeleteCommentNotFound() {
        Integer postId = 15;
        Integer commentId = 25;

        when(postCommentRepository.existsByIdAndPostId(commentId, postId)).thenReturn(false);

        PostCommentNotFoundException exception = assertThrows(PostCommentNotFoundException.class, () -> {
            postCommentService.deleteComment(postId, commentId);
        });

        assertTrue(exception.getMessage().contains(String.valueOf(commentId)));
        verify(postCommentRepository, never()).deleteComment(anyInt(), anyInt());

    }


}
