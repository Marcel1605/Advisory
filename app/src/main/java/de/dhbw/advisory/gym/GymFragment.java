package de.dhbw.advisory.gym;


import android.app.Activity;
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

    //Card UI-Elemente initialisieren
    private CardView Gym_Card1;
    private CardView Gym_Card2;
    private CardView Park_Card1;
    private CardView Park_Card2;
    private CardView Stadium_Card1;
    private CardView Stadium_Card2;

    //UI-Elemente für MyPlace initialisieren
    private TextView MyPlace_Header_Content;
    private TextView MyPlace_Adresse_Content;

    //UI-Elemente für Gyms initialisieren
    private TextView Gym_Name_Content1;
    private TextView Gym_Adresse_Content1;
    private TextView Gym_Entfernung_Content1;
    private TextView Gym_Name_Content2;
    private TextView Gym_Adresse_Content2;
    private TextView Gym_Entfernung_Content2;

    //UI-Elemente für Parks initialisieren
    private TextView Park_Name_Content1;
    private TextView Park_Adresse_Content1;
    private TextView Park_Entfernung_Content1;
    private TextView Park_Name_Content2;
    private TextView Park_Adresse_Content2;
    private TextView Park_Entfernung_Content2;

    //UI-Elemente für Stadiums initialisieren
    private TextView Stadium_Name_Content1;
    private TextView Stadium_Adresse_Content1;
    private TextView Stadium_Entfernung_Content1;
    private TextView Stadium_Name_Content2;
    private TextView Stadium_Adresse_Content2;
    private TextView Stadium_Entfernung_Content2;

    //sonstige Variablen initialisieren
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
        alertDialog = new ProgressDialog(getContext());
        this.container = container;
        alertDialog.setMessage(getResources().getString(R.string.loader));
        alertDialog.setCancelable(false);
        alertDialog.show();

        final GPSDetection gps = new GPSDetection(this.getContext());
        gps.setPosition(new Runnable(){

                @Override
                public void run() {
                   GooglePlacesWebserviceAufruf aufruf = new GooglePlacesWebserviceAufruf(gps);
                   aufruf.execute();
                }
        });

        if (gps.GPSaktiv) {
            v = inflater.inflate(R.layout.fragment_gym, container, false);

            //UI-Elemente für MyPlace zuweisen
            MyPlace_Adresse_Content = (TextView) v.findViewById(R.id.MyPlace_Adresse_Content);
            MyPlace_Header_Content = (TextView) v.findViewById(R.id.MyPlace_Header_Content);

            //UI-Elemente für Gyms zuweisen
            Gym_Name_Content1 = (TextView) v.findViewById(R.id.Gym_Name_Content1);
            Gym_Adresse_Content1 = (TextView) v.findViewById(R.id.Gym_Adresse_Content1);
            Gym_Entfernung_Content1 = (TextView) v.findViewById(R.id.Gym_Entfernung_Content1);
            Gym_Name_Content2 = (TextView) v.findViewById(R.id.Gym_Name_Content2);
            Gym_Adresse_Content2 = (TextView) v.findViewById(R.id.Gym_Adresse_Content2);
            Gym_Entfernung_Content2 = (TextView) v.findViewById(R.id.Gym_Entfernung_Content2);

            //UI-Elemente für Parks zuweisen
            Park_Name_Content1 = (TextView) v.findViewById(R.id.Park_Name_Content1);
            Park_Adresse_Content1 = (TextView) v.findViewById(R.id.Park_Adresse_Content1);
            Park_Entfernung_Content1 = (TextView) v.findViewById(R.id.Park_Entfernung_Content1);
            Park_Name_Content2 = (TextView) v.findViewById(R.id.Park_Name_Content2);
            Park_Adresse_Content2 = (TextView) v.findViewById(R.id.Park_Adresse_Content2);
            Park_Entfernung_Content2 = (TextView) v.findViewById(R.id.Park_Entfernung_Content2);

            //UI-Elemente für Stadiums zuweisen
            Stadium_Name_Content1 = (TextView) v.findViewById(R.id.Stadium_Name_Content1);
            Stadium_Adresse_Content1 = (TextView) v.findViewById(R.id.Stadium_Adresse_Content1);
            Stadium_Entfernung_Content1 = (TextView) v.findViewById(R.id.Stadium_Entfernung_Content1);
            Stadium_Name_Content2 = (TextView) v.findViewById(R.id.Stadium_Name_Content2);
            Stadium_Adresse_Content2 = (TextView) v.findViewById(R.id.Stadium_Adresse_Content2);
            Stadium_Entfernung_Content2 = (TextView) v.findViewById(R.id.Stadium_Entfernung_Content2);

            //Cards UI-Elemente zuweisen
            Gym_Card1 = (CardView) v.findViewById(R.id.Gym_Card1);
            Gym_Card2 = (CardView) v.findViewById(R.id.Gym_Card2);
            Park_Card1 = (CardView) v.findViewById(R.id.Park_Card1);
            Park_Card2 = (CardView) v.findViewById(R.id.Park_Card2);
            Stadium_Card1 = (CardView) v.findViewById(R.id.Stadium_Card1);
            Stadium_Card2 = (CardView) v.findViewById(R.id.Stadium_Card2);


            //OnClickListener auf Cards setzen
            Gym_Card1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMapsOrBrowser(Gym_Adresse_Content1.getText().toString());
                }
            });

            Gym_Card2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMapsOrBrowser(Gym_Adresse_Content2.getText().toString());
                }
            });

            Park_Card1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMapsOrBrowser(Park_Adresse_Content1.getText().toString());
                }
            });

            Park_Card2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMapsOrBrowser(Park_Adresse_Content2.getText().toString());
                }
            });

            Stadium_Card1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMapsOrBrowser(Stadium_Adresse_Content1.getText().toString());
                }
            });

            Stadium_Card2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMapsOrBrowser(Stadium_Adresse_Content2.getText().toString());
                }
            });
        } else {

            v = inflater.inflate(R.layout.fragment_gym_nogps, container, false);

            //UI-Elemente für MyPlace zuweisen
            MyPlace_Adresse_Content = (TextView) v.findViewById(R.id.MyPlace_Adresse_Content);
            MyPlace_Header_Content = (TextView) v.findViewById(R.id.MyPlace_Header_Content);

            //UI-Elemente für MyPlace beschreiben
            MyPlace_Header_Content.setText("Fehler in der Ortung");
            MyPlace_Adresse_Content.setText("Bitte schalten Sie Ihre GPS-Ortung ein.");

            alertDialog.hide();
        }

        return v;
    }

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

    class GooglePlacesWebserviceAufruf extends AsyncTask<String, GPSDetection, AsyncTaskResult<ArrayList<String>>> {
        private GPSDetection gps;

        public GooglePlacesWebserviceAufruf(GPSDetection gps) {
            this.gps = gps;
        }

        @Override
        protected AsyncTaskResult<ArrayList<String>> doInBackground(String... params) {
            try {
                //gym-request
                String gym = "gym";
                String jsonResponsePlacesGym = placesAPIAufrufen(gym, gps);
                ArrayList<String> ergGym = parseGooglePlaces(jsonResponsePlacesGym);

                //park-request
                String park = "park";
                String jsonResponsePlacesPark = placesAPIAufrufen(park, gps);
                ArrayList<String> ergPark = parseGooglePlaces(jsonResponsePlacesPark);

                //stadium-request
                String stadium = "stadium";
                String jsonResponsePlacesStadium = placesAPIAufrufen(stadium, gps);
                ArrayList<String> ergStadium = parseGooglePlaces(jsonResponsePlacesStadium);

                List<ArrayList<String>> erg = new ArrayList();
                erg.add(0, ergGym);
                erg.add(1, ergPark);
                erg.add(2, ergStadium);

                return new AsyncTaskResult<ArrayList<String>>(erg);
            } catch(IOException e) {
                return new AsyncTaskResult<ArrayList<String>>(e);
            } catch (JSONException e) {
                return new AsyncTaskResult<ArrayList<String>>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<ArrayList<String>> taskResult) {

            if(taskResult.isSuccessful()) {
                List<ArrayList<String>> ergebnis = taskResult.getResult();

                if (gps.GPSaktiv) {
                    ArrayList<String> gymListe = ergebnis.get(0);
                    Gym_Name_Content1.setText(gymListe.get(0));
                    Gym_Adresse_Content1.setText(gymListe.get(1));
                    Gym_Entfernung_Content1.setText(gymListe.get(2));
                    Gym_Name_Content2.setText(gymListe.get(3));
                    Gym_Adresse_Content2.setText(gymListe.get(4));
                    Gym_Entfernung_Content2.setText(gymListe.get(5));

                    ArrayList<String> parkListe = ergebnis.get(1);
                    Park_Name_Content1.setText(parkListe.get(0));
                    Park_Adresse_Content1.setText(parkListe.get(1));
                    Park_Entfernung_Content1.setText(parkListe.get(2));
                    Park_Name_Content2.setText(parkListe.get(3));
                    Park_Adresse_Content2.setText(parkListe.get(4));
                    Park_Entfernung_Content2.setText(parkListe.get(5));

                    ArrayList<String> stadiumListe = ergebnis.get(2);
                    Stadium_Name_Content1.setText(stadiumListe.get(0));
                    Stadium_Adresse_Content1.setText(stadiumListe.get(1));
                    Stadium_Entfernung_Content1.setText(stadiumListe.get(2));
                    Stadium_Name_Content2.setText(stadiumListe.get(3));
                    Stadium_Adresse_Content2.setText(stadiumListe.get(4));
                    Stadium_Entfernung_Content2.setText(stadiumListe.get(5));

                    MyPlace_Header_Content.setText("Ihre aktuelle Position ist");
                    MyPlace_Adresse_Content.setText(stadiumListe.get(6));
                }

                alertDialog.hide();
                hideSnackbar();
            } else {

                // IOException wird u.a. geworfen falls kein Internet vorhanden ist
                if(taskResult.getError() instanceof IOException) {
                    // lösche den loading dialog komplett
                    alertDialog.dismiss();

                    FitnessFragmentOverview fitnessFragment = new FitnessFragmentOverview();

                    // weise den Startbildschirm an, die Snackbar mit der Meldung für den Benutzer anzuzeigen
                    Bundle bundle = new Bundle();
                    bundle.putInt(FitnessFragmentOverview.SNACKBAR_STATE, FitnessFragmentOverview.SNACKBAR_STATE_SHOW);
                    fitnessFragment.setArguments(bundle);

                    // springe zurück auf den Startbildschirm
                    fragmentChangeListener.onFragmentChangeRequest(fitnessFragment, false);
                } else {
                    // alle anderen exceptions
                    taskResult.getError().printStackTrace();
                    Log.e("GpsKomponente", taskResult.getError().toString());
                    alertDialog.dismiss();
                    showSnackbar();
                }
            }

            gps.stopGPSposition();
        }

        protected String placesAPIAufrufen(String typ, GPSDetection gps) throws IOException {
            //1. Request Factory holen
            Log.i("placesAPIAufrufen: ","Request Factory holen begonnen");
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();

            //2. Url hinzufügen
            GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            url.put("location", + gps.getBreitengrad() + "," + gps.getLaengengrad());
            url.put("rankby", "distance");
            url.put("type", typ);
            url.put("key", apiKey);

            //3. Request absetzen
            HttpRequest request = requestFactory.buildGetRequest(url);

            HttpResponse httpResponse = request.execute();
            String jsonResponseString = httpResponse.parseAsString();

            Log.d("placesAPIAufrufen: ","API Aufruf beendet");
            Log.d("placesAPIAufrufen: ","Erhaltene JSON: " + jsonResponseString);

            return jsonResponseString;
        }

        protected ArrayList parseGooglePlaces(String jsonString) throws JSONException, IOException {
            Log.i("parseGooglePlaces:", "Beginnt das parsen");
            Log.i("parseGooglePlaces:", "Übergebenes Objekt: " + jsonString);
            //Eigentliches parsen
            JSONObject jsonObject = new JSONObject(jsonString);

            //Wichtigsten Inhalt entnehmen
            JSONArray jsonArray1 = jsonObject.getJSONArray("results");
            JSONObject i1 = jsonArray1.getJSONObject(0);

            JSONArray jsonArray2 = jsonObject.getJSONArray("results");
            JSONObject i2 = jsonArray2.getJSONObject(1);

            //name
            String name1 = i1.getString("name");
            String name2 = i2.getString("name");

            //location1 parsen und lat und lng selektieren
            JSONObject geometry1 = i1.getJSONObject("geometry");
            JSONObject location1 = geometry1.getJSONObject("location");
            Double location1_lat = location1.getDouble("lat");
            Double location1_lng = location1.getDouble("lng");

            //Entfernung für location1 aufrufen und parsen
            String ergebnis1 = distanceAPIAufrufen(location1_lat, location1_lng);
            JSONObject distanceObject1 = new JSONObject(ergebnis1);
            JSONArray distanceArray1 = distanceObject1.getJSONArray("rows");
            JSONObject distanceObject1_changed = distanceArray1.getJSONObject(0);
            JSONArray distanceArray1_changend = distanceObject1_changed.getJSONArray("elements");
            distanceObject1_changed = distanceArray1_changend.getJSONObject(0);
            distanceObject1_changed = distanceObject1_changed.getJSONObject("distance");
            String distance1 = distanceObject1_changed.getString("text");

            //location2 parsen und lat und lng selektieren
            JSONObject geometry2 = i2.getJSONObject("geometry");
            JSONObject location2 = geometry2.getJSONObject("location");
            Double location2_lat = location2.getDouble("lat");
            Double location2_lng = location2.getDouble("lng");

            //Entfernung für location2 aufrufen und parsen
            String ergebnis2 = distanceAPIAufrufen(location2_lat, location2_lng);
            JSONObject distanceObject2 = new JSONObject(ergebnis2);
            JSONArray distanceArray2 = distanceObject2.getJSONArray("rows");
            JSONObject distanceObject2_changed = distanceArray2.getJSONObject(0);
            JSONArray distanceArray2_changed = distanceObject2_changed.getJSONArray("elements");
            distanceObject2_changed = distanceArray2_changed.getJSONObject(0);
            distanceObject2_changed = distanceObject2_changed.getJSONObject("distance");
            String distance2 = distanceObject2_changed.getString("text");

            //destination_address1 selektieren und parsen
            JSONArray destination_address1_array = distanceObject1.getJSONArray("destination_addresses");
            String destination_address1 = (String) destination_address1_array.get(0);
            JSONArray origin_address_array = distanceObject1.getJSONArray("origin_addresses");
            String origin_address = (String) origin_address_array.get(0);


            //destination_address2 selektieren und parsen
            JSONArray destination_address2_array = distanceObject2.getJSONArray("destination_addresses");
            String destination_address2 = (String) destination_address2_array.get(0);

            //Informationen speichern
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

        /**
         *
         * Die Methode macht die HTTP Anfrage an die API
         *
         * @param lat
         * @param lng
         * @return gibt den HTTP Request zurück
         * @throws Exception
         */
        protected String distanceAPIAufrufen(Double lat, Double lng) throws IOException {
            Log.i("distanceAPIAufrufen: ","Methode begonnen");
            Log.i("distanceAPIAufrufen: ","Übergebene Werte: " + lat + " und " + lng );

            //1. Request Factory holen
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();

            //2. Url hinzufügen
            GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/distancematrix/json?");
            url.put("destinations", + lat + "," + lng);
            url.put("origins", + gps.getBreitengrad() + "," + gps.getLaengengrad());
            url.put("units", "metric");
            url.put("key", apiKey);

            //3. Request absetzen
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            String jsonResponseString = httpResponse.parseAsString();

            Log.i("distanceAPIAufrufen: ","API Aufruf beendet");
            Log.i("distanceAPIAufrufen: ","Erhaltene JSON: " + jsonResponseString);

            return jsonResponseString;
        }
    }

    public void showSnackbar() {
        snackbar = Snackbar.make(container, "Keine Orte in der Umgebung", Snackbar.LENGTH_LONG);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void hideSnackbar() {
        if(this.snackbar != null) {
            this.snackbar.dismiss();
        }
    }
}


