package com.garynfox.pathogenanalyzer;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserDialog;

public class ChooseAction extends Activity {

	// Value pairs needed to pass bundle along
	public final static String STANDARD_CURVE_INFO = "com.garynfox.pathogenanalyzer.STANDARD_CURVE_INFO";	
	public final static String REACTION_INFO = "com.garynfox.pathogenanalyzer.REACTION_INFO";
	public final static String REFERENCE_INFO = "com.garynfox.pathogenanalyzer.REFERENCE_INFO";
	public final static String RECORDING_TYPE = "com.garynfox.pathogenanalyzer.RECORDING_TYPE";
	public final static String SOURCE_TYPE = "com.garynfox.pathogenanalyzer.SOURCE_TYPE";
	public final static int STANDARD_CURVE = 1;
	public final static int SAMPLE = 2;

	
	// Declare buttons	
	Button chooseActionButtonRecordStandardCurve;
	// Button chooseActionButtonViewStandardCurve;
	// Button chooseActionButtonViewSample;
	Button chooseActionButtonRecordSample;
	Button chooseActionButtonProceedResults;
	Button chooseActionButtonBack;
	
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
	String sourceType;
	
	// What type of recording will this be
	int recordingType = 999;
	
	// Bundle of all of it
	String[] standardCurveInfo = new String[3];
	String[] sampleInfo = new String[3];
	String[] referenceInfo = new String[3];
	
	// Edit Texts for dialog prompts
	EditText test;
	
	// don't want to launch next screen unless file is selected
	boolean isFileSelected = false;

	// For some reason this line needed to execute custom dialog inflater
	final Context context = this;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent previousScreen = getIntent();

		// Unpacking bundle from previous intent, mainly to pass to next intent
		Bundle bundleReceived = previousScreen.getExtras();
		sourceType = bundleReceived.getString(ChooseSource.SOURCE_TYPE);
		Log.d("reaction source type", sourceType + "");

		// Inflate the GUI
		setContentView(R.layout.activity_choose_action);
		
		// Establish buttons for listening
		chooseActionButtonRecordStandardCurve = (Button) findViewById(R.id.chooseActionButtonRecordStandardCurve);
		// chooseActionButtonViewStandardCurve = (Button) findViewById(R.id.chooseActionButtonViewStandardCurve);
		chooseActionButtonRecordSample = (Button) findViewById(R.id.chooseActionButtonRecordSample);
		// chooseActionButtonViewSample = (Button) findViewById(R.id.chooseActionButtonViewSample);
		chooseActionButtonBack = (Button) findViewById(R.id.chooseActionButtonBack);
		chooseActionButtonProceedResults = (Button) findViewById(R.id.chooseActionButtonProceedResults);
				
		// EditTexts for entering new file names (not displaying on this screen)
		// test = (EditText) findViewById(R.id.testPathDisplay);
		
		// call button listener method
		setButtonOnClickListeners();			
	}  

	private void setButtonOnClickListeners(){
		
		// Set up intent to launch recording reaction screen and to choose reference if that is selected
		final Intent intentChooseReference = new Intent(ChooseAction.this, ChooseReferenceFile.class);
		final Intent intentPrepareRecording = new Intent(ChooseAction.this, PrepareRecording.class);
		final Intent intentViewResults = new Intent(ChooseAction.this, ChooseReferenceFile.class);
		
		// Create new ".pasc" file and directory if a new standard curve is needed
		chooseActionButtonRecordStandardCurve.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view3) {
							
				// Launch Dialog to create file name for standard curve
				// Create dialog_choose_action... etc... xml view
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				View promptView = layoutInflater.inflate(R.layout.dialog_choose_action_enter_standard_curve_name, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				
				// set dialog_choose_action... etc... xml to be the layout file of the alertdialog builder
				alertDialogBuilder.setView(promptView);
				final EditText chooseActionEditTextEnterStandardCurveName = (EditText) promptView.findViewById(R.id.chooseActionEditTextEnterStandardCurveName);
				
				// sets up the dialog window
				alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						
						// inform the program if it is a standard curve or sample
						recordingType = STANDARD_CURVE;
						
						// get user input and set it to result
						standardCurveName = chooseActionEditTextEnterStandardCurveName.getText().toString();						
					
						// name is set, create directory and file in that directory of that name
						File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
					               Environment.DIRECTORY_PICTURES), standardCurveName);
						if (! mediaStorageDir.exists()){
					         if (! mediaStorageDir.mkdirs()){
					             Log.d("PathogenAnalyzer", "failed to create directory");
					         }
					    }
						
						// Create the standard curve file, and strings for the path as well as the name and path as one string
						File standardCurveFileNameAndPath;
						standardCurveFileNameAndPath = new File(mediaStorageDir.getPath() + File.separator + sourceType + "_" + standardCurveName + ".pasc");
						standardCurveNameAndPath = standardCurveFileNameAndPath.toString();
						File standardCurveFilePath;
						standardCurveFilePath = new File(mediaStorageDir.getPath());
						standardCurvePath = standardCurveFilePath.toString();
						
						try {
							FileOutputStream fos = new FileOutputStream(standardCurveFileNameAndPath);
							fos.write(standardCurvePath.getBytes());
							fos.close();
						} catch (FileNotFoundException e) {
							Log.d("PathogenAnalyzer", "File Not Found");
						} catch (IOException e) {
							Log.d("PathogenAnalyzer", "Error accessing file");
						}
											
						// test.setText(standardCurveName);
						
						// prepare the bundle to send the path of the filename to so the recording photos go in the correct directory												
						standardCurveInfo[0] = standardCurveName;
						standardCurveInfo[1] = standardCurvePath;
						standardCurveInfo[2] = standardCurveNameAndPath;
						Bundle b1 = new Bundle();
						b1.putStringArray(STANDARD_CURVE_INFO, standardCurveInfo);
						b1.putStringArray(REACTION_INFO, sampleInfo);
						b1.putStringArray(REFERENCE_INFO, referenceInfo);
						b1.putInt(RECORDING_TYPE, recordingType);
						b1.putString(SOURCE_TYPE, sourceType);
						intentPrepareRecording.putExtras(b1);
						
						startActivity(intentPrepareRecording);																		
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				
				// create an alert dialog
				AlertDialog alertD = alertDialogBuilder.create();
				alertD.show();			
			}		
		});

        // Create new ".parr" file and directory for the run
        chooseActionButtonRecordSample.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view4) {

                // Launch Dialog to create file name for sample run
                // Create dialog_choose_action... etc... xml view
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View promptView = layoutInflater.inflate(R.layout.dialog_choose_action_enter_reaction_name, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set dialog_choose_action... etc... xml to be the layout file of the alertdialog builder
                alertDialogBuilder.setView(promptView);
                final EditText chooseActionEditTextEnterReactionName = (EditText) promptView.findViewById(R.id.chooseActionEditTextEnterReactionName);

                // sets up the dialog window
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // inform the program if it is a standard curve or sample
                        recordingType = SAMPLE;

                        // get user input and set it to result
                        sampleName = chooseActionEditTextEnterReactionName.getText().toString();

                        // name is set, create directory and file in that directory of that name
                        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), sampleName);
                        if (! mediaStorageDir.exists()){
                            if (! mediaStorageDir.mkdirs()){
                                Log.d("PathogenAnalyzer", "failed to create directory");
                            }
                        }

                        // Create the sample file, and strings for the path as well as the name and path as one string
                        File sampleFileNameAndPath;
                        sampleFileNameAndPath = new File(mediaStorageDir.getPath() + File.separator + sourceType + "_" + sampleName + ".parr");
                        sampleNameAndPath = sampleFileNameAndPath.toString();
                        File sampleFilePath;
                        sampleFilePath = new File(mediaStorageDir.getPath());
                        samplePath = sampleFilePath.toString();

                        try {
                            FileOutputStream fos = new FileOutputStream(sampleFileNameAndPath);
                            fos.write(samplePath.getBytes());
                            fos.close();
                        } catch (FileNotFoundException e) {
                            Log.d("PathogenAnalyzer", "File Not Found");
                        } catch (IOException e) {
                            Log.d("PathogenAnalyzer", "Error accessing file");
                        }

                        // test.setText(sampleName);

                        // prepare the bundle to send the path of the filename to so the recording photos go in the correct directory
                        sampleInfo[0] = sampleName;
                        sampleInfo[1] = samplePath;
                        sampleInfo[2] = sampleNameAndPath;
                        Bundle b1 = new Bundle();
                        b1.putStringArray(STANDARD_CURVE_INFO, standardCurveInfo);
                        b1.putStringArray(REACTION_INFO, sampleInfo);
                        b1.putStringArray(REFERENCE_INFO, referenceInfo);
                        b1.putInt(RECORDING_TYPE, recordingType);
						b1.putString(SOURCE_TYPE, sourceType);
                        intentPrepareRecording.putExtras(b1);

                        startActivity(intentPrepareRecording);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                // create an alert dialog
                AlertDialog alertD = alertDialogBuilder.create();
                alertD.show();
            }
        });

        /*
		// Create new ".parr" file and directory and select a standard curve for reference
		chooseActionButtonRecordSample.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view3) {
							
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				View promptView = layoutInflater.inflate(R.layout.dialog_choose_action_enter_reaction_name, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				
				// set dialog_choose_action... etc... xml to be the layout file of the alertdialog builder
				alertDialogBuilder.setView(promptView);
				final EditText chooseActionEditTextEnterReactionName = (EditText) promptView.findViewById(R.id.chooseActionEditTextEnterReactionName);
				
				// sets up the dialog window
				alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						
						// inform the program if it is a standard curve or sample
						recordingType = SAMPLE;					
						
						// get user input and set it to result
						sampleName = chooseActionEditTextEnterReactionName.getText().toString();						
					
						// name is set, create directory and file in that directory of that name
						File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
					               Environment.DIRECTORY_PICTURES), sampleName);
						if (! mediaStorageDir.exists()){
					         if (! mediaStorageDir.mkdirs()){
					             Log.d("PathogenAnalyzer", "failed to create directory");
					         }
					    }
						
						// Create the standard curve file, and strings for the path as well as the name and path as one string
						File sampleFileNameAndPath;
						sampleFileNameAndPath = new File(mediaStorageDir.getPath() + File.separator + sampleName + ".parr");						
						sampleNameAndPath = sampleFileNameAndPath.toString();
						File sampleFilePath;
						sampleFilePath = new File(mediaStorageDir.getPath());
						samplePath = sampleFilePath.toString();
						
						try {
							FileOutputStream fos = new FileOutputStream(sampleFileNameAndPath);
							fos.write(samplePath.getBytes());
							fos.close();
						} catch (FileNotFoundException e) {
							Log.d("PathogenAnalyzer", "File Not Found");
						} catch (IOException e) {
							Log.d("PathogenAnalyzer", "Error accessing file");
						}
											
						test.setText(sampleName);										
						
						// prepare the bundle to send the path of the filename to so the recording photos go in the correct directory												
						sampleInfo[0] = sampleName;
						sampleInfo[1] = samplePath;
						sampleInfo[2] = sampleNameAndPath;
						Bundle b2 = new Bundle();
						b2.putStringArray(STANDARD_CURVE_INFO, standardCurveInfo);
						b2.putStringArray(REACTION_INFO, sampleInfo);
						b2.putStringArray(REFERENCE_INFO, referenceInfo);
						b2.putInt(RECORDING_TYPE, recordingType);
						intentChooseReference.putExtras(b2);
						
						startActivity(intentChooseReference);																		
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				
				// create an alert dialog
				AlertDialog alertD = alertDialogBuilder.create();
				alertD.show();			
			}		
		});
		*/
		

		/*
		// View standard curve results ".pasc" file
		chooseActionButtonViewStandardCurve.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view3) {
				// Launch Dialog to create file name for sample but only after a reference standard curve has been selected
				FileChooserDialog dialog = new FileChooserDialog(ChooseAction.this);
	    		
	    		// Assign listener for the select event.
	    		dialog.addListener(ChooseAction.this.onFileSelectedListener);  		
	    		
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
		*/

		/*
		// View sample results ".parr" file
		chooseActionButtonViewSample.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view3) {
				// Launch Dialog to create file name for sample but only after a reference standard curve has been selected
				FileChooserDialog dialog = new FileChooserDialog(ChooseAction.this);
	    		
	    		// Assign listener for the select event.
	    		dialog.addListener(ChooseAction.this.onFileSelectedListener);  		
	    		
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
		*/

		// Pack up the bundle with data and send it to the view results screen if results have been picked
		chooseActionButtonProceedResults.setOnClickListener(new OnClickListener(){
			
			public void onClick(View view3) {

                Bundle b3 = new Bundle();
			    b3.putStringArray(STANDARD_CURVE_INFO, standardCurveInfo);
				b3.putStringArray(REFERENCE_INFO, referenceInfo);
				b3.putStringArray(REACTION_INFO, sampleInfo);
				b3.putInt(RECORDING_TYPE, recordingType);
				b3.putString(SOURCE_TYPE, sourceType);
				android.util.Log.d("checking results bundle", "" + sourceType);
				intentViewResults.putExtras(b3);
                startActivity(intentViewResults);


			}
		});
		
		// Take user back to home screen if back is clicked
		chooseActionButtonBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view2) {
				// Intent to launch Tutorial 1 of 4 screen (activity)
				Intent i2 = new Intent(ChooseAction.this, HomeScreen.class);
				startActivity(i2);	
			}		
		});
				
	}
	
	private FileChooserDialog.OnFileSelectedListener onFileSelectedListener = new FileChooserDialog.OnFileSelectedListener() {
		public void onFileSelected(Dialog source, File file) {
			source.hide();
			Toast toast = Toast.makeText(ChooseAction.this, "File selected: " + file.getName(), Toast.LENGTH_LONG);
			toast.show();
			String filenameArray[] = file.getName().split("\\.");
			String extension = filenameArray[filenameArray.length-1];
			Toast toast1 = Toast.makeText(ChooseAction.this, filenameArray[1], Toast.LENGTH_LONG);
			toast1.show();
			if(extension.equals("pasc")) {		
				standardCurveName = file.getName();
				standardCurvePath = file.getPath();
				standardCurveNameAndPath = standardCurvePath + standardCurveName;
				standardCurveInfo[0] = standardCurveName;
				standardCurveInfo[1] = standardCurvePath;
				standardCurveInfo[2] = standardCurveNameAndPath;
				recordingType = STANDARD_CURVE;
				//test.setText("File selected:  " + standardCurveName);
				isFileSelected = true;			
			} else if(extension.equals("parr")) {
				sampleName = file.getName();
				samplePath = file.getPath();
				sampleNameAndPath = samplePath + sampleName;
				sampleInfo[0] = sampleName;
				sampleInfo[1] = samplePath;
				sampleInfo[2] = sampleNameAndPath;
				recordingType = SAMPLE;
				//test.setText("File selected:  " + sampleName);
				isFileSelected = true;
			}
		}
		
		public void onFileSelected(Dialog source, File folder, String name) {
			source.hide();
			Toast toast = Toast.makeText(ChooseAction.this, "File created: " + folder.getName() + "/" + name, Toast.LENGTH_LONG);
			toast.show();
			// pathTest.setText(folder.getName());
		}
	};
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_action, menu);
		return true;
	}
}
