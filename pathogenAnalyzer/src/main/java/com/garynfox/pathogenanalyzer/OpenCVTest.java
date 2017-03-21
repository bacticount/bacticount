package com.garynfox.pathogenanalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.opencv.imgcodecs.Imgcodecs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserDialog;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class OpenCVTest extends Activity {
		
	Button goHome;
	Button pickPhoto;
	Button openCVTestButtonTestResults;
	Button regression;
	TextView data1;
	String photoName;
	String photoPath;
	String photoNameAndPath;
	double[][] regressionData = { { 1, 3 }, {2, 5 }, {3, 7 }, {4, 14 }, {5, 11 }};
	
	
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("Load openCV", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_open_cvtest);	
		
		goHome = (Button) findViewById(R.id.goHome);
		pickPhoto = (Button) findViewById(R.id.pickPhoto);
		regression = (Button) findViewById(R.id.regression);
		data1 = (TextView) findViewById(R.id.data1);
		openCVTestButtonTestResults = (Button) findViewById(R.id.openCVTestButtonTestResults);
		setButtonOnClickListeners();
	}

	private void setButtonOnClickListeners(){
		goHome.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view1) {
				// Intent to launch Tutorial 1 of 4 screen (activity)
				Intent i1 = new Intent(OpenCVTest.this, HomeScreen.class);
				startActivity(i1);	
			}		
		});
		
		openCVTestButtonTestResults.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				// Intent to reaction results screen for testing only (will hard code in csv path)
				Intent i1 = new Intent(OpenCVTest.this, ReactionResults.class);
				Bundle b1 = new Bundle();
				b1.putStringArray(RecordingReaction.STANDARD_CURVE_INFO, new String[] {"0", "0", "0"});
				b1.putStringArray(RecordingReaction.REFERENCE_INFO, new String[] {"0", "0", "0"});
				b1.putStringArray(RecordingReaction.REACTION_INFO, new String[] {"0", "0", "0"});
				b1.putInt(RecordingReaction.RECORDING_TYPE, 1);
				b1.putInt(RecordingReaction.TIMECOUNT, 420);
				b1.putInt(RecordingReaction.DATA_POINTS, 420);
				i1.putExtras(b1);
				startActivity(i1);	
			}		
		});
		
		pickPhoto.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view3) {
				// Launch Dialog to create file name for sample but only after a reference standard curve has been selected
				FileChooserDialog dialog = new FileChooserDialog(OpenCVTest.this);
	    		
	    		// Assign listener for the select event.
	    		dialog.addListener(OpenCVTest.this.onFileSelectedListener);  		
	    		
	    		// Testing picture storage directory (Gary)
	    		String referencePathTemp; // this only gives a reference point for the user to search from
	    		referencePathTemp = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
	    		dialog.loadFolder(referencePathTemp);
	    		
	    		dialog.setFilter(".*jpg");
	    		dialog.setShowOnlySelectable(false);
	    		
	    		// Show the dialog.
	            dialog.show();
				
			}
		});

		///*
		regression.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view1) {

				SimpleRegression regressionTest1 = new SimpleRegression();
				regressionTest1.addData(regressionData);
				double intercept = regressionTest1.getIntercept();
				Log.d("intercept" , String.valueOf(intercept));
				double slope = regressionTest1.getSlope();
				Log.d("slope" , String.valueOf(slope));
				double rsq = regressionTest1.getRSquare();
				Log.d("r sq" , String.valueOf(rsq));
			}
		});
		//*/

	}
	
	private FileChooserDialog.OnFileSelectedListener onFileSelectedListener = new FileChooserDialog.OnFileSelectedListener() {
		public void onFileSelected(Dialog source, File file) {
			source.hide();
			String[] nextLine = new String[3];
			Toast toast = Toast.makeText(OpenCVTest.this, "File selected: " + file.getName(), Toast.LENGTH_LONG);
			toast.show();
			photoName = file.getName();
			photoPath = file.getPath();
			photoNameAndPath = photoPath + photoName;
			
			// Testing out some matrix stuff here
			Mat A;
			A = Imgcodecs.imread(photoPath, Imgcodecs.IMREAD_ANYDEPTH | Imgcodecs.IMREAD_ANYCOLOR);
			double[] testPoint = A.get(670, 1500);
			String[] testPointString = new String[testPoint.length];
			for(int i = 0; i < testPoint.length; i++){
				testPointString[i] = String.valueOf(testPoint[i]);
			}
			try {
				CSVWriter writer = new CSVWriter(new FileWriter("/storage/emulated/0/Pictures/_1e4/_1e4.pasc"));
				String[] entries = "red#green#blue".split("#");
			    writer.writeNext(entries);
				writer.writeNext(testPointString);
				writer.writeNext(testPointString);
				writer.writeNext(testPointString);
				writer.writeNext(testPointString);
				writer.writeNext(testPointString);
				writer.writeNext(testPointString);
				writer.writeNext(testPointString);
				writer.writeNext(testPointString);
				writer.writeNext(testPointString);
				writer.writeNext(testPointString);
				writer.writeNext(testPointString);
				writer.writeNext(testPointString);
				writer.writeNext(testPointString);
				writer.writeNext(testPointString);
				writer.writeNext(testPointString);		
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			try {
				CSVReader reader = new CSVReader(new FileReader("/storage/emulated/0/Pictures/_1e4/_1e4.pasc"));
				try {
					nextLine = reader.readNext();
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			data1.setText(testPoint[0] + " " + testPoint[1] + " " + testPoint[2] + " ");			
			
		}
		
		// Don't understand this method
		@Override
		public void onFileSelected(Dialog source, File folder, String name) {
			source.hide();
			// TODO Auto-generated method stub		
		}
	};
	
    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.open_cvtest, menu);
		return true;
	}
}
