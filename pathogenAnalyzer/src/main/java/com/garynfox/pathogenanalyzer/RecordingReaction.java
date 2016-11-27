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
	// public final static int MINUTES = 4;
	
	// Stuff being packed into the bundle
	static String[] standardCurveInfo = new String[3];
	static String[] sampleInfo = new String[3];
	static String[] referenceInfo = new String[3];
	static int recordingType = 999;
	
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
	double sample00Intensity = 0;
	double sample01Intensity = 0;
	double sample02Intensity = 0;
	double sample03Intensity = 0;
	double sample10Intensity = 0;
	double sample11Intensity = 0;
	double sample12Intensity = 0;
	double sample13Intensity = 0;
	double sample20Intensity = 0;
	double sample21Intensity = 0;
	double sample22Intensity = 0;
	double sample23Intensity = 0;
	String[] sampleValuesString = new String[16];
	
	// versions to display on the screen
	int sample00Display = 0;
	int sample01Display = 0;
	int sample02Display = 0;
	int sample03Display = 0;
	int sample10Display = 0;
	int sample11Display = 0;
	int sample12Display = 0;
	int sample13Display = 0;
	int sample20Display = 0;
	int sample21Display = 0;
	int sample22Display = 0;
	int sample23Display = 0;
	
	// number of pictures taken = 50 min * 6 per minute = 300
	int dataPoints = 300;
		
	// Declare cancel button
	// Button recordingReactionButtonCancel;

	// Declare text view displaying realtime intensity changes
	TextView recordingReactionTextViewDisplayIntensity00;
	TextView recordingReactionTextViewDisplayIntensity01;
	TextView recordingReactionTextViewDisplayIntensity02;
	TextView recordingReactionTextViewDisplayIntensity03;
	TextView recordingReactionTextViewDisplayIntensity10;
	TextView recordingReactionTextViewDisplayIntensity11;
	TextView recordingReactionTextViewDisplayIntensity12;
	TextView recordingReactionTextViewDisplayIntensity13;
	TextView recordingReactionTextViewDisplayIntensity20;
	TextView recordingReactionTextViewDisplayIntensity21;
	TextView recordingReactionTextViewDisplayIntensity22;
	TextView recordingReactionTextViewDisplayIntensity23;
	
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
		
		// Unpacking bundle from previous intent, mainly to pass to next intent
		Intent previousScreen = getIntent();
		Bundle bundleReceived = previousScreen.getExtras();
				
		standardCurveInfo = bundleReceived.getStringArray(ChooseAction.STANDARD_CURVE_INFO);
		sampleInfo = bundleReceived.getStringArray(ChooseAction.REACTION_INFO);
		referenceInfo = bundleReceived.getStringArray(ChooseAction.REFERENCE_INFO);
		recordingType = bundleReceived.getInt(ChooseAction.RECORDING_TYPE);
		sourceType = bundleReceived.getString(ChooseAction.SOURCE_TYPE);
		
		// Inflate the GUI
		setContentView(R.layout.activity_recording_reaction);
		
		// Determine reaction type
		if(recordingType == STANDARD_CURVE){
			dataPath = standardCurveInfo[2];
		} else if(recordingType == SAMPLE){
			dataPath = sampleInfo[2];
		}
		String[] entries = "smpl00#smpl01#smpl02#smpl03#smpl10#smpl11#smpl12#smpl13#smpl20#smpl21#smpl22#smpl23#time#focus_dist1#focus_dist2#focus_dist3".split("#");
					
		try {
			csvWriter = new CSVWriter(new FileWriter(dataPath));
			csvWriter.writeNext(entries);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// display the text view that shows intensity
		recordingReactionTextViewDisplayIntensity00 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensity00);
		recordingReactionTextViewDisplayIntensity01 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensity01);
		recordingReactionTextViewDisplayIntensity02 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensity02);
		recordingReactionTextViewDisplayIntensity03 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensity03);
		recordingReactionTextViewDisplayIntensity10 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensity10);
		recordingReactionTextViewDisplayIntensity11 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensity11);
		recordingReactionTextViewDisplayIntensity12 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensity12);
		recordingReactionTextViewDisplayIntensity13 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensity13);
		recordingReactionTextViewDisplayIntensity20 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensity20);
		recordingReactionTextViewDisplayIntensity21 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensity21);
		recordingReactionTextViewDisplayIntensity22 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensity22);
		recordingReactionTextViewDisplayIntensity23 = (TextView) findViewById(R.id.recordingReactionTextViewDisplayIntensity23);
		
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

        // countDownTimer2 = new MyCountDownTimer(3000, 1500);

        countDownTimer2 = new MyCountDownTimer(3000000, 10000);

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
			
			// test starting from one frame at (0, 0) then (100, 100)
			sample00Intensity = vialAreaIntensity(30, 1888, 1569, A);
			sample01Intensity = vialAreaIntensity(30, 1668, 1573,  A);
			sample02Intensity = vialAreaIntensity(30, 1465, 1563, A);
			sample03Intensity = vialAreaIntensity(30, 1249,  1556, A);
			sample10Intensity = vialAreaIntensity(30, 1887, 1786,  A);
			sample11Intensity = vialAreaIntensity(30, 1666, 1790, A);
			sample12Intensity = vialAreaIntensity(30, 1458, 1779, A);
			sample13Intensity = vialAreaIntensity(30, 1244,  1779, A);
			sample20Intensity = vialAreaIntensity(30, 1875, 2008, A);
			sample21Intensity = vialAreaIntensity(30, 1659, 2002, A);
			sample22Intensity = vialAreaIntensity(30, 1451, 1993, A);
			sample23Intensity = vialAreaIntensity(30, 1253,  1998, A);
			
			// filling an array string with the values to be copied into the csv
			sampleValuesString[0] =  String.valueOf(sample00Intensity);
			sampleValuesString[1] =  String.valueOf(sample01Intensity);
			sampleValuesString[2] =  String.valueOf(sample02Intensity);
			sampleValuesString[3] =  String.valueOf(sample03Intensity);
			sampleValuesString[4] =  String.valueOf(sample10Intensity);
			sampleValuesString[5] =  String.valueOf(sample11Intensity);
			sampleValuesString[6] =  String.valueOf(sample12Intensity);
			sampleValuesString[7] =  String.valueOf(sample13Intensity);
			sampleValuesString[8] =  String.valueOf(sample20Intensity);
			sampleValuesString[9] =  String.valueOf(sample21Intensity);
			sampleValuesString[10] = String.valueOf(sample22Intensity);
			sampleValuesString[11] = String.valueOf(sample23Intensity);
			
			// rounding to nearest int for display
			sample00Display = (int)Math.round(sample00Intensity);
			sample01Display = (int)Math.round(sample01Intensity);
			sample02Display = (int)Math.round(sample02Intensity);
			sample03Display = (int)Math.round(sample03Intensity);
			sample10Display = (int)Math.round(sample10Intensity);
			sample11Display = (int)Math.round(sample11Intensity);
			sample12Display = (int)Math.round(sample12Intensity);
			sample13Display = (int)Math.round(sample13Intensity);
			sample20Display = (int)Math.round(sample20Intensity);
			sample21Display = (int)Math.round(sample21Intensity);
			sample22Display = (int)Math.round(sample22Intensity);
			sample23Display = (int)Math.round(sample23Intensity);
			
			
			timeCount++;

            Parameters params2 = camera.getParameters();
            float focusDistances[] = new float[3];
            params2.getFocusDistances(focusDistances);

            sampleValuesString[12] = String.valueOf(timeCount);
            sampleValuesString[13] = (focusDistances[0]+"");
            sampleValuesString[14] = (focusDistances[1]+"");
            sampleValuesString[15] = (focusDistances[2]+"");
			
			// write it
			csvWriter.writeNext(sampleValuesString);
			
			// display the average intensity of these two squares in the test area
			recordingReactionTextViewDisplayIntensity00.setText(String.valueOf(sample00Display));
			recordingReactionTextViewDisplayIntensity01.setText(String.valueOf(sample01Display));
			recordingReactionTextViewDisplayIntensity02.setText(String.valueOf(sample02Display));
			recordingReactionTextViewDisplayIntensity03.setText(String.valueOf(sample03Display));
			recordingReactionTextViewDisplayIntensity10.setText(String.valueOf(sample10Display));
			recordingReactionTextViewDisplayIntensity11.setText(String.valueOf(sample11Display));
			recordingReactionTextViewDisplayIntensity12.setText(String.valueOf(sample12Display));
			recordingReactionTextViewDisplayIntensity13.setText(String.valueOf(sample13Display));
			recordingReactionTextViewDisplayIntensity20.setText(String.valueOf(sample20Display));
			recordingReactionTextViewDisplayIntensity21.setText(String.valueOf(sample21Display));
			recordingReactionTextViewDisplayIntensity22.setText(String.valueOf(sample22Display));
			recordingReactionTextViewDisplayIntensity23.setText(String.valueOf(sample23Display));
			
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
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recording_reaction, menu);
		return true;
	}

}