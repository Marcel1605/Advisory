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
    Location position;

    //Die Variable LocationManager wird später benötigt und ermöglicht die Positionsbestimmung
    protected LocationManager locationManager;

    //
    private Runnable onPositionDetected;

    //
    int counter = 0;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param c
     */
    public GPSDetection(Context c) {
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
        synchronized (this){
            if(counter == 0){
                onPositionDetected.run();
                //counter ++;
            }
        }
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

    public void setPosition(Runnable onPositionDetected) {
        this.onPositionDetected = onPositionDetected;
        try {
            Log.i("GPSDetection", "setPosition gestartet");

            locationManager = (LocationManager) c.getSystemService(LOCATION_SERVICE);

            //ließt den GPS Status aus (Aktiv = Ja/Nein)
            GPSaktiv = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (GPSaktiv) {
                Log.i("GPSDetection", "setPosition: GPSaktiv erkannt");
                //Bestimmung der GPS Position, sofern GPS eingeschaltet ist. Die daten werden in der Variable "position" gespeichert.


                Log.i("GPSDetection", "LocationManager ausgeführt: " + locationManager);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, this);
                if (locationManager != null) {
                    Log.i("GPSDetection", "setPosition: Speicherung wird durchgeführt");

                    position = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (position != null){
                        synchronized (this) {
                            //counter++;
                            onPositionDetected.run();
                        }
                    } else {
                        //donothing
                    }

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
            locationManager.removeUpdates(this);
        }
    }

    //Gibt den Breitengrad als Double zurück
    public double getBreitengrad() {
        Log.i("GPSDetection", "getBreitengrad gestartet");
        if (position != null) {
            Log.i("GPSDetection", "getBreitengrad position != null");
            return position.getLatitude();
        } else {
            Log.i("GPSDetection", "getBreitengrad: position leer");
            return 0;
        }
    }

    //Gibt den Längengrad als double zurück
    public double getLaengengrad() {
        Log.i("GPSDetection", "getLaengengrad gestartet");
        if (position != null) {
            Log.i("GPSDetection", "getLaengengrad position != null");
            return position.getLongitude();
        } else {
            Log.i("GPSDetection", "getLaengengrad: position leer");
            return 0;
        }
    }
}