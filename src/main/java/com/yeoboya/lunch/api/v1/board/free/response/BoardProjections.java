package com.yeoboya.lunch.api.v1.board.free.response;

public class BoardProjections {

    public interface BoardSummary {
        String title();
        boolean secret();
        String email();
    }

    public interface BoardDetail {

    }
}
