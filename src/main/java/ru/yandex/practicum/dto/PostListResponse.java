package ru.yandex.practicum.dto;

import java.util.List;

public class PostListResponse {

    private List<PostResponse> postResponseList;
    private boolean hasPrev;
    private boolean hasNext;
    private int lastPage;

    public PostListResponse() {
    }

    public PostListResponse(List<PostResponse> postResponseList, boolean hasPrev, boolean hasNext, int lastPage) {
        this.postResponseList = postResponseList;
        this.hasPrev = hasPrev;
        this.hasNext = hasNext;
        this.lastPage = lastPage;
    }

    public List<PostResponse> getPostResponseList() {
        return postResponseList;
    }

    public void setPostResponseList(List<PostResponse> postResponseList) {
        this.postResponseList = postResponseList;
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
