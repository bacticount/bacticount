package com.garynfox.pathogenanalyzer;

import android.content.Intent;
//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class RecordingSuccess extends Activity {

    // Value pairs needed to pass bundle along
    public final static String STANDARD_CURVE_INFO = "com.garynfox.pathogenanalyzer.STANDARD_CURVE_INFO";
    public final static String REACTION_INFO = "com.garynfox.pathogenanalyzer.REACTION_INFO";
    public final static String REFERENCE_INFO = "com.garynfox.pathogenanalyzer.REFERENCE_INFO";
    public final static String RECORDING_TYPE = "com.garynfox.pathogenanalyzer.RECORDING_TYPE";
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
    static int timeCount = 0;
    static int dataPoints = 420;
    static double[] sampleTimes = new double[12];

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

    Button recordingSuccessButtonHome;

    TextView recordingSuccessIdentifier;
    TextView recordingSuccessMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_success);

        Intent previousScreen = getIntent();
        Bundle bundleReceived = previousScreen.getExtras();

        standardCurveInfo = bundleReceived.getStringArray(RecordingReaction.STANDARD_CURVE_INFO);
        sampleInfo = bundleReceived.getStringArray(RecordingReaction.REACTION_INFO);
        referenceInfo = bundleReceived.getStringArray(RecordingReaction.REFERENCE_INFO);
        recordingType = bundleReceived.getInt(RecordingReaction.RECORDING_TYPE);
        timeCount = bundleReceived.getInt(RecordingReaction.TIMECOUNT);
        dataPoints = bundleReceived.getInt(RecordingReaction.DATA_POINTS);

        recordingSuccessIdentifier = (TextView) findViewById(R.id.recordingSuccessIdentifier);
        recordingSuccessMessage = (TextView) findViewById(R.id.recordingSuccessMessage);
        recordingSuccessButtonHome = (Button) findViewById(R.id.recordingSuccessButtonHome);

        if(recordingType == STANDARD_CURVE){
            recordingSuccessIdentifier.setText("Standard Curve Complete");
        } else if(recordingType == SAMPLE){
            recordingSuccessIdentifier.setText("Unknown Samples Complete");
        }

        setButtonOnClickListeners();

    }


    private void setButtonOnClickListeners(){

        // Go back home after seeing results
        recordingSuccessButtonHome.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                // Launch recording reaction screen
                Intent i1 = new Intent(RecordingSuccess.this, HomeScreen.class);
                startActivity(i1);
            }
        });
    }
}
