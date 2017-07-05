package de.dhbw.advisory.fitness.component.custom;

import android.widget.ListAdapter;

import java.util.List;

public interface FitnessAdapter extends ListAdapter {

    void appendItems(List<FitnessItem> newItems);

    void setItems(List<FitnessItem> moreItems);
}