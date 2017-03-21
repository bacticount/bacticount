package com.garynfox.pathogenanalyzer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Tutorial3of4 extends Activity {
	
	Button tutorial3of4ButtonNextScreen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Inflate the GUI
		setContentView(R.layout.activity_tutorial_3_of_4);
		// Establish buttons for listening		
		tutorial3of4ButtonNextScreen = (Button) findViewById(R.id.tutorial3of4ButtonNextScreen);
		// call button listener method
		setButtonOnClickListeners();		
	}
	
	private void setButtonOnClickListeners(){
		
		// Launch tutorial activity screens for 4rd tutorial button
		tutorial3of4ButtonNextScreen.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				// Intent to launch Tutorial 4 of 4 screen (activity)
				Intent i1 = new Intent(Tutorial3of4.this, Tutorial4of4.class);
				startActivity(i1);	
			}		
		});				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tutorial3of4, menu);
		return true;
	}

}