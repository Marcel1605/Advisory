package de.dhbw.advisory;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.model.SearchResult;

import java.util.Collection;
import java.util.List;

public class AsyncTaskResult <T> {
    private Throwable error;
    private List<T> result;

    public AsyncTaskResult(List<T> items) {
        this.result = items;
    }

    public AsyncTaskResult(Throwable e) {
        this.error = e;
    }

    public boolean isSuccessful() {
        return error == null;
    }

    public Throwable getError() {
        return error;
    }

    public List<T> getResult() {
        return result;
    }
}