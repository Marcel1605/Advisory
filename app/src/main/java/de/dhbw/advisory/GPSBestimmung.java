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

    //Gibt zurück, ob GPS eingeschaltet ist (true oder false)
    public boolean istGPSaktiv() {
        return this.GPSaktiv;
    }

    public void setPosition() {
        try {
            locationManager = (LocationManager) c.getSystemService(LOCATION_SERVICE);

            //ließt den GPS Status aus (Aktiv = Ja/Nein)
            GPSaktiv = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (GPSaktiv) {
                //Bestimmung der GPS Position, sofern GPS eingeschaltet ist. Die daten werden in der Variable "position" gespeichert.


                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, this);
                if (locationManager != null) {
                    position = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Beendet GPS Bestimmung
    public void stopGPSposition(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSBestimmung.this);
        }
    }

}

