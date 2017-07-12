package de.dhbw.advisory.recipe;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Magnus on 01.07.17.
 */

/**
 * Mit Hilfe dieser Klasse wird abgefragt, ob das Endgerät eine Internetverbindung hat (egal ob Mobile Daten oder normale Internetverbindung)
 */
public class AppStatus {

    private static AppStatus instance = new AppStatus();
    static Context context;
    ConnectivityManager connectivityManager;

    boolean connected = false;

    public static AppStatus getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    /**
     * Diese Methode gibt zurück, ob das Gerät Internetverbindung hat oder nicht
     * @return true --> ja Internet , false --> kein Internet
     */
    public boolean isOnline() {
        try {
            //Mit Hilfe des ConnectivityManagers wird ausgelesen ob eine Internetverbindung besteht oder nicht
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;


        } catch (Exception e) {
            Log.e("AppStatus - isOnline", "Fehler:" + e.getMessage());
        }
        return connected;
    }
}
