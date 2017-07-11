package de.dhbw.advisory.gym;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import de.dhbw.advisory.R;
import de.dhbw.advisory.common.AsyncTaskResult;
import de.dhbw.advisory.common.FragmentChangeListener;
import de.dhbw.advisory.fitness.FitnessFragmentOverview;

public class GymFragment extends Fragment {

    // Card UI-Elemente initialisieren
    private CardView gymCard1;
    private CardView gymCard2;
    private CardView parkCard1;
    private CardView parkCard2;
    private CardView stadiumCard1;
    private CardView stadiumCard2;

    // UI-Elemente für MyPlace initialisieren
    private TextView myPlaceHeaderContent;
    private TextView myPlaceAdresseContent;

    // UI-Elemente für Gyms initialisieren
    private TextView gymNameContent1;
    private TextView gymAdresseContent1;
    private TextView gymEntfernungContent1;
    private TextView gymNameContent2;
    private TextView gymAdresseContent2;
    private TextView gymEntfernungContent2;

    // UI-Elemente für Parks initialisieren
    private TextView parkNameContent1;
    private TextView parkAdresseContent1;
    private TextView parkEntfernungContent1;
    private TextView parkNameContent2;
    private TextView parkAdresseContent2;
    private TextView parkEntfernungContent2;

    // UI-Elemente für Stadiums initialisieren
    private TextView stadiumNameContent1;
    private TextView stadiumAdresseContent1;
    private TextView stadiumEntfernungContent1;
    private TextView stadiumNameContent2;
    private TextView stadiumAdresseContent2;
    private TextView stadiumEntfernungContent2;

    // sonstige Variablen initialisieren
    private ProgressDialog alertDialog;
    private String apiKey;
    private FragmentChangeListener fragmentChangeListener;
    private ViewGroup container;
    private Snackbar snackbar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Der developer key wird später aus der properties-Datei ausgelesen
        Properties properties = new Properties();
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("apikey.properties");
            properties.load(inputStream);
            apiKey = properties.getProperty("maps.apikey");
        } catch (IOException e) {
            Toast.makeText(context, "Fragment konnte nicht geladen werden", Toast.LENGTH_SHORT).show();
        }

        try {
            fragmentChangeListener = (FragmentChangeListener) context;
        } catch (ClassCastException e) {
            throw new RuntimeException("Activity must implement FragmentChangeListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = null;

        // Lade-Anzeige erstellen und anzeigen, bis sie nach Abschluss der API-Aufrufe beendet wird
        alertDialog = new ProgressDialog(getContext());
        this.container = container;
        alertDialog.setMessage(getResources().getString(R.string.loader));
        alertDialog.setCancelable(false);
        alertDialog.show();

        // aktuelle GPS-Position bestimmen und speichern
        final GPSDetection gps = new GPSDetection(this.getContext());
        gps.setPosition(new Runnable(){

                @Override
                public void run() {
                   GooglePlacesWebserviceAufruf aufruf = new GooglePlacesWebserviceAufruf(gps);
                   aufruf.execute();
                }
        });

        // falls die GPS-Ortung aktiv ist, wird das layout "fragment_gym" geladen und zugewiesen
        if (gps.GPSaktiv) {
            // View mit layout "fragment_gym" erstellen
            v = inflater.inflate(R.layout.fragment_gym, container, false);

            // UI-Elemente für MyPlace zuweisen
            myPlaceAdresseContent = (TextView) v.findViewById(R.id.MyPlace_Adresse_Content);
            myPlaceHeaderContent = (TextView) v.findViewById(R.id.MyPlace_Header_Content);

            // UI-Elemente für Gyms zuweisen
            gymNameContent1 = (TextView) v.findViewById(R.id.Gym_Name_Content1);
            gymAdresseContent1 = (TextView) v.findViewById(R.id.Gym_Adresse_Content1);
            gymEntfernungContent1 = (TextView) v.findViewById(R.id.Gym_Entfernung_Content1);
            gymNameContent2 = (TextView) v.findViewById(R.id.Gym_Name_Content2);
            gymAdresseContent2 = (TextView) v.findViewById(R.id.Gym_Adresse_Content2);
            gymEntfernungContent2 = (TextView) v.findViewById(R.id.Gym_Entfernung_Content2);

            // UI-Elemente für Parks zuweisen
            parkNameContent1 = (TextView) v.findViewById(R.id.Park_Name_Content1);
            parkAdresseContent1 = (TextView) v.findViewById(R.id.Park_Adresse_Content1);
            parkEntfernungContent1 = (TextView) v.findViewById(R.id.Park_Entfernung_Content1);
            parkNameContent2 = (TextView) v.findViewById(R.id.Park_Name_Content2);
            parkAdresseContent2 = (TextView) v.findViewById(R.id.Park_Adresse_Content2);
            parkEntfernungContent2 = (TextView) v.findViewById(R.id.Park_Entfernung_Content2);

            // UI-Elemente für Stadiums zuweisen
            stadiumNameContent1 = (TextView) v.findViewById(R.id.Stadium_Name_Content1);
            stadiumAdresseContent1 = (TextView) v.findViewById(R.id.Stadium_Adresse_Content1);
            stadiumEntfernungContent1 = (TextView) v.findViewById(R.id.Stadium_Entfernung_Content1);
            stadiumNameContent2 = (TextView) v.findViewById(R.id.Stadium_Name_Content2);
            stadiumAdresseContent2 = (TextView) v.findViewById(R.id.Stadium_Adresse_Content2);
            stadiumEntfernungContent2 = (TextView) v.findViewById(R.id.Stadium_Entfernung_Content2);

            // Cards UI-Elemente zuweisen
            gymCard1 = (CardView) v.findViewById(R.id.Gym_Card1);
            gymCard2 = (CardView) v.findViewById(R.id.Gym_Card2);
            parkCard1 = (CardView) v.findViewById(R.id.Park_Card1);
            parkCard2 = (CardView) v.findViewById(R.id.Park_Card2);
            stadiumCard1 = (CardView) v.findViewById(R.id.Stadium_Card1);
            stadiumCard2 = (CardView) v.findViewById(R.id.Stadium_Card2);


            // OnClickListener auf Cards setzen
            gymCard1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMapsOrBrowser(gymAdresseContent1.getText().toString());
                }
            });
            gymCard2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMapsOrBrowser(gymAdresseContent2.getText().toString());
                }
            });
            parkCard1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMapsOrBrowser(parkAdresseContent1.getText().toString());
                }
            });
            parkCard2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMapsOrBrowser(parkAdresseContent2.getText().toString());
                }
            });
            stadiumCard1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMapsOrBrowser(stadiumAdresseContent1.getText().toString());
                }
            });
            stadiumCard2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMapsOrBrowser(stadiumAdresseContent2.getText().toString());
                }
            });
        } else {
            // View mit layout "fragment_gym_nogps" erstellen, wenn kein GPS eingeschaltet ist
            v = inflater.inflate(R.layout.fragment_gym_nogps, container, false);

            // UI-Elemente für MyPlace zuweisen
            myPlaceAdresseContent = (TextView) v.findViewById(R.id.MyPlace_Adresse_Content);
            myPlaceHeaderContent = (TextView) v.findViewById(R.id.MyPlace_Header_Content);

            // UI-Elemente für MyPlace beschreiben
            myPlaceHeaderContent.setText("Fehler in der Ortung");
            myPlaceAdresseContent.setText("Bitte schalten Sie Ihre GPS-Ortung ein.");

            // Lade-Anzeige verstecken
            alertDialog.hide();
        }
        return v;
    }

    // Übergabe des Intent an die google maps app. Wenn keine google maps app auf dem Device ist,
    // wird der intent an einen Browser übergeben
    private void openMapsOrBrowser(String place) {
        try {
            Uri gmmIntentUri = Uri.parse("https://www.google.de/maps/place/" + place);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } catch (ActivityNotFoundException omg) {
            String url = "https://www.google.de/maps/place/" + place;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(url));
            startActivity(browserIntent);
        }
    }

    //Klasse zum Aufruf der GooglePlacesApi
    class GooglePlacesWebserviceAufruf extends AsyncTask<String, GPSDetection, AsyncTaskResult<ArrayList<String>>> {
        // initialisierung der gps-Variable
        private GPSDetection gps;

        // Definition des Konstruktors und Übergabe eines gps-Objekts
        public GooglePlacesWebserviceAufruf(GPSDetection gps) {
            this.gps = gps;
        }

        @Override
        protected AsyncTaskResult<ArrayList<String>> doInBackground(String... params) {
            try {
                // gym-request an GooglePlacesAPI absenden und parsen
                String gym = "gym";
                String jsonResponsePlacesGym = placesAPIAufrufen(gym, gps);
                ArrayList<String> ergGym = parseGooglePlaces(jsonResponsePlacesGym);

                // park-request an GooglePlacesAPI absenden und parsen
                String park = "park";
                String jsonResponsePlacesPark = placesAPIAufrufen(park, gps);
                ArrayList<String> ergPark = parseGooglePlaces(jsonResponsePlacesPark);

                // stadium-request an GooglePlacesAPI absenden und parsen
                String stadium = "stadium";
                String jsonResponsePlacesStadium = placesAPIAufrufen(stadium, gps);
                ArrayList<String> ergStadium = parseGooglePlaces(jsonResponsePlacesStadium);

                // geparste Antworten in einer Arrayliste speichern und übergeben
                List<ArrayList<String>> erg = new ArrayList();
                erg.add(0, ergGym);
                erg.add(1, ergPark);
                erg.add(2, ergStadium);
                return new AsyncTaskResult<ArrayList<String>>(erg);

            // allgemeine Fehlerbehandlung
            } catch(IOException e) {
                return new AsyncTaskResult<ArrayList<String>>(e);
            } catch (JSONException e) {
                return new AsyncTaskResult<ArrayList<String>>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<ArrayList<String>> taskResult) {
            if(taskResult.isSuccessful()) {
                // Wenn der vorangegangene AsynchTask erfolgreich abgearbeitetet wurde,
                // wird dessen Ergebnis in der lokalen Variable ergebnis gespeiochert
                List<ArrayList<String>> ergebnis = taskResult.getResult();

                // falls aktiv ist, werden die entsprechenden Felder mit den Ergebnissen aus den API
                // Abfragen befüllt und der Ladebildschirm beendet
                if (gps.GPSaktiv) {

                    // Ergebnis-Array der Gym-Daten zuweisen
                    ArrayList<String> gymListe = ergebnis.get(0);
                    gymNameContent1.setText(gymListe.get(0));
                    gymAdresseContent1.setText(gymListe.get(1));
                    gymEntfernungContent1.setText(gymListe.get(2));
                    gymNameContent2.setText(gymListe.get(3));
                    gymAdresseContent2.setText(gymListe.get(4));
                    gymEntfernungContent2.setText(gymListe.get(5));

                    // Ergebnis-Array der Park-Daten zuweisen
                    ArrayList<String> parkListe = ergebnis.get(1);
                    parkNameContent1.setText(parkListe.get(0));
                    parkAdresseContent1.setText(parkListe.get(1));
                    parkEntfernungContent1.setText(parkListe.get(2));
                    parkNameContent2.setText(parkListe.get(3));
                    parkAdresseContent2.setText(parkListe.get(4));
                    parkEntfernungContent2.setText(parkListe.get(5));

                    // Ergebnis-Array der Stadium-Daten zuweisen
                    ArrayList<String> stadiumListe = ergebnis.get(2);
                    stadiumNameContent1.setText(stadiumListe.get(0));
                    stadiumAdresseContent1.setText(stadiumListe.get(1));
                    stadiumEntfernungContent1.setText(stadiumListe.get(2));
                    stadiumNameContent2.setText(stadiumListe.get(3));
                    stadiumAdresseContent2.setText(stadiumListe.get(4));
                    stadiumEntfernungContent2.setText(stadiumListe.get(5));

                    // Ergebnis-Array der Header-Daten zuweisen
                    myPlaceHeaderContent.setText("Ihre aktuelle Position ist");
                    myPlaceAdresseContent.setText(stadiumListe.get(6));
                }

                // Ladedialog und Snackbar verstecken
                alertDialog.hide();
                hideSnackbar();
            }

            // Wenn GPS (noch immer) ausgeschaltet ist, werden entsprechende Aktionen ausgeführt
            else {

                // IOException wird u.a. geworfen falls kein Internet vorhanden ist
                if(taskResult.getError() instanceof IOException) {

                    // lösche den loading dialog komplett
                    alertDialog.dismiss();

                    // Weiterleitung an das fitnessFragment als Startseite erstellen
                    FitnessFragmentOverview fitnessFragment = new FitnessFragmentOverview();

                    // weise den Startbildschirm an, die Snackbar mit der Meldung für den Benutzer anzuzeigen
                    Bundle bundle = new Bundle();
                    bundle.putInt(FitnessFragmentOverview.SNACKBAR_STATE, FitnessFragmentOverview.SNACKBAR_STATE_SHOW);
                    fitnessFragment.setArguments(bundle);

                    // springe zurück auf den Startbildschirm
                    fragmentChangeListener.onFragmentChangeRequest(fitnessFragment, false);
                }

                // alle anderen exceptions abfangen
                else {
                    taskResult.getError().printStackTrace();
                    Log.e("GpsKomponente", taskResult.getError().toString());
                    alertDialog.dismiss();
                    showSnackbar();
                }
            }

            // GPS-Bestimmung beendet, um Akku zu sparen
            gps.stopGPSposition();
        }

        // Webaufruf der API bauen, absetzen und das Ergebnis zurückgeben
        protected String placesAPIAufrufen(String typ, GPSDetection gps) throws IOException {

            // Request Factory holen
            Log.i("placesAPIAufrufen: ","Request Factory holen begonnen");
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();

            // Url hinzufügen
            GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            url.put("location", + gps.getBreitengrad() + "," + gps.getLaengengrad());
            url.put("rankby", "distance");
            url.put("type", typ);
            url.put("key", apiKey);

            // Request absetzen
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            String jsonResponseString = httpResponse.parseAsString();
            Log.d("placesAPIAufrufen: ","API Aufruf beendet");
            Log.d("placesAPIAufrufen: ","Erhaltene JSON: " + jsonResponseString);

            // Ergebnis zurückgeben
            return jsonResponseString;
        }

        // Webaufruf der API im Ergebnis parsen und in einem Ergebnis-Array für die spätere
        // Zuweisung speichern
        protected ArrayList parseGooglePlaces(String jsonString) throws JSONException, IOException {
            Log.i("parseGooglePlaces:", "Beginnt das parsen");
            Log.i("parseGooglePlaces:", "Übergebenes Objekt: " + jsonString);

            // Eigentliches parsen beginnen
            JSONObject jsonObject = new JSONObject(jsonString);

            // Wichtigsten Inhalt entnehmen
            JSONArray jsonArray1 = jsonObject.getJSONArray("results");
            JSONObject i1 = jsonArray1.getJSONObject(0);
            JSONArray jsonArray2 = jsonObject.getJSONArray("results");
            JSONObject i2 = jsonArray2.getJSONObject(1);

            // Zwischenschritt um einfacher an weitere Daten zu gelangen
            String name1 = i1.getString("name");
            String name2 = i2.getString("name");

            // location1 parsen und lat und lng selektieren
            JSONObject geometry1 = i1.getJSONObject("geometry");
            JSONObject location1 = geometry1.getJSONObject("location");
            Double location1_lat = location1.getDouble("lat");
            Double location1_lng = location1.getDouble("lng");

            // Entfernung für location1 ufrufen, indem die distanceAPI aufgerufen wird,
            // und anschließendes parsen des Ergebnisses
            String ergebnis1 = distanceAPIAufrufen(location1_lat, location1_lng);
            JSONObject distanceObject1 = new JSONObject(ergebnis1);
            JSONArray distanceArray1 = distanceObject1.getJSONArray("rows");
            JSONObject distanceObject1_changed = distanceArray1.getJSONObject(0);
            JSONArray distanceArray1_changend = distanceObject1_changed.getJSONArray("elements");
            distanceObject1_changed = distanceArray1_changend.getJSONObject(0);
            distanceObject1_changed = distanceObject1_changed.getJSONObject("distance");
            String distance1 = distanceObject1_changed.getString("text");

            // location2 parsen und lat und lng selektieren
            JSONObject geometry2 = i2.getJSONObject("geometry");
            JSONObject location2 = geometry2.getJSONObject("location");
            Double location2_lat = location2.getDouble("lat");
            Double location2_lng = location2.getDouble("lng");

            // Entfernung für location2 aufrufen, indem die distanceAPI aufgerufen wird,
            // und anschließendes parsen des Ergebnisses
            String ergebnis2 = distanceAPIAufrufen(location2_lat, location2_lng);
            JSONObject distanceObject2 = new JSONObject(ergebnis2);
            JSONArray distanceArray2 = distanceObject2.getJSONArray("rows");
            JSONObject distanceObject2_changed = distanceArray2.getJSONObject(0);
            JSONArray distanceArray2_changed = distanceObject2_changed.getJSONArray("elements");
            distanceObject2_changed = distanceArray2_changed.getJSONObject(0);
            distanceObject2_changed = distanceObject2_changed.getJSONObject("distance");
            String distance2 = distanceObject2_changed.getString("text");

            // destination_address1 selektieren und parsen
            JSONArray destination_address1_array = distanceObject1.getJSONArray("destination_addresses");
            String destination_address1 = (String) destination_address1_array.get(0);
            JSONArray origin_address_array = distanceObject1.getJSONArray("origin_addresses");
            String origin_address = (String) origin_address_array.get(0);


            // destination_address2 selektieren und parsen
            JSONArray destination_address2_array = distanceObject2.getJSONArray("destination_addresses");
            String destination_address2 = (String) destination_address2_array.get(0);

            // Informationen im Ergebnis-Array speichern und zurückgeben
            ArrayList<String> information = new ArrayList<>();
            information.add(0, name1);
            information.add(1, destination_address1);
            information.add(2, distance1);
            information.add(3, name2);
            information.add(4, destination_address2);
            information.add(5, distance2);
            information.add(6, origin_address);
            Log.i("parseGooglePlaces:", "Parsen beendet: " + information);
            return information;
        }

        // Aufruf der distanceAPI um die Entfernungen zwischen dem eigenen Standort
        // un den Ergebnis-Standorten abzurufen
        protected String distanceAPIAufrufen(Double lat, Double lng) throws IOException {
            Log.i("distanceAPIAufrufen: ","Methode begonnen");
            Log.i("distanceAPIAufrufen: ","Übergebene Werte: " + lat + " und " + lng );

            // Request Factory holen
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();

            // Url hinzufügen
            GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/distancematrix/json?");
            url.put("destinations", + lat + "," + lng);
            url.put("origins", + gps.getBreitengrad() + "," + gps.getLaengengrad());
            url.put("units", "metric");
            url.put("key", apiKey);

            // Request absetzen, ergebnis speichern und zurückgeben
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            String jsonResponseString = httpResponse.parseAsString();
            Log.i("distanceAPIAufrufen: ","API Aufruf beendet");
            Log.i("distanceAPIAufrufen: ","Erhaltene JSON: " + jsonResponseString);
            return jsonResponseString;
        }
    }

    // Methode um die Snackbar anzuzeigen
    public void showSnackbar() {
        snackbar = Snackbar.make(container, "Keine Orte in der Umgebung", Snackbar.LENGTH_LONG);

        // Farbe des Aktion Button verändern
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    // Methode um die Snackbar zu verstecken
    public void hideSnackbar() {
        if(this.snackbar != null) {
            this.snackbar.dismiss();
        }
    }
}


