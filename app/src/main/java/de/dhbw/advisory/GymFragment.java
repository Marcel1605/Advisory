package de.dhbw.advisory;


import android.app.ProgressDialog;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.ArrayList;


/**
 * Created by Magnus on 30.05.17.
 */


public class GymFragment extends Fragment {

    //Card UI-Elemente initialisieren
    static CardView Gym_Card1;
    static CardView Gym_Card2;
    static CardView Park_Card1;
    static CardView Park_Card2;
    static CardView Stadium_Card1;
    static CardView Stadium_Card2;


    //UI-Elemente für MyPlace initialisieren
    static TextView MyPlace_Header_Content;
    static TextView MyPlace_Adresse_Content;


    //UI-Elemente für Gyms initialisieren
    static TextView Gym_Name_Content1;
    static TextView Gym_Adresse_Content1;
    static TextView Gym_Entfernung_Content1;
    static TextView Gym_Name_Content2;
    static TextView Gym_Adresse_Content2;
    static TextView Gym_Entfernung_Content2;

    //UI-Elemente für Parks initialisieren
    static TextView Park_Name_Content1;
    static TextView Park_Adresse_Content1;
    static TextView Park_Entfernung_Content1;
    static TextView Park_Name_Content2;
    static TextView Park_Adresse_Content2;
    static TextView Park_Entfernung_Content2;

    //UI-Elemente für Stadiums initialisieren
    static TextView Stadium_Name_Content1;
    static TextView Stadium_Adresse_Content1;
    static TextView Stadium_Entfernung_Content1;
    static TextView Stadium_Name_Content2;
    static TextView Stadium_Adresse_Content2;
    static TextView Stadium_Entfernung_Content2;

    //sonstige Variablen initialisieren
    private static ProgressDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GPSBestimmung gps = new GPSBestimmung(this.getContext());
        gps.setPosition();
        View v = null;

        //Warten-Dialog erstellen und anzeigen
        alertDialog = new ProgressDialog(getContext());
        alertDialog.setMessage(getResources().getString(R.string.loader));
        alertDialog.setCancelable(false);
        alertDialog.show();

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
            Park_Card1  = (CardView) v.findViewById(R.id.Park_Card1);
            Park_Card2 = (CardView) v.findViewById(R.id.Park_Card2);
            Stadium_Card1 = (CardView) v.findViewById(R.id.Stadium_Card1);
            Stadium_Card2 = (CardView) v.findViewById(R.id.Stadium_Card2);

            //OnClickListener auf Cards setzen
              Gym_Card1.setOnClickListener(
                      new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              try{
                                  Uri gmmIntentUri = Uri.parse("https://www.google.de/maps/place/" + Gym_Adresse_Content1.getText());
                                  Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                  mapIntent.setPackage("com.google.android.apps.maps");
                                  startActivity(mapIntent);
                              } catch (ActivityNotFoundException omg){
                                  String url = "https://www.google.de/maps/place/" + Gym_Adresse_Content1.getText();
                                  Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                  browserIntent.setData(Uri.parse(url));
                                  startActivity(browserIntent);
                              }
                          }
                      }
              );
             Gym_Card2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Uri gmmIntentUri = Uri.parse("https://www.google.de/maps/place/" + Gym_Adresse_Content2.getText());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    } catch (ActivityNotFoundException omg){
                        String url = "https://www.google.de/maps/place/" + Gym_Adresse_Content2.getText();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(url));
                        startActivity(browserIntent);
                    }
                }
             });
              Park_Card1.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      try{
                      Uri gmmIntentUri = Uri.parse("https://www.google.de/maps/place/" + Park_Adresse_Content1.getText());
                      Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                      mapIntent.setPackage("com.google.android.apps.maps");
                      startActivity(mapIntent);
                      } catch (ActivityNotFoundException omg){
                          String url = "https://www.google.de/maps/place/" + Park_Adresse_Content1.getText();
                          Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                          browserIntent.setData(Uri.parse(url));
                          startActivity(browserIntent);
                      }
                  }
              });
              Park_Card2.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      try{
                          Uri gmmIntentUri = Uri.parse("https://www.google.de/maps/place/" + Park_Adresse_Content2.getText());
                          Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                          mapIntent.setPackage("com.google.android.apps.maps");
                          startActivity(mapIntent);
                      } catch (ActivityNotFoundException omg){
                          String url = "https://www.google.de/maps/place/" + Park_Adresse_Content2.getText();
                          Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                          browserIntent.setData(Uri.parse(url));
                          startActivity(browserIntent);
                      }
                  }
              });
              Stadium_Card1.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      try{
                          Uri gmmIntentUri = Uri.parse("https://www.google.de/maps/place/" + Stadium_Adresse_Content1.getText());
                          Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                          mapIntent.setPackage("com.google.android.apps.maps");
                          startActivity(mapIntent);
                      } catch (ActivityNotFoundException omg){
                          String url = "https://www.google.de/maps/place/" + Stadium_Adresse_Content1.getText();
                          Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                          browserIntent.setData(Uri.parse(url));
                          startActivity(browserIntent);
                      }
                  }
              });
              Stadium_Card2.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      try{
                          Uri gmmIntentUri = Uri.parse("https://www.google.de/maps/place/" + Stadium_Adresse_Content2.getText());
                          Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                          mapIntent.setPackage("com.google.android.apps.maps");
                          startActivity(mapIntent);
                      } catch (ActivityNotFoundException omg){
                          String url = "https://www.google.de/maps/place/" + Stadium_Adresse_Content2.getText();
                          Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                          browserIntent.setData(Uri.parse(url));
                          startActivity(browserIntent);
                      }
                  }
              });
        } else {
            v = inflater.inflate(R.layout.fragment_gym_nogps, container, false);

            //UI-Elemente für MyPlace zuweisen
            MyPlace_Adresse_Content = (TextView) v.findViewById(R.id.MyPlace_Adresse_Content);
            MyPlace_Header_Content = (TextView) v.findViewById(R.id.MyPlace_Header_Content);
        }

        GooglePlacesWebserviceAufruf aufruf = new GooglePlacesWebserviceAufruf(gps);
        aufruf.execute();


        return v;
    }

    @Override
    public void onPause(){
       //The system calls this method as the first indication that the user is leaving the fragment (though it does not always mean the fragment is being destroyed). This is usually where you should commit any changes that should be persisted beyond the current user session (because the user might not come back).
       Log.e("DEBUG", "OnPause of RezepteFragment");
        alertDialog.hide();
        super.onPause();
    }

    public static class GooglePlacesWebserviceAufruf extends AsyncTask<String, GPSBestimmung, ArrayList> {

        GPSBestimmung gps;
        ArrayList erg;

        GooglePlacesWebserviceAufruf(GPSBestimmung gps) {
            this.gps = gps;
        }


        @Override
        protected ArrayList doInBackground(String... params) {
            try {
                //gym-request
                String gym = "gym";
                Log.i("GooglePlacesWebserviceAufruf: ","Request für " + gym + " starten");
                String jsonResponsePlacesGym = placesAPIAufrufen(gym, gps);
                Log.i("GooglePlacesWebserviceAufruf: ","Request für " + gym + "  parsen");
                ArrayList ergGym = parseGooglePlaces(jsonResponsePlacesGym);
                Log.i("GooglePlacesWebserviceAufruf: ","Request  für " + gym + " geparst und Ergebnis: " + ergGym);

                //park-request
                String park = "park";
                Log.i("GooglePlacesWebserviceAufruf: ","Request für " + park + " starten");
                String jsonResponsePlacesPark = placesAPIAufrufen(park, gps);
                Log.i("GooglePlacesWebserviceAufruf: ","Request für " + park + " parsen");
                ArrayList ergPark = parseGooglePlaces(jsonResponsePlacesPark);
                Log.i("GooglePlacesWebserviceAufruf: ","Request  für " + park + " geparst und Ergebnis: " + ergPark);

                //stadium-request
                String stadium = "stadium";
                Log.i("GooglePlacesWebserviceAufruf: ","Request für " + stadium + " starten");
                String jsonResponsePlacesStadium = placesAPIAufrufen(stadium, gps);
                Log.i("GooglePlacesWebserviceAufruf: ","Request für " + stadium + " parsen");
                ArrayList ergStadium = parseGooglePlaces(jsonResponsePlacesStadium);
                Log.i("GooglePlacesWebserviceAufruf: ","Request  für " + stadium + " geparst und Ergebnis: " + ergStadium);

                ArrayList erg = new ArrayList();
                erg.add(0, ergGym);
                erg.add(1, ergPark);
                erg.add(2, ergStadium);

                return erg;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return erg;
        }

        @Override
        protected void onPostExecute(ArrayList ergebnis) {

            try{

                Log.i("onPostExecute: ","Methode begonnen " + ergebnis);

                if (gps.GPSaktiv)
                {
                    ArrayList gymListe = (ArrayList) ergebnis.get(0);
                    Gym_Name_Content1.setText( (String) gymListe.get(0));
                    Gym_Adresse_Content1.setText((String) gymListe.get(1));
                    Gym_Entfernung_Content1.setText((String) gymListe.get(2));
                    Gym_Name_Content2.setText( (String) gymListe.get(3));
                    Gym_Adresse_Content2.setText((String) gymListe.get(4));
                    Gym_Entfernung_Content2.setText((String) gymListe.get(5));

                    ArrayList parkListe = (ArrayList) ergebnis.get(1);
                    Park_Name_Content1.setText( (String) parkListe.get(0));
                    Park_Adresse_Content1.setText((String) parkListe.get(1));
                    Park_Entfernung_Content1.setText((String) parkListe.get(2));
                    Park_Name_Content2.setText( (String) parkListe.get(3));
                    Park_Adresse_Content2.setText((String) parkListe.get(4));
                    Park_Entfernung_Content2.setText((String) parkListe.get(5));

                    ArrayList stadiumListe = (ArrayList) ergebnis.get(2);
                    Stadium_Name_Content1.setText( (String) stadiumListe.get(0));
                    Stadium_Adresse_Content1.setText((String) stadiumListe.get(1));
                    Stadium_Entfernung_Content1.setText((String) stadiumListe.get(2));
                    Stadium_Name_Content2.setText( (String) stadiumListe.get(3));
                    Stadium_Adresse_Content2.setText((String) stadiumListe.get(4));
                    Stadium_Entfernung_Content2.setText((String) stadiumListe.get(5));

                    MyPlace_Header_Content.setText("Ihre aktuelle Position ist");
                    MyPlace_Adresse_Content.setText( (String) stadiumListe.get(6));
                } else {
                    MyPlace_Header_Content.setText("Fehler in der Ortung");
                    MyPlace_Adresse_Content.setText("Bitte schalten Sie Ihre GPS-Ortung ein.");
                }




                alertDialog.hide();
                gps.stopGPSposition();
                super.onPostExecute(ergebnis);


            } catch (Exception e) {
                Log.i("Fehler onPostExecute","" + e);
                alertDialog.hide();
            }
        }


        /**
         *
         * Die Methode macht die HTTP Anfrage an die API
         *
         * @param typ
         * @param gps
         * @return gibt den HTTP Request zurück
         * @throws Exception
         */
        protected String placesAPIAufrufen(String typ, GPSBestimmung gps) throws Exception {
            try {
                Log.i("placesAPIAufrufen: ","Methode begonnen");
                Log.i("placesAPIAufrufen: ","Übergebener Wert: " + typ);
                Log.i("placesAPIAufrufen: ","Starte API Aufruf");

                //1. Request Factory holen
                Log.i("placesAPIAufrufen: ","Request Factory holen begonnen");
                HttpTransport httpTransport = new NetHttpTransport();
                HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
                Log.i("placesAPIAufrufen: ","Request Factory holen beendet");


                //2. Url hinzufügen
                Log.i("placesAPIAufrufen: ","URL hinzufügen begonnen");
                GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                url.put("location", + gps.getBreitengrad() + "," + gps.getLaengengrad());
                url.put("rankby", "distance");
                url.put("type", typ);
                url.put("key", "AIzaSyDXzj9XCSY5FtsEyvtKaOtxctEX7ybzrpU");
                Log.i("placesAPIAufrufen: ","URL hinzufügen beendet: " + url);


                //3. Request absetzen
                Log.i("placesAPIAufrufen: ","Request absetzen begonnen");
                HttpRequest request = requestFactory.buildGetRequest(url);
                Log.i("placesAPIAufrufen: ","Request absetzen");

                HttpResponse httpResponse = request.execute();
                Log.i("placesAPIAufrufen: ","Request absetzen beendet");

                Log.i("placesAPIAufrufen: ","Request parsen");
                String jsonResponseString = httpResponse.parseAsString();

                Log.i("placesAPIAufrufen: ","API Aufruf beendet");
                Log.i("placesAPIAufrufen: ","Erhaltene JSON: " + jsonResponseString);
                return jsonResponseString;

            } catch (Exception e) {
                Log.i("Fehler placesAPIAufrufen", "Request absetzen fehlgeschlagen. Fehlermeldung " + e);
                return "error";
            }
        }

        protected ArrayList parseGooglePlaces(String jsonString) throws JSONException {
            try{
                Log.i("parseGooglePlaces:", "Beginnt das parsen");
                Log.i("parseGooglePlaces:", "Übergebenes Objekt: " + jsonString);
                //Eigentliches parsen
                JSONObject jsonObject = new JSONObject(jsonString);

                //Wichtigsten Inhalt entnehmen
                JSONArray jsonArray1 = jsonObject.getJSONArray("results");
                JSONObject i1 = jsonArray1.getJSONObject(0);

                JSONArray jsonArray2 = jsonObject.getJSONArray("results");
                JSONObject i2 = jsonArray2.getJSONObject(1);
                Log.i("parseGooglePlaces:", "results geparst");

                //name
                String name1 = i1.getString("name");
                Log.i("parseGooglePlaces:", "Name geparst " + name1);
                String name2 = i2.getString("name");
                Log.i("parseGooglePlaces:", "Name geparst " + name2);

                //location1 parsen und lat und lng selektieren
                JSONObject geometry1 = i1.getJSONObject("geometry");
                JSONObject location1 = geometry1.getJSONObject("location");
                Double location1_lat = location1.getDouble("lat");
                Double location1_lng = location1.getDouble("lng");
                Log.i("parseGooglePlaces:", "location1_lat gesetzt "+ location1_lat);
                Log.i("parseGooglePlaces:", "location1_lng gesetzt "+ location1_lng);

                //Entfernung für location1 aufrufen und parsen
                String ergebnis1 = distanceAPIAufrufen(location1_lat, location1_lng);
                Log.i("parseGooglePlaces:", "ergenis1 gesetzt "+ ergebnis1);
                JSONObject distanceObject1 = new JSONObject(ergebnis1);
                Log.i("parseGooglePlaces:", "distanceObject1 gesetzt "+ distanceObject1);
                JSONArray distanceArray1 = distanceObject1.getJSONArray("rows");
                JSONObject distanceObject1_changed = distanceArray1.getJSONObject(0);
                JSONArray distanceArray1_changend = distanceObject1_changed.getJSONArray("elements");
                distanceObject1_changed = distanceArray1_changend.getJSONObject(0);
                distanceObject1_changed = distanceObject1_changed.getJSONObject("distance");
                String distance1 = distanceObject1_changed.getString("text");
                Log.i("parseGooglePlaces:", "distance1 gesetzt "+ distance1);

                //location2 parsen und lat und lng selektieren
                JSONObject geometry2 = i2.getJSONObject("geometry");
                JSONObject location2 = geometry2.getJSONObject("location");
                Double location2_lat = location2.getDouble("lat");
                Double location2_lng = location2.getDouble("lng");
                Log.i("parseGooglePlaces:", "location2_lat gesetzt "+ location2_lat);
                Log.i("parseGooglePlaces:", "location2_lng gesetzt "+ location2_lng);

                //Entfernung für location2 aufrufen und parsen
                String ergebnis2 = distanceAPIAufrufen(location2_lat, location2_lng);
                Log.i("parseGooglePlaces:", "ergenis2 gesetzt "+ ergebnis2);
                JSONObject distanceObject2 = new JSONObject(ergebnis2);
                Log.i("parseGooglePlaces:", "distanceObject2 gesetzt "+ distanceObject2);
                JSONArray distanceArray2 = distanceObject2.getJSONArray("rows");
                JSONObject distanceObject2_changed = distanceArray2.getJSONObject(0);
                JSONArray distanceArray2_changed = distanceObject2_changed.getJSONArray("elements");
                distanceObject2_changed = distanceArray2_changed.getJSONObject(0);
                distanceObject2_changed = distanceObject2_changed.getJSONObject("distance");
                String distance2 = distanceObject2_changed.getString("text");
                Log.i("parseGooglePlaces:", "distance2 gesetzt "+ distance2);

                //destination_address1 selektieren und parsen
                JSONArray destination_address1_array = distanceObject1.getJSONArray("destination_addresses");
                String destination_address1 = (String) destination_address1_array.get(0);
                Log.i("parseGooglePlaces:", "destination_address1 gesetzt "+ destination_address1);

                //origin_addresses selektieren und parsen
                Log.i("parseGooglePlaces:", "marcel test " + distanceObject1);

                JSONArray origin_address_array = distanceObject1.getJSONArray("origin_addresses");
                Log.i("parseGooglePlaces:", "marcel test "+ origin_address_array);

                String origin_address = (String) origin_address_array.get(0);
                Log.i("parseGooglePlaces:", "origin_address gesetzt "+ origin_address);


                //destination_address2 selektieren und parsen
                JSONArray destination_address2_array = distanceObject2.getJSONArray("destination_addresses");
                String destination_address2 = (String) destination_address2_array.get(0);
                Log.i("parseGooglePlaces:", "destination_address2 gesetzt "+ destination_address2);

                //Informationen speichern
                ArrayList<Object> information = new ArrayList<>();

                Log.i("parseGooglePlaces:", "Parsen fast beendet");


                information.add(0, name1);
                information.add(1, destination_address1);
                information.add(2, distance1);
                information.add(3, name2);
                information.add(4, destination_address2);
                information.add(5, distance2);
                information.add(6, origin_address);

                Log.i("parseGooglePlaces:", "Parsen beendet: " + information);

                return information;
            } catch (Exception e) {
                //Fehler Exception
                Log.i("parseGooglePlaces","parseGooglePlaces fehlgeschlagen: " + e);
                return new ArrayList();
            }

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
        protected String distanceAPIAufrufen(Double lat, Double lng) throws Exception {

            try {
                Log.i("distanceAPIAufrufen: ","Methode begonnen");
                Log.i("distanceAPIAufrufen: ","Übergebene Werte: " + lat + " und " + lng );
                Log.i("distanceAPIAufrufen: ","Starte API Aufruf");

                //1. Request Factory holen
                Log.i("distanceAPIAufrufen: ","Request Factory holen begonnen");
                HttpTransport httpTransport = new NetHttpTransport();
                HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
                Log.i("distanceAPIAufrufen: ","Request Factory holen beendet");


                //2. Url hinzufügen
                Log.i("distanceAPIAufrufen: ","URL hinzufügen begonnen");
                GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/distancematrix/json?");
                url.put("destinations", + lat + "," + lng);
                url.put("origins", + gps.getBreitengrad() + "," + gps.getLaengengrad());
                url.put("units", "metric");
                url.put("key", "AIzaSyDXzj9XCSY5FtsEyvtKaOtxctEX7ybzrpU");
                Log.i("placesAPIAufrufen: ","URL hinzufügen beendet: " + url);


                //3. Request absetzen
                Log.i("distanceAPIAufrufen: ","Request absetzen begonnen");
                HttpRequest request = requestFactory.buildGetRequest(url);
                Log.i("distanceAPIAufrufen: ","Request absetzen");

                HttpResponse httpResponse = request.execute();
                Log.i("distanceAPIAufrufen: ","Request absetzen beendet");

                Log.i("distanceAPIAufrufen: ","Request parsen");
                String jsonResponseString = httpResponse.parseAsString();

                Log.i("distanceAPIAufrufen: ","API Aufruf beendet");
                Log.i("distanceAPIAufrufen: ","Erhaltene JSON: " + jsonResponseString);
                return jsonResponseString;

            } catch (Exception e) {
                Log.i("Fehler distanceAPIAufrufen", "Request absetzen fehlgeschlagen. Fehlermeldung " + e);
                return "error";
            }
        }




    }

    /**
     * Created by NEU PC on 13.06.2017.
     */

    public static class GPSBestimmung extends Service implements LocationListener {

        private final Context c;

        //Variable, zum speichern, ob GPS aktiv ist
        private boolean GPSaktiv = false;

        //Die Variable Location wird später benötigt und beinhaltet den Längen- und Breitengrad
        Location position;

        //Die Variable LocationManager wird später benötigt und ermöglicht die Positionsbestimmung
        protected LocationManager locationManager;

        /**
         * Creates an IntentService.  Invoked by your subclass's constructor.
         *
         * @param c
         */
        public GPSBestimmung(Context c) {
            this.c = c;
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            position = location;
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }


        //Gibt zurück, ob GPS eingeschaltet ist (true oder false)
        public boolean istGPSaktiv() {
            return this.GPSaktiv;
        }

        public void setPosition() {
            try {
                Log.i("GPSBestimmung", "setPosition gestartet");

                locationManager = (LocationManager) c.getSystemService(LOCATION_SERVICE);

                //ließt den GPS Status aus (Aktiv = Ja/Nein)
                GPSaktiv = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (GPSaktiv) {
                    Log.i("GPSBestimmung", "setPosition: GPSaktiv erkannt");
                    //Bestimmung der GPS Position, sofern GPS eingeschaltet ist. Die daten werden in der Variable "position" gespeichert.

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, this);
                    Log.i("GPSBestimmung", "LocationManager ausgeführt: " + locationManager);

                    if (locationManager != null) {
                        Log.i("GPSBestimmung", "setPosition: Speicherung wird durchgeführt");

                        position = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            } catch (SecurityException s) {
                s.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }


        //Beendet GPS Bestimmung
        public void stopGPSposition(){
            if(locationManager != null){
                locationManager.removeUpdates(GPSBestimmung.this);
            }
        }

        //Gibt den Breitengrad als Double zurück
        public double getBreitengrad() {
            Log.i("GPSBestimmung", "getBreitengrad gestartet");
            if (position != null) {
                Log.i("GPSBestimmung", "getBreitengrad position != null");
                return position.getLatitude();
            } else {
                Log.i("GPSBestimmung", "getBreitengrad: position leer");
                return 0;
            }
        }

        //Gibt den Längengrad als double zurück
        public double getLaengengrad() {
            Log.i("GPSBestimmung", "getLaengengrad gestartet");
            if (position != null) {
                Log.i("GPSBestimmung", "getLaengengrad position != null");
                return position.getLongitude();
            } else {
                Log.i("GPSBestimmung", "getLaengengrad: position leer");
                return 0;
            }
        }


    }
}


