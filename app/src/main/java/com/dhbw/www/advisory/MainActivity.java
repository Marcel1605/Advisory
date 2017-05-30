package com.dhbw.www.advisory;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    protected BottomNavigationView _bottomNavigation = null;
    protected Fragment _fragment;
    protected FragmentManager _fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        _bottomNavigation.inflateMenu(R.menu.navigation_menu);
        _fragmentManager = getSupportFragmentManager();

        _bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.fitness:
                        _fragment = new FitnessFragment();
                        break;
                    case R.id.rezepte:
                        //_fragment = new RezepteFragment();
                        break;
                    case R.id.gym:
                       // _fragment = new GymFragment();
                        break;
                }
                final FragmentTransaction transaction = _fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, _fragment).commit();
                return true;
            }
        }

    }
}
