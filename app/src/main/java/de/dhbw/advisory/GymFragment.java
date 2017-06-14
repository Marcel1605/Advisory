package de.dhbw.advisory;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
 * Created by Magnus on 30.05.17.
 */


public class GymFragment extends Fragment {

    //UI-Elemente für Gyms initialisieren
    TextView Gym_Name_Content1;
    TextView Gym_Entfernung_Content1;
    TextView Gym_Name_Content2;
    TextView Gym_Entfernung_Content2;

    //UI-Elemente für Parks initialisieren
    TextView Park_Name_Content1;
    TextView Park_Entfernung_Content1;
    TextView Park_Name_Content2;
    TextView Park_Entfernung_Content2;

    //UI-Elemente für Stadiums initialisieren
    TextView Stadium_Name_Content1;
    TextView Stadium_Entfernung_Content1;
    TextView Stadium_Name_Content2;
    TextView Stadium_Entfernung_Content2;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gym, container, false);

        //UI-Elemente für Gyms zuweisen
        Gym_Name_Content1 = (TextView) v.findViewById(R.id.Gym_Name_Content1);
        Gym_Entfernung_Content1 = (TextView) v.findViewById(R.id.Gym_Entfernung_Content1);
        Gym_Name_Content2 = (TextView) v.findViewById(R.id.Gym_Name_Content2);
        Gym_Entfernung_Content2 = (TextView) v.findViewById(R.id.Gym_Entfernung_Content2);

        //UI-Elemente für Parks zuweisen
        Park_Name_Content1 = (TextView) v.findViewById(R.id.Park_Name_Content1);
        Park_Entfernung_Content1 = (TextView) v.findViewById(R.id.Park_Entfernung_Content1);
        Park_Name_Content2 = (TextView) v.findViewById(R.id.Park_Name_Content2);
        Park_Entfernung_Content2 = (TextView) v.findViewById(R.id.Park_Entfernung_Content2);

        //UI-Elemente für Stadiums zuweisen
        Stadium_Name_Content1 = (TextView) v.findViewById(R.id.Stadium_Name_Content1);
        Stadium_Entfernung_Content1 = (TextView) v.findViewById(R.id.Stadium_Entfernung_Content1);
        Stadium_Name_Content2 = (TextView) v.findViewById(R.id.Stadium_Name_Content2);
        Stadium_Entfernung_Content2 = (TextView) v.findViewById(R.id.Stadium_Entfernung_Content2);




        GymAPI mat = new GymAPI(this.getContext());
        mat.doInBackground();


    return v;
    }

    @Override
    public void onPause(){
       //The system calls this method as the first indication that the user is leaving the fragment (though it does not always mean the fragment is being destroyed). This is usually where you should commit any changes that should be persisted beyond the current user session (because the user might not come back).
       Log.e("DEBUG", "OnPause of RezepteFragment");
       super.onPause();
    }

    /**
     * Klasse für den Hintergrund Thread
     */
    public static class GymAPI extends AsyncTask<Object, Object, Void> {

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
                Log.i("GymFragment: ","URL hinzufügen beendet: " + url);


                //3. Request absetzen
                Log.i("GymFragment: ","Request absetzen begonnen");
                HttpRequest request = requestFactory.buildGetRequest(url);
                Log.i("GymFragment: ","Request absetzen");
                HttpResponse httpResponse = request.execute();
                Log.i("GymFragment: ","Request absetzen beendet");

                Log.i("GymFragment: ","Request parsen");
                String jsonResponseString = httpResponse.parseAsString();

                Log.i("GymFragment: ","API Aufruf beendet");
                Log.i("GymFragment: ","Erhaltene JSON: " + jsonResponseString);
                return jsonResponseString;
            } catch (Exception e) {
                Log.i("Fehler GymFragment", "Request absetzen fehlgeschlagen. Fehlermeldung " + e);
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
}


