package de.dhbw.advisory;


import android.app.Service;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Magnus on 30.05.17.
 */
public class GymFragment extends Fragment {

    GPSBestimmung GPS;
    TextView mein_laengengrad;
    TextView mein_breitengradgrad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gym, container, false);

        GPS = new GPSBestimmung(this.getContext());
        GPS.setPosition();

        mein_laengengrad = (TextView) v.findViewById(R.id.Gym_Name_Content1);
        mein_breitengradgrad = (TextView) v.findViewById(R.id.Gym_Entfernung_Content1);

        if(GPS.istGPSaktiv() == true){
            //GPS ist aktiv, Grade ausgeben
            mein_laengengrad.setText("Längengrad: " + GPS.getLaengengrad());
            mein_breitengradgrad.setText("Breitengrad: " + GPS.getBreitengrad());
        }

    return v;
    }

    @Override
    public void onPause(){
       //The system calls this method as the first indication that the user is leaving the fragment (though it does not always mean the fragment is being destroyed). This is usually where you should commit any changes that should be persisted beyond the current user session (because the user might not come back).
       Log.e("DEBUG", "OnPause of RezepteFragment");
       super.onPause();
    }

}

