package com.garynfox.pathogenanalyzer;


import static com.garynfox.pathogenanalyzer.CameraHelper.cameraAvailable;
import static com.garynfox.pathogenanalyzer.CameraHelper.getCameraInstance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
// import org.opencv.highgui.Highgui;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import au.com.bytecode.opencsv.CSVWriter;

public class RecordingReaction extends Activity {
	
	/*
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.d("Load openCV", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    */

	
	
	// Value pairs needed to pass bundle along
	public final static String STANDARD_CURVE_INFO = "com.garynfox.pathogenanalyzer.STANDARD_CURVE_INFO";	
	public final static String REACTION_INFO = "com.garynfox.pathogenanalyzer.REACTION_INFO";
	public final static String REFERENCE_INFO = "com.garynfox.pathogenanalyzer.REFERENCE_INFO";
	public final static String RECORDING_TYPE = "com.garynfox.pathogenanalyzer.RECORDING_TYPE";
	public final static String SOURCE_TYPE = "com.garynfox.pathogenanalyzer.SOURCE_TYPE";
	public final static int STANDARD_CURVE = 1;
	public final static int SAMPLE = 2;
	public final static String TIMECOUNT = "com.garynfox.pathogenanalyzer.TIMECOUNT";
	public final static String DATA_POINTS = "com.garynfox.pathogenanalyzer.DATA_POINTS";
	public final static int TIME_OFFSET = 0;
	public final static int BLOOD = 1;
	public final static int URINE = 2;
	public final static int FECES = 3;
	public final static int OTHER = 4;
	
	// Stuff being packed into the bundle
	static String[] standardCurveInfo = new String[3];
	static String[] sampleInfo = new String[3];
	static String[] referenceInfo = new String[3];
	static int recordingType = 999;
	static int offsetTimeInt = 0;

	// load openCV manager statically without using the manager
	static {
		if (!OpenCVLoader.initDebug()) {
			// Handle initialization error
		}
	}
	
	// Strings
	String standardCurveName;			// 0
	String standardCurvePath;			// 1
	String standardCurveNameAndPath;	// 2
	String sampleName;					// 0
	String samplePath;					// 1
	String sampleNameAndPath;			// 2
	String referenceName;				// 0
	String referencePath;				// 1
	String referenceNameAndPath;		// 2
	String tempPhotoPath;               // the path loaded into the computation matrix
	String sourceType;
	
	// doubles with the vial intensity at different times, needed for scope
	double sampleA1Intensity = 0;
	double sampleA2Intensity = 0;
	double sampleA3Intensity = 0;
	double sampleA4Intensity = 0;
	double sampleA5Intensity = 0;
	double sampleA6Intensity = 0;
	double sampleB1Intensity = 0;
	double sampleB2Intensity = 0;
	double sampleB3Intensity = 0;
	double sampleB4Intensity = 0;
	double sampleB5Intensity = 0;
	double sampleB6Intensity = 0;
	double sampleC1Intensity = 0;
	double sampleC2Intensity = 0;
	double sampleC3Intensity = 0;
	double sampleC4Intensity = 0;
	double sampleC5Intensity = 0;
	double sampleC6Intensity = 0;
	double sampleD1Intensity = 0;
	double sampleD2Intensity = 0;
	double sampleD3Intensity = 0;
	double sampleD4Intensity = 0;
	double sampleD5Intensity = 0;
	double sampleD6Intensity = 0;
	double sampleE1Intensity = 0;
	double sampleE2Intensity = 0;
	double sampleE3Intensity = 0;
	double sampleE4Intensity = 0;
	double sampleE5Intensity = 0;
	double sampleE6Intensity = 0;
	double sampleF1Intensity = 0;
	double sampleF2Intensity = 0;
	double sampleF3Intensity = 0;
	double sampleF4Intensity = 0;
	double sampleF5Intensity = 0;
	double sampleF6Intensity = 0;
	String[] sampleValuesString = new String[40];
	
	// versions to display on the screen
	int sampleA1Display = 0;
	int sampleA2Display = 0;
	int sampleA3Display = 0;
	int sampleA4Display = 0;
	int sampleA5Display = 0;
	int sampleA6Display = 0;
	int sampleB1Display = 0;
	int sampleB2Display = 0;
	int sampleB3Display = 0;
	int sampleB4Display = 0;
	int sampleB5Display = 0;
	int sampleB6Display = 0;
	int sampleC1Display = 0;
	int sampleC2Display = 0;
	int sampleC3Display = 0;
	int sampleC4Display = 0;
	int sampleC5Display = 0;
	int sampleC6Display = 0;
	int sampleD1Display = 0;
	int sampleD2Display = 0;
	int sampleD3Display = 0;
	int sampleD4Display = 0;
	int sampleD5Display = 0;
	int sampleD6Display = 0;
	int sampleE1Display = 0;
	int sampleE2Display = 0;
	int sampleE3Display = 0;
	int sampleE4Display = 0;
	int sampleE5Display = 0;
	int sampleE6Display = 0;
	int sampleF1Display = 0;
	int sampleF2Display = 0;
	int sampleF3Display = 0;
	int sampleF4Display = 0;
	int sampleF5Display = 0;
	int sampleF6Display = 0;
	
	// number of pictures taken = 50 min * 6 per minute = 300
	int dataPoints = 300;
		
	// Declare cancel button
	// Button recordingReactionButtonCancel;

	// Declare text view displaying realtime intensity changes
	TextView recordingReactionTextViewDisplayIntensityA1;
	TextView recordingReactionTextViewDisplayIntensityA2;
	TextView recordingReactionTextViewDisplayIntensityA3;
	TextView recordingReactionTextViewDisplayIntensityA4;
	TextView recordingReactionTextViewDisplayIntensityA5;
	TextView recordingReactionTextViewDisplayIntensityA6;
	TextView recordingReactionTextViewDisplayIntensityB1;
	TextView recordingReactionTextViewDisplayIntensityB2;
	TextView recordingReactionTextViewDisplayIntensityB3;
	TextView recordingReactionTextViewDisplayIntensityB4;
	TextView recordingReactionTextViewDisplayIntensityB5;
	TextView recordingReactionTextViewDisplayIntensityB6;
	TextView recordingReactionTextViewDisplayIntensityC1;
	TextView recordingReactionTextViewDisplayIntensityC2;
	TextView recordingReactionTextViewDisplayIntensityC3;
	TextView recordingReactionTextViewDisplayIntensityC4;
	TextView recordingReactionTextViewDisplayIntensityC5;
	TextView recordingReactionTextViewDisplayIntensityC6;
	TextView recordingReactionTextViewDisplayIntensityD1;
	TextView recordingReactionTextViewDisplayIntensityD2;
	TextView recordingReactionTextViewDisplayIntensityD3;
	TextView recordingReactionTextViewDisplayIntensityD4;
	TextView recordingReactionTextViewDisplayIntensityD5;
	TextView recordingReactionTextViewDisplayIntensityD6;
	TextView recordingReactionTextViewDisplayIntensityE1;
	TextView recordingReactionTextViewDisplayIntensityE2;
	TextView recordingReactionTextViewDisplayIntensityE3;
	TextView recordingReactionTextViewDisplayIntensityE4;
	TextView recordingReactionTextViewDisplayIntensityE5;
	TextView recordingReactionTextViewDisplayIntensityE6;
	TextView recordingReactionTextViewDisplayIntensityF1;
	TextView recordingReactionTextViewDisplayIntensityF2;
	TextView recordingReactionTextViewDisplayIntensityF3;
	TextView recordingReactionTextViewDisplayIntensityF4;
	TextView recordingReactionTextViewDisplayIntensityF5;
	TextView recordingReactionTextViewDisplayIntensityF6;
	
	TextView recordingReactionTextTimeLeft;

	// Declare camera objects, preview and the camera itself
    private Camera camera;
	private CameraPreview cameraPreview;
	
	// trying out the csvWriter global stuff
	CSVWriter csvWriter;
	String dataPath = "dummy";
	
	// time stamp for data
	int timeCount = 0;
	
	// must have focused before taking photo
	// boolean isAutoFocused = false;
	
	MyCountDownTimer countDownTimer2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        hideButtons();

		Log.d("verify",String.valueOf(OpenCVLoader.initDebug()));

        // Unpacking bundle from previous intent, mainly to pass to next intent
		Intent previousScreen = getIntent();
		Bundle bundleReceived = previousScreen.getExtras();
				
		standardCurveInfo = bundleReceived.getStringArray(ChooseAction.STANDARD_CURVE_INFO);
		sampleInfo = bundleReceived.getStringArray(ChooseAction.REACTION_INFO);
		referenceInfo = bundleReceived.getStringArray(ChooseAction.REFERENCE_INFO);
		recordingType = bundleReceived.getInt(ChooseAction.RECORDING_TYPE);
		offsetTimeInt = bundleReceived.getInt(PrepareRecording.OFFSET_TIME);

		// set the timecount to the offset time for computation
		timeCount = offsetTimeInt;

		sourceType = bundleReceived.getString(ChooseAction.SOURCE_TYPE);
		
		// Inflate the GUI
		setContentView(R.layout.activity_recording_reaction);
		
		// Determine reaction type
		if(recordingType == STANDARD_CURVE){
			dataPath = standardCurveInfo[2];
		} else if(recordingType == SAMPLE){
			dataPath = sampleInfo[2];
		}
		String[] entries = "A1#A2#A3#A4#A5#A6#B1#B2#B3#B4#B5#B6#C1#C2#C3#C4#C5#C6#D1#D2#D3#D4#D5#D6#E1#E2#E3#E4#E5#E6#F1#F2#F3#F4#F5#F6#time#focus_dist1#focus_dist2#focus_dist3".split("#");
					
		try {
			csvWriter = new CSVWriter(new FileWriter(dataPath));
			csvWriter.writeNext(entries);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// display the text view that shows intensity
		recordingReactionTextViewDisplayIntensityA1 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityA1);
		recordingReactionTextViewDisplayIntensityA2 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityA2);
		recordingReactionTextViewDisplayIntensityA3 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityA3);
		recordingReactionTextViewDisplayIntensityA4 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityA4);
		recordingReactionTextViewDisplayIntensityA5 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityA5);
		recordingReactionTextViewDisplayIntensityA6 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityA6);
		recordingReactionTextViewDisplayIntensityB1 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityB1);
		recordingReactionTextViewDisplayIntensityB2 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityB2);
		recordingReactionTextViewDisplayIntensityB3 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityB3);
		recordingReactionTextViewDisplayIntensityB4 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityB4);
		recordingReactionTextViewDisplayIntensityB5 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityB5);
		recordingReactionTextViewDisplayIntensityB6 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityB6);
		recordingReactionTextViewDisplayIntensityC1 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityC1);
		recordingReactionTextViewDisplayIntensityC2 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityC2);
		recordingReactionTextViewDisplayIntensityC3 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityC3);
		recordingReactionTextViewDisplayIntensityC4 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityC4);
		recordingReactionTextViewDisplayIntensityC5 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityC5);
		recordingReactionTextViewDisplayIntensityC6 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityC6);
		recordingReactionTextViewDisplayIntensityD1 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityD1);
		recordingReactionTextViewDisplayIntensityD2 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityD2);
		recordingReactionTextViewDisplayIntensityD3 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityD3);
		recordingReactionTextViewDisplayIntensityD4 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityD4);
		recordingReactionTextViewDisplayIntensityD5 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityD5);
		recordingReactionTextViewDisplayIntensityD6 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityD6);
		recordingReactionTextViewDisplayIntensityE1 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityE1);
		recordingReactionTextViewDisplayIntensityE2 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityE2);
		recordingReactionTextViewDisplayIntensityE3 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityE3);
		recordingReactionTextViewDisplayIntensityE4 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityE4);
		recordingReactionTextViewDisplayIntensityE5 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityE5);
		recordingReactionTextViewDisplayIntensityE6 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityE6);
		recordingReactionTextViewDisplayIntensityF1 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityF1);
		recordingReactionTextViewDisplayIntensityF2 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityF2);
		recordingReactionTextViewDisplayIntensityF3 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityF3);
		recordingReactionTextViewDisplayIntensityF4 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityF4);
		recordingReactionTextViewDisplayIntensityF5 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityF5);
		recordingReactionTextViewDisplayIntensityF6 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensityF6);
		
		recordingReactionTextTimeLeft = (TextView) findViewById(R.id.recordingReactionTextTimeLeft);
		
		// call button listener method
		//recordingReactionButtonCancel = (Button) findViewById(R.id.recordingReactionButtonCancel);
		// setButtonOnClickListeners();
		
		// initialize the camera
		camera = getCameraInstance();
		
		// checks to see if camera is available before instantiating it
		if(cameraAvailable(camera)){
			initCameraPreview();
		} else {
			finish();
		}

        Parameters params = camera.getParameters();
        Log.d("Focal Length pre 1 pic", params.getFocalLength()+"");
		//Log.d("Focal Length pre 1 pic", params.getFocalLength()+"");

        params.setJpegQuality(100);
        params.setFocusMode("auto");
        //params.setWhiteBalance("fluorescent");
        params.setAutoWhiteBalanceLock(true);
        params.setPictureSize(4032, 3024);
        Log.d("White Balance RECORDING", params.getWhiteBalance());
        Log.d("Focus Mode RECORDING", params.getFocusMode());
        camera.setParameters(params);
        camera.autoFocus(myAutoFocusCallback);

        countDownTimer2 = new MyCountDownTimer(3000000, 10000);

        // countDownTimer2 = new MyCountDownTimer(180000, 5000);

        RefreshTimer();
        countDownTimer2.Start();

		// Instructional message
		Toast.makeText(getApplicationContext(), "Do not move device! Recording in progress!", Toast.LENGTH_LONG).show();

	}
		
	// Show the camera view on the activity
	private void initCameraPreview() {
		cameraPreview = (CameraPreview) findViewById(R.id.camera_recording);
		cameraPreview.init(camera);
	}
	
	// When a picture is taken
	private PictureCallback photo = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera paCamera) {
			
			File recordedImage = getOutputMediaFile();
			if (recordedImage == null) {
				Log.d("onPictureTaken", "Error taking recorded image, check storage permissions");
				return;
			}
			
			try {
				FileOutputStream fos = new FileOutputStream(recordedImage);
				fos.write(data);
				fos.close();				
			} catch (FileNotFoundException e) {
				Log.d("File not found: ", e.getMessage());
			} catch (IOException e) {
				Log.d("Error accessing file: ", e.getMessage());
			}
			
			tempPhotoPath = recordedImage.getPath();
			
			Mat A;
			A = Imgcodecs.imread(tempPhotoPath, Imgcodecs.CV_LOAD_IMAGE_ANYDEPTH | Imgcodecs.CV_LOAD_IMAGE_COLOR);

			int rad = 57;
			int xCalDelta = -76;
			// convention is y,x for openCV, note distinctly different from viewfinder which is not
			// not only a different orientation but a DIFFERENT image altogether, rotated 90_deg
			sampleA1Intensity = vialAreaIntensity(rad, 1990, (1518 + xCalDelta), A);
			sampleA2Intensity = vialAreaIntensity(rad, 1792, (1519 + xCalDelta), A);
			sampleA3Intensity = vialAreaIntensity(rad, 1595, (1519 + xCalDelta), A);
			sampleA4Intensity = vialAreaIntensity(rad, 1399, (1519 + xCalDelta), A);
			sampleA5Intensity = vialAreaIntensity(rad, 1200, (1519 + xCalDelta), A);
			sampleA6Intensity = vialAreaIntensity(rad, 1009, (1519 + xCalDelta), A);
			sampleB1Intensity = vialAreaIntensity(rad, 1989, (1714 + xCalDelta), A);
			sampleB2Intensity = vialAreaIntensity(rad, 1790, (1714 + xCalDelta), A);
			sampleB3Intensity = vialAreaIntensity(rad, 1597, (1717 + xCalDelta), A);
			sampleB4Intensity = vialAreaIntensity(rad, 1401, (1717 + xCalDelta), A);
			sampleB5Intensity = vialAreaIntensity(rad, 1205, (1717 + xCalDelta), A);
			sampleB6Intensity = vialAreaIntensity(rad, 1006, (1717 + xCalDelta), A);
			sampleC1Intensity = vialAreaIntensity(rad, 1990, (1910 + xCalDelta), A);
			sampleC2Intensity = vialAreaIntensity(rad, 1791, (1909 + xCalDelta), A);
			sampleC3Intensity = vialAreaIntensity(rad, 1597, (1908 + xCalDelta), A);
			sampleC4Intensity = vialAreaIntensity(rad, 1405, (1906 + xCalDelta), A);
			sampleC5Intensity = vialAreaIntensity(rad, 1208, (1908 + xCalDelta), A);
			sampleC6Intensity = vialAreaIntensity(rad, 1014, (1911 + xCalDelta), A);
			sampleD1Intensity = vialAreaIntensity(rad, 1987, (2100 + xCalDelta), A);
			sampleD2Intensity = vialAreaIntensity(rad, 1796, (2101 + xCalDelta), A);
			sampleD3Intensity = vialAreaIntensity(rad, 1601, (2104 + xCalDelta), A);
			sampleD4Intensity = vialAreaIntensity(rad, 1408, (2104 + xCalDelta), A);
			sampleD5Intensity = vialAreaIntensity(rad, 1213, (2100 + xCalDelta), A);
			sampleD6Intensity = vialAreaIntensity(rad, 1011, (2112 + xCalDelta), A);
			sampleE1Intensity = vialAreaIntensity(rad, 1990, (2297 + xCalDelta), A);
			sampleE2Intensity = vialAreaIntensity(rad, 1792, (2299 + xCalDelta), A);
			sampleE3Intensity = vialAreaIntensity(rad, 1601, (2299 + xCalDelta), A);
			sampleE4Intensity = vialAreaIntensity(rad, 1401, (2303 + xCalDelta), A);
			sampleE5Intensity = vialAreaIntensity(rad, 1211, (2302 + xCalDelta), A);
			sampleE6Intensity = vialAreaIntensity(rad, 1012, (2307 + xCalDelta), A);
			sampleF1Intensity = vialAreaIntensity(rad, 1993, (2499 + xCalDelta), A);
			sampleF2Intensity = vialAreaIntensity(rad, 1799, (2503 + xCalDelta), A);
			sampleF3Intensity = vialAreaIntensity(rad, 1606, (2507 + xCalDelta), A);
			sampleF4Intensity = vialAreaIntensity(rad, 1405, (2503 + xCalDelta), A);
			sampleF5Intensity = vialAreaIntensity(rad, 1210, (2506 + xCalDelta), A);
			sampleF6Intensity = vialAreaIntensity(rad, 1009, (2512 + xCalDelta), A);
			
			// filling an array string with the values to be copied into the csv
			sampleValuesString[0] =  String.valueOf(sampleA1Intensity);
			sampleValuesString[1] =  String.valueOf(sampleA2Intensity);
			sampleValuesString[2] =  String.valueOf(sampleA3Intensity);
			sampleValuesString[3] =  String.valueOf(sampleA4Intensity);
			sampleValuesString[4] =  String.valueOf(sampleA5Intensity);
			sampleValuesString[5] =  String.valueOf(sampleA6Intensity);
			sampleValuesString[6] =  String.valueOf(sampleB1Intensity);
			sampleValuesString[7] =  String.valueOf(sampleB2Intensity);
			sampleValuesString[8] =  String.valueOf(sampleB3Intensity);
			sampleValuesString[9] =  String.valueOf(sampleB4Intensity);
			sampleValuesString[10] =  String.valueOf(sampleB5Intensity);
			sampleValuesString[11] =  String.valueOf(sampleB6Intensity);
			sampleValuesString[12] =  String.valueOf(sampleC1Intensity);
			sampleValuesString[13] =  String.valueOf(sampleC2Intensity);
			sampleValuesString[14] =  String.valueOf(sampleC3Intensity);
			sampleValuesString[15] =  String.valueOf(sampleC4Intensity);
			sampleValuesString[16] =  String.valueOf(sampleC5Intensity);
			sampleValuesString[17] =  String.valueOf(sampleC6Intensity);
			sampleValuesString[18] =  String.valueOf(sampleD1Intensity);
			sampleValuesString[19] =  String.valueOf(sampleD2Intensity);
			sampleValuesString[20] =  String.valueOf(sampleD3Intensity);
			sampleValuesString[21] =  String.valueOf(sampleD4Intensity);
			sampleValuesString[22] =  String.valueOf(sampleD5Intensity);
			sampleValuesString[23] =  String.valueOf(sampleD6Intensity);
			sampleValuesString[24] =  String.valueOf(sampleE1Intensity);
			sampleValuesString[25] =  String.valueOf(sampleE2Intensity);
			sampleValuesString[26] =  String.valueOf(sampleE3Intensity);
			sampleValuesString[27] =  String.valueOf(sampleE4Intensity);
			sampleValuesString[28] =  String.valueOf(sampleE5Intensity);
			sampleValuesString[29] =  String.valueOf(sampleE6Intensity);
			sampleValuesString[30] =  String.valueOf(sampleF1Intensity);
			sampleValuesString[31] =  String.valueOf(sampleF2Intensity);
			sampleValuesString[32] =  String.valueOf(sampleF3Intensity);
			sampleValuesString[33] =  String.valueOf(sampleF4Intensity);
			sampleValuesString[34] =  String.valueOf(sampleF5Intensity);
			sampleValuesString[35] =  String.valueOf(sampleF6Intensity);

			
			// rounding to nearest int for display
			sampleA1Display = (int)Math.round(sampleA1Intensity);
			sampleA2Display = (int)Math.round(sampleA2Intensity);
			sampleA3Display = (int)Math.round(sampleA3Intensity);
			sampleA4Display = (int)Math.round(sampleA4Intensity);
			sampleA5Display = (int)Math.round(sampleA5Intensity);
			sampleA6Display = (int)Math.round(sampleA6Intensity);
			sampleB1Display = (int)Math.round(sampleB1Intensity);
			sampleB2Display = (int)Math.round(sampleB2Intensity);
			sampleB3Display = (int)Math.round(sampleB3Intensity);
			sampleB4Display = (int)Math.round(sampleB4Intensity);
			sampleB5Display = (int)Math.round(sampleB5Intensity);
			sampleB6Display = (int)Math.round(sampleB6Intensity);
			sampleC1Display = (int)Math.round(sampleC1Intensity);
			sampleC2Display = (int)Math.round(sampleC2Intensity);
			sampleC3Display = (int)Math.round(sampleC3Intensity);
			sampleC4Display = (int)Math.round(sampleC4Intensity);
			sampleC5Display = (int)Math.round(sampleC5Intensity);
			sampleC6Display = (int)Math.round(sampleC6Intensity);
			sampleD1Display = (int)Math.round(sampleD1Intensity);
			sampleD2Display = (int)Math.round(sampleD2Intensity);
			sampleD3Display = (int)Math.round(sampleD3Intensity);
			sampleD4Display = (int)Math.round(sampleD4Intensity);
			sampleD5Display = (int)Math.round(sampleD5Intensity);
			sampleD6Display = (int)Math.round(sampleD6Intensity);
			sampleE1Display = (int)Math.round(sampleE1Intensity);
			sampleE2Display = (int)Math.round(sampleE2Intensity);
			sampleE3Display = (int)Math.round(sampleE3Intensity);
			sampleE4Display = (int)Math.round(sampleE4Intensity);
			sampleE5Display = (int)Math.round(sampleE5Intensity);
			sampleE6Display = (int)Math.round(sampleE6Intensity);
			sampleF1Display = (int)Math.round(sampleF1Intensity);
			sampleF2Display = (int)Math.round(sampleF2Intensity);
			sampleF3Display = (int)Math.round(sampleF3Intensity);
			sampleF4Display = (int)Math.round(sampleF4Intensity);
			sampleF5Display = (int)Math.round(sampleF5Intensity);
			sampleF6Display = (int)Math.round(sampleF6Intensity);

			timeCount = timeCount + 10;
			Log.d("count + offset time ", timeCount+"");

            Parameters params2 = camera.getParameters();
            float focusDistances[] = new float[3];
            params2.getFocusDistances(focusDistances);

            sampleValuesString[36] = String.valueOf(timeCount);
            sampleValuesString[37] = (focusDistances[0]+"");
            sampleValuesString[38] = (focusDistances[1]+"");
            sampleValuesString[39] = (focusDistances[2]+"");
			
			// write it
			csvWriter.writeNext(sampleValuesString);
			
			// display the average intensity of these two squares in the test area
			recordingReactionTextViewDisplayIntensityA1.setText(String.valueOf(sampleA1Display));
			recordingReactionTextViewDisplayIntensityA2.setText(String.valueOf(sampleA2Display));
			recordingReactionTextViewDisplayIntensityA3.setText(String.valueOf(sampleA3Display));
			recordingReactionTextViewDisplayIntensityA4.setText(String.valueOf(sampleA4Display));
			recordingReactionTextViewDisplayIntensityA5.setText(String.valueOf(sampleA5Display));
			recordingReactionTextViewDisplayIntensityA6.setText(String.valueOf(sampleA6Display));
			recordingReactionTextViewDisplayIntensityB1.setText(String.valueOf(sampleB1Display));
			recordingReactionTextViewDisplayIntensityB2.setText(String.valueOf(sampleB2Display));
			recordingReactionTextViewDisplayIntensityB3.setText(String.valueOf(sampleB3Display));
			recordingReactionTextViewDisplayIntensityB4.setText(String.valueOf(sampleB4Display));
			recordingReactionTextViewDisplayIntensityB5.setText(String.valueOf(sampleB5Display));
			recordingReactionTextViewDisplayIntensityB6.setText(String.valueOf(sampleB6Display));
			recordingReactionTextViewDisplayIntensityC1.setText(String.valueOf(sampleC1Display));
			recordingReactionTextViewDisplayIntensityC2.setText(String.valueOf(sampleC2Display));
			recordingReactionTextViewDisplayIntensityC3.setText(String.valueOf(sampleC3Display));
			recordingReactionTextViewDisplayIntensityC4.setText(String.valueOf(sampleC4Display));
			recordingReactionTextViewDisplayIntensityC5.setText(String.valueOf(sampleC5Display));
			recordingReactionTextViewDisplayIntensityC6.setText(String.valueOf(sampleC6Display));
			recordingReactionTextViewDisplayIntensityD1.setText(String.valueOf(sampleD1Display));
			recordingReactionTextViewDisplayIntensityD2.setText(String.valueOf(sampleD2Display));
			recordingReactionTextViewDisplayIntensityD3.setText(String.valueOf(sampleD3Display));
			recordingReactionTextViewDisplayIntensityD4.setText(String.valueOf(sampleD4Display));
			recordingReactionTextViewDisplayIntensityD5.setText(String.valueOf(sampleD5Display));
			recordingReactionTextViewDisplayIntensityD6.setText(String.valueOf(sampleD6Display));
			recordingReactionTextViewDisplayIntensityE1.setText(String.valueOf(sampleE1Display));
			recordingReactionTextViewDisplayIntensityE2.setText(String.valueOf(sampleE2Display));
			recordingReactionTextViewDisplayIntensityE3.setText(String.valueOf(sampleE3Display));
			recordingReactionTextViewDisplayIntensityE4.setText(String.valueOf(sampleE4Display));
			recordingReactionTextViewDisplayIntensityE5.setText(String.valueOf(sampleE5Display));
			recordingReactionTextViewDisplayIntensityE6.setText(String.valueOf(sampleE6Display));
			recordingReactionTextViewDisplayIntensityF1.setText(String.valueOf(sampleF1Display));
			recordingReactionTextViewDisplayIntensityF2.setText(String.valueOf(sampleF2Display));
			recordingReactionTextViewDisplayIntensityF3.setText(String.valueOf(sampleF3Display));
			recordingReactionTextViewDisplayIntensityF4.setText(String.valueOf(sampleF4Display));
			recordingReactionTextViewDisplayIntensityF5.setText(String.valueOf(sampleF5Display));
			recordingReactionTextViewDisplayIntensityF6.setText(String.valueOf(sampleF6Display));
			
			camera.startPreview();

            params2.setAutoExposureLock(true);
            camera.setParameters(params2);
            Log.d("resuming prev wht lock", params2.getAutoWhiteBalanceLock()+"");
            Log.d("resuming prev exp lock", params2.getAutoExposureLock()+"");
            Log.d("after pic exp comp is", params2.getExposureCompensation()+"");
            Log.d("after pic exp comp step", params2.getExposureCompensationStep()+"");
		}
	};
	
	@SuppressLint("SimpleDateFormat")
	private static File getOutputMediaFile() {
		// To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.		
		// The path has already been determined from the two screens ago when we selected
		// the name of the standard curve. The path is then passed via bundles through the
		// selection (creation) screen to the prepare recording screen and now to this screen
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile = null;
	    
	    // put the stuff in the directory depending on the reaction type
	    if(recordingType == STANDARD_CURVE) {
	    	 mediaFile = new File(standardCurveInfo[1] + File.separator + "IMG_"+ timeStamp + ".jpg");
		} else if(recordingType == SAMPLE) {
			 mediaFile = new File(sampleInfo[1] + File.separator + "IMG_"+ timeStamp + ".jpg");
		}
	    return mediaFile;
	}
							
	// Don't want any other applications to touch camera
	// ALWAYS remember to release the camera when you are finished
	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
	}

    Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback(){

        @Override
        public void onAutoFocus(boolean autoFocusSuccess, Camera camera) {
            // after autofocus occurs, lock the exposure and white balance settings
            if(autoFocusSuccess == true){
                Log.d("Autofocus", "camera is auto focused");
                Parameters params1 = camera.getParameters();
                Log.d("post AF exp lock state", params1.getAutoExposureLock()+"");
                Log.d("post AF wht lock state", params1.getAutoWhiteBalanceLock()+"");
                params1.setAutoExposureLock(true);
                params1.setAutoWhiteBalanceLock(true);
                camera.setParameters(params1);
                Log.d("post force wht lock", params1.getAutoWhiteBalanceLock()+"");
                Log.d("post force exp lock", params1.getAutoExposureLock()+"");
                // Log.d("AutoExposureLock", "auto exposure is locked");



            }
        }
    };

	// UNSURE IF WISE TO COMPLETELY DISCONNECT CAMERA OR NOT
	private void releaseCamera() {
		if(camera != null){
			camera.release();
			camera = null;
		}
	}
	

	/*
	private void setButtonOnClickListeners(){
		
		// Cancel and go back to prepare recording screen
		recordingReactionButtonCancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				// Cancel and go back to prepare recording screen
				// Intent i1 = new Intent(RecordingReaction.this, PrepareRecording.class);
				// startActivity(i1);
                Log.d("Cancel Button", "It works! App did not stop? :D");
			}		
		});			
	}
	*/
	
	private double vialAreaIntensity(int r, int x_0, int y_0, Mat photoFrame){
		 
		// initializing the average intensity of the vial and the sum
		double averageGreenIntensity = 0;
		double sumAllGreenPixels = 0;
		// summing the number of pixels over a certain threshold
		int count = 0;
		double[] testPoint = new double[3];	
		
		
 
		// scan over the frame and count the green value of the area for every pixel within a threshold
		// start with the initial coordinates in the upper left section of that photo

		for(int x = (x_0 - r); x < (x_0 + r); x++){
			for(int y = (y_0 - r); y < (y_0 + r); y++){
				if((((x - x_0) * (x - x_0)) + ((y - y_0) * (y - y_0))) < (r * r)){
					testPoint = photoFrame.get(x, y);
 					count++;
					sumAllGreenPixels = sumAllGreenPixels + testPoint[1];				
				}	
			}
		}
		
		averageGreenIntensity = sumAllGreenPixels / count;		
		return averageGreenIntensity;
	}

    public void RefreshTimer() {
        final Handler handler = new Handler();
        final Runnable counter = new Runnable(){

            public void run(){
                recordingReactionTextTimeLeft.setText(Long.toString((countDownTimer2.getCurrentTime())/1000));
                handler.postDelayed(this, 100);
            }
        };

        handler.postDelayed(counter, 100);
    }

    public class MyCountDownTimer {
        private long millisInFuture;
        private long countDownInterval;
        private boolean status;
        public MyCountDownTimer(long pMillisInFuture, long pCountDownInterval) {
            this.millisInFuture = pMillisInFuture;
            this.countDownInterval = pCountDownInterval;
            status = false;
            Initialize();
        }

        public void Stop() {
            status = false;
        }

        public long getCurrentTime() {
            return millisInFuture;
        }

        public void Start() {
            status = true;
        }
        public void Initialize()
        {
            final Handler handler = new Handler();
            Log.v("status", "starting");
            final Runnable counter = new Runnable(){

                public void run(){
                    long sec = millisInFuture/1000;
                    if(status) {
                        if(millisInFuture <= 0) {
                            Log.v("status", "done");
                            try {
                                csvWriter.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            Intent i1 = new Intent(RecordingReaction.this, RecordingSuccess.class);
                            Bundle b1 = new Bundle();
                            b1.putStringArray(STANDARD_CURVE_INFO, standardCurveInfo);
                            b1.putStringArray(REFERENCE_INFO, referenceInfo);
                            b1.putStringArray(REACTION_INFO, sampleInfo);
                            b1.putInt(RECORDING_TYPE, recordingType);
                            b1.putInt(TIMECOUNT, timeCount);
                            b1.putInt(DATA_POINTS, dataPoints);
							b1.putString(SOURCE_TYPE, sourceType);
                            i1.putExtras(b1);
                            startActivity(i1);

                        } else {
                            Log.v("status", Long.toString(sec) + " seconds remain");
                            millisInFuture -= countDownInterval;
                            // Parameters params3 = camera.getParameters();

                            // float focusDistances[] = new float[3];
                            // params3.getFocusDistances(focusDistances);

                            // Log.d("before taking picture, white balance lock state is", params3.getAutoWhiteBalanceLock()+"");
                            // Log.d("before taking picture, exposure lock state is", params3.getAutoExposureLock()+"");
                            // Log.d("before taking picture, exposure compensation is", params3.getExposureCompensation()+"");
                            // Log.d("before taking picture, exposure comp step is", params3.getExposureCompensationStep()+"");
                            // Log.d("","");
                            // Log.d("Focal Length before Photos", params3.getFocalLength()+"");
                            // Log.d("Focus Distances BEFORE picture", focusDistances.toString());
                            // params3.setAutoExposureLock(true);
                            // camera.setParameters(params3);
                            camera.takePicture(null, null, photo);

                            Parameters params3 = camera.getParameters();
                            params3.setAutoExposureLock(true);
                            camera.setParameters(params3);

                            // Log.d("","");
                            // Log.d("after taking picture, white balance lock state is", params3.getAutoWhiteBalanceLock()+"");
                            // Log.d("after taking picture, exposure lock state is", params3.getAutoExposureLock()+"");
                            //  Log.d("after taking picture, exposure compensation is", params3.getExposureCompensation()+"");
                            // Log.d("Focus Distances after picture", focusDistances.toString());
                            // params3.setAutoExposureLock(true);
                            // camera.setParameters(params3);


                            // Log.d("after taking picture, exposure comp step is", params3.getExposureCompensationStep()+"");
                            // Log.d("Focal Length AFTER Photos", params3.getFocalLength()+"");

                            // camera.startPreview();
                            handler.postDelayed(this, countDownInterval);


                        }
                    } else {
                        Log.v("status", Long.toString(sec) + " seconds remain and timer has stopped!");
                        handler.postDelayed(this, countDownInterval);
                    }
                }
            };

            handler.postDelayed(counter, countDownInterval);
        }
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
	
	@Override
    public void onResume()
    {
        super.onResume();
        // OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recording_reaction, menu);
		return true;
	}

}