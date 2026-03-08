package ru.yandex.practicum.dto;

public class CommentRequest {

    private Integer id;
    private String text;
    private Integer postId;

    public CommentRequest() {
    }

    public CommentRequest(int id, String text, int postId) {
        this.text = text;
        this.id = id;
        this.postId = postId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
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
