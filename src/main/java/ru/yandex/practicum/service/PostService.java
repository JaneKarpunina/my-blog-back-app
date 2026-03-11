package ru.yandex.practicum.service;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.dto.PostListResponse;
import ru.yandex.practicum.dto.PostRequest;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.exception.PostNotFoundException;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostCommentRepository;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.repository.TagPostRepository;
import ru.yandex.practicum.repository.TagRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private static final String IMAGES_DIR = "uploads/";

    public static final int SYMBOL_COUNT = 128;
    private final PostRepository postRepository;

    private final TagRepository tagRepository;

    private final TagPostRepository tagPostRepository;

    private final PostCommentRepository postCommentRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository,
                       TagPostRepository tagPostRepository, PostCommentRepository postCommentRepository) {

        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.tagPostRepository = tagPostRepository;
        this.postCommentRepository = postCommentRepository;
    }

    @Transactional
    public Post createPost(PostRequest postRequest) {
        // Создаем пост
        Integer postId = postRepository.savePost(postRequest.getTitle(), postRequest.getText());

        // Обрабатываем теги
        List<Integer> tagIds = postRequest.getTags().stream()
                .map(tagRepository::getOrCreateTag)
                .collect(Collectors.toList());

        // Связываем пост и теги
        tagPostRepository.addTagsToPost(postId, tagIds);

        return new Post(postId, postRequest.getTitle(), postRequest.getText(), 0, 0,
                postRequest.getTags());
    }

    @Transactional
    public PostResponse updatePost(PostRequest postRequest) {
        Integer postId = postRequest.getId();

        // Обновляем текст и название
        int rows = postRepository.updatePost(postId, postRequest.getTitle(), postRequest.getText());
        if (rows == 0) {
            throw new PostNotFoundException("Пост с идентификатором: " + postId + " не найден");
        }

        // Обновляем теги:
        // 1. Удаляем старые связи
        tagPostRepository.deleteTagsForPost(postId);
        // 2. Обрабатываем новые
        List<Integer> tagIds = postRequest.getTags().stream()
                .map(tagRepository::getOrCreateTag)
                .collect(Collectors.toList());
        // 3. Добавляем новые связи
        tagPostRepository.addTagsToPost(postId, tagIds);

        // Получаем обновленный пост и комментарии
        Map<String, Object> postData = postRepository.getPostById(postId);
        int commentsCount = postCommentRepository.countComments(postId);

        // Формируем ответ
        PostResponse response = new PostResponse();
        response.setId(postId);
        response.setTitle((String) postData.get("title"));
        response.setText((String) postData.get("text"));
        response.setLikesCount((Integer) postData.get("likesCount"));
        response.setCommentsCount(commentsCount);
        response.setTags(postRequest.getTags());

        return response;
    }

    @Transactional
    public PostListResponse getPosts(String search, int pageNumber, int pageSize) {
        List<PostResponse> filteredPosts = findPosts(search); // Генерируем список всех постов

        if (filteredPosts.isEmpty()) {
            return new PostListResponse(filteredPosts, false, false, 0);
        }

        for (PostResponse postResponse : filteredPosts) {
            if (postResponse.getText().length() > SYMBOL_COUNT) {
                postResponse.setText(postResponse.getText().substring(0, SYMBOL_COUNT) + "...");
            }
        }

        int totalPosts = filteredPosts.size();
        int totalPages = (int) Math.ceil((double) totalPosts / pageSize);
        if (pageNumber > totalPages) {
            pageNumber = totalPages; // если запрошена страница больше последней
        }
        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalPosts);
        List<PostResponse> pagePosts = filteredPosts.subList(fromIndex, toIndex);

        boolean hasPrev = pageNumber > 1;
        boolean hasNext = pageNumber < totalPages;

        return new PostListResponse(pagePosts, hasPrev, hasNext, totalPages);
    }

    private List<PostResponse> findPosts(String search) {

        String[] words = search.trim().split("\\s+");
        List<String> parsedWords = Arrays.stream(words)
                .filter(w -> !w.isEmpty())
                .toList();

        List<String> tags = new ArrayList<>();
        StringBuilder titleWords = new StringBuilder();

        for (String word : parsedWords) {
            if (word.startsWith("#") && word.length() > 1) {
                tags.add(word.substring(1));
            } else {
                titleWords.append(word);
                titleWords.append(" ");
            }
        }

        return postRepository.findPosts(tags, titleWords.toString().trim());
    }

    @Transactional
    public Optional<PostResponse> findPostById(Integer id) {
        return postRepository.findPostById(id);
    }

    @Transactional
    public void deletePost(Integer id) {
        if (!postRepository.existsById(id)) {
            throw new PostNotFoundException("Пост с идентификатором: " + id + " не найден");
        }
        tagPostRepository.deleteTagsForPost(id);
        postCommentRepository.deleteCommentsForPost(id);
        postRepository.deletePost(id);
    }

    @Transactional
    public Integer incrementLikes(Integer id) {
        if (!postRepository.existsById(id)) {
            throw new PostNotFoundException("Пост c идентификатором: " + id + " не найден");
        }
        return postRepository.incrementLikes(id);
    }

    @Transactional
    public void updatePostImage(Integer id, MultipartFile image) {
        if (!postRepository.existsById(id)) {
            throw new PostNotFoundException("Пост c идентификатором: " + id + "не найден");
        }
        String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();

        Path filePath = Paths.get(IMAGES_DIR, filename);
        try {

            Path uploadDir = Paths.get(IMAGES_DIR);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Произошла ошибка при попытке записи в файл: " + filePath);
        }

        postRepository.updatePostImage(id, filePath.toString());
    }

    @Transactional
    public Resource getPostImage(Integer id) {
        String imagePath = postRepository.findImagePathById(id);
        if (imagePath == null) {
            return new ByteArrayResource(new byte[0]);
        }
        return new ByteArrayResource(getImageBytes(imagePath));

    }

    private byte[] getImageBytes(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            return Files.readAllBytes(path);
        } catch (IOException exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }
}
