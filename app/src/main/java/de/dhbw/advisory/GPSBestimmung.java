package de.dhbw.advisory;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by NEU PC on 13.06.2017.
 */

public class GPSBestimmung extends Service implements LocationListener {

    private final Context c;

    //Variable, zum speichern, ob GPS aktiv ist
    private boolean GPSaktiv = false;

    //Die Variable Location wird später benötigt und beinhaltet den Längen- und Breitengrad
    Location position;

    //Die Variable LocationManager wird später benötigt und ermöglicht die Positionsbestimmung
    protected LocationManager locationManager;

    public GPSBestimmung(Context context) {
        this.c = context;
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

