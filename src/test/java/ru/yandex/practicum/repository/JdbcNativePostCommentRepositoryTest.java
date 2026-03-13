package ru.yandex.practicum.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.practicum.configuration.DataSourceConfiguration;
import ru.yandex.practicum.dto.CommentRequest;
import ru.yandex.practicum.model.PostComment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcNativePostCommentRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class JdbcNativePostCommentRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("ALTER TABLE post ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE post_comment ALTER COLUMN id RESTART WITH 1");

        jdbcTemplate.execute("DELETE FROM post_comment");
        jdbcTemplate.execute("DELETE FROM post");

        jdbcTemplate.execute("INSERT INTO post (title, text, likes_count) VALUES ('title1', 'abc', 3)");
        jdbcTemplate.execute("INSERT INTO post (title, text, likes_count) VALUES ('title2', 'abc', 1)");

        jdbcTemplate.execute("INSERT INTO post_comment (text, post_id) VALUES ('abc1', 1)");
        jdbcTemplate.execute("INSERT INTO post_comment (text, post_id) VALUES ('abc2', 1)");
    }

    @Test
    public void testCountComments() {
        Integer count = postCommentRepository.countComments(1);

        assertEquals(2, count);
    }

    @Test
    public void testDeleteCommentsForPosts() {
        postCommentRepository.deleteCommentsForPost(1);

        Integer count = postCommentRepository.countComments(1);

        assertEquals(0, count);
    }

    @Test
    public void testFindCommentsByPostId() {

        int postId = 1;
        List<PostComment> postComments = postCommentRepository.findCommentsByPostId(postId);

        assertEquals(2, postComments.size());
    }

    @Test
    public void testFindCommentByPostIdCommentId() {
        PostComment postComment = postCommentRepository.findCommentsByPostIdCommentId(2, 1);

        assertEquals("abc2", postComment.getText());
    }

    @Test
    public void testCreateComment() {

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setPostId(1);
        commentRequest.setText("new comment");

        Integer id = postCommentRepository.createComment(commentRequest);

        assertEquals(3, id);
    }

    @Test
    public void testUpdateComment() {

        String newText = "new text";
        postCommentRepository.updateComment(2,
                newText, 1);

        PostComment postComment = postCommentRepository.findCommentsByPostIdCommentId(2, 1);

        assertEquals(newText, postComment.getText());

    }

    @Test
    public void testExistsByIdPostId() {
        boolean exists = postCommentRepository.existsByIdAndPostId(2, 1);

        assertTrue(exists);
    }

    @Test
    public void testDeleteComment() {
        postCommentRepository.deleteComment(2, 1);

        assertThrows(EmptyResultDataAccessException.class, () ->postCommentRepository.findCommentsByPostIdCommentId(2, 1));

    }
}
