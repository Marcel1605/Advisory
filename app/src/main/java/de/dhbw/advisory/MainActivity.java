package de.dhbw.advisory;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

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

        //Fitness Fragment als erstes Fragment hinzufügen
        _fragment = new FitnessFragment();
        FragmentTransaction transaction = _fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, _fragment).commit();

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
                _fragment = new GymFragment();
                break;
        }
        FragmentTransaction transaction = _fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, _fragment).commit();
        return true;
    }
}