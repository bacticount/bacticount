package com.garynfox.pathogenanalyzer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class HomeScreen extends Activity {
	
	// Declare buttons	
	Button homeScreenButtonStartTutorial;
	Button homeScreenButtonStartAnalysis;
	Button homeScreenButtonOpenCVManager;
	Button homeScreenButtonESFile;
	Button homeScreenButtonExport;
	
	// Debugging
	// Button openCVTesting;
	
	// Permissions definition

	final int ANALYSIS_PERMISSIONS = 999;
	final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Inflate the GUI
		setContentView(R.layout.home_screen);
		// Establish buttons for listening		
		homeScreenButtonStartTutorial = (Button) findViewById(R.id.homeScreenButtonStartTutorial);
		homeScreenButtonStartAnalysis = (Button) findViewById(R.id.homeScreenButtonStartAnalysis);
		homeScreenButtonOpenCVManager = (Button) findViewById(R.id.homeScreenButtonOpenCVManager);
		homeScreenButtonESFile = (Button) findViewById(R.id.homeScreenButtonESFile);
		homeScreenButtonExport = (Button) findViewById(R.id.homeScreenButtonExport);

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
				Intent i1 = new Intent(HomeScreen.this, TutorialPagerActivity.class);
				startActivity(i1);	
			}		
		});	
		
		// Launch analysis screen to aim camera
		homeScreenButtonStartAnalysis.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view2) {

				// Ask camera and read write permissions




				if(ContextCompat.checkSelfPermission(HomeScreen.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
					&& ContextCompat.checkSelfPermission(HomeScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
					&& ContextCompat.checkSelfPermission(HomeScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

					// Intent to launch Tutorial 1 of 4 screen (activity)
					Intent i2 = new Intent(HomeScreen.this, ChooseSource.class);
					startActivity(i2);
				} else {
					// Toast.makeText(getApplicationContext(), "Allowing read-write and camera access is required for Bacticount.", Toast.LENGTH_LONG).show();
					ActivityCompat.requestPermissions(HomeScreen.this, permissions, ANALYSIS_PERMISSIONS);
				}

			}		
		});

		homeScreenButtonESFile.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view3) {
				// Point user to ES File Explorer on App Store
				Intent i5 = new Intent(Intent.ACTION_VIEW);
				i5.setData(Uri.parse("market://details?id=com.estrongs.android.pop"));
				startActivity(i5);
			}
		});

		homeScreenButtonOpenCVManager.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view4) {
				// Point user to Open CV Manager
				Intent i4 = new Intent(Intent.ACTION_VIEW);
				i4.setData(Uri.parse("market://details?id=org.opencv.engine"));
				startActivity(i4);
			}
		});


		homeScreenButtonExport.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view5) {
				// Point user to export procedure
				Toast.makeText(getApplicationContext(), "In your computer's file explorer, find the named folder for the run and open the .pasc or .parr as a .csv in any spreadsheet software.", Toast.LENGTH_LONG).show();

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
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		boolean cameraAccepted = false;
		boolean writeAccepted = false;
		boolean readAccepted = false;

		switch(requestCode){

			case ANALYSIS_PERMISSIONS:
				cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
				writeAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
				readAccepted = grantResults[2]==PackageManager.PERMISSION_GRANTED;
				break;

		}

		if(cameraAccepted && writeAccepted && readAccepted) {
			Intent i2 = new Intent(HomeScreen.this, ChooseSource.class);
			startActivity(i2);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_screen, menu);
		return true;
	}

}
