package ru.yandex.practicum.repository;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcNativePostCommentRepository implements PostCommentRepository{

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativePostCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer countComments(Integer postId) {
        String sql = "SELECT COUNT(*) FROM post_comment WHERE post_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, postId);
    }

    public void deleteCommentsForPost(Integer postId) {
        jdbcTemplate.update("DELETE FROM post_comment WHERE post_id = ?", postId);
    }
}
