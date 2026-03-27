package ru.yandex.practicum.model;

public class PostComment {

    private Integer id;
    private String text;
    private Integer postId;

    public PostComment() {
    }

    public PostComment(Integer id, String text, Integer postId) {
        this.text = text;
        this.id = id;
        this.postId = postId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
