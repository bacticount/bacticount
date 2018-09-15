package com.garynfox.pathogenanalyzer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ReactionResultsImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ReactionResultsEntry> resultList;
    private boolean isDetailedView;

    public ReactionResultsImageAdapter(Context c, ArrayList<ReactionResultsEntry> results, boolean isDetailedView) {
        mContext = c;
        resultList = results;
        this.isDetailedView = isDetailedView;
    }

    public int getCount() {
        return resultList.size();
    }

    public Object getItem(int position) {
        return resultList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext); // 1
            convertView = inflater.inflate(R.layout.circular_result_button, null);
        }

        ReactionResultsEntry result = resultList.get(position);
        TextView sampleNameTextView = (TextView) convertView.findViewById(R.id.sampleName);
        TextView sampleValueTextView = (TextView) convertView.findViewById(R.id.sampleValue);
        ImageView statusImage = (ImageView) convertView.findViewById(R.id.statusImage);

        sampleNameTextView.setText(result.getKey());

        if(isDetailedView) {
            sampleValueTextView.setVisibility(View.VISIBLE);
            sampleValueTextView.setText((result.isPathogenDetected()? result.getStringValueShort() : ""));
            statusImage.setVisibility(View.GONE);
        } else {
            sampleValueTextView.setVisibility(View.GONE);
            statusImage.setVisibility(View.VISIBLE);
            statusImage.setImageResource((result.isPathogenDetected()? R.drawable.ic_warning : R.drawable.ic_check));
        }

        convertView.setBackgroundResource((result.isPathogenDetected()? R.drawable.result_red_circle_background : R.drawable.result_green_circle_background));
        return convertView;
    }
}
