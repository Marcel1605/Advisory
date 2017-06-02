package de.dhbw.advisory;

import com.google.api.services.youtube.model.SearchResult;

public interface IYoutubeCardView {
    void cancelProgressDialog();
    void addCard(SearchResult item);
}