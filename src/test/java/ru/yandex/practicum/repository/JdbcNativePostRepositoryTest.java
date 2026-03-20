package ru.yandex.practicum.repository;


/*import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.practicum.configuration.DataSourceConfiguration;
import ru.yandex.practicum.dto.PostResponse;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcNativePostRepository.class,
                   JdbcNativeTagRepository.class, JdbcNativePostCommentRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class JdbcNativePostRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;


    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("ALTER TABLE post ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE tag ALTER COLUMN id RESTART WITH 1");

        jdbcTemplate.execute("DELETE FROM post_tags");
        jdbcTemplate.execute("DELETE FROM tag");
        jdbcTemplate.execute("DELETE FROM post");

        jdbcTemplate.execute("INSERT INTO post (title, text, likes_count) VALUES ('title1', 'abc', 3)");
        jdbcTemplate.execute("INSERT INTO post (title, text, likes_count) VALUES ('title2', 'abc', 1)");
        jdbcTemplate.execute("INSERT INTO post (title, text, likes_count) VALUES ('title3', 'abc', 0)");

        jdbcTemplate.execute("INSERT INTO tag (name) VALUES ('tag1')");
        jdbcTemplate.execute("INSERT INTO tag (name) VALUES ('tag2')");
        jdbcTemplate.execute("INSERT INTO tag (name) VALUES ('tag3')");

        jdbcTemplate.execute("INSERT INTO post_tags (post_id, tag_id) VALUES (1, 1)");
        jdbcTemplate.execute("INSERT INTO post_tags (post_id, tag_id) VALUES (1, 2)");
        jdbcTemplate.execute("INSERT INTO post_tags (post_id, tag_id) VALUES (2, 1)");
        jdbcTemplate.execute("INSERT INTO post_tags (post_id, tag_id) VALUES (2, 3)");
        jdbcTemplate.execute("INSERT INTO post_tags (post_id, tag_id) VALUES (3, 1)");

    }

    @Test
    public void testSavePost() {

        String title = "title4";
        String text = "New post";

        Integer postId = postRepository.savePost(title, text);

        assertNotNull(postId);

        PostResponse postResponse = postRepository.findPostById(postId).orElse(null);

        assertNotNull(postResponse);
        assertEquals(text, postResponse.getText());
        assertEquals(title, postResponse.getTitle());
    }

    @Test
    public void testUpdatePost() {

        String titleNew = "title_new";
        String newText = "new text";
        int rows = postRepository.updatePost(1, titleNew, newText);

        assertEquals(1, rows);

        PostResponse postResponse = postRepository.findPostById(1).orElse(null);

        assertNotNull(postResponse);
        assertEquals(newText, postResponse.getText());
        assertEquals(titleNew, postResponse.getTitle());

    }

    @Test
    public void testGetPostById() {

        Map<String, Object> postData = postRepository.getPostById(1);

        assertNotNull(postData);
        assertEquals(1, postData.get("id"));
        assertEquals("title1", postData.get("title"));
        assertEquals("abc", postData.get("text"));
        assertEquals(3, postData.get("likesCount"));
    }

    @Test
    public void testFindPosts() {

        List<PostResponse> responseList = postRepository.findPosts(List.of("tag1"),"title");

        assertEquals(3, responseList.size());

    }

    @Test
    public void testExistsById() {
        boolean existing = postRepository.existsById(3);

        assertTrue(existing);
    }


    @Test
    public void testDeletePost() {
        postRepository.deletePost(1);

        PostResponse postResponse =  postRepository.findPostById(1).orElse(null);

        assertNull(postResponse);
    }

    @Test
    public void testIncrementLikes() {
        Integer likes = postRepository.incrementLikes(1);

        assertEquals(4, likes);
    }

    @Test
    public void testUpdatePostImage() {
        int id = 1;

        String filePath = "path/to/image.jpg";
        postRepository.updatePostImage(id, filePath);

        String pathToImage = postRepository.findImagePathById(id);

        assertEquals(filePath, pathToImage);
    }





}*/
