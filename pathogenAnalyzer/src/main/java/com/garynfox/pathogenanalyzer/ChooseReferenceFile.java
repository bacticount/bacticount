package com.garynfox.pathogenanalyzer;


import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.*;

public class ChooseReferenceFile extends Activity {
	
	// Value pairs needed to pass bundle along
	public final static String STANDARD_CURVE_INFO = "com.garynfox.pathogenanalyzer.STANDARD_CURVE_INFO";	
	public final static String REACTION_INFO = "com.garynfox.pathogenanalyzer.REACTION_INFO";
	public final static String REFERENCE_INFO = "com.garynfox.pathogenanalyzer.REFERENCE_INFO";
	public final static String RECORDING_TYPE = "com.garynfox.pathogenanalyzer.RECORDING_TYPE";
	public final static String SOURCE_TYPE = "com.garynfox.pathogenanalyzer.SOURCE_TYPE";
	public final static int STANDARD_CURVE = 1;
	public final static int SAMPLE = 2;
	
	// Stuff being packed into the bundle
	static String[] standardCurveInfo = new String[3];
	static String[] sampleInfo = new String[3];
	static String[] referenceInfo = new String[3];
	static int recordingType = 999;
	
	// Strings
	String standardCurveName;
	String standardCurvePath;
	String standardCurveNameAndPath;
	String sampleName;
	String samplePath;
	String sampleNameAndPath;
	String referenceName;
	String referencePath;
	String referenceNameAndPath;
    String tempName;
    String tempPath;
    String tempNameAndPath;
	String sourceType;
	
	// Buttons
	Button chooseReferenceFileButtonSelectPASCFile;
    Button chooseReferenceFileButtonSelectPARRFile;
	Button chooseReferenceFileButtonProceed;
	
	// Text views
	TextView chooseReferenceFileTextViewPASCSelected;
    TextView chooseReferenceFileTextViewPARRSelected;
	
	// don't want to launch next screen unless file is selected
	boolean isFilePASCSelected = false;
    boolean isFilePARRSelected = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Intent previousScreen = getIntent();
		
		// Unpacking bundle from previous intent, mainly to pass to next intent
		Bundle bundleReceived = previousScreen.getExtras();
		
		standardCurveInfo = bundleReceived.getStringArray(ChooseAction.STANDARD_CURVE_INFO);
		sampleInfo = bundleReceived.getStringArray(ChooseAction.REACTION_INFO);
		referenceInfo = bundleReceived.getStringArray(ChooseAction.REFERENCE_INFO);
		recordingType = bundleReceived.getInt(ChooseAction.RECORDING_TYPE);
		sourceType = bundleReceived.getString(ChooseAction.SOURCE_TYPE);

        // Log.d("stcname before selection", standardCurveName.toString());
        /*
        Log.d("stuff before files selected");
        Log.d(standardCurveName);
        Log.d(standardCurvePath);
        Log.d(standardCurveNameAndPath);
        Log.d(sampleName);
        Log.d(samplePath);
        Log.d(sampleNameAndPath);
        Log.d(referenceName);
        Log.d(referencePath);
        Log.d(referenceNameAndPath);
        */
		
		setContentView(R.layout.activity_choose_reference_file);
		
		// Establish buttons for listening
		chooseReferenceFileButtonSelectPASCFile = (Button) findViewById(R.id.chooseReferenceFileButtonSelectPASCFile);
        chooseReferenceFileButtonSelectPARRFile = (Button) findViewById(R.id.chooseReferenceFileButtonSelectPARRFile);
		chooseReferenceFileButtonProceed = (Button) findViewById(R.id.chooseReferenceFileButtonProceed);
		
		// Text View displays file chosen
		chooseReferenceFileTextViewPASCSelected = (TextView) findViewById(R.id.chooseReferenceFileTextViewPASCSelected);
        chooseReferenceFileTextViewPARRSelected = (TextView) findViewById(R.id.chooseReferenceFileTextViewPARRSelected);

		// call button listener method
		setButtonOnClickListeners();
	}

	private void setButtonOnClickListeners(){
	
		final Intent i = new Intent(ChooseReferenceFile.this, ReactionResults.class);
		
		
		// button launches file browser to select a valid .pasc file
		chooseReferenceFileButtonSelectPASCFile.setOnClickListener(new OnClickListener(){
			
			public void onClick(View view3) {
				// Launch Dialog to create file name for sample but only after a reference standard curve has been selected
				FileChooserDialog dialog = new FileChooserDialog(ChooseReferenceFile.this);
	    		
	    		// Assign listener for the select event.
	    		dialog.addListener(ChooseReferenceFile.this.onFileSelectedListener);  		
	    		
	    		// Testing picture storage directory (Gary)
	    		String referencePathTemp; // this only gives a reference point for the user to search from
	    		referencePathTemp = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
	    		dialog.loadFolder(referencePathTemp);
	    		
	    		// Define the filter for selecting .pasc files
	    		dialog.setFilter(".*pasc");
	    		dialog.setShowOnlySelectable(false);
	    		
	    		// Show the dialog.
	            dialog.show();
				
			}
		});

        // button launches file browser to select a valid .parr file
        chooseReferenceFileButtonSelectPARRFile.setOnClickListener(new OnClickListener(){

            public void onClick(View view3) {
                // Launch Dialog to create file name for sample but only after a reference standard curve has been selected
                FileChooserDialog dialog = new FileChooserDialog(ChooseReferenceFile.this);

                // Assign listener for the select event.
                dialog.addListener(ChooseReferenceFile.this.onFileSelectedListener);

                // Testing picture storage directory (Gary)
                String referencePathTemp; // this only gives a reference point for the user to search from
                referencePathTemp = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                dialog.loadFolder(referencePathTemp);

                // Define the filter for selecting .pasc files
                dialog.setFilter(".*parr");
                dialog.setShowOnlySelectable(false);

                // Show the dialog.
                dialog.show();

            }
        });
		
		chooseReferenceFileButtonProceed.setOnClickListener(new OnClickListener(){
			
			public void onClick(View view3) {
				if(isFilePASCSelected == true && isFilePARRSelected == true){
					Bundle b1 = new Bundle();
					b1.putStringArray(STANDARD_CURVE_INFO, standardCurveInfo);
					b1.putStringArray(REACTION_INFO, sampleInfo);
					b1.putString(SOURCE_TYPE, sourceType);
					i.putExtras(b1);
					startActivity(i);
				}
			}
		});
	}
	
	private FileChooserDialog.OnFileSelectedListener onFileSelectedListener = new FileChooserDialog.OnFileSelectedListener() {
		public void onFileSelected(Dialog source, File file) {
			source.hide();
			Toast toast = Toast.makeText(ChooseReferenceFile.this, "File selected: " + file.getName(), Toast.LENGTH_LONG);
			toast.show();			
			tempName = file.getName();
			tempPath = file.getPath();
			tempNameAndPath = tempPath + tempName;
            String extension = tempNameAndPath.substring(tempNameAndPath.lastIndexOf(".") + 1, tempNameAndPath.length());
            if(extension.equals("pasc")){
                standardCurveInfo[0] = tempName;
                standardCurveInfo[1] = tempPath;
                standardCurveInfo[2] = tempNameAndPath;
                Log.d("for pasc");
                Log.d(tempNameAndPath);
				Log.d(tempPath);
                isFilePASCSelected = true;
                chooseReferenceFileTextViewPASCSelected.setText("File selected:  " + tempName);
            } else if(extension.equals("parr")){
                sampleInfo[0] = tempName;
                sampleInfo[1] = tempPath;
                sampleInfo[2] = tempNameAndPath;
                Log.d("for parr");
                Log.d(tempNameAndPath);
				Log.d(tempPath);
                isFilePARRSelected = true;
                chooseReferenceFileTextViewPARRSelected.setText("File selected:  " + tempName);
            }

            /*
			referenceInfo[0] = referenceName;
			referenceInfo[1] = referencePath;
			referenceInfo[2] = referenceNameAndPath;
			*/


		}
		
		public void onFileSelected(Dialog source, File folder, String name) {
			source.hide();
			Toast toast = Toast.makeText(ChooseReferenceFile.this, "File created: " + folder.getName() + "/" + name, Toast.LENGTH_LONG);
			toast.show();
			// pathTest.setText(folder.getName());
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_reference_file, menu);
		return true;
	}
}
