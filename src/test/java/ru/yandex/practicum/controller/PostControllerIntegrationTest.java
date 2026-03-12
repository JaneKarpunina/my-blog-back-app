package ru.yandex.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.yandex.practicum.dto.PostRequest;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(classes = {
       TestWebConfiguration.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
class PostControllerIntegrationTest {

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
        jdbcTemplate.execute("ALTER TABLE tag ALTER COLUMN id RESTART WITH 1");

        // Чистим и наполняем БД перед каждым тестом
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
    void getPosts_returnsJsonArray() throws Exception {
        mockMvc.perform(get("/api/posts")
                        .param("search", "#tag1 title #tag2")
                        .param("pageNumber", "1")
                        .param("pageSize", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.posts[0].id").value(1))
                .andExpect(jsonPath("$.posts[0].title").value("title1"))
                .andExpect(jsonPath("$.posts[0].text").value("abc"))
                .andExpect(jsonPath("$.posts[0].tags").isArray())
                .andExpect(jsonPath("$.posts[0].tags[0]").value("tag1"))
                .andExpect(jsonPath("$.posts[0].tags[1]").value("tag2"))
                .andExpect(jsonPath("$.posts[0].likesCount").value(3))
                .andExpect(jsonPath("$.posts[0].commentsCount").value(0))
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.lastPage").value(1));
    }

    @Test
    public void testGetPost_Found() throws Exception {

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("title1"));
    }

    @Test
    public void testGetPost_NotFound() throws Exception {

        mockMvc.perform(get("/api/posts/999")) // замените на актуальный путь
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreatePost_Success() throws Exception {

        String json = """
                  {"title":"titel4","text":"abc","tags":["tag1", "tag2"]}
                """;

        mockMvc.perform(post("/api/posts") // замените на актуальный путь
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.title").value("titel4"))
                .andExpect(jsonPath("$.text").value("abc"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags[0]").value("tag1"))
                .andExpect(jsonPath("$.tags[1]").value("tag2"));
    }

    @Test
    public void testCreatePost_BadRequest() throws Exception {
        PostRequest invalidRequest = new PostRequest();
        invalidRequest.setTitle(null);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate_Success() throws Exception {
        int id = 1;
        PostRequest postRequest = new PostRequest();
        postRequest.setId(id);
        postRequest.setTitle("Updated Title");
        postRequest.setText("Updated Content");
        postRequest.setTags(List.of("tag1", "tag2"));

        mockMvc.perform(put("/api/posts/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    public void testUpdate_BadRequest_IdMismatch() throws Exception {
        int id = 1;
        PostRequest postRequest = new PostRequest();
        postRequest.setId(2); // не совпадает с путём
        postRequest.setTitle("Title");
        postRequest.setText("Content");
        postRequest.setTags(List.of("tag"));

        mockMvc.perform(put("/api/posts/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate_NotFound() throws Exception {
        int id = 5;
        PostRequest postRequest = new PostRequest();
        postRequest.setId(id);
        postRequest.setTitle("Title");
        postRequest.setText("Content");
        postRequest.setTags(List.of("tag"));

        mockMvc.perform(put("/api/posts/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deletePost_Success() throws Exception {
        int id = 1;
        mockMvc.perform(delete("/api/posts/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void deletePost_NotFound() throws Exception {
        int id = 999;

        mockMvc.perform(delete("/api/posts/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void incrementLikes_Success() throws Exception {
        int id = 1;
        int newLikes = 4;

        mockMvc.perform(post("/api/posts/" + id + "/likes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(newLikes));
    }

    @Test
    public void incrementLikes_PostNotFound() throws Exception {
        int id = 999;

        mockMvc.perform(post("/api/posts/" + id + "/likes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

