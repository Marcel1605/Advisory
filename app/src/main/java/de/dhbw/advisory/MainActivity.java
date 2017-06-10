package de.dhbw.advisory;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import de.dhbw.advisory.placepicker.cardstream.CardStream;
import de.dhbw.advisory.placepicker.cardstream.CardStreamFragment;
import de.dhbw.advisory.placepicker.cardstream.CardStreamState;
import de.dhbw.advisory.placepicker.cardstream.OnCardClickListener;
import de.dhbw.advisory.placepicker.cardstream.StreamRetentionFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    protected BottomNavigationView _bottomNavigation = null;
    protected Fragment _fragment;
    protected FragmentManager _fragmentManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        _fragmentManager = getSupportFragmentManager();


        //Beim initialen Activity-Start Fitness Fragment als erstes Fragment hinzufügen
        if (savedInstanceState == null) {
            _fragment = new FitnessFragment();
            FragmentTransaction transaction = _fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container, _fragment).commit();
        }
        _bottomNavigation.setOnNavigationItemSelectedListener(this);
    }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.fitness:
                _fragment = new FitnessFragment();
                break;
            case R.id.rezepte:
                _fragment = new RezepteFragment();
                break;
            case R.id.gym:
                _fragment = new PlacePickerFragment();
                break;
        }
        FragmentTransaction transaction = _fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, _fragment).commit();
        return true;
    }
}