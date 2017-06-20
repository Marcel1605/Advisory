package de.dhbw.advisory.fitness;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import de.dhbw.advisory.R;
import de.dhbw.advisory.common.AsyncTaskResult;

public class FitnessFragment extends Fragment implements IYoutubeCardView {
    private ProgressDialog alertDialog;
    private ViewGroup container;
    private LayoutInflater inflater;

    public FitnessFragment(){ }

    public void cancelProgressDialog() {
        alertDialog.dismiss();
    }

    /*
    Methode veranlasst, dass die von der YouTube API empfangenen Ergebnisse in Form von Karten in das Layout eingefügt werden.
    Die Karte besteht aus einem Vorschaubild des Videos und dem Titel des Videos
     */
    public void addCard(final SearchResult item) {
        LinearLayout root = (LinearLayout) this.container.findViewById(R.id.youtubeRoot);
        View view = inflater.inflate(R.layout.card, root, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Sofern die YouTube App auf dem Gerät installiert ist, soll das Video dort abgespielt werden
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + item.getId().getVideoId()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    //Sollte die YouTube App nicht auf dem Gerät installiert sein, wird das Video in einer anderen geeigneten App gestartet (z.B Browser)
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/embed/" + item.getId().getVideoId() + "?autoplay=1"));
                    startActivity(i);
                }
            }
        });
        //Die Karte wird mit dem Titel des Videos versehen
        TextView tv = (TextView) view.findViewById(R.id.videoTitle);
        tv.setText(item.getSnippet().getTitle());

        //Das Vorschaubild des Videos wird referenziert
        ImageView imageView = (ImageView) view.findViewById(R.id.thumbnail);
        Thumbnail thumbnail = item.getSnippet().getThumbnails().getHigh();
        if(thumbnail == null) {
            thumbnail = item.getSnippet().getThumbnails().getDefault();
        }

        //Die Picasso-Bibliothek übernimmt den Download und das Caching der Bilddatei
        Picasso.with(getContext()).load(thumbnail.getUrl()).into(imageView);

        System.out.println("Card added!");

        root.addView(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.container = container;
        this.inflater = inflater; 
        alertDialog = new ProgressDialog(getContext());
        alertDialog.setMessage(getResources().getString(R.string.loader));
        alertDialog.setCancelable(false);
        alertDialog.show();
        AsyncTask<String, Void, AsyncTaskResult<SearchResult>> task = new YoutubeSearch(getContext(), this);
        task.execute(getArguments().getString("url"));

        return inflater.inflate(R.layout.fragment_fitness, container, false);
    }

    @Override
    public void onPause(){
        //Das System ruft diese Methode auf, sobald der Nutzer das erste mal andeutet, dass er das Fragment verlassen möchte. Hier sollten alle Änderungen commited werden, die länger als die aktuelle user session zu Verfügung stehen sollen
        Log.e("DEBUG", "OnPause of loginFragment");
        super.onPause();
    }
}
