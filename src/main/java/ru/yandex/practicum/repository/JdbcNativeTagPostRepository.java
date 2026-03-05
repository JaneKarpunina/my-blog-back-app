package ru.yandex.practicum.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcNativeTagPostRepository implements TagPostRepository{

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativeTagPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addTagsToPost(Integer postId, List<Integer> tagIds) {
        String sql = "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)";
        for (Integer tagId : tagIds) {
            jdbcTemplate.update(sql, postId, tagId);
        }
    }
}
