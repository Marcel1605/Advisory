package de.dhbw.advisory;

/**
 * Created by Konstantin on 01.06.2017.
 */

        import android.content.Context;
        import android.content.res.AssetManager;
        import android.os.AsyncTask;

        import com.google.api.client.googleapis.json.GoogleJsonResponseException;
        import com.google.api.client.http.HttpRequest;
        import com.google.api.client.http.HttpRequestInitializer;
        import com.google.api.client.http.HttpTransport;
        import com.google.api.client.http.javanet.NetHttpTransport;
        import com.google.api.client.json.JsonFactory;
        import com.google.api.client.json.jackson2.JacksonFactory;
        import com.google.api.services.youtube.YouTube;
        import com.google.api.services.youtube.model.ResourceId;
        import com.google.api.services.youtube.model.SearchListResponse;
        import com.google.api.services.youtube.model.SearchResult;
        import com.google.api.services.youtube.model.Thumbnail;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.util.Collection;
        import java.util.Iterator;
        import java.util.List;
        import java.util.Properties;

/**
 * Ausgabe einer Liste von Videos, die einer Sucheingabe entsprechen.
 */

public class YoutubeSearch extends AsyncTask<String, Void, AsyncTaskResult> {
    private static final String PROPERTIES_FILENAME = "youtube.properties";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private final YouTube youtube;
    private Context context;
    private final IYoutubeCardView resultView;

    public YoutubeSearch(Context context, IYoutubeCardView resultView) {
        this.context = context;
        this.resultView = resultView;
        // This object is used to make YouTube Data API requests. The last
        // argument is required, but since we don't need anything
        // initialized when the HttpRequest is initialized, we override
        // the interface and provide a no-op function.
        this.youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("advisory").build();
    }

    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name and thumbnail image of each video in the result set.
     */
    private AsyncTaskResult searchVideo(Context context, String queryTerm) {
        // Read the developer key from the properties file.
        Properties properties = new Properties();
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("youtube.properties");
            properties.load(inputStream);

        } catch (IOException e) {
            return new AsyncTaskResult(e);
        }

        try {
            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the {{ Google Cloud Console }} for
            // non-authenticated requests. See:
            // {{ https://cloud.google.com/console }}
            String apiKey = properties.getProperty("youtube.apikey");
            search.setKey(apiKey);
            search.setQ(queryTerm);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/high/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
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

    @Override
    protected AsyncTaskResult doInBackground(String... params) {
        return searchVideo(this.context, params[0]);
    }

    @Override
    protected void onPostExecute(AsyncTaskResult asyncTaskResult) {
        resultView.cancelProgressDialog();
        if(asyncTaskResult.isSuccessful()) {
            for(SearchResult item : asyncTaskResult.getResult()) {
                resultView.addCard(item);
            }
        } else {
            System.err.println(asyncTaskResult.getError().getMessage());
        }
    }
}
