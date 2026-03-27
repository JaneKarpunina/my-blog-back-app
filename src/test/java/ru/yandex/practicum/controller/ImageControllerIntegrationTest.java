package ru.yandex.practicum.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.utils.Utils;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
public class ImageControllerIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {

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

    @AfterAll
    public static void deleteDirectory() throws IOException {
        Utils.deleteDirectory("uploads/");
    }


    @Test
    void uploadAndDownloadImage_success() throws Exception {
        int id = 1;
        byte[] jpegBytes = new byte[] {
                (byte)0xFF, (byte)0xD8, // SOI (Start of Image)
                (byte)0xFF, (byte)0xD9 // EOI (End of Image)
        };;
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                jpegBytes
        );

        mockMvc.perform(
                        multipart("/api/posts/" + id + "/image").file(image)
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/" + id + "/image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(jpegBytes));
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
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
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
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andExpect(status().isNotFound());
    }
}
