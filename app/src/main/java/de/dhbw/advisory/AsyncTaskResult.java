package de.dhbw.advisory;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.model.SearchResult;

import java.util.Collection;
import java.util.List;

public class AsyncTaskResult {
    private Throwable error;
    private Collection<SearchResult> result;

    public AsyncTaskResult(List<SearchResult> items) {
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

    public Collection<SearchResult> getResult() {
        return result;
    }
}