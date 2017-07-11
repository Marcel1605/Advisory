package de.dhbw.advisory.gym;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSDetection extends Service implements LocationListener {
    private final Context c;

    //Variable, zum speichern, ob GPS aktiv ist
    public boolean GPSaktiv = false;

    //Die Variable Location wird später benötigt und beinhaltet den Längen- und Breitengrad
    private Location position;

    //Die Variable LocationManager wird später benötigt und ermöglicht die Positionsbestimmung
    protected LocationManager locationManager;
    private Runnable onPositionDetected;
    public GPSDetection(Context c) {
        this.c = c;
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onLocationChanged(Location location) {
        position = location;
        onPositionDetected.run();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    //Methode zur Speicherung der aktuellen Position
    public void setPosition(Runnable onPositionDetected) throws SecurityException {
        this.onPositionDetected = onPositionDetected;
        locationManager = (LocationManager) c.getSystemService(LOCATION_SERVICE);

        //ließt den GPS Status aus (Aktiv = Ja/Nein)
        GPSaktiv = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (GPSaktiv) {
            //Bestimmung der GPS Position, sofern GPS eingeschaltet ist. Die daten werden in der Variable "position" gespeichert.
            Log.i("GPSDetection", "LocationManager ausgeführt.");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, this);
            if (locationManager != null) {
                Log.i("GPSDetection", "setPosition: Speicherung wird durchgeführt");

                position = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (position != null){
                    synchronized (this) {
                        onPositionDetected.run();
                    }
                }
            }
        }
    }

    //Beendet GPS Bestimmung
    public void stopGPSposition(){
        if(locationManager != null){
            locationManager.removeUpdates(this);
        }
    }

    //Gibt den Breitengrad als Double zurück
    public double getBreitengrad() {
        if (position != null) {
            return position.getLatitude();
        } else {
            return 0;
        }
    }

    //Gibt den Längengrad als double zurück
    public double getLaengengrad() {
        if (position != null) {
            return position.getLongitude();
        } else {
            return 0;
        }
    }
}