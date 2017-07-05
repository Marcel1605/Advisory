package de.dhbw.advisory.fitness;

import com.google.api.services.youtube.model.SearchResult;

/**
 * Dieses Interface muss von Klassen implementiert werden, welche die YouTube Data API verwenden.
 * Es stellt Callback-Methoden bereit
 */
public interface IYoutubeCardView {
    void cancelProgressDialog();
    void addCard(SearchResult item);
}