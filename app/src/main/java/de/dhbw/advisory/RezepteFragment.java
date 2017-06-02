package de.dhbw.advisory;


        import android.annotation.TargetApi;
        import android.os.Build;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.support.v4.app.Fragment;
        import android.widget.TextView;

        import java.text.SimpleDateFormat;
        import java.util.Calendar;

/**
 * Created by Magnus on 30.05.17.
 */
public class RezepteFragment extends Fragment {

    //Mittagessen, Lunch, Frühstück, Mitternachtssnach, Abendessen,...
    private String essenTyp;

    //Titel des Rezepts
    private String rezepte_Titel;

    //Variablen für die Nutritions
    private int kohlenhydrate;
    private int proteine;
    private int zucker;
    private int fett;

    //2 Dimensionale Liste für die Zutaten: 1. Zutat 2. Anzahl
    private int [][] zutaten;

    //Rezept Text
    private String rezeptText;

    //Aktuelle Uhrzeit (nur Stunden)
    private int actualHour;

    /**UI-Elemente*/
    protected TextView _rezepteTitel = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rezepte, container, false);

        //Referenzen der Textviews abfragen
        _rezepteTitel = (TextView) view.findViewById(R.id.Rezepte_Titel);


        _rezepteTitel.setText("" + actualHour());


        return view;
    }


    @Override
    public void onPause(){
        //The system calls this method as the first indication that the user is leaving the fragment (though it does not always mean the fragment is being destroyed). This is usually where you should commit any changes that should be persisted beyond the current user session (because the user might not come back).
        Log.e("DEBUG", "OnPause of RezepteFragment");
        super.onPause();
    }

    /**
     * Diese Methode liest die aktuelle Stundenzeit anhand der Systemzeit aus
     *
     * @return gibt die aktuelle Stundenzeit zurück
     */
    public int actualHour(){
        int hh;
        Calendar kalendar = Calendar.getInstance();
        SimpleDateFormat zeitformatH = new SimpleDateFormat("HH");
        String h = zeitformatH.format(kalendar.getTime());
        hh = Integer.parseInt(h);
        return hh;
    }

    /**
     * Diese Methode aktualisiert die Überschrift des Fragments
     *
     *
     * @param hh
     */
    public void setEssensTyp(int hh) {

    }
}

