package de.dhbw.advisory;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.os.AsyncTask;
import android.content.Context;
import android.widget.TableRow.LayoutParams;
import java.io.InputStream;
import android.graphics.BitmapFactory;


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
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    protected TextView _rezepteAnleitung = null;
    protected TableLayout _rezepteZutatenliste = null;
    protected ImageView _rezepteIcon = null;
    protected TextView _rezepteZutaten = null;
    protected TextView _rezepteRezepte = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rezepte, container, false);

        //Referenzen der Textviews abfragen
        _rezepteTitel = (TextView) view.findViewById(R.id.Rezepte_Titel);
        _rezepteÜberschrift = (TextView) view.findViewById(R.id.Rezepte_Überschrift);
        _rezepteAnleitung = (TextView) view.findViewById(R.id.rezepte_Rezept_Text);
        _rezepteZutatenliste = (TableLayout) view.findViewById(R.id.rezepte_Zutatenliste);
        _rezepteIcon = (ImageView) view.findViewById(R.id.rezepte_Icon);
        _rezepteZutaten = (TextView) view.findViewById(R.id.rezepte_Zutaten);
        _rezepteRezepte = (TextView) view.findViewById(R.id.rezepte_Rezept);

        //Titel aktualisieren
        int h= actualHour();
        _rezepteÜberschrift.setText(getEssensTyp(h));

        //API Aufruf starten und Felder aktualisieren
        RecipeAPI mat = new RecipeAPI(this.getContext());

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
            typ = "main course";
        } else if (hh == 15) {
            //side dish
            typ = "side dish";
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


    public class GetImage extends AsyncTask <String, String, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... url) {
            Bitmap icon = null;

            Log.i("imageDoInBackground", "gestartet");

            try {
                InputStream in = new java.net.URL(url[0]).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e ) {
                Log.e("Error", e.getMessage());
            }

            return icon;
        }

        @Override
        protected void onPostExecute(Bitmap icon) {

            _rezepteIcon.setImageBitmap(icon);
        }


    }

    /**
     * Klasse für den Hintergrund Thread
     */
    public class RecipeAPI extends AsyncTask<String, String, ArrayList> {

        protected Context context;

        RecipeAPI(Context context){
            super();
            this.context = context;
        }

        @Override
        protected ArrayList doInBackground(String... params) {
            try{
                //Zu untersuchende ID via API herausfinden
                Log.i("doInBackground: ", "Methode begonnen");
                String jsonResponseRecipeSearch = getRecipe(params[0]);
                Log.i("doInBackground: ", "API Aufruf beendet");
                ArrayList erg = parseRecipe(jsonResponseRecipeSearch);
                Log.i("doInBackground: ", "parsen beendet");

                Log.i("doInBackground: ", "Methode beendet");
                return erg;
            } catch (Exception e){
                Log.i("doInBackground: ", "Fehler doInBackground");
                return new ArrayList();
            }

        }


        @Override
        protected void onPostExecute(ArrayList ergebnis) {
            Log.i("onPostExecute", "Methode begonnen");

            if (ergebnis.isEmpty()) {
                //Zurückgegebenes Ergebnis ist leer; Fehler
                _rezepteTitel.setText("ERROR");
                Log.i("onPostExecute","Fehler: Zurückgegebene Arraylist ist leer.");
            } else {
                Log.i("onPostExecute","Methode begonnen");
                //Titel setzten
                _rezepteTitel.setText(ergebnis.get(0).toString());
                //Rezeptanleitung setzen
                _rezepteAnleitung.setText(ergebnis.get(2).toString());

                GetImage img = new GetImage();
                img.execute((String) ergebnis.get(3));

                //Zutaten bekommen
                String[][] zutaten = (String[][]) ergebnis.get(1);

                //TableRow & TextView Arrays erstellen
                TableRow[] tr = new TableRow[zutaten.length];
                TextView[] tv_ing = new TextView[zutaten.length];
                TextView[] tv_amount = new TextView[zutaten.length];

                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                params.weight = 1f;

                //Zutaten in die Liste setzen
               for (int i = 0; i < zutaten.length; i++) {
                   tr[i] = new TableRow(context);
                   tv_ing[i] = new TextView(context);
                   tv_amount[i] = new TextView(context);

                   tv_ing[i].setLayoutParams(params);
                   tv_ing[i].setTextColor(Color.BLACK);
                   tv_ing[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                   tv_ing[i].setText("" + zutaten[i][0] + ":");

                   tv_amount[i].setLayoutParams(params);
                   tv_amount[i].setTextColor(Color.BLACK);
                   tv_amount[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                   tv_amount[i].setText("" + zutaten[i][1] + " " + zutaten[i][2]);

                   tr[i].addView(tv_ing[i]);
                   tr[i].addView(tv_amount[i]);
                   _rezepteZutatenliste.addView(tr[i]);
               }

                _rezepteRezepte.setText("Rezepte:");
               _rezepteZutaten.setText("Zutaten:");

                _rezepteZutatenliste.refreshDrawableState();
                Log.i("onPostExecute","Views aktualisiert");

            }

            Log.i("onPostExecute", "Methode beendet");
        }

        /**
         *
         * Die Methode macht die HTTP Anfrage an die API
         *
         * @param typ Gibt an nach welchem Typ gesucht wird (Mittagessen, Lunch, Suppe,..)
         * @return gibt den HTTP Request zurück
         * @throws Exception
         */
        protected String getRecipe(String typ) throws Exception {
            try {
                Log.i("getRecipe: ","Methode begonnen");
                Log.i("getRecipe: ","Übergebener Wert: " + typ);
                Log.i("getRecipe: ","Starte API Aufruf");
                //1. Request Factory holen
                Log.i("getRecipe: ","Request Factory holen begonnen");
                HttpTransport httpTransport = new NetHttpTransport();
                HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
                Log.i("getRecipe: ","Request Factory holen beendet");

                //2. Url hinzufügen
                Log.i("getRecipe: ","URL hinzufügen begonnen");
                GenericUrl url = new GenericUrl("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/random");
                url.put("limitLicense", "false");
                url.put("number", "1");
                url.put("tags", "" + typ);
                Log.i("getRecipe","URL hinzufügen, typ: " + typ);
                Log.i("getRecipe: ","URL hinzufügen beendet");


                //3. Request absetzen
                Log.i("getRecipe: ","Request absetzen begonnen");
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.set("X-Mashape-Key", "knWwtaM7bHmshY9d7wMYMZJd8AS3p1xbRHvjsnq4NKZcfVsSiP");
                HttpRequest request = requestFactory.buildGetRequest(url);
                request.setHeaders(httpHeaders);
                Log.i("getRecipe: ","Request absetzen Daten Header hinzugefügt");
                HttpResponse httpResponse = request.execute();
                Log.i("getRecipe: ","Request absetzen beendet");

                Log.i("getRecipe: ","Request parsen");
                String jsonResponseString = httpResponse.parseAsString();

                Log.i("getRecipe: ","API Aufruf beendet");
                Log.i("getRecipe: ","Erhaltene JSON: " + jsonResponseString);
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
        protected ArrayList parseRecipe(String jsonString) throws JSONException {
            try{
                Log.i("parseRecipe:", "Beginnt das parsen");
                Log.i("parseRecipe:", "Übergebenes Objekt: " + jsonString);
                //Eigentliches parsen
                JSONObject jsonObject = new JSONObject(jsonString);

                //Wichtigsten Inhalt entnehmen
                JSONArray jsonArray = jsonObject.getJSONArray("recipes");
                JSONObject i = jsonArray.getJSONObject(0);
                Log.i("parseRecipe:", "Recipes geparst");

                int servings = i.getInt("servings");
                Log.i("parseRecipe:", "Servings geparst");

                JSONArray ingredients = i.getJSONArray("extendedIngredients");
                int length = ingredients.length();
                //[][0] = aisle
                //[][1] = amount
                //[][2] = unit
                String[][] ing = new String[length][3];

                for(int x = 0; x < length; x++){
                    ing [x][0] = ingredients.getJSONObject(x).getString("aisle");
                    ing [x][1] = ingredients.getJSONObject(x).getString("amount");
                    ing [x][2] = ingredients.getJSONObject(x).getString("unit");
                }

                Log.i("parseRecipe:", "Ingredients geparst");

                String title = i.getString("title");
                Log.i("parseRecipe:", "title gesetzt");
                String img_url = i.getString("image");

                Log.i("parseRecipe:", "title und img_url gesetzt");
                //Instructions verschönern
                String newinstructions = i.getString("instructions");
                String teiler = "                         ";
                String instructions = newinstructions.replaceAll(teiler, "\n");

                //TODO durch eine Map ersetzen
                ArrayList<Object> information = new ArrayList<>();

                Log.i("parseRecipe:", "Parsen fast beendet");
                Log.i("parseRecipe:", "title: " + title);
                Log.i("parseRecipe:", "ing: " + ing.toString());
                Log.i("parseRecipe:", "instructions: " + instructions);

                information.add(0,title);
                information.add(1, ing);
                information.add(2,instructions);
                information.add(3, img_url);

                return information;
            } catch (Exception e) {
                //Fehler Exception
                Log.i("parseRecipeSearch","Parsen ReipeSearch fehlgeschlagen");
                return new ArrayList();
            }

        }
    }
}