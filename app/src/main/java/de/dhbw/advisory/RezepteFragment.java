package de.dhbw.advisory;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import android.os.AsyncTask;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
        import java.util.Calendar;

/**
 * Created by Magnus on 30.05.17.
 */
public class RezepteFragment extends Fragment {

    //Mittagessen, Lunch, Frühstück, Mitternachtssnach, Abendessen,...
    private String essenTyp;

    //Titel des Rezepts
    private String rezepte_Titel;

    //Variablen für die Nutritions
    private int kohlenhydrate;
    private int proteine;
    private int zucker;
    private int fett;

    //2 Dimensionale Liste für die Zutaten: 1. Zutat 2. Anzahl
    private int [][] zutaten;

    //Rezept Text
    private String rezeptText;

    //Aktuelle Uhrzeit (nur Stunden)
    private int actualHour;

    /**UI-Elemente*/
    protected TextView _rezepteÜberschrift = null;
    protected TextView _rezepteTitel = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rezepte, container, false);

        //Referenzen der Textviews abfragen
        _rezepteTitel = (TextView) view.findViewById(R.id.Rezepte_Titel);
        _rezepteÜberschrift = (TextView) view.findViewById(R.id.Rezepte_Überschrift);

        //Titel aktualisieren
        int h= actualHour();
        _rezepteÜberschrift.setText(getEssensTyp(h));

        //API Aufruf starten und Felder aktualisieren
        MeinAsyncTask mat = new MeinAsyncTask();

        mat.execute(getEssensTypAnfrage(h));


        return view;
    }


    @Override
    public void onPause(){
        //The system calls this method as the first indication that the user is leaving the fragment (though it does not always mean the fragment is being destroyed). This is usually where you should commit any changes that should be persisted beyond the current user session (because the user might not come back).
        Log.e("DEBUG", "OnPause of RezepteFragment");
        super.onPause();
    }




    /**
     * Diese Methode liest die aktuelle Stundenzeit anhand der Systemzeit aus
     *
     * @return gibt die aktuelle Stundenzeit zurück
     */
    public int actualHour(){
        int hh;
        Calendar kalendar = Calendar.getInstance();
        SimpleDateFormat zeitformatH = new SimpleDateFormat("HH");
        String h = zeitformatH.format(kalendar.getTime());
        hh = Integer.parseInt(h);
        return hh;
    }

    /**
     *
     */
    public String getEssensTyp(int hh){
        //bread 6-9
        //appetizer 10-11
        //main course 12-14
        //side dish 15
        //dessert 16
        //main course 17-21
        //salad 22-23
        //soup 24-5

        String typ;

        if (hh == 24 || hh <= 5) {
            //Soup
            typ = "ihre Suppe";
        } else if (hh >= 6 && hh <= 9) {
            //Bread
            typ = "ihr Brot";
        } else if (hh >= 10 && hh <= 11) {
            //Appetizer
            typ = "ihr Appetizer";
        } else if (hh >= 12 && hh <= 14 || hh >= 17 && hh <= 21) {
            //Main Course
            typ = "ihr Hauptgericht";
        } else if (hh == 15) {
            //side dish
            typ = "ihr Kleiner Happen";
        } else if (hh == 16) {
            //Dessert
            typ = "ihr Dessert";
        }  else if (hh >= 22 && hh <= 23) {
            //Salad
            typ = "ihr Salat";
        } else {
            //Fehler
            typ = "Fehler";
        }
       String r= "Hier " + typ + "!";

      return r;
    }

    /**
     * Diese Methode aktualisiert die Überschrift des Fragments
     *
     *
     * @param hh
     */
    public String getEssensTypAnfrage(int hh) {
        //bread 6-9
        //appetizer 10-11
        //main course 12-14
        //side dish 15
        //dessert 16
        //main course 17-21
        //salad 22-23
        //soup 24-5

        String typ;

        if (hh == 24 || hh <= 5) {
            //Soup
            typ = "soup";
        } else if (hh >= 6 && hh <= 9) {
            //Bread
            typ = "bread";
        } else if (hh >= 10 && hh <= 11) {
            //Appetizer
            typ = "appetizer";
        } else if (hh >= 12 && hh <= 14 || hh >= 17 && hh <= 21) {
            //Main Course
            typ = "main+course";
        } else if (hh == 15) {
            //side dish
            typ = "side+dish";
        } else if (hh == 16) {
            //Dessert
            typ = "dessert";
        }  else if (hh >= 22 && hh <= 23) {
            //Salad
            typ = "salad";
        } else {
            //Fehler
            typ = "Fehler!";
        }

        return typ;
    }


    /**
     * Klasse für den Hintergrund Thread
     */
    public class MeinAsyncTask extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            try{
                //Zu untersuchende ID via API herausfinden
                String jsonResponseString = getRecipeSearch(params[0]);
                int id = parseRecipeSearch(jsonResponseString);
                String[] erg = new String[1];
                erg[0] = String.valueOf(id);
                return erg;
            } catch (Exception e){
                return new String[0];
            }

        }

        @Override
        protected void onPostExecute(String[] ergebnis) {
            
        }

        /**
         *
         * Die Methode macht die HTTP Anfrage an die API
         *
         * @param typ Gibt an nach welchem Typ gesucht wird (Mittagessen, Lunch, Suppe,..)
         * @return gibt den HTTP Request zurück
         * @throws Exception
         */
        protected String getRecipeSearch(String typ) throws Exception {
            try {
                //1. Request Factory holen
                HttpTransport httpTransport = new NetHttpTransport();
                HttpRequestFactory requestFactory = httpTransport.createRequestFactory();

                //2. Url hinzufügen
                GenericUrl url = new GenericUrl("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/search");
                url.put("instructionsRequired", "true");
                url.put("number", "10");
                url.put("query", typ);
                url.put("type", typ);

                //3. Request absetzen
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.set("X-Mashape-Key", "knWwtaM7bHmshY9d7wMYMZJd8AS3p1xbRHvjsnq4NKZcfVsSiP");
                HttpRequest request = requestFactory.buildGetRequest(url);
                request.setHeaders(httpHeaders);
                HttpResponse httpResponse = request.execute();

                String jsonResponseString = httpResponse.parseAsString();
                return jsonResponseString;
            } catch (Exception e) {
                //! Fehler Exception unspeziefisch
                Log.i("Fehler getRecipeSearch", "Request absetzen fehlgeschlagen");
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
        protected int parseRecipeSearch(String jsonString) throws JSONException {
            try{
                //Eigentliches parsen
                JSONObject jsonObject = new JSONObject(jsonString);

                //Zufällig ein Rezept JSON Objekt aus dem JSON Array nehmen
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                int length = jsonArray.length();
                int position =  ((int) Math.random()) *length;
                JSONObject recipe = jsonArray.getJSONObject(position);

                //ID des zufällig gewählten JSON Objekt auswählen
                int id = recipe.getInt("id");

                return id;
            } catch (Exception e) {
                //Fehler Exception
                Log.i("parseRecipeSearch","Parsen ReipeSearch fehlgeschlagen");
                return 0;
            }

        }
    }
}