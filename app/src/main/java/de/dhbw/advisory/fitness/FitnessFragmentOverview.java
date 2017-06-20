package de.dhbw.advisory.fitness;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;

import java.util.ArrayList;
import java.util.List;

import de.dhbw.advisory.R;
import de.dhbw.advisory.common.FragmentChangeListener;
import de.dhbw.advisory.fitness.component.custom.DefaultListAdapter;
import de.dhbw.advisory.fitness.component.custom.DemoAdapter;
import de.dhbw.advisory.fitness.component.custom.DemoItem;

public class FitnessFragmentOverview extends Fragment {
    private AsymmetricGridView listView;
    private DemoAdapter adapter;
    private ViewGroup container;
    private FragmentChangeListener fragmentChangeListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            fragmentChangeListener = (FragmentChangeListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException("Activity must implement FragmentChangeListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.container = container;
        return inflater.inflate(R.layout.fragment_fitness_overview, container, false);
    }

    public static final int ITEM_BAUCH = 0;
    public static final int ITEM_BIZEPS = 1;
    public static final int ITEM_TRIZEPS = 2;
    public static final int ITEM_BRUST = 3;
    public static final int ITEM_BEINE = 4;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (AsymmetricGridView) container.findViewById(R.id.listView);
        List<DemoItem> items = new ArrayList<>();
        DemoItem item_bauch = new DemoItem(2, 1, ITEM_BAUCH, "https://www.foodspring.de/magazine/wp-content/uploads/2015/03/shutterstock_252795637-800x500.jpg", "Bauch");
        DemoItem item_bizeps = new DemoItem(1, 1, ITEM_BIZEPS, "http://gangsterreport.com/wp-content/uploads/2017/02/12-rep_arms_main_4.jpg", "Bizeps");
        DemoItem item_trizeps = new DemoItem(1, 1, ITEM_TRIZEPS, "http://www.menshealth.com/sites/menshealth.com/files/styles/slideshow-desktop/public/images/slideshow2/1-triceps-intro_0.jpg?itok=SD8SWyJz", "Trizeps");
        DemoItem item_brust = new DemoItem(1, 1, ITEM_BRUST, "http://www.thebetterdays.de/wp-content/uploads/2014/09/brustmuskulatur.jpg", "Brust");
        DemoItem item_beine = new DemoItem(1, 1, ITEM_BEINE, "http://brachiale-fitness.de/wp-content/uploads/2015/08/Fotolia_85931103_S.jpg", "Beine");

        items.add(item_bauch);
        items.add(item_bizeps);
        items.add(item_trizeps);
        items.add(item_brust);
        items.add(item_beine);

        adapter = new DefaultListAdapter(getContext(), items, fragmentChangeListener);
        listView.setRequestedColumnCount(2);
        listView.setRequestedHorizontalSpacing(0);
        listView.setAdapter(getNewAdapter());
    }

    private AsymmetricGridViewAdapter getNewAdapter() {
        return new AsymmetricGridViewAdapter(getContext(), listView, adapter);
    }
}