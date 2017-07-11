package de.dhbw.advisory.recipe;


import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
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
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;

import java.io.IOException;
import java.io.InputStream;
import android.graphics.BitmapFactory;
import android.app.ProgressDialog;
import android.widget.Toast;



import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.io.CharStreams;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import de.dhbw.advisory.R;
import de.dhbw.advisory.common.AsyncTaskResult;

public class RezepteFragment extends Fragment {
    //Ladebildschirm
    private ProgressDialog alertDialog;

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
    protected TableRow _rezepteTableRow = null;

    //Snackbar
    private Snackbar snackbar;

    //Apikey
    private String apiKey;

    public DisplayMetrics metrics;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Der developer key wird später aus der properties-Datei ausgelesen
        Properties properties = new Properties();

        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("apikey.properties");
            properties.load(inputStream);
            apiKey = properties.getProperty("spoonacular.apikey");
        } catch (IOException e) {
            Toast.makeText(context, "Fragment konnte nicht geladen werden", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rezepte, container, false);

        metrics = this.getResources().getDisplayMetrics();

        //Ladebildschirm einfügen
        alertDialog = new ProgressDialog(getContext());
        alertDialog.setMessage(getResources().getString(R.string.loader));
        alertDialog.setCancelable(false);
        alertDialog.show();

        //Referenzen der Textviews abfragen
        _rezepteTitel = (TextView) view.findViewById(R.id.Rezepte_Titel);
        _rezepteÜberschrift = (TextView) view.findViewById(R.id.Rezepte_Überschrift);
        _rezepteAnleitung = (TextView) view.findViewById(R.id.rezepte_Rezept_Text);
        _rezepteZutatenliste = (TableLayout) view.findViewById(R.id.rezepte_Zutatenliste);
        _rezepteIcon = (ImageView) view.findViewById(R.id.rezepte_Icon);
        _rezepteZutaten = (TextView) view.findViewById(R.id.rezepte_Zutaten);
        _rezepteRezepte = (TextView) view.findViewById(R.id.rezepte_Rezept);
        _rezepteTableRow = (TableRow) view.findViewById(R.id.rezepte_TableRow);

        //Titel aktualisieren
        int h= actualHour();
        _rezepteÜberschrift.setText(getEssensTyp(h));

        //API Aufruf starten und Felder aktualisieren
        RecipeAPI mat = new RecipeAPI(this.getContext());

        if (AppStatus.getInstance(this.getContext()).isOnline() == false) {
            snackbar = Snackbar.make(container, "Keine Internetverbindung!", Snackbar.LENGTH_LONG);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }

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
     * Diese Methode hidet den Progress Dialog
     */
    public void cancelProgressDialog() {
        alertDialog.dismiss();
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
        //breakfast 6-9
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
            //Breakfast
            typ = "ihr Frühstück";
        } else if (hh >= 10 && hh <= 11) {
            //Appetizer
            typ = "ihr Appetizer";
        } else if (hh >= 12 && hh <= 14 || hh >= 17 && hh <= 21) {
            //Main Course
            typ = "ihr Hauptgericht";
        } else if (hh == 15) {
            //side dish
            typ = "ihr kleiner Happen";
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
     * @param hh
     */
    public String getEssensTypAnfrage(int hh) {
        //breakfast 6-9
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
            //Breakfast
            typ = "breakfast";
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


    public class GetImage extends AsyncTask <String, String, Bitmap> {

        protected Context context;

        GetImage(Context context){
            super();
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            Log.i("GetImage - DoInBackground", "Methode gestartet");

            Bitmap icon = null;
            if (AppStatus.getInstance(context).isOnline()) {
                //Internetverbindung aktiv
                Log.i("GetImage - DoInBackground", "Internetverbindung aktiv");

                try {
                    InputStream in = new java.net.URL(url[0]).openStream();
                    icon = BitmapFactory.decodeStream(in);
                } catch (Exception e ) {
                    Log.e("Error", e.getMessage());
                }
                Log.i("imageDoInBackground", "Beendet");
                return icon;
            } else {
                //Internetverbindung nicht aktiv
                Log.i("imageDoInBackground", "Beendet");
                return icon;
            }


        }

        @Override
        protected void onPostExecute(Bitmap icon) {
            if (icon != null) {
                //Ein Bild ist angekommen

                //Bildbreite so breit wie Auflösung setzen
                //int bildbreite = metrics.widthPixels;
                int bildbreite = _rezepteTableRow.getWidth();

                int breite_alt = icon.getWidth();
                int hoehe_alt = icon.getHeight();

                double skalierung = ((double)hoehe_alt) / ((double)breite_alt);

                int bildhoehe = (int) (bildbreite * skalierung);

                float scaleWidth = ((float) bildbreite) / breite_alt;

                float scaleHeight = ((float) bildhoehe) / hoehe_alt;

                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);

                Log.d("bildhoehe/breite", String.valueOf(bildhoehe) + "|" + String.valueOf(bildbreite));

                Bitmap resizedImage = Bitmap.createBitmap(icon, 0, 0, breite_alt, hoehe_alt, matrix, false);

                _rezepteIcon.setImageBitmap(resizedImage);
                _rezepteIcon.setImageAlpha(90);
            } else {
                //Das Bild ist nicht richtig angekommen
            }

            cancelProgressDialog();
        }

    }

    /**
     * Klasse für den Hintergrund Thread
     */
    public class RecipeAPI extends AsyncTask<String, String, AsyncTaskResult<?>> {

        protected Context context;

        RecipeAPI(Context context){
            super();
            this.context = context;
        }

        @Override
        protected AsyncTaskResult<?> doInBackground(String... params) {
            ArrayList erg;
            String typ = params[0];
            try{
                //Zu untersuchende ID via API herausfinden
                Log.i("doInBackground ", "Methode getRecipe begonnen");
                String jsonResponseRecipeSearch = getRecipe(typ);
                erg = parseRecipe(jsonResponseRecipeSearch);

                Log.i("doInBackground ", "Methode beendet");
                return new AsyncTaskResult(erg);
            } catch (Exception e){
                try {
                    RecipeExample example = new RecipeExample();
                    return new AsyncTaskResult(parseRecipe(example.getExampleJsonString(typ)));
                } catch (Exception f) {
                    Log.i("doInBackground", "Fehler doInBackground: " + e.getMessage());
                    return new AsyncTaskResult(f);
                }

            }

        }

        @Override
        protected void onPostExecute(AsyncTaskResult asyncTaskResult) {
            Log.i("onPostExecute", "Methode begonnen");
            if (asyncTaskResult.isSuccessful()) {
                final List ergebnis = asyncTaskResult.getResult();
                Log.i("onPostExecute","AsynTaskResult ist successfull");

                //Titel setzten
                _rezepteTitel.setText(ergebnis.get(0).toString());

                //Rezeptanleitung setzen
                _rezepteAnleitung.setText(ergebnis.get(2).toString());

                GetImage img = new GetImage(context);
                img.execute((String) ergebnis.get(3));


                _rezepteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = ergebnis.get(4).toString();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });

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
                    tv_ing[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                    tv_ing[i].setText("" + zutaten[i][0] + ":");

                    tv_amount[i].setLayoutParams(params);
                    tv_amount[i].setTextColor(Color.BLACK);
                    tv_amount[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                    tv_amount[i].setText("" + zutaten[i][1] + " " + zutaten[i][2]);

                    tr[i].setPadding(0,5,0,5);
                    tr[i].addView(tv_ing[i]);
                    tr[i].addView(tv_amount[i]);
                    _rezepteZutatenliste.addView(tr[i]);
                }

                _rezepteRezepte.setText("Rezepte:");
                _rezepteZutaten.setText("Zutaten:");

                _rezepteZutatenliste.refreshDrawableState();


            } else {

                Log.i("onPostExecute","AsynTaskResult ist nicht successfull");
                _rezepteTitel.setText("Bitte versuchen sie es später erneut! :)");


            }
            Log.i("onPostExecute", "Methode beendet");
            cancelProgressDialog();
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
            Log.i("getRecipe ","Methode begonnen");
            if (AppStatus.getInstance(context).isOnline()) {
                Log.i("getRecipe ","Internetverbindung vorhanden");

                //1. Request Factory holen
                Log.i("getRecipe ","Request Factory holen begonnen");
                HttpTransport httpTransport = new NetHttpTransport();
                HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
                Log.i("getRecipe ","Request Factory holen beendet");

                //2. Url hinzufügen
                GenericUrl url = new GenericUrl("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/random");
                url.put("limitLicense", "false");
                url.put("number", "1");
                url.put("tags", "" + typ);
                url.put("measure", "metric");


                //3. Request absetzen
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.set("X-Mashape-Key", apiKey);
                HttpRequest request = requestFactory.buildGetRequest(url);
                request.setHeaders(httpHeaders);

                HttpResponse httpResponse = request.execute();

                String jsonResponseString ="";
                try{
                    InputStream inputStream = httpResponse.getContent();
                    jsonResponseString = CharStreams.toString( new InputStreamReader( inputStream, "UTF-8" ) );
                }
                finally {
                    httpResponse.disconnect();
                }


                return jsonResponseString;

            } else {
                Log.i("getRecipe ","Internetverbindung NICHT vorhanden");
                throw new Exception();

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
                Log.i("parseRecipe:", "Beginnt das parsen");
                Log.i("parseRecipe:", "Übergebenes Objekt: " + jsonString);
                //Eigentliches parsen
                JSONObject jsonObject = new JSONObject(jsonString);

                //Wichtigsten Inhalt entnehmen
                JSONArray jsonArray = jsonObject.getJSONArray("recipes");
                JSONObject i = jsonArray.getJSONObject(0);
                Log.i("parseRecipe:", "Recipes geparst");

                //Rezept parsen (aus den verschiedenen Schritten des Rezepts
                JSONArray recipe = i.getJSONArray("analyzedInstructions").getJSONObject(0).getJSONArray("steps");
                String recipeString = "";

                for(int l = 0; l < recipe.length(); l++) {
                    recipeString = recipeString + recipe.getJSONObject(l).getString("step");
                    recipeString = recipeString + "\n";
                }

                Log.i("parseRecipe:", "Servings geparst");

                JSONArray ingredients = i.getJSONArray("extendedIngredients");
                int length = ingredients.length();
                //[][0] = name
                //[][1] = amount
                //[][2] = unit
                String[][] ing = new String[length][3];

                for(int x = 0; x < length; x++){
                    ing [x][0] = ingredients.getJSONObject(x).getString("name");
                    ing [x][1] = ingredients.getJSONObject(x).getString("amount");
                    ing [x][2] = ingredients.getJSONObject(x).getString("unit");
                }

                Log.i("parseRecipe:", "Ingredients geparst");

                String title = i.getString("title");
                String img_url = i.getString("image");
                String recipe_url = i.getString("sourceUrl");

                Log.i("parseRecipe:", "title und img_url gesetzt");

                ArrayList<Object> information = new ArrayList<>();

                Log.i("parseRecipe:", "Parsen fast beendet");
                Log.i("parseRecipe:", "title: " + title);
                Log.i("parseRecipe:", "ing: " + ing.toString());
                Log.i("parseRecipe:", "instructions: " + recipeString);

                information.add(0,title);
                information.add(1, ing);
                information.add(2,recipeString);
                information.add(3, img_url);
                information.add(4, recipe_url);


                return information;

        }
    }
}