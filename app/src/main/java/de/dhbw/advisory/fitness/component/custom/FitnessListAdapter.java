package de.dhbw.advisory.fitness.component.custom;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.dhbw.advisory.R;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.dhbw.advisory.fitness.FitnessFragmentOverview;
import de.dhbw.advisory.common.FragmentChangeListener;
import de.dhbw.advisory.fitness.FitnessFragment;

/**
 * Adapterklasse zur Anzeige des GridView auf der "Startseite" der App (FitnessFragmentOverview)
 */
public class FitnessListAdapter extends ArrayAdapter<FitnessItem> implements FitnessAdapter {

    private final LayoutInflater layoutInflater;
    private final FragmentChangeListener fragmentChangeListener;
    private final View container;

    public FitnessListAdapter(Context context, List<FitnessItem> items, FragmentChangeListener fragmentChangeListener, View container) {
        super(context, 0, items);
        this.container = container;
        layoutInflater = LayoutInflater.from(context);
        this.fragmentChangeListener = fragmentChangeListener;
    }

    public FitnessListAdapter(Context context) {
        super(context, 0);
        layoutInflater = LayoutInflater.from(context);
        this.fragmentChangeListener = null;
        this.container = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;

        final FitnessItem item = getItem(position);
        boolean isRegular = getItemViewType(position) == 0;

        if (convertView == null) {
            v = layoutInflater.inflate(R.layout.adapter_item, parent, false);
        } else {
            v = convertView;
        }

        //Grid mit motivierenden Bildern füllen
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        Picasso.with(getContext()).load(item.getImageUrl()).error(R.drawable.ic_error_outline_black_48dp).into(imageView, new Callback() {
            @Override
            public void onSuccess() {}

            @Override
            public void onError() {
                FitnessFragmentOverview.showSnackbar(FitnessListAdapter.this.container);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FitnessFragment fitnessFragment = new FitnessFragment();
                Bundle bundle = new Bundle();
                fitnessFragment.setArguments(bundle);

                switch(item.getPosition()) {
                    case FitnessFragmentOverview.ITEM_BAUCH:
                        bundle.putString("url", "bauch übungen zuhause");
                        break;
                    case FitnessFragmentOverview.ITEM_BIZEPS:
                        bundle.putString("url", "bizeps übungen zuhause");
                        break;
                    case FitnessFragmentOverview.ITEM_TRIZEPS:
                        bundle.putString("url", "trizeps übungen zuhause");
                        break;
                    case FitnessFragmentOverview.ITEM_BRUST:
                        bundle.putString("url", "brust übungen zuhause");
                        break;
                    case FitnessFragmentOverview.ITEM_BEINE:
                        bundle.putString("url", "bein übungen zuhause");
                        break;
                }

                fragmentChangeListener.onFragmentChangeRequest(fitnessFragment, true);
            }
        });

        TextView textView = (TextView) v.findViewById(R.id.header);
        textView.setText(item.getName());

        return v;
    }

    @Override public int getViewTypeCount() {
        return 2;
    }

    @Override public int getItemViewType(int position) {
        return position % 2 == 0 ? 1 : 0;
    }

    public void appendItems(List<FitnessItem> newItems) {
        addAll(newItems);
        notifyDataSetChanged();
    }

    public void setItems(List<FitnessItem> moreItems) {
        clear();
        appendItems(moreItems);
    }
}