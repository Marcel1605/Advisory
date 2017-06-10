package de.dhbw.advisory;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;


import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;




/**
 * Created by Magnus on 30.05.17.
 */
public class GymFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gym, container, false);

        return v;
    }

    @Override
    public void onPause(){
        //The system calls this method as the first indication that the user is leaving the fragment (though it does not always mean the fragment is being destroyed). This is usually where you should commit any changes that should be persisted beyond the current user session (because the user might not come back).
        Log.e("DEBUG", "OnPause of RezepteFragment");
        super.onPause();
    }



}
