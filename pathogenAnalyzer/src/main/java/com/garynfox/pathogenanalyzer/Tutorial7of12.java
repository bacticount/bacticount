package com.garynfox.pathogenanalyzer;

import android.content.Intent;
//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Tutorial7of12 extends Activity {

    Button tutorial7of12ButtonNextScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial7of12);
        // Establish buttons for listening
        tutorial7of12ButtonNextScreen = (Button) findViewById(R.id.tutorial7of12ButtonNextScreen);
        // call button listener method
        setButtonOnClickListeners();
    }

    private void setButtonOnClickListeners(){

        // Launch tutorial activity screens for 2nd tutorial button
        tutorial7of12ButtonNextScreen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Intent to launch Tutorial 8 of 12 screen (activity)
                Intent i1 = new Intent(Tutorial7of12.this, Tutorial8of12.class);
                startActivity(i1);
            }
        });
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial7of12, menu);
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
    */
}
