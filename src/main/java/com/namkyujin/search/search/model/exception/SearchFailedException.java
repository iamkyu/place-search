package com.namkyujin.search.search.model.exception;

import java.util.List;

public class SearchFailedException extends RuntimeException {

    public SearchFailedException(String message) {
        super(message);
    }

    public SearchFailedException(Throwable cause) {
        super(cause);
    }

    public static SearchFailedException createMultiSearchFailedException(List<Throwable> throwables) {
        return new SearchFailedException(throwables.toString());
    }
}