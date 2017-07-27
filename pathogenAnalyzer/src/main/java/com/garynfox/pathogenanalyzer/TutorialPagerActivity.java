package com.garynfox.pathogenanalyzer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TutorialPagerActivity extends FragmentActivity {

    private final int[] mResources = {
            R.drawable.tut2,
            R.drawable.tut3,
            R.drawable.tut4,
            R.drawable.tut5,
            R.drawable.tut6,
            R.drawable.tut7,
            R.drawable.tut8,
            R.drawable.tut9,
            R.drawable.tut10,
            R.drawable.tut11,
            R.drawable.tut12,
            R.drawable.tut13,
            R.drawable.tut14,
    };

    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_fragment_activity);

        // Instead of having mResources be static, you can pass whatever list you want inside a bundle
        // unpack it here and set it in TutorialPagerAdapter as the second variable

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.tutorialViewPager);
        mPagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager(), mResources);
        mPager.setAdapter(mPagerAdapter);

        // LinearLayout tutorialButtonLayout = (LinearLayout) findViewById(R.id.tutorialButtonLayout);
        // set this to tutorialButtonLayout.setVisibility(View.VISIBLE)
        // if you want to see the bottom bar with done and next buttons

        Button doneButton = (Button) findViewById(R.id.tutorialDoneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TutorialPagerActivity.this.finish();
            }
        });

        Button nextButton = (Button) findViewById(R.id.tutorialNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPager.getCurrentItem() < (mPager.getChildCount() - 1)) {
                    mPager.setCurrentItem(mPager.getCurrentItem()+1) ;
                } else {
                    Toast.makeText(TutorialPagerActivity.this, "You have reached the end of the tutorial!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class TutorialPagerAdapter extends FragmentStatePagerAdapter {
        protected int[] resourceArray;
        public TutorialPagerAdapter(FragmentManager fm, int[] resourceArray) {
            super(fm);
            this.resourceArray = resourceArray;
        }

        @Override
        public Fragment getItem(int position) {
            return new TutorialFragment(resourceArray[position]);
        }

        @Override
        public int getCount() {
            return resourceArray.length;
        }
    }
}
