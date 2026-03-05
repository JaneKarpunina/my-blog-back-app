package ru.yandex.practicum.repository;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class JdbcNativeTagRepository implements TagRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativeTagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Поиск тэга по имени
    public Integer findTagIdByName(String name) {
        String sql = "SELECT id FROM tag WHERE name = ?";
        List<Integer> ids = jdbcTemplate.queryForList(sql, Integer.class, name);
        return ids.isEmpty() ? null : ids.getFirst();
    }

    public Integer createTag(String name) {
        String sql = "INSERT INTO tags (name) VALUES (?)";

        KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public Integer getOrCreateTag(String name) {
        Integer id = findTagIdByName(name);
        if (id != null) {
            return id;
        }
        return createTag(name);
    }
}
