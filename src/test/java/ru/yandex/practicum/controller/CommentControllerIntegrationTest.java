package ru.yandex.practicum.controller;


/*import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.configuration.TestWebConfiguration;
import ru.yandex.practicum.dto.CommentRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig(classes = {
        TestWebConfiguration.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class CommentControllerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        jdbcTemplate.execute("ALTER TABLE post ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE post_comment ALTER COLUMN id RESTART WITH 1");

        jdbcTemplate.execute("DELETE FROM post_comment");
        jdbcTemplate.execute("DELETE FROM post");

        jdbcTemplate.execute("INSERT INTO post (title, text, likes_count) VALUES ('title1', 'abc', 3)");
        jdbcTemplate.execute("INSERT INTO post (title, text, likes_count) VALUES ('title2', 'abc', 0)");

        jdbcTemplate.execute("INSERT INTO post_comment (text, post_id) VALUES ('abc1', 1)");
        jdbcTemplate.execute("INSERT INTO post_comment (text, post_id) VALUES ('abc2', 1)");
    }

    @Test
    public void testGetComments() throws Exception {
        int postId = 1;

        mockMvc.perform(get("/api/posts/" + postId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("abc1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].text").value("abc2"));
    }

    @Test
    public void testGetComment_success() throws Exception {
        int postId = 1;
        int commentId = 2;

        mockMvc.perform(get("/api/posts/" + postId + "/comments/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.text").value("abc2"));
    }

    @Test
    public void testGetComment_notFound() throws Exception {
        int postId = 1;
        int commentId = 999;

        mockMvc.perform(get("/api/posts/" + postId + "/comments/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddComment_success() throws Exception {
        int postId = 1;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setPostId(postId);
        commentRequest.setText("New comment");

        mockMvc.perform(post("/api/posts/" + postId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.text").value("New comment"))
                .andExpect(jsonPath("$.postId").value(postId));
    }

    @Test
    public void testAddComment_badRequest() throws Exception {
        int postId = 1;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setPostId(999);
        commentRequest.setText(null);

        mockMvc.perform(post("/api/posts/" + postId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isBadRequest());

        commentRequest.setPostId(postId);
        commentRequest.setText(null);

        mockMvc.perform(post("/api/posts/" + postId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateComment_success() throws Exception {
        int postId = 1;
        int commentId = 2;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setId(commentId);
        commentRequest.setPostId(postId);
        commentRequest.setText("Updated comment");

        mockMvc.perform(put("/api/posts/" + postId + "/comments/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.text").value("Updated comment"))
                .andExpect(jsonPath("$.postId").value(postId));

    }

    @Test
    public void testUpdateComment_badRequest() throws Exception {
        int postId = 1;
        int commentId = 10;

        CommentRequest badRequest1 = new CommentRequest();
        badRequest1.setPostId(postId);
        badRequest1.setText("Text");

        mockMvc.perform(put("/api/posts/" + postId + "/comments/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest1)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testUpdateComment_notFound() throws Exception {
        int postId = 1;
        int commentId = 10;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setId(commentId);
        commentRequest.setPostId(postId);
        commentRequest.setText("Some text");

        mockMvc.perform(put("/api/posts/" + postId + "/comments/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteComment_success() throws Exception {
        int postId = 1;
        int commentId = 2;

        mockMvc.perform(delete("/api/posts/" + postId + "/comments/" + commentId))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteComment_notFound() throws Exception {
        int postId = 1;
        int commentId = 123;

        mockMvc.perform(delete("/api/posts/" + postId + "/comments/" + commentId))
                .andExpect(status().isNotFound());
    }
} */