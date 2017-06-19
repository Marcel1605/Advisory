package de.dhbw.advisory;

import android.os.Parcel;
import android.os.Parcelable;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;


public class DemoItem implements AsymmetricItem {
    private int columnSpan;
    private int rowSpan;
    private int position;
    private String imageUrl;
    private String name;

    public DemoItem() {
        this(1, 1, 0, "", "");
    }

    public DemoItem(int columnSpan, int rowSpan, int position, String imageUrl, String name) {
        this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
        this.position = position;
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public DemoItem(Parcel in) {
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

    /* Parcelable interface implementation */
    public static final Parcelable.Creator<DemoItem> CREATOR = new Parcelable.Creator<DemoItem>() {
        @Override public DemoItem createFromParcel(Parcel in) {
            return new DemoItem(in);
        }

        @Override public DemoItem[] newArray(int size) {
            return new DemoItem[size];
        }
    };
}
