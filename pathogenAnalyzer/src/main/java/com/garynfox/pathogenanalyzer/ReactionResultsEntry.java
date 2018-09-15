package com.garynfox.pathogenanalyzer;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;

public class ReactionResultsEntry implements Parcelable {

    String key;
    double resValue;
    boolean isPathogenDetected;

    DecimalFormat dfLong = new DecimalFormat("0.0000000000E00");
    DecimalFormat dfReg = new DecimalFormat("0.00E00");
    DecimalFormat dfShort = new DecimalFormat("0.0E00");



    public ReactionResultsEntry(String key, double value, boolean isPathogenDetected)
    {
        this.key = key;
        this.resValue = value;
        this.isPathogenDetected = isPathogenDetected;
    }

    public String getKey() {
        return key;
    }

    public double getValue() {
        return resValue;
    }

    public String getStringValueShort() {
        return dfShort.format(resValue);
    }

    public String getStringValue() { return dfReg.format(resValue); }

    public String getStringValueLong() { return dfLong.format(resValue); }

    public boolean isPathogenDetected() {
        return isPathogenDetected;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeDouble(resValue);
        dest.writeInt(isPathogenDetected? 1 : 0);
    }

    public static final Parcelable.Creator<ReactionResultsEntry> CREATOR
            = new Parcelable.Creator<ReactionResultsEntry>() {
        public ReactionResultsEntry createFromParcel(Parcel in) {
            return new ReactionResultsEntry(in);
        }

        public ReactionResultsEntry[] newArray(int size) {
            return new ReactionResultsEntry[size];
        }
    };

    private ReactionResultsEntry(Parcel in) {
        key = in.readString();
        resValue = in.readDouble();
        isPathogenDetected = (in.readInt() == 1);
    }
}
