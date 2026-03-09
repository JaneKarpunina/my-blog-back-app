package ru.yandex.practicum.exception;

public class PostCommentNotFoundException extends RuntimeException {
    public PostCommentNotFoundException(String message) {
        super(message);
    }
}
