package com.garynfox.pathogenanalyzer;

import static com.garynfox.pathogenanalyzer.CameraHelper.cameraAvailable;
import static com.garynfox.pathogenanalyzer.CameraHelper.getCameraInstance;
import static com.garynfox.pathogenanalyzer.MediaHelper.getOutputMediaFile;
import static com.garynfox.pathogenanalyzer.MediaHelper.saveToFile;

import java.io.File;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.AutoFocusCallback;

import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class PrepareRecording extends Activity implements PictureCallback {

	// Value pair for bundles information sent to recording activity
	public final static String STANDARD_CURVE_INFO = "com.garynfox.pathogenanalyzer.STANDARD_CURVE_INFO";	
	public final static String REACTION_INFO = "com.garynfox.pathogenanalyzer.REACTION_INFO";
	public final static String REFERENCE_INFO = "com.garynfox.pathogenanalyzer.REFERENCE_INFO";
	public final static String RECORDING_TYPE = "com.garynfox.pathogenanalyzer.RECORDING_TYPE";
	public final static String SOURCE_TYPE = "com.garynfox.pathogenanalyzer.SOURCE_TYPE";
	public final static String OFFSET_TIME = "com.garynfox.pathogenanalyzer.OFFSET_TIME";
	public final static int STANDARD_CURVE = 1;
	public final static int SAMPLE = 2;
	
	// And the actual contents of the bundle
	static String[] standardCurveInfo = new String[3];
	static String[] sampleInfo = new String[3];
	static String[] referenceInfo = new String[3];
	static int recordingType = 999;
	static Integer offsetTimeInt = 0;

	String sourceType;
	
	// Declare buttons	
    Button prepareRecordingButtonStartRecordingReaction;
    Button prepareRecordingButtonFocusImage;
    
    // Declare Text View for test of passing from bundles
    // TextView textViewTest;
    
    // Unsure what this is for, but using it I think as a path identifier
    protected static final String EXTRA_IMAGE_PATH = "com.garynfox.cameratest.EXTRA_IMAGE_PATH";       
    
    // Declare camera objects, preview and the camera itself
    private Camera camera;
	private CameraPreview cameraPreview;

	// adds the time to the total computation
	private Chronometer chrono;
    
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        hideButtons();
		// Get values from bundle
		Intent intent = getIntent();
		Bundle bundleReceived = intent.getExtras();
		
		standardCurveInfo = bundleReceived.getStringArray(ChooseAction.STANDARD_CURVE_INFO);
		sampleInfo = bundleReceived.getStringArray(ChooseAction.REACTION_INFO);
		referenceInfo = bundleReceived.getStringArray(ChooseAction.REFERENCE_INFO);
		recordingType = bundleReceived.getInt(ChooseAction.RECORDING_TYPE);
		sourceType = bundleReceived.getString(ChooseAction.SOURCE_TYPE);
		/*
		String standardCurvePath = b.getString(ChooseAction.STANDARD_CURVE_PATH);
		String reactionName = b.getString(ChooseAction.REACTION_NAME);
		String reactionPath = b.getString(ChooseAction.REACTION_PATH);
		*/
		
		// Inflate the GUI
		setContentView(R.layout.activity_prepare_recording);

		chrono = (Chronometer) findViewById(R.id.prepareRecordingChrono);
		chrono.start();

        hideButtons();

        setResult(RESULT_CANCELED);
		prepareRecordingButtonStartRecordingReaction = (Button) findViewById(R.id.prepareRecordingButtonStartRecordingReaction);
		prepareRecordingButtonFocusImage = (Button) findViewById(R.id.prepareRecordingButtonFocusImage);
		// textViewTest = (TextView) findViewById(R.id.textViewTest);
		
		// set path on screen for testing purposes
		/*
		if(recordingType == STANDARD_CURVE) {
			textViewTest.setText(standardCurveInfo[1]);
		} else if(recordingType == SAMPLE) {
			textViewTest.setText(sampleInfo[1]);
		}
		*/

		// call button listener method
		setButtonOnClickListeners();
		camera = getCameraInstance();
		
		// Instructional message
		Toast.makeText(getApplicationContext(), "Please be sure to read the yellow instructions, they are very important for getting great results!", Toast.LENGTH_LONG).show();
		
		// checks to see if camera is available before instantiating it
		if(cameraAvailable(camera)){
			initCameraPreview();
		} else {
			finish();
		}

        hideButtons();

	}
    
    // Show the camera view on the activity
 	private void initCameraPreview() {
 		cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
 		cameraPreview.init(camera);
 	}
 	
 	// When a picture is taken
 	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
 		Log.d("Picture taken" , "");
		String path = savePictureToFileSystem(data);
		setResult(path);
		finish();
	}

 	private static String savePictureToFileSystem(byte[] data) {
		File file = getOutputMediaFile(standardCurveInfo[1]);
		saveToFile(data, file);
		return file.getAbsolutePath();
	}
	

	
	private void setResult(String path) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_IMAGE_PATH, path);
		setResult(RESULT_OK, intent);
	}
	

	// Don't want any other applications to touch camera
	// ALWAYS remember to release the camera when you are finished
	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
	}
	
	
	/* // re-instantiating the camera after a pause
	@Override
	protected void onResume() {
		super.onResume();
		try {
			camera.reconnect();
		} catch (IOException e) {
			Log.d("camera reconnect failed", e);
		}
	}
	*/
	
	
	 // UNSURE IF WISE TO COMPLETELY DISCONNECT CAMERA OR NOT
	private void releaseCamera() {
		if(camera != null){
			camera.release();
			camera = null;
		}
	}
	
 	
	private void setButtonOnClickListeners(){
		
		// Launch recording reaction screen
		prepareRecordingButtonStartRecordingReaction.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {

				// Get elapsed time since this screen launched for computation later
				long timeElapsed = SystemClock.elapsedRealtime() - chrono.getBase();
				long offsetTimeLong = timeElapsed / 1000;
				offsetTimeInt = (int) (long) offsetTimeLong;

				Log.d("Offset time: ", offsetTimeInt + "");

				// Launch recording reaction screen
				Intent i1 = new Intent(PrepareRecording.this, RecordingReaction.class);
				// After picture is decided on, sends user to recording screen which will automatically take the photos
				// Necessary to include the same bundle of filename and path
				Bundle b1 = new Bundle();
				b1.putStringArray(STANDARD_CURVE_INFO, standardCurveInfo);
				b1.putStringArray(REFERENCE_INFO, referenceInfo);
				b1.putStringArray(REACTION_INFO, sampleInfo);
				b1.putInt(RECORDING_TYPE, recordingType);
				b1.putInt(OFFSET_TIME, offsetTimeInt);
				b1.putString(SOURCE_TYPE, sourceType);
				i1.putExtras(b1);
				startActivity(i1);	
			}		
		});	
		
		prepareRecordingButtonFocusImage.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View view){
			Parameters params = camera.getParameters();
            List focusModesList = params.getSupportedFocusModes();
            String focusModesString = focusModesList.toString();
            Log.d("Focus Modes", focusModesString);
            List whiteBalanceModesList = params.getSupportedWhiteBalance();
            String whiteBalanceModesString = whiteBalanceModesList.toString();
            Log.d("White Bal Prep Rec", whiteBalanceModesString);
			params.setFocusMode("auto");
            params.setWhiteBalance("fluorescent");
			camera.setParameters(params);
            camera.autoFocus(myAutoFocusCallback);




			}
		});
	
	}

    private void hideButtons() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

        @Override
        public void onAutoFocus(boolean autoFocusSuccess, Camera camera) {
            // after autofocus occurs, lock the exposure and white balance settings
            if(autoFocusSuccess == true){
                Parameters params1 = camera.getParameters();
                float focusDistances[] = new float[3];
                params1.getFocusDistances(focusDistances);
                Log.d("after auto focus, exposure lock state is", params1.getAutoExposureLock()+"");
                Log.d("Autofocus Prepare Recording", "camera is auto focused");
                Log.d("Focal Length After AutoFocus", params1.getFocalLength()+"");
                Log.d("Focus Distances After AutoFocus", focusDistances.toString());
                params1.setAutoExposureLock(true);
                camera.setParameters(params1);
                Log.d("after forcing exposure lock to locked", params1.getAutoExposureLock()+"");
                // Log.d("AutoExposureLock", "auto exposure is locked");
            }
        }};

    /* @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prepare_recording, menu);
		return true;
	} */
}