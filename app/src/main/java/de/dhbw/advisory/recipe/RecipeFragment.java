package de.dhbw.advisory.recipe;


import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
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

public class RecipeFragment extends Fragment {
    //Ladebildschirm
    private ProgressDialog _alertDialog;

    //UI-Elemente
    protected TextView _rezepteÜberschrift = null;
    protected TextView _rezepteTitel = null;
    protected TextView _rezepteAnleitung = null;
    protected TableLayout _rezepteZutatenliste = null;
    protected ImageView _rezepteIcon = null;
    protected TextView _rezepteZutaten = null;
    protected TextView _rezepteRezepte = null;
    protected TableRow _rezepteTableRow = null;

    //Snackbar
    private Snackbar _snackbar;

    //Apikey
    private String apiKey;

    public DisplayMetrics metrics;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //Der Developer key wird später aus der properties-Datei ausgelesen
        Properties properties = new Properties();

        try {
            //Hier wird der API-Key geladen
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("apikey.properties");
            properties.load(inputStream);
            apiKey = properties.getProperty("spoonacular.apikey");
        } catch (IOException e) {
            //Falls das Fragment nicht geladen werden konnte
            Toast.makeText(context, "Fragment konnte nicht geladen werden", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rezepte, container, false);

        metrics = this.getResources().getDisplayMetrics();

        //Ladebildschirm einfügen
        _alertDialog = new ProgressDialog(getContext());
        _alertDialog.setMessage(getResources().getString(R.string.loader));
        _alertDialog.setCancelable(false);
        _alertDialog.show();

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



        //Hier wird überprüft, ob eine Internetverbindung vorhanden ist, falls diese nicht vorhanden ist wird eine Meldung ausgegeben
        if (AppStatus.getInstance(this.getContext()).isOnline() == false) {
            _snackbar = Snackbar.make(container, "Keine Internetverbindung!", Snackbar.LENGTH_LONG);

            View sbView = _snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            _snackbar.show();
        }

        //API Aufruf starten und Felder aktualisieren
        RecipeAPI mat = new RecipeAPI(this.getContext());
        mat.execute(getEssensTypAnfrage(h));

        return view;
    }


    @Override
    public void onPause(){
        super.onPause();
    }

    /**
     * Diese Methode hidet den Progress Dialog
     */
    public void cancelProgressDialog() {
        _alertDialog.dismiss();
    }


    /**
     * Diese Methode liest die aktuelle Stundenzeit anhand der Systemzeit aus
     *
     * @return gibt die aktuelle Stundenzeit zurück
     */
    public int actualHour(){
        int hh;
        Calendar kalendar = Calendar.getInstance();
        //Umstellung des Zeitformat nur auf die Stunden
        SimpleDateFormat zeitformatH = new SimpleDateFormat("HH");
        String h = zeitformatH.format(kalendar.getTime());
        hh = Integer.parseInt(h);
        return hh;
    }

    /**
     * Diese Methode ermittelt anhand der übergebenen Zeit den Titel des Fragments
     * @param hh gibt die aktuelle Stundenzeit an
     * @return gibt den Essentyp für den Titel an
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
        String r;
        if (typ == "Fehler") {
           r = typ;
        } else
            r= "Hier " + typ + "!";

      return r;
    }

    /**
     * Diese Methode erstellt den Typ des Rezepts für den AI Aufruf
     * @param hh die aktuelle Stundenzeit
     * @return gibt den Typ des Essens zurück (für den API Parameter typ)
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


    /**
     * Diese Klasse übernimmt den Abruf eines Bildes und setzt dieses in den entsprechenden ImageView ein
     */
    public class GetImage extends AsyncTask <String, String, Bitmap> {

        protected Context context;

        GetImage(Context context){
            super();
            this.context = context;
        }

        /**
         * Hier wird das Bild in einem Async Task aufgerufen
         * @param url die URL von der das Bild abgerufen werden soll
         * @return gibt das Bild in Form einer Bitmap zurück
         */
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
                Log.i("GetImage - DoInBackground", "Beendet");
                return icon;
            } else {
                //Internetverbindung nicht aktiv; gibt dann ein leeres Bild zurück
                Log.i("GetImage - DoInBackground", "Internetverbindung nicht aktiv");
                return icon;
            }


        }

        /**
         * Die Methode skaliert das erhaltene Bild auf die komplette Breite des CardViews und setzt dieses anschließend darein
         * @param icon das zu setzende Bild
         */
        @Override
        protected void onPostExecute(Bitmap icon) {
            Log.i("GetImage - onPostExecute", "Methode gestartet");
            if (icon != null) {
                //Es ist ein nicht fehlerhaftes Bild angekommen
                //Bildbreite so breit wie die TableRow (=Breite CardView) setzten
                int bildbreite = _rezepteTableRow.getWidth();

                int breite_alt = icon.getWidth();
                int hoehe_alt = icon.getHeight();

                //Das Seitenverhältnis wird beibehalten
                double skalierung = ((double)hoehe_alt) / ((double)breite_alt);

                int bildhoehe = (int) (bildbreite * skalierung);

                float scaleWidth = ((float) bildbreite) / breite_alt;

                float scaleHeight = ((float) bildhoehe) / hoehe_alt;

                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);

                Bitmap resizedImage = Bitmap.createBitmap(icon, 0, 0, breite_alt, hoehe_alt, matrix, false);

                _rezepteIcon.setImageBitmap(resizedImage);
                _rezepteIcon.setImageAlpha(90);
            } else {
                //Das Bild ist fehlerhaft
            }
            cancelProgressDialog();
        }

    }

    /**
     * In dieser Klasse wird der API Aufruf durchgeführt
     */
    public class RecipeAPI extends AsyncTask<String, String, AsyncTaskResult<?>> {

        protected Context context;

        RecipeAPI(Context context){
            super();
            this.context = context;
        }

        /**
         * Diese Methode ruft die Daten aus der API
         * @param params
         * @return gibt eine ArrayList zurück die alle benötigten Infos enthält
         */
        @Override
        protected AsyncTaskResult<?> doInBackground(String... params) {
            Log.i("Recipe API - doInBackground ", "Methode begonnen");
            ArrayList erg;
            String typ = params[0];
            try{
                //Hier wird der API Aufruf gemacht
                String jsonResponseRecipeSearch = getRecipe(typ);
                //Hier wird geparset
                erg = parseRecipe(jsonResponseRecipeSearch);

                return new AsyncTaskResult(erg);
            } catch (Exception e){
                try {
                    //Keine Internetverbindung, daher wird eine gespeicherte JSON weitergegeben
                    Log.i("Recipe API - doInBackground ", "Keine Internetverbindung");
                    RecipeExample example = new RecipeExample();
                    return new AsyncTaskResult(parseRecipe(example.getExampleJsonString(typ)));
                } catch (Exception f) {
                    Log.i("Recipe API - doInBackground ", "Fehler: " + e.getMessage());
                    return new AsyncTaskResult(f);
                }
            }
        }

        /**
         * Diese Methode setzt die aus der API erhaltenenn Informationen in die Views ein
         * @param asyncTaskResult die Informationen die eingesetzt werden sollen (ArrayList)
         */
        @Override
        protected void onPostExecute(AsyncTaskResult asyncTaskResult) {
            Log.i("onPostExecute", "Methode begonnen");

            if (asyncTaskResult.isSuccessful()) {
                Log.i("onPostExecute","AsynTaskResult ist successfull");

                final List ergebnis = asyncTaskResult.getResult();

                //Titel setzten
                _rezepteTitel.setText(ergebnis.get(0).toString());

                //Rezeptanleitung setzen
                _rezepteAnleitung.setText(ergebnis.get(2).toString());

                //Hier wird das Bild abgerufen
                GetImage img = new GetImage(context);
                img.execute((String) ergebnis.get(3));

                //Mit Klick auf das Rezepte Bild öffnet sich der Link des Rezepts
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

                //Legt ein einheitliches Design für die Zutaten fest
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

                //Titel Zubereitung und Zutaten setzen
                _rezepteRezepte.setText("Zubereitung:");
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
                HttpTransport httpTransport = new NetHttpTransport();
                HttpRequestFactory requestFactory = httpTransport.createRequestFactory();

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
                Log.i("getRecipe ","Internetverbindung nicht vorhanden");
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
            Log.i("parseRecipe", "Methode begonnen");

            //Eigentliches parsen
            JSONObject jsonObject = new JSONObject(jsonString);

            //Wichtigsten Inhalt entnehmen
            JSONArray jsonArray = jsonObject.getJSONArray("recipes");
            JSONObject i = jsonArray.getJSONObject(0);

            //Rezept parsen (aus den verschiedenen Schritten des Rezepts
            JSONArray recipe = i.getJSONArray("analyzedInstructions").getJSONObject(0).getJSONArray("steps");
            String recipeString = "";

            //Hier wird das eigentliche Rezept entnommen
            for(int l = 0; l < recipe.length(); l++) {
                recipeString = recipeString + recipe.getJSONObject(l).getString("step");
                recipeString = recipeString + "\n";
            }

            JSONArray ingredients = i.getJSONArray("extendedIngredients");
            int length = ingredients.length();
            //[][0] = name
            //[][1] = amount
            //[][2] = unit
            String[][] ing = new String[length][3];

            //Zutatenliste erstellen
            for(int x = 0; x < length; x++){
                ing [x][0] = ingredients.getJSONObject(x).getString("name");
                ing [x][1] = ingredients.getJSONObject(x).getString("amount");
                ing [x][2] = ingredients.getJSONObject(x).getString("unit");
            }

            //Titel, Bild und Rezept URL parsen
            String title = i.getString("title");
            String img_url = i.getString("image");
            String recipe_url = i.getString("sourceUrl");

            ArrayList<Object> information = new ArrayList<>();

            //Infos in die ArrayList setzen
            information.add(0,title);
            information.add(1, ing);
            information.add(2,recipeString);
            information.add(3, img_url);
            information.add(4, recipe_url);

            return information;
        }
    }
}