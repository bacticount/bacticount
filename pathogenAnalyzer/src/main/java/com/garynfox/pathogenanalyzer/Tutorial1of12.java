package com.garynfox.pathogenanalyzer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;



public class Tutorial1of12 extends ActionBarActivity {

    Button tutorial1of12ButtonNextScreen;
    ViewPager slideShow;
    CustomPagerAdapter mCustomPagerAdapter;

    int[] mResources = {
            R.drawable.tutorial1of12,
            R.drawable.tutorial2of12,
            R.drawable.tutorial3of12,
            R.drawable.tutorial4of12,
            R.drawable.tutorial5of12,
            R.drawable.tutorial6of12
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial1of12);
        // Establish buttons for listening
        tutorial1of12ButtonNextScreen = (Button) findViewById(R.id.tutorial1of12ButtonNextScreen);

        mCustomPagerAdapter = new CustomPagerAdapter(this);

        slideShow = (ViewPager) findViewById(R.id.slideShow);
        //int numSlides = mResources.length;
        slideShow.setAdapter(mCustomPagerAdapter);

        // call button listener method
        setButtonOnClickListeners();
    }

    private void setButtonOnClickListeners(){

        // Launch tutorial activity screens for 2nd tutorial button
        tutorial1of12ButtonNextScreen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Intent to launch Tutorial 2 of 12 screen (activity)
                Intent i1 = new Intent(Tutorial1of12.this, Tutorial2of12.class);
                startActivity(i1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial1of12, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
