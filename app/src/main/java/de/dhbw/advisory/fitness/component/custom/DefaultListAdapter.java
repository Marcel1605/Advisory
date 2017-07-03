package de.dhbw.advisory.fitness.component.custom;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.dhbw.advisory.R;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import de.dhbw.advisory.fitness.FitnessFragmentOverview;
import de.dhbw.advisory.common.FragmentChangeListener;
import de.dhbw.advisory.fitness.FitnessFragment;

/**
 * Sample adapter implementation extending from AsymmetricGridViewAdapter<DemoItem> This is the
 * easiest way to get started.
 */
public class DefaultListAdapter extends ArrayAdapter<DemoItem> implements DemoAdapter {

    private final LayoutInflater layoutInflater;
    private final FragmentChangeListener fragmentChangeListener;

    public DefaultListAdapter(Context context, List<DemoItem> items, FragmentChangeListener fragmentChangeListener) {
        super(context, 0, items);
        layoutInflater = LayoutInflater.from(context);
        this.fragmentChangeListener = fragmentChangeListener;
    }

    public DefaultListAdapter(Context context) {
        super(context, 0);
        layoutInflater = LayoutInflater.from(context);
        this.fragmentChangeListener = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;

        final DemoItem item = getItem(position);
        boolean isRegular = getItemViewType(position) == 0;

        if (convertView == null) {
            v = layoutInflater.inflate(R.layout.adapter_item, parent, false);
        } else {
            v = convertView;
        }

        //Grid mit motivierenden Bilder füllen
        ImageView imageView= (ImageView) v.findViewById(R.id.imageView);
        Picasso.with(getContext()).load(item.getImageUrl()).into(imageView);
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

                fragmentChangeListener.onFragmentChangeRequest(fitnessFragment);
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

    public void appendItems(List<DemoItem> newItems) {
        addAll(newItems);
        notifyDataSetChanged();
    }

    public void setItems(List<DemoItem> moreItems) {
        clear();
        appendItems(moreItems);
    }
}