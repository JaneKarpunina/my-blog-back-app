package ru.yandex.practicum.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;

@Repository
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Создать пост и вернуть его ID
    public Integer savePost(String title, String text) {
        String sql = "INSERT INTO posts (title, text, likes_count) VALUES (?, ?, 0)";

        KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setString(2, text);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    @Override
    public Integer updatePost(Integer postId, String title, String text) {
        String sql = "UPDATE posts SET title = ?, text = ? WHERE id = ?";
        return jdbcTemplate.update(sql, title, text, postId);
    }

    public Map<String, Object> getPostById(Integer id) {
        String sql = "SELECT id, title, text, likes_count AS likesCount FROM posts WHERE id = ?";
        return jdbcTemplate.queryForMap(sql, id);
    }
}
