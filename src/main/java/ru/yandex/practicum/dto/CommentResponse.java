package ru.yandex.practicum.dto;

public class CommentResponse {

    private int id;
    private String text;
    private int postId;

    public CommentResponse() {
    }

    public CommentResponse(int id, String text, int postId) {
        this.text = text;
        this.id = id;
        this.postId = postId;
    }

    public int getId() {
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

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
