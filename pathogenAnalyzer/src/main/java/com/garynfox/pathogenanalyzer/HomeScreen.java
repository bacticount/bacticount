package com.garynfox.pathogenanalyzer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeScreen extends Activity {
	
	// Declare buttons	
	Button homeScreenButtonStartTutorial;
	Button homeScreenButtonStartAnalysis;
	
	// Debugging
	// Button openCVTesting;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Inflate the GUI
		setContentView(R.layout.home_screen);
		// Establish buttons for listening		
		homeScreenButtonStartTutorial = (Button) findViewById(R.id.homeScreenButtonStartTutorial);
		homeScreenButtonStartAnalysis = (Button) findViewById(R.id.homeScreenButtonStartAnalysis);
		// openCVTesting = (Button) findViewById(R.id.openCVTesting);
		// call button listener method
		setButtonOnClickListeners();		
	}
	
	private void setButtonOnClickListeners(){
		
		// Launch tutorial activity screens for tutorial button
		homeScreenButtonStartTutorial.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view1) {
				// Intent to launch Tutorial 1 of 4 screen (activity)
				Intent i1 = new Intent(HomeScreen.this, Tutorial1of12.class);
				startActivity(i1);	
			}		
		});	
		
		// Launch analysis screen to aim camera
		homeScreenButtonStartAnalysis.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view2) {
				// Intent to launch Tutorial 1 of 4 screen (activity)
				Intent i2 = new Intent(HomeScreen.this, ChooseSource.class);
				startActivity(i2);	
			}		
		});		

		/*
		// THIS PART IS FOR OPEN CV TESTING
		openCVTesting.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view2) {
				// Intent to launch Tutorial 1 of 4 screen (activity)
				Intent i3 = new Intent(HomeScreen.this, OpenCVTest.class);
				startActivity(i3);	
			}		
		});
		*/
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_screen, menu);
		return true;
	}

}
