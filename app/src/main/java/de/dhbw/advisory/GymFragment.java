package de.dhbw.advisory;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import de.android.common.activities.SampleActivityBase;
import de.dhbw.advisory.placepicker.PlacePickerFragment;
import de.dhbw.advisory.placepicker.cardstream.CardStream;
import de.dhbw.advisory.placepicker.cardstream.CardStreamFragment;
import de.dhbw.advisory.placepicker.cardstream.CardStreamState;
import de.dhbw.advisory.placepicker.cardstream.OnCardClickListener;
import de.dhbw.advisory.placepicker.cardstream.StreamRetentionFragment;


/**
 * Created by Magnus on 30.05.17.
 */
public class GymFragment extends SampleActivityBase implements CardStream {

    public static final String TAG = "MainActivity";
    public static final String FRAGTAG = "PlacePickerFragment";
    private CardStreamFragment mCardStreamFragment;
    private StreamRetentionFragment mRetentionFragment;
    private static final String RETENTION_TAG = "retention";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gym);

        FragmentManager fm = getSupportFragmentManager();
        PlacePickerFragment fragment =
                (PlacePickerFragment) fm.findFragmentByTag(FRAGTAG);

        if (fragment == null) {
            FragmentTransaction transaction = fm.beginTransaction();
            fragment = new PlacePickerFragment();
            transaction.add(fragment, FRAGTAG);
            transaction.commit();
        }

        // Use fragment as click listener for cards, but must implement correct interface
        if (!(fragment instanceof OnCardClickListener)){
            throw new ClassCastException("PlacePickerFragment must " +
                    "implement OnCardClickListener interface.");
        }
        OnCardClickListener clickListener = (OnCardClickListener) fm.findFragmentByTag(FRAGTAG);

        mRetentionFragment = (StreamRetentionFragment) fm.findFragmentByTag(RETENTION_TAG);
        if (mRetentionFragment == null) {
            mRetentionFragment = new StreamRetentionFragment();
            fm.beginTransaction().add(mRetentionFragment, RETENTION_TAG).commit();
        } else {
            // If the retention fragment already existed, we need to pull some state.
            // pull state out
            CardStreamState state = mRetentionFragment.getCardStream();

            // dump it in CardStreamFragment.
            mCardStreamFragment =
                    (CardStreamFragment) fm.findFragmentById(R.id.fragment_cardstream);
            mCardStreamFragment.restoreState(state, clickListener);
        }
    }

    public CardStreamFragment getCardStream() {
        if (mCardStreamFragment == null) {
            mCardStreamFragment = (CardStreamFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_cardstream);
        }
        return mCardStreamFragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CardStreamState state = getCardStream().dumpState();
        mRetentionFragment.storeCardStream(state);
    }

    //@Override
    //public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    //    View v = inflater.inflate(R.layout.fragment_gym, container, false);
    //
    //return v;
    //}

    //@Override
    //public void onPause(){
    //   //The system calls this method as the first indication that the user is leaving the fragment (though it does not always mean the fragment is being destroyed). This is usually where you should commit any changes that should be persisted beyond the current user session (because the user might not come back).
    //   Log.e("DEBUG", "OnPause of RezepteFragment");
    //   super.onPause();
    //}

}
