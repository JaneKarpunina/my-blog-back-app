package ru.yandex.practicum.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.PostResponse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public List<PostResponse> findPosts(List<String> tags,String titleWords) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.id, p.title, p.text, p.likes_count AS likesCount, ");
        sql.append(" (SELECT COUNT(*) FROM post_comments pc WHERE pc.post_id = p.id) AS commentsCount ");
        sql.append("FROM posts p ");

        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (!tags.isEmpty()) {
            // Для тегов — нужно убедиться, что у поста есть все указанные теги
            for (String tag : tags) {
                sql.append("JOIN post_tags pt_").append(tag.hashCode()).append(" ON p.id = pt_").append(tag.hashCode())
                        .append(".post_id AND pt_").append(tag.hashCode())
                        .append(".tag_id = (SELECT id FROM tags t WHERE t.name = ?)");
                params.add(tag);
            }
        }

        if (!titleWords.isEmpty()) {
                sql.append("WHERE p.title LIKE ?");
                params.add("%" + titleWords + "%");
        }

        // Выполняем запрос
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            PostResponse post = new PostResponse();
            post.setId(rs.getInt("id"));
            post.setTitle(rs.getString("title"));
            post.setText(rs.getString("text"));
            post.setLikesCount(rs.getInt("likesCount"));
            post.setCommentsCount(rs.getInt("commentsCount"));
            List<String> postTags = getTagsForPost(post.getId());
            post.setTags(postTags);
            return post;
        }, params.toArray());
    }

    private List<String> getTagsForPost(Integer postId) {
        String sql = "SELECT t.name FROM tags t JOIN post_tags pt ON t.id = pt.tag_id WHERE pt.post_id = ?";
        return jdbcTemplate.queryForList(sql, String.class, postId);
    }
}
