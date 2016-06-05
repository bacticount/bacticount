package com.garynfox.pathogenanalyzer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Tutorial2of4 extends Activity {
	
	Button tutorial2of4ButtonNextScreen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Inflate the GUI
		setContentView(R.layout.activity_tutorial_2_of_4);

        // hideButtons();

		// Establish buttons for listening		
		tutorial2of4ButtonNextScreen = (Button) findViewById(R.id.tutorial2of4ButtonNextScreen);
		// call button listener method
		setButtonOnClickListeners();		
	}
	
	private void setButtonOnClickListeners(){
		
		// Launch tutorial activity screens for 3rd tutorial button
		tutorial2of4ButtonNextScreen.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				// Intent to launch Tutorial 3 of 4 screen (activity)
				Intent i1 = new Intent(Tutorial2of4.this, Tutorial3of4.class);
				startActivity(i1);	
			}		
		});				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tutorial2of4, menu);
		return true;
	}

	/*
    private void hideButtons() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
	*/
}