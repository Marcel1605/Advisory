package de.dhbw.advisory.fitness;
        import android.content.Context;
        import android.content.res.AssetManager;
        import android.os.AsyncTask;

        import com.google.api.client.googleapis.json.GoogleJsonResponseException;
        import com.google.api.client.http.HttpRequest;
        import com.google.api.client.http.HttpRequestInitializer;
        import com.google.api.client.http.javanet.NetHttpTransport;
        import com.google.api.client.json.jackson2.JacksonFactory;
        import com.google.api.services.youtube.YouTube;
        import com.google.api.services.youtube.model.ResourceId;
        import com.google.api.services.youtube.model.SearchListResponse;
        import com.google.api.services.youtube.model.SearchResult;
        import com.google.api.services.youtube.model.Thumbnail;

        import java.io.IOException;
        import java.io.InputStream;
        import java.util.Iterator;
        import java.util.Properties;

        import de.dhbw.advisory.common.AsyncTaskResult;

/**
 * Ausgabe einer Liste von Videos, die einer Sucheingabe entsprechen.
 */

public class YoutubeSearch extends AsyncTask<String, Void, AsyncTaskResult<SearchResult>> {
    private static final String PROPERTIES_FILENAME = "apikey.properties";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

    /**
     * Die Instanz des YouTube-Objekts wird dazu verwendet eine Datenanfrage mit der YouTube API zu machen
     */
    private final YouTube youtube;
    private Context context;
    private final IYoutubeCardView resultView;

    public YoutubeSearch(Context context, IYoutubeCardView resultView) {
        this.context = context;
        this.resultView = resultView;
        //Dieses Objekt wird dazu verwendet die Datenanfrage mit der YouTube API zu machen
        //Das letzte Argument muss angegeben werden, aber da hier nichts initialisiert werden muss,
        //wenn der HttpRequest initilisiert wird kann das Interface mit einer "no-operation"-Funktion überschrieben werden
        this.youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("advisory").build();
    }

    /*
     * Diese Methode vollzieht den Aufruf der YouTube-API um Videos und dazugehörige Informationen abzurufen
     */
    private AsyncTaskResult<SearchResult> searchVideo(Context context, String queryTerm) {
        // Der developer key wird später aus der properties-Datei ausgelesen
        Properties properties = new Properties();

        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("apikey.properties");
            properties.load(inputStream);

        } catch (IOException e) {
            return new AsyncTaskResult(e);
        }

        try {
            // Definition der API-Anfrage für abzurufende Suchergebnisse
            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Auslesen des developer key
            String apiKey = properties.getProperty("youtube.apikey");
            search.setKey(apiKey);
            search.setQ(queryTerm);

            // Die Suchergbenisse werden auf Videos beschränkt. Vgl.:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // Um die Abfrage zu beschleunigen werden nur die Infos abgerufen, die in der Applikation benötigt werden:
            //Später soll der Titel des Videos und das Vorschaubild abgebildet werden
            //Beim Klick auf das Video soll dieses abgespielt werden
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/high/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Hier erfolgt der Aufruf der API
            SearchListResponse searchResponse = search.execute();

            return new AsyncTaskResult(searchResponse.getItems());
        } catch (GoogleJsonResponseException e) {
            return new AsyncTaskResult(e);
        } catch (IOException e) {
            return new AsyncTaskResult(e);
        } catch (Throwable t) {
            return new AsyncTaskResult(t);
        }
    }

    /*
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param query Search query (String)
     */
    public static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
    }

    //Auslagern des Aufrufs
    @Override
    protected AsyncTaskResult<SearchResult> doInBackground(String... params) {
        return searchVideo(this.context, params[0]);
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<SearchResult> asyncTaskResult) {
        ////Sobald Videos gefunden wurden, wird zunächst der Ladebildschirm entfernt
        resultView.cancelProgressDialog();

        //Gibt es Ergbnisse, so werden diese dem View in Form einer Karte hinzugefügt
        if(asyncTaskResult.isSuccessful()) {
            for(SearchResult item : asyncTaskResult.getResult()) {
                resultView.addCard(item);
            }

        } else {
            System.err.println(asyncTaskResult.getError().getMessage());
        }
    }
}
