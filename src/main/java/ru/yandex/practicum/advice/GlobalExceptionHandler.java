package ru.yandex.practicum.advice;


import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import ru.yandex.practicum.exception.PostCommentNotFoundException;
import ru.yandex.practicum.exception.PostNotFoundException;
import ru.yandex.practicum.exception.UnsupportedMediaTypeException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({PostNotFoundException.class, PostCommentNotFoundException.class,
            DataAccessException.class})
    public ResponseEntity<Void> handlePostCommentAndDataAccessExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<Void> handleUnsupportedMediaTypeException(UnsupportedMediaTypeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE)
                .header("Access-Control-Allow-Origin", "http://localhost")
                .body("File size exceeds limit");

    }
}
