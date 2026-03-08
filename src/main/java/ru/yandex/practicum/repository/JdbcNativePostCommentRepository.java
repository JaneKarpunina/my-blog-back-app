package ru.yandex.practicum.repository;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.CommentRequest;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.model.PostComment;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

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

    @Override
    public List<PostComment> findCommentsByPostId(int postId) {
        String sql = "SELECT id, text, post_id FROM post_comment WHERE post_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            PostComment comment = new PostComment();
            comment.setId(rs.getInt("id"));
            comment.setText(rs.getString("text"));
            comment.setPostId(rs.getInt("post_id"));
            return comment;
        }, postId);
    }

    public PostComment findCommentsByPostIdCommentId(int commentId, int postId) {

        String sql = "SELECT id, text, post_id FROM post_comment WHERE id = ? AND post_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            PostComment comment = new PostComment();
            comment.setId(rs.getInt("id"));
            comment.setText(rs.getString("text"));
            comment.setPostId(rs.getInt("post_id"));
            return comment;
        }, commentId, postId);

    }

    @Override
    public Integer createComment(CommentRequest comment) {
        String sql = "INSERT INTO post_comment (text, post_id) VALUES (?, ?)";

        KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, comment.getText());
            ps.setInt(2, comment.getPostId());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() == null) {
           throw new RuntimeException("Не удалось сохранить в базу данных комментарий");
        }

        return keyHolder.getKey().intValue();
    }
}
