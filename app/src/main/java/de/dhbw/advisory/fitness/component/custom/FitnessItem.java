package de.dhbw.advisory.fitness.component.custom;

import android.os.Parcel;
import android.os.Parcelable;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

/**
 * Datenmodell für die Grid-Elemente (Items) auf der Startseite (FitnessFragmentOverview)
 *
 */

public class FitnessItem implements AsymmetricItem {
    //Variable die festlegt wo das Item angezeigt wird
    private int columnSpan;
    private int rowSpan;
    private int position;

    //Variable die festlegt was angezeigt wird
    private String imageUrl;
    private String name;

    public FitnessItem() {
        this(1, 1, 0, "", "");
    }

    public FitnessItem(int columnSpan, int rowSpan, int position, String imageUrl, String name) {
        this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
        this.position = position;
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public FitnessItem(Parcel in) {
        readFromParcel(in);
    }

    @Override public int getColumnSpan() {
        return columnSpan;
    }

    @Override public int getRowSpan() {
        return rowSpan;
    }

    public int getPosition() {
        return position;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override public String toString() {
        return String.format("%s: %sx%s", position, rowSpan, columnSpan);
    }

    @Override public int describeContents() {
        return 0;
    }

    public String getName() {
        return this.name;
    }

    private void readFromParcel(Parcel in) {
        columnSpan = in.readInt();
        rowSpan = in.readInt();
        position = in.readInt();
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(columnSpan);
        dest.writeInt(rowSpan);
        dest.writeInt(position);
    }

    public static final Parcelable.Creator<FitnessItem> CREATOR = new Parcelable.Creator<FitnessItem>() {
        @Override public FitnessItem createFromParcel(Parcel in) {
            return new FitnessItem(in);
        }

        @Override public FitnessItem[] newArray(int size) {
            return new FitnessItem[size];
        }
    };
}
