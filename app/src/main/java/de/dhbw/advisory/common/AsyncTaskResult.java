package de.dhbw.advisory.common;

import java.util.List;


/**
 * Dies ist ein Wrapper-Objekt, welches entweder ein Eergnis einer API Anfrage oder einen Fehler wrapped
 * @param <T> Der Typ des Ergebnisses
 */
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