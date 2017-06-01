package de.dhbw.advisory;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.squareup.picasso.Picasso;

/**
 * Created by Magnus on 30.05.17.
 */


public class FitnessFragment extends Fragment implements IYoutubeCardView {
    private ProgressDialog alertDialog;
    private ViewGroup container;

    public FitnessFragment(){ }

    public void cancelProgressDialog() {
        alertDialog.dismiss();
    }

    public void addCard(SearchResult item) {
        LinearLayout root = (LinearLayout) getView().findViewById(R.id.youtubeRoot);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.card, root, false);
        TextView tv = (TextView) view.findViewById(R.id.videoTitle);
        tv.setText(item.getSnippet().getTitle());

        ImageView imageView = (ImageView) view.findViewById(R.id.thumbnail);
        Thumbnail thumbnail = item.getSnippet().getThumbnails().getHigh();
        if(thumbnail == null) {
            thumbnail = item.getSnippet().getThumbnails().getDefault();
        }
        Picasso.with(getContext()).load(thumbnail.getUrl()).into(imageView);

        System.out.println("Card added!");

        root.addView(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.container = container;
        return inflater.inflate(R.layout.fragment_fitness, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        alertDialog = new ProgressDialog(getContext());
        alertDialog.setMessage(getResources().getString(R.string.loader));
        alertDialog.setCancelable(false);
        alertDialog.show();

        AsyncTask<String, Void, AsyncTaskResult> task = new YoutubeSearch(getContext(), this);
        task.execute("Bizeps Übungen");
    }

    @Override
    public void onPause(){
        //The system calls this method as the first indication that the user is leaving the fragment (though it does not always mean the fragment is being destroyed). This is usually where you should commit any changes that should be persisted beyond the current user session (because the user might not come back).
        Log.e("DEBUG", "OnPause of loginFragment");
        super.onPause();
    }
}
