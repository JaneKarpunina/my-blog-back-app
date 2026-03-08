package ru.yandex.practicum.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.PostResponse;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    private final TagRepository tagRepository;

    private final PostCommentRepository postCommentRepository;

    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate, TagRepository tagRepository,
                                    PostCommentRepository postCommentRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.tagRepository = tagRepository;
        this.postCommentRepository = postCommentRepository;
    }


    public Integer savePost(String title, String text) {
        String sql = "INSERT INTO post (title, text, likes_count) VALUES (?, ?, 0)";

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
        String sql = "UPDATE post SET title = ?, text = ? WHERE id = ?";
        return jdbcTemplate.update(sql, title, text, postId);
    }

    public Map<String, Object> getPostById(Integer id) {
        String sql = "SELECT id, title, text, likes_count AS likesCount FROM post WHERE id = ?";
        return jdbcTemplate.queryForMap(sql, id);
    }

    @Override
    public List<PostResponse> findPosts(List<String> tags,String titleWords) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.id, p.title, p.text, p.likes_count AS likesCount, ");
        sql.append(" (SELECT COUNT(*) FROM post_comment pc WHERE pc.post_id = p.id) AS commentsCount ");
        sql.append("FROM post p ");

        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (!tags.isEmpty()) {
            // Для тегов — нужно убедиться, что у поста есть все указанные теги
            for (String tag : tags) {
                sql.append("JOIN post_tags pt_").append(tag.hashCode()).append(" ON p.id = pt_").append(tag.hashCode())
                        .append(".post_id AND pt_").append(tag.hashCode())
                        .append(".tag_id = (SELECT id FROM tag t WHERE t.name = ?)");
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
            List<String> postTags = tagRepository.getTagsForPost(post.getId());
            post.setTags(postTags);
            return post;
        }, params.toArray());
    }

    @Override
    public Optional<PostResponse> findPostById(Integer id) {

        String sql = "SELECT p.id, p.title, p.text, p.likes_count AS likesCount FROM post p WHERE p.id = ?";

        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);
            Integer postId = (Integer) result.get("id");
            String title = (String) result.get("title");
            String text = (String) result.get("text");
            int likesCount = (int) result.get("likesCount");
            List<String> tags = tagRepository.getTagsForPost(postId);
            int commentsCount = postCommentRepository.countComments(postId);

            return Optional.of(new PostResponse(postId, title, text, tags, likesCount, commentsCount));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM post WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void deletePost(Integer postId) {
        jdbcTemplate.update("DELETE FROM post WHERE id = ?", postId);
    }

    @Override
    public Integer incrementLikes(Integer id) {
        jdbcTemplate.update("UPDATE post SET likes_count = likes_count + 1 WHERE id = ?", id);
        return jdbcTemplate.queryForObject(
                "SELECT likes_count FROM post WHERE id = ?", Integer.class, id);
    }

    @Override
    public void updatePostImage(Integer id, String filePath) {
        jdbcTemplate.update(
                "UPDATE post SET image_path = ? WHERE id = ?", filePath, id);

    }

    @Override
    public String findImagePathById(Integer id) {
        return jdbcTemplate.queryForObject(
                "SELECT image_path FROM post WHERE id = ?", String.class, id);
    }
}
