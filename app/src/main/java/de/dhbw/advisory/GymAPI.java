package de.dhbw.advisory;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Klasse für den Hintergrund Thread
 */
public class GymAPI extends AsyncTask<Object, Object, Void> {

    protected Context context;

    GymAPI(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Object... params) {
        try{
            GPSBestimmung gps = new GPSBestimmung(context);
            Log.i("GymFragment", "setPosition gestartet");
            gps.setPosition();
            getGyms("gym" ,gps);

        } catch(Exception e) {
           Log.i("GymFragment:","doInBackground Test");
        }
        return null;
    }



    /**
     *
     * Die Methode macht die HTTP Anfrage an die API
     *
     * @param
     * @return gibt den HTTP Request zurück
     * @throws Exception
     */
    protected String getGyms(String typ, GPSBestimmung gps) throws Exception {
        try {
            Log.i("GymFragment: ","Methode begonnen");
            Log.i("GymFragment: ","Übergebener Wert: " + typ);
            Log.i("GymFragment: ","Starte API Aufruf");

            //1. Request Factory holen
            Log.i("GymFragment: ","Request Factory holen begonnen");
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            Log.i("GymFragment: ","Request Factory holen beendet");





            //2. Url hinzufügen
            Log.i("GymFragment: ","URL hinzufügen begonnen");
            GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            url.put("location", + gps.getBreitengrad() + "," + gps.getLaengengrad());
            url.put("rankby", "distance");
            url.put("typ", typ);
            url.put("key", "AIzaSyDXzj9XCSY5FtsEyvtKaOtxctEX7ybzrpU");
            Log.i("GymFragment: ","URL hinzufügen beendet" + url);


            //3. Request absetzen
            Log.i("GymFragment: ","Request absetzen begonnen");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("X-Mashape-Key", "knWwtaM7bHmshY9d7wMYMZJd8AS3p1xbRHvjsnq4NKZcfVsSiP");
            HttpRequest request = requestFactory.buildGetRequest(url);
            request.setHeaders(httpHeaders);
            Log.i("GymFragment: ","Request absetzen Daten Header hinzugefügt");
            HttpResponse httpResponse = request.execute();
            Log.i("GymFragment: ","Request absetzen beendet");

            Log.i("GymFragment: ","Request parsen");
            String jsonResponseString = httpResponse.parseAsString();

            Log.i("GymFragment: ","API Aufruf beendet");
            Log.i("GymFragment: ","Erhaltene JSON: " + jsonResponseString);
            return jsonResponseString;
        } catch (Exception e) {
            //! Fehler Exception unspeziefisch
            Log.i("Fehler GymFragment", "Request absetzen fehlgeschlagen");
            return "error";
        }
    }

    /**
     *
     * Diese Methode parsed die Http Antwort der RecipeSearch Methode
     *
     * @param jsonString RecipeSearch Http Antwort als String parsen
     * @return
     * @throws JSONException
     */
    protected ArrayList parseRecipe(String jsonString) throws JSONException {
        try{

        } catch (Exception e) {
            //Fehler Exception

            return new ArrayList();
        }

        return null;
    }


}
