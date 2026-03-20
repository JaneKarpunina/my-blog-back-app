package ru.yandex.practicum.repository;

/*import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.practicum.configuration.DataSourceConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcNativeTagRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class JdbcNativeTagRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TagRepository tagRepository;

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
        jdbcTemplate.execute("INSERT INTO post_tags (post_id, tag_id) VALUES (1, 2)");

    }

    @Test
    public void testFindTagIdByName() {
        Integer tagId = tagRepository.findTagIdByName("tag1");

        assertEquals(1, tagId);

    }

    @Test
    public void testCreateTag() {

        Integer id = tagRepository.createTag("tag3");

        assertEquals(3, id);

    }

    @Test
    public void testGetTagsForPost() {

        int postId = 1;

        List<String> tags = tagRepository.getTagsForPost(postId);

        assertEquals(2, tags.size());
    }

    @Test
    public void testGetOrCreateTag() {

        Integer id = tagRepository.getOrCreateTag("tag1");

        assertEquals(1, id);

        id = tagRepository.getOrCreateTag("tag3");

        assertEquals(3, id);



    }


}*/
