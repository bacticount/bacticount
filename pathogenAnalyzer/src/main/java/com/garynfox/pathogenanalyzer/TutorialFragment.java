package com.garynfox.pathogenanalyzer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Sarah on 4/10/17.
 */

public class TutorialFragment extends Fragment {

    private int drawableID;

    public TutorialFragment(int drawableResouce)
    {
        drawableID = drawableResouce;
    }

    public TutorialFragment()
    {
        drawableID = R.drawable.ic_launcher;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.tutorial_fragment, container, false);

        ImageView tutorialImageView = (ImageView) rootView.findViewById(R.id.tutorialImage);
        tutorialImageView.setImageResource(drawableID);

        return rootView;
    }
}
