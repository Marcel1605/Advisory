package de.dhbw.advisory;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.Toast;

import de.dhbw.advisory.common.FragmentChangeListener;
import de.dhbw.advisory.gym.GymFragment;
import de.dhbw.advisory.recipe.RezepteFragment;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, FragmentChangeListener{

    private static final int GPS_REQUEST_CODE = 200;
    protected BottomNavigationView _bottomNavigation = null;
    protected Fragment _fragment;
    protected FragmentManager _fragmentManager;
    protected FragmentTransaction transaction;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        _fragmentManager = getSupportFragmentManager();

        //Beim initialen Activity-Start Fitness Fragment als erstes Fragment hinzufügen
        if (savedInstanceState == null) {
            _fragment = new de.dhbw.advisory.fitness.FitnessFragmentOverview();
            FragmentTransaction transaction = _fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container, _fragment).commit();
        }
        _bottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
        case R.id.fitness:
            _fragment = new de.dhbw.advisory.fitness.FitnessFragmentOverview();
            transaction = _fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container, _fragment).addToBackStack("tag").commit();
            break;
        case R.id.rezepte:
            _fragment = new RezepteFragment();
            transaction = _fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container, _fragment).addToBackStack("tag").commit();
            break;
        case R.id.gym:
            boolean permissionGranted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (permissionGranted) {
                _fragment = new GymFragment();
                transaction = _fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, _fragment).addToBackStack("tag").commit();
            } else {
                //Fenster das User nach Permission fragt. Löst Callback zur Methode onRequestPermissionsResult() aus.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_REQUEST_CODE);
            }
            break;
    }

        return true;
    }

    @Override
    public void onFragmentChangeRequest(Fragment fragment, boolean pushOnBackStack) {
        _fragment = fragment;
        transaction = _fragmentManager.beginTransaction();
        if(pushOnBackStack) {
            transaction.replace(R.id.main_container, _fragment).addToBackStack("tag").commit();
        }
        else {
            transaction.replace(R.id.main_container, _fragment).commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == GPS_REQUEST_CODE) {
            //Wenn Permission vorhanden: GymFragment wird aufgerufen
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                _fragment = new GymFragment();
                transaction = _fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, _fragment).addToBackStack("tag").commit();
                //Wenn Permission nicht vorhanden, dann zeige einen Toast
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Bitte akzeptiere die Permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}