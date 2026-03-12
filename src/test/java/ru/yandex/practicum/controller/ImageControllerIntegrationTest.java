package ru.yandex.practicum.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.configuration.TestWebConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig(classes = {
        TestWebConfiguration.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class ImageControllerIntegrationTest {

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

        jdbcTemplate.execute("DELETE FROM post_tags");
        jdbcTemplate.execute("DELETE FROM tag");
        jdbcTemplate.execute("DELETE FROM post");

        jdbcTemplate.execute("INSERT INTO post (title, text, likes_count) VALUES ('title1', 'abc', 3)");

        jdbcTemplate.execute("INSERT INTO tag (name) VALUES ('tag1')");
        jdbcTemplate.execute("INSERT INTO tag (name) VALUES ('tag2')");
        jdbcTemplate.execute("INSERT INTO tag (name) VALUES ('tag3')");

        jdbcTemplate.execute("INSERT INTO post_tags (post_id, tag_id) VALUES (1, 1)");
        jdbcTemplate.execute("INSERT INTO post_tags (post_id, tag_id) VALUES (1, 2)");
    }

    @Test
    void uploadAndDownloadImage_success() throws Exception {
        int id = 1;
        byte[] stub = "FakeImageData".getBytes();
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                stub
        );

        mockMvc.perform(
                        multipart("/api/posts/" + id + "/image").file(image)
                        .with(request -> { request.setMethod("PUT"); return request; })
                )
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/" + id + "/image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(stub));
    }

    @Test
    public void getPostImage_NotFound() throws Exception {
        int id = 999;

        mockMvc.perform(get("/api/posts/" + id + "/image"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updatePostImage_EmptyFile() throws Exception {
        int id = 1;
        MockMultipartFile emptyFile = new MockMultipartFile(
                "image", "empty.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]
        );

        mockMvc.perform(
                        multipart("/api/posts/" + id + "/image")
                                .file(emptyFile)
                                .with(request -> { request.setMethod("PUT"); return request; })
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updatePostImage_PostNotFound() throws Exception {
        int id = 999;
        MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "FakeImageData".getBytes()
        );

        mockMvc.perform(
                        multipart("/api/posts/" + id + "/image")
                                .file(image)
                                .with(request -> { request.setMethod("PUT"); return request; })
                )
                .andExpect(status().isNotFound());
    }
}
