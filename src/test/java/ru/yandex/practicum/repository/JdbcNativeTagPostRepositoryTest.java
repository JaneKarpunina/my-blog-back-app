package ru.yandex.practicum.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.practicum.configuration.DataSourceConfiguration;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcNativeTagPostRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class JdbcNativeTagPostRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TagPostRepository tagPostRepository;

    @BeforeEach
    void setUp() {

        jdbcTemplate.execute("ALTER TABLE post ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE tag ALTER COLUMN id RESTART WITH 1");

        jdbcTemplate.execute("DELETE FROM post_tags");
        jdbcTemplate.execute("DELETE FROM tag");
        jdbcTemplate.execute("DELETE FROM post");

        jdbcTemplate.execute("INSERT INTO post (title, text, likes_count) VALUES ('title1', 'abc', 3)");

        jdbcTemplate.execute("INSERT INTO tag (name) VALUES ('tag1')");
        jdbcTemplate.execute("INSERT INTO tag (name) VALUES ('tag2')");

        jdbcTemplate.execute("INSERT INTO post_tags (post_id, tag_id) VALUES (1, 1)");

    }

    @Test
    public void testAddTagsToPost() {

        int postId = 1;
        tagPostRepository.addTagsToPost(postId, List.of(2));

        String sql = "SELECT post_id, tag_id FROM post_tags WHERE post_id = ?";
        List<Map<String, Object>> records = jdbcTemplate.queryForList(sql, postId);

        assertEquals(2, records.size());
        assertTrue(records.stream().anyMatch(r -> r.get("tag_id").equals(1)));
        assertTrue(records.stream().anyMatch(r -> r.get("tag_id").equals(2)));

    }

    @Test
    public void testDeleteTagsForPost() {

        int postId = 1;
        tagPostRepository.deleteTagsForPost(postId);

        String sql = "SELECT post_id, tag_id FROM post_tags WHERE post_id = ?";
        List<Map<String, Object>> records = jdbcTemplate.queryForList(sql, postId);

        assertEquals(0, records.size());

    }

}
