package ru.yandex.practicum.service;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.repository.PostCommentRepository;

public class BaseTest {

    @MockitoBean
    public PostCommentRepository postCommentRepository;
}
