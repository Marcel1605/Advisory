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
        //bread 6-9
        //appetizer 10-11
        //main course 12-14
        //side dish 15
        //dessert 16
        //main course 17-21
        //salad 22-23
        //soup 24-5

        String query;
        String type;

        if (hh == 24 || hh <= 5) {
            //Soup
            query = "soup";
            type = "soup";
        } else if (hh >= 6 && hh <= 9) {
            //Bread
            query = "bread";
            type = "bread";
        } else if (hh >= 10 && hh <= 11) {
            //Appetizer
            query = "appetizer";
            type = "appetizer";
        } else if (hh >= 12 && hh <= 14 || hh >= 17 && hh <= 21) {
            //Main Course
            query = "main+course";
            type = "main+course";
        } else if (hh == 15) {
            //side dish
            query = "side+dish";
            type = "side+dish";
        } else if (hh == 16) {
            //Dessert
            query = "dessert";
            type = "dessert";
        }  else if (hh >= 22 && hh <= 23) {
            //Salad
            query = "salad";
            type = "salad";
        } else {
            //Fehler
            
        }


    }
}

