package ru.yandex.practicum.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.PostListResponse;
import ru.yandex.practicum.dto.PostRequest;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostCommentRepository;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.repository.TagPostRepository;
import ru.yandex.practicum.repository.TagRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    public static final int SYMBOL_COUNT = 128;
    private final PostRepository postRepository;

    private final TagRepository tagRepository;

    private final TagPostRepository tagPostRepository;

    private final PostCommentRepository postCommentRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository,
                       TagPostRepository tagPostRepository, PostCommentRepository postCommentRepository){

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
            throw new RuntimeException("Пост с идентификатором: " + postId + " не найден");
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

    public PostListResponse getPosts(String search, int pageNumber, int pageSize) {
        List<PostResponse> filteredPosts = findPosts(search); // Генерируем список всех постов

        for(PostResponse postResponse : filteredPosts) {
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

    public Optional<PostResponse> findPostById(Integer id) {
        return postRepository.findPostById(id);
    }
}
