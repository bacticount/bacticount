package com.garynfox.pathogenanalyzer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Tutorial4of4 extends Activity {
	
	Button tutorial4of4ButtonNextScreen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Inflate the GUI
		setContentView(R.layout.activity_tutorial_4_of_4);
		// Establish buttons for listening		
		tutorial4of4ButtonNextScreen = (Button) findViewById(R.id.tutorial4of4ButtonNextScreen);
		// call button listener method
		setButtonOnClickListeners();		
	}
	
	private void setButtonOnClickListeners(){
		
		// launch activity for home screen again (tutorial is done)
		tutorial4of4ButtonNextScreen.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				// Intent to launch home screen (activity)
				Intent i1 = new Intent(Tutorial4of4.this, HomeScreen.class);
				startActivity(i1);	
			}		
		});				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tutorial4of4, menu);
		return true;
	}

}