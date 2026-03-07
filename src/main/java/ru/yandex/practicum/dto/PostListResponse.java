package ru.yandex.practicum.dto;

import java.util.List;

public class PostListResponse {

    private List<PostResponse> posts;
    private boolean hasPrev;
    private boolean hasNext;
    private int lastPage;

    public PostListResponse() {
    }

    public PostListResponse(List<PostResponse> postResponseList, boolean hasPrev, boolean hasNext, int lastPage) {
        this.posts = postResponseList;
        this.hasPrev = hasPrev;
        this.hasNext = hasNext;
        this.lastPage = lastPage;
    }

    public List<PostResponse> getPosts() {
        return posts;
    }

    public void setPosts(List<PostResponse> posts) {
        this.posts = posts;
    }

    public boolean isHasPrev() {
        return hasPrev;
    }

    public void setHasPrev(boolean hasPrev) {
        this.hasPrev = hasPrev;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }
}
